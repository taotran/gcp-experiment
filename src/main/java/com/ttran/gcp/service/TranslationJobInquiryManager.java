package com.ttran.gcp.service;


import lombok.Getter;
import lombok.Setter;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class TranslationJobInquiryManager {

    private static final Map<String, TaskEnquiryDetails> tasks = new ConcurrentHashMap<>();

    @Scheduled
    public void inquiryTranslationStatus() {

    }


    @Getter
    @Setter
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
