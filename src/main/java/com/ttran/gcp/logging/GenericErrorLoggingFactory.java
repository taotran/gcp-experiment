package com.ttran.gcp.logging;

import com.ttran.gcp.enumerations.ErrorCode;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class GenericErrorLoggingFactory {

    private static final Map<Class<?>, ErrorCode> ERROR_CODE_MAPPINGS = new HashMap<>();

    static {
        ERROR_CODE_MAPPINGS.put(IllegalArgumentException.class, ErrorCode.MDM_ERROR_201);
        ERROR_CODE_MAPPINGS.put(UnsupportedOperationException.class, ErrorCode.MDM_ERROR_202);
        ERROR_CODE_MAPPINGS.put(IllegalStateException.class, ErrorCode.MDM_ERROR_203);
    }

    public static ErrorCode get(Class<?> tClass) {
        return ERROR_CODE_MAPPINGS.get(tClass);
    }
}
