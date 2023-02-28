package com.ttran.gcp.service;

import com.ttran.gcp.SqlTranslationRequest;
import com.ttran.gcp.SqlTranslationResponse;
import com.ttran.gcp.TranslationDialectFactory;
import com.ttran.gcp.TranslationTaskType;

public interface SqlTranslationService {

    SqlTranslationResponse process(SqlTranslationRequest request) throws Exception;

    default TranslationDialectFactory.TranslationDialect translationDialect() {
        return TranslationDialectFactory.create(taskType());
    }

    TranslationTaskType taskType();

    default int numberOfRetries() {
        return 4;
    }

    default boolean tryGetResult(){
        return true;
    }

    default long resultRetrieveInterval() {
        return 5000L;
    }
}
