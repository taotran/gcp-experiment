package com.ttran.gcp.service;


import com.google.cloud.bigquery.migration.v2.MigrationTask;
import com.ttran.gcp.TaskWrapper;
import com.ttran.gcp.properties.GcpTranslationProperties;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@RequiredArgsConstructor
@Component
public class TranslationJobInquiryManager {

    private static final Map<String, TaskEnquiryDetails> TASKS_MAP = new ConcurrentHashMap<>();

    private final GcpTranslationProperties gcpTranslationProperties;

    private final MigrationService migrationService;

    @Scheduled(fixedDelay = 5000L)
    public void inquiryTranslationStatus() {

        log.debug("TaskEnquiryDetails size: {}", TASKS_MAP.size());

        TASKS_MAP.forEach((taskId, taskDetails) -> {
            final String project = StringUtils.hasText(taskDetails.getProjectId()) ? taskDetails.getProjectId() :
                    gcpTranslationProperties.getProjectId();

            final TaskWrapper taskWrapper = migrationService.getMigrationTask(project,
                    UUID.fromString(taskDetails.getWorkflowId()),
                    UUID.fromString(taskId));

            log.info("Getting task done, taskId {}, current status {}", taskWrapper.getId(), taskWrapper.getState());

            if (MigrationTask.State.SUCCEEDED.name().equalsIgnoreCase(taskWrapper.getState())) {
                log.info("Task {} has been done, details {}", taskId, taskDetails);
                TASKS_MAP.remove(taskId);
            }
        });

    }

    public void addTask(String taskId, TaskEnquiryDetails taskEnquiryDetails) {
        TASKS_MAP.put(taskId, taskEnquiryDetails);
    }


    @Getter
    @Setter
    @ToString
    @AllArgsConstructor
    public static final class TaskEnquiryDetails {
        private String taskId;
        private String projectId;
        private String workflowId;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            TaskEnquiryDetails that = (TaskEnquiryDetails) o;
            return taskId.equals(that.taskId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(taskId);
        }
    }

}
