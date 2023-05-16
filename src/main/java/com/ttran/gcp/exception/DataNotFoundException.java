package com.ttran.gcp.exception;

import com.ttran.gcp.enumerations.ErrorCode;

public class DataNotFoundException extends BaseMdmException {

    public DataNotFoundException(String arg) {
        super(ErrorCode.MDM_ERROR_101, arg);

    }

    public DataNotFoundException(String arg, String loggerName) {
        super(ErrorCode.MDM_ERROR_101, arg, loggerName);
    }
}
