package com.ttran.gcp.exception;

import com.ttran.gcp.enumerations.ErrorCode;
import org.springframework.boot.logging.LogLevel;

public class BaseMdmException extends RuntimeException {

    private final ErrorCode errorCode;

    private final LogLevel levelName;

    private final String loggerName;

    public BaseMdmException(ErrorCode errorCode, String message) {
        super(message);
        this.levelName = LogLevel.ERROR;
        this.errorCode = errorCode;
        this.loggerName = this.getStackTrace()[0].toString();
    }


    public BaseMdmException(ErrorCode errorCode, String arg, String loggerName) {
        this(LogLevel.ERROR, ErrorCode.MDM_ERROR_100, String.format(errorCode.getMessage(), arg), loggerName);
    }

    public BaseMdmException(LogLevel levelName, ErrorCode errorCode, String message, String loggerName) {
        super(message);
        this.errorCode = errorCode;
        this.levelName = levelName;
        this.loggerName = this.getStackTrace()[0].toString();
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public LogLevel getLevelName() {
        return levelName;
    }

    public String getLoggerName() {
        return loggerName;
    }
}
