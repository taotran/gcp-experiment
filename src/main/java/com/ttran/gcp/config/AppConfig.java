package com.ttran.gcp.config;

import com.google.cloud.bigquery.migration.v2.MigrationServiceClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    public MigrationServiceClient migrationServiceClient() throws Exception {
        return MigrationServiceClient.create();
    }
}
