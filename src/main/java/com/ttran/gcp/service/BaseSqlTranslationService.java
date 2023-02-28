package com.ttran.gcp.service;

import com.google.cloud.bigquery.migration.v2.*;
import com.google.protobuf.Timestamp;
import com.ttran.gcp.*;
import com.ttran.gcp.properties.GcpTranslationProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public abstract class BaseSqlTranslationService implements SqlTranslationService {

    private static final String GS_BUCKET_PREFIX = "gs://";
    private final GcpBucketService bucketService;
    private final MigrationService migrationService;

    @Autowired
    private GcpTranslationProperties translationProperties;

    public BaseSqlTranslationService(GcpBucketService bucketService, MigrationService migrationService) {
        this.bucketService = bucketService;
        this.migrationService = migrationService;
    }

    @Override
    public SqlTranslationResponse process(SqlTranslationRequest request) throws Exception {
        log.info("Processing TranslationRequest {}", request);
        final SqlTranslationRequest postPreProcReq = preProcess(request);
        log.info("After pre-process request {}", request);

        // write raw data to bucket
        final boolean inputDataCreated = bucketService.createBlob("test_conversion.hql", request.getData(),
                request.getProjectId(),
                request.getInPath());

        Assert.isTrue(inputDataCreated, "Failed on input data creation");

        final LocationName parent = LocationName.of(postPreProcReq.getProjectId(), postPreProcReq.getLocation());

        final MigrationWorkflow migrationWorkflow =
                createTranslationWorkflow(
                        postPreProcReq.getProjectId(),
                        parent,
                        postPreProcReq.getOutPath(),
                        postPreProcReq.getInPath(),
                        postPreProcReq.getWorkflowName(),
                        postPreProcReq.getTaskName());

        log.info("Start creating migration workflow: {}", migrationWorkflow);
        final MigrationWorkflow response = migrationService.createWorkflow(parent, migrationWorkflow);

        final String taskId = reportWorkflowStatus(response, request.getTaskName());
        log.info("Task {}", taskId);
        return postProcess(migrationWorkflow,
                SqlTranslationResponse.of(response.getName(), response.getDisplayName(), taskId, Collections.emptyList()));
    }

    protected SqlTranslationRequest preProcess(SqlTranslationRequest request) {
        return request;
    }

    protected SqlTranslationResponse postProcess(MigrationWorkflow responseWorkflow, SqlTranslationResponse response) throws Exception {
        if (!tryGetResult()) {
            return response;
        }

        int count = 1;
        while (true) {
            final TaskWrapper task = migrationService.getMigrationTask(response.getWorkflowName(),
                    UUID.fromString(response.getTaskId()));

            log.info("current status: {}, {} tries", task.getState(), count);
            if (MigrationTask.State.SUCCEEDED.name().equalsIgnoreCase(task.getState())) {
                log.info("SUCCESS, {}", task);
                response.setOutput(formatOutput(getConvertedQueries()));
                return response;
            }

            if (count >= numberOfRetries()) {
                break;
            }
            count++;
            Thread.sleep(resultRetrieveInterval());
        }
        return response;
    }

    protected String getConvertedQueries() throws Exception {
        return bucketService.getBlobContent("test_conversion.hql", translationProperties.getProjectId(),
                translationProperties.getOutPath());
    }

    private MigrationWorkflow createTranslationWorkflow(String projectId,
                                                        LocationName parent,
                                                        String outPath,
                                                        String inPath,
                                                        String workflowName,
                                                        String taskName) {
        MigrationWorkflow workflow = null;
        final TranslationTaskType taskType = taskType();
        final TranslationDialectFactory.TranslationDialect translationDialect = translationDialect();
        try {

            final TranslationConfigDetails taskDetails = TranslationConfigDetails.newBuilder()
                    .setGcsSourcePath(correctGsBucketUri(inPath))
                    .setGcsTargetPath(correctGsBucketUri(outPath))
                    .setSourceDialect(translationDialect.getSource())
                    .setTargetDialect(translationDialect.getTarget())
                    .build();

            final MigrationTask task = MigrationTask.newBuilder()
                    .setType(taskType.getGcpType())
                    .setTranslationConfigDetails(taskDetails)
                    .build();

            workflow = MigrationWorkflow.newBuilder()
                    .setName(MigrationWorkflowName.of(projectId, parent.getLocation(), workflowName).toString())
                    .setDisplayName(workflowName)
                    .putTasks(taskName, task)
                    .setCreateTime(Timestamp.newBuilder().build())
                    .setLastUpdateTime(Timestamp.newBuilder().build())
                    .build();
//            log.info("migration workflow defined.");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return workflow;
    }

    private String reportWorkflowStatus(MigrationWorkflow workflow, String taskName) {
        log.info("Migration workflow " + workflow.getName() + " is in state " + workflow.getState() + ".");

        for (Map.Entry<String, MigrationTask> entry : workflow.getTasksMap().entrySet()) {
            String k = entry.getKey();
            MigrationTask task = entry.getValue();

            log.info("Task {} had id {}", k, task.getId());

            if (task.hasProcessingError()) {
                log.error("Task execution with processing error: {}", task.getProcessingError().getReason());
            }
            if (taskName.equalsIgnoreCase(k)) {
                return task.getId();
            }
        }

        return null;
    }

    private String correctGsBucketUri(String inputPath) {
        Assert.hasText(inputPath, "Empty path is not allowed");

        return inputPath.contains(GS_BUCKET_PREFIX) ? inputPath : String.format("%s%s", GS_BUCKET_PREFIX, inputPath);
    }

    private MigrationServiceSettings migrationServiceSettings() throws IOException {
        return MigrationServiceSettings.newBuilder().build();
    }

    protected List<String> formatOutput(String rawContent) {
        return StringUtils.isBlank(rawContent) ? Collections.emptyList() :
                Arrays.stream(rawContent.split(";")).map(s->s.replace("\n"," ")).collect(Collectors.toList());
    }

}