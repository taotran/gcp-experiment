package com.ttran.gcp;

import com.google.cloud.bigquery.migration.v2.BigQueryDialect;
import com.google.cloud.bigquery.migration.v2.Dialect;
import com.google.cloud.bigquery.migration.v2.HiveQLDialect;
import com.google.cloud.bigquery.migration.v2.MySQLDialect;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;


@NoArgsConstructor
public class TranslationDialectFactory {

    private static final Map<TranslationTaskType, Supplier<TranslationDialect>> DIALECT_SUPPLIERS = new HashMap<>();

    static {
        DIALECT_SUPPLIERS.put(TranslationTaskType.HIVE_TO_BQ, TranslationDialectFactory::hiveToBQ);
        DIALECT_SUPPLIERS.put(TranslationTaskType.MYSQL_TO_BQ, TranslationDialectFactory:: mysqlToBQ);
    }

    public static TranslationDialect create(TranslationTaskType taskType) {
        return DIALECT_SUPPLIERS.get(taskType).get();
    }

    private static TranslationDialect hiveToBQ() {
        return new TranslationDialect(
                Dialect.newBuilder().setHiveqlDialect(HiveQLDialect.newBuilder().build()).build(),
                Dialect.newBuilder().setBigqueryDialect(BigQueryDialect.newBuilder().build()).build()
        );
    }

    private static TranslationDialect mysqlToBQ() {
        return new TranslationDialect(
                Dialect.newBuilder().setMysqlDialect(MySQLDialect.newBuilder().build()).build(),
                Dialect.newBuilder().setBigqueryDialect(BigQueryDialect.newBuilder().build()).build()
        );
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class TranslationDialect {
        private Dialect source;
        private Dialect target;
    }
}
