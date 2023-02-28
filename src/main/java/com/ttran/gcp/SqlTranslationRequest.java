package com.ttran.gcp;

import com.ttran.gcp.properties.GcpTranslationProperties;
import lombok.*;
import org.junit.platform.commons.util.StringUtils;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class SqlTranslationRequest {
    private String projectId;
    private String location = "us";
    private String inPath;
    private String outPath;
    private String workflowName;
    private String taskName;
    private Object data;

    public SqlTranslationRequest(Object data, String inPath, String outPath) {
        this.data = data;
        this.inPath = inPath;
        this.outPath = outPath;
    }

    public SqlTranslationRequest(Object data) {
        this.data = data;
    }

    public static SqlTranslationRequest of(TranslationRequestPayload<?> requestPayload, GcpTranslationProperties properties) {
        return SqlTranslationRequest
                .builder()
                .projectId(properties.getProjectId())
                .location(properties.getLocation())
                .inPath(StringUtils.isBlank(requestPayload.getConfigInPath()) ? properties.getInPath() :
                        requestPayload.getConfigInPath())
                .outPath(StringUtils.isBlank(requestPayload.getConfigOutput()) ? properties.getOutPath() :
                        requestPayload.getConfigOutput())
                .workflowName(properties.getWorkflowName())
                .taskName(properties.getTaskName())
                .data(requestPayload.getData())
                .build();
    }
}
