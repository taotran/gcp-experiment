package com.ttran.gcp;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class SqlTranslationResponse implements Serializable {
    private String workflowName;
    private String workflowDisplayName;
    private String taskId;
    private List<String> output;

    public static SqlTranslationResponse of(String workflowName, String workflowDisplayName,
                                            String taskId, List<String> output) {
        return new SqlTranslationResponse(workflowName, workflowDisplayName, taskId, output);
    }
}
