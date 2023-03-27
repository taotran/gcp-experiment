package com.ttran.gcp.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;


@Getter
@Setter
@ConfigurationProperties("gcloud.bigquery.translation")
public class GcpTranslationProperties extends BaseTranslationProperties {
    private String projectId;
    private String location;
    private String inPath;
    private String workflowName;
    private String taskName;
}

