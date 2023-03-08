package com.ttran.gcp;

import lombok.*;

import java.io.Serializable;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class SqlTranslationResponse implements Serializable {
    private String workflowName;
    private String workflowDisplayName;
    private String taskId;
    private String latestStatus;
    private List<String> output;
}
