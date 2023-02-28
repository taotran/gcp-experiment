package com.ttran.gcp.properties;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ConfigurationProperties("gcloud.bigquery.translation")
public class GcpTranslationProperties {
    private String projectId;
    private String location;
    private String inPath;
    private String outPath;
    private String workflowName;
    private String taskName;
}

