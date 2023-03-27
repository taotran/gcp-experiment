package com.ttran.gcp;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ttran.gcp.properties.BaseTranslationProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class TranslationRequestPayload<T> {

    private String type;
    private T data;
    private BaseTranslationProperties config = null;


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
}
