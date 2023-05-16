package com.ttran.gcp.logging;

import com.google.cloud.logging.*;
import com.ttran.gcp.enumerations.ErrorCode;
import com.ttran.gcp.enumerations.InfoCode;
import com.ttran.gcp.exception.BaseMdmException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.logging.LogLevel;
import org.springframework.stereotype.Component;

import java.util.*;

@Slf4j
@Component
public class GcpLoggingManager {

    private static final String DEFAULT_LOG_NAME = "application.log";
    private static Map<String, String> DEFAULT_LABELS = new LinkedHashMap<>();

    static {
        DEFAULT_LABELS.put("app_name", "mdm-api");
        DEFAULT_LABELS.put("project_name", "axp-lumi");
        DEFAULT_LABELS.put("car_id", "600001868");

        DEFAULT_LABELS = Collections.unmodifiableMap(DEFAULT_LABELS);
    }

    public void writeMdmGenericError(Exception ex) throws Exception {
        final ErrorCode associatedErrorCode = GenericErrorLoggingFactory.get(ex.getClass());

        if (associatedErrorCode == null) {
            log.warn("Unable to find the associated errorCode with {}", ex.getClass().getSimpleName());
            return;
        }

        final Map<String, String> labels = new HashMap<>(DEFAULT_LABELS);
        labels.put("levelName", LogLevel.ERROR.name());
        labels.put("levelValue", associatedErrorCode.name());
        labels.put("loggerName", "");
        this.writeJsonError(associatedErrorCode.getMessage(), labels);
    }

    public void writeMdmError(BaseMdmException mdmException) throws Exception {
//        if (mdmException.getErrorCode() != null) {
        final Map<String, String> labels = new HashMap<>(DEFAULT_LABELS);
        labels.put("levelName", mdmException.getLevelName().name());
        labels.put("levelValue", mdmException.getErrorCode().name());
        labels.put("loggerName", mdmException.getLoggerName());
        this.writeJsonError(mdmException.getMessage(), labels);
//        } else {
//            final Map<String, String> labels = new HashMap<>(DEFAULT_LABELS);
//            labels.put("levelName", mdmException.getLevelName().name());
//            labels.put("levelValue", mdmException.getErrorCode().name());
//            labels.put("loggerName", mdmException.getLoggerName());
//            this.writeJsonError(mdmException.getMessage(), labels);
//        }
    }

    public void writeJsonError(String message) throws Exception {
        this.writeJsonError(message, DEFAULT_LABELS);
    }

    public void writeJsonError(String message, Map<String, String> labels) throws Exception {
        this.writeJsonLog(message, Severity.ERROR, labels);
    }

    public void writeJsonInfo(InfoCode infoCode, String loggerName) throws Exception {
        final Map<String, String> labels = new HashMap<>(DEFAULT_LABELS);
        labels.put("levelName", "INFO");
        labels.put("levelValue", infoCode.name());
        labels.put("loggerName", loggerName);
        this.writeJsonLog(infoCode.getMessage(), Severity.INFO, labels);
    }

    public void writeJsonInfo(String infoCode, String message, Map<String, String> labels) throws Exception {
        this.writeJsonLog(message, Severity.INFO, labels);
    }

    public void writeJsonLog(String message, Severity severity, Map<String, String> labels) throws Exception {
        this.writeJsonLog(this.buildDefaultJsonPayload(message), severity, labels);
    }

    public void writeJsonLog(Payload.JsonPayload jsonPayload, Severity severity, Map<String, String> labels) throws Exception {
        final LogEntry logEntry =
                LogEntry.newBuilder(jsonPayload)
                        .setSeverity(severity)
                        .setLabels(labels)
                        .setLogName(DEFAULT_LOG_NAME)
                        .build();
        this.writeLog(logEntry);
    }

    public void writeLog(Collection<LogEntry> entries) throws Exception {
        try (Logging logging = LoggingOptions.getDefaultInstance().getService()) {
            logging.write(entries);
            logging.flush();
        }
    }

    public void writeLog(LogEntry entry) throws Exception {
        this.writeLog(Collections.singleton(entry));
    }

    private Payload.JsonPayload buildDefaultJsonPayload(String message) {
        final Map<String, Object> structs = new LinkedHashMap<>();
        structs.put("message", message);
        return Payload.JsonPayload.of(structs);
    }
}
