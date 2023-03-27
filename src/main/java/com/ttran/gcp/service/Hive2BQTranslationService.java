package com.ttran.gcp.service;

import com.ttran.gcp.TranslationTaskType;
import org.springframework.stereotype.Component;

@Component
public class Hive2BQTranslationService extends BaseSqlTranslationService {

    public Hive2BQTranslationService(GcpBucketService bucketService, MigrationService migrationService, TranslationJobInquiryManager jobInquiryManager) {
        super(bucketService, migrationService, jobInquiryManager);
    }

    @Override
    public TranslationTaskType taskType() {
        return TranslationTaskType.HIVE_TO_BQ;
    }
}
