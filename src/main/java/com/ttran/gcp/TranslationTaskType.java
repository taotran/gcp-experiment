package com.ttran.gcp;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.junit.platform.commons.util.StringUtils;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum TranslationTaskType {
    HIVE_TO_BQ("Translation_HiveQL2BQ"),
    MYSQL_TO_BQ("Translation_MySQL2BQ"),
    SPARK_TO_BQ("Translation_SparkSQL2BQ");

    private final String gcpType;

    public static TranslationTaskType of(String gcpType) {
        return Arrays
                .stream(values())
                .filter(t-> StringUtils.isNotBlank(gcpType) && gcpType.equalsIgnoreCase(t.getGcpType()))
                .findFirst()
                .orElse(null);
    }
}
