package com.ttran.gcp.config;

import com.ttran.gcp.TranslationTaskType;
import com.ttran.gcp.service.SqlTranslationService;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@AllArgsConstructor
@Configuration
public class SqlTranslationConfig {

    private final List<SqlTranslationService> translationServices;

    @Bean
    public Map<TranslationTaskType, SqlTranslationService> translationServiceMap() {
        return translationServices.stream().collect(Collectors.toMap(SqlTranslationService::taskType, s -> s));
    }
}
