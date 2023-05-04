package com.ttran.gcp.rest;

import com.google.cloud.bigquery.migration.v2.LocationName;
import com.google.cloud.bigquery.migration.v2.MigrationServiceClient;
import com.google.cloud.bigquery.migration.v2.MigrationWorkflow;
import com.ttran.gcp.SqlTranslationRequest;
import com.ttran.gcp.TranslationRequestPayload;
import com.ttran.gcp.TranslationTaskType;
import com.ttran.gcp.properties.GcpTranslationProperties;
import com.ttran.gcp.service.MigrationService;
import com.ttran.gcp.service.SqlTranslationService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/ngbd/mdm-api/workflows")
@AllArgsConstructor
public class SqlTranslationController {

    private final Map<TranslationTaskType, SqlTranslationService> translationServices;

    private final GcpTranslationProperties gcpTranslationProperties;

    private final MigrationService migrationService;

    private final MigrationServiceClient migrationServiceClient;

    @PostMapping("/conversions")
    public ResponseEntity<?> convert(@RequestBody TranslationRequestPayload<?> requestPayload) throws Exception {
        final long start = System.currentTimeMillis();
        log.info("Start doing the conversion, request: {}", requestPayload);
        try {
            log.info("Request data {}", requestPayload);
            final TranslationTaskType type = TranslationTaskType.of(requestPayload.getType());

            Assert.notNull(type, "Invalid input type, requestData = " + requestPayload);

            final SqlTranslationRequest request = SqlTranslationRequest.of(requestPayload, gcpTranslationProperties);

            return ResponseEntity.ok(translationServices.get(type).process(request));
        } finally {
            log.info("Conversion finished, latency={}", System.currentTimeMillis() - start);
        }
    }

    @GetMapping("/conversions/task/{taskId}/status")
    public ResponseEntity<?> getTaskStatus(@PathVariable UUID taskId,
                                           @RequestParam UUID workflowId,
                                           @RequestParam(required = false) String projectNumber) {
        final String project = StringUtils.hasText(projectNumber) ? projectNumber :
                gcpTranslationProperties.getProjectId();

        return ResponseEntity.ok(migrationService.getMigrationTask(project, workflowId, taskId));
    }

//
//    @GetMapping("/conversions/")
//    public ResponseEntity<?> get

//    @GetMapping("/conversions/workflow")
//    public ResponseEntity<?> getTaskStatus1(@RequestParam String workflowName) {
//        final MigrationWorkflow workflow = migrationService.getWorkflow(workflowName);
//        if (workflow == null) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
//        }
//        return ResponseEntity.ok(workflow);
//    }
//
//    @GetMapping("/conversions/workflow/all")
//    public ResponseEntity<?> getAllWorkflows(@RequestParam(required = false) String project) {
//        final MigrationServiceClient.ListMigrationWorkflowsPagedResponse response =
//                migrationServiceClient.listMigrationWorkflows(LocationName.of(StringUtils.hasText(project) ? project : gcpTranslationProperties.getProjectId(),
//                        "us"));
//        Iterator<MigrationWorkflow> iterator = response.iterateAll().iterator();
//
//        final List<MigrationWorkflow> result = new ArrayList<>();
//        while (iterator.hasNext()) {
//            result.add(iterator.next());
//        }
//
//        return ResponseEntity.ok(result);
//    }
}
