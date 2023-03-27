package com.ttran.gcp;

import com.ttran.gcp.properties.BaseTranslationProperties;
import com.ttran.gcp.properties.GcpTranslationProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.junit.platform.commons.util.StringUtils;

@SuperBuilder
@Getter
@Setter
public class SqlTranslationRequest extends BaseTranslationProperties {
    private String projectId;
    private String location = "us";
    private String workflowName;
    private String taskName;
    private Object data;

    public static SqlTranslationRequest of(TranslationRequestPayload<?> requestPayload, GcpTranslationProperties properties) {
        return SqlTranslationRequest
                .builder()
                .projectId(properties.getProjectId())
                .location(properties.getLocation())
                .inPath(StringUtils.isBlank(requestPayload.getConfigInPath()) ? properties.getInPath() :
                        requestPayload.getConfigInPath())
                .srcFilePath(StringUtils.isBlank(requestPayload.getConfigSrcFilePath()) ?
                        properties.getSrcFilePath() : requestPayload.getConfigSrcFilePath())
                .outPath(StringUtils.isBlank(requestPayload.getConfigOutput()) ? properties.getOutPath() :
                        requestPayload.getConfigOutput())
                .destFilePath(StringUtils.isBlank(requestPayload.getConfigDestFilePath()) ?
                        properties.getDestFilePath() : requestPayload.getConfigDestFilePath())
                .workflowName(properties.getWorkflowName())
                .taskName(properties.getTaskName())
                .data(requestPayload.getData())
                .build();
    }
}
