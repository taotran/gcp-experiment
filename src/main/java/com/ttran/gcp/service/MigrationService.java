package com.ttran.gcp.service;

import com.google.cloud.bigquery.migration.v2.*;
import com.ttran.gcp.TaskWrapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@AllArgsConstructor
public class MigrationService {

    private final MigrationServiceClient migrationServiceClient;

    public MigrationWorkflow getWorkflow(String workflowName) {
        return migrationServiceClient.getMigrationWorkflow(workflowName);
    }

    public MigrationWorkflow getWorkflow(MigrationWorkflowName migrationWorkflowName) {
        return migrationServiceClient.getMigrationWorkflow(migrationWorkflowName);
    }

    public MigrationWorkflow createWorkflow(LocationName parent, MigrationWorkflow builtWorkflow) {
        return migrationServiceClient.createMigrationWorkflow(parent, builtWorkflow);
    }

    public TaskWrapper getMigrationTask(String projectNumber, UUID workflowId, UUID taskId) {
        final String workflowName = String.format("projects/%s/locations/us/workflows/%s", projectNumber, workflowId);
        final MigrationWorkflow workflow = getWorkflow(workflowName);

        if (workflow == null) {
            log.warn("No workflow found with workflowName = {}", workflowName);
            return null;
        }

        final MigrationTask task = workflow
                .getTasksMap()
                .values()
                .stream()
                .filter(migrationTask -> migrationTask.getId().equalsIgnoreCase(taskId.toString()))
                .findFirst()
                .orElse(null);


        return TaskWrapper.of(task);
    }

    public TaskWrapper getMigrationTask(String workflowName, UUID taskId) {
        final MigrationWorkflow workflow = getWorkflow(workflowName);

        if (workflow == null) {
            log.warn("No workflow found with workflowName = {}", workflowName);
            return null;
        }
        return getWorkflowTask(workflow, taskId);
    }

    private TaskWrapper getWorkflowTask(MigrationWorkflow workflow, UUID taskId) {
        final MigrationTask task = workflow
                .getTasksMap()
                .values()
                .stream()
                .filter(migrationTask -> migrationTask.getId().equalsIgnoreCase(taskId.toString()))
                .findFirst()
                .orElse(null);


        return TaskWrapper.of(task);
    }

}
