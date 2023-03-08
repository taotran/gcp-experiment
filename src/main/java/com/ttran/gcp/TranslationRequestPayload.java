package com.ttran.gcp;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class TranslationRequestPayload<T> {

    private String type;
    private T data;
    private GcpConfigInfo config = null;

    public String getConfigInPath() {
        return config == null ? null : config.getInPath();
    }

    public String getConfigSrcFilePath() {
        return config == null || StringUtils.isBlank(config.getSrcFilePath()) ? null : config.getSrcFilePath();
    }

    public String getConfigDestFilePath() {
        return config == null || StringUtils.isBlank(config.getDestFilePath()) ? null : config.getDestFilePath();
    }

    public String getConfigOutput() {
        return config == null ? null : config.getOutPath();
    }

    @NoArgsConstructor
    @Getter
    @Setter
    public static class GcpConfigInfo {
        private String inPath;
        private String srcFilePath;
        private String outPath;
        private String destFilePath;
    }
}
