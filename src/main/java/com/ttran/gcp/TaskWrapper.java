package com.ttran.gcp;

import com.google.cloud.bigquery.migration.v2.MigrationTask;
import com.google.cloud.bigquery.migration.v2.TranslationConfigDetails;
import com.google.protobuf.Timestamp;
import lombok.*;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class TaskWrapper implements Serializable {

    private String id;
    private String type;
    private String state;
    private String createTime;
    private String lastUpdateTime;
    private String taskDetails;

    public static TaskWrapper of(MigrationTask migrationTask) {
        return TaskWrapper.builder()
                .id(migrationTask.getId())
                .type(migrationTask.getType())
                .state(migrationTask.getState().toString())
                .createTime(migrationTask.getCreateTime().toString())
                .lastUpdateTime(migrationTask.getLastUpdateTime().toString())
                .taskDetails(migrationTask.getTaskDetailsCase().toString())
                .build();
    }
}
