package com.ttran.gcp;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

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

    public String getConfigOutput() {
        return config == null ? null : config.getOutPath();
    }

    @NoArgsConstructor
    @Getter
    @Setter
    public static class GcpConfigInfo {
        private String inPath;
        private String outPath;
    }
}
