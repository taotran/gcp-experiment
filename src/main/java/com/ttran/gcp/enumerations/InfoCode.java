package com.ttran.gcp.enumerations;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public enum InfoCode {

    MDM_INFO_101("Remigrate", "REMIGRATE_INFO");

    private static final Map<String, InfoCode> INFO_CODE_MAP = new HashMap<>();

    static {
        Arrays.stream(InfoCode.values()).forEach(ic -> INFO_CODE_MAP.put(ic.readableCode, ic));
    }


    private final String message;
    private final String readableCode;

    InfoCode(String message, String readableCode) {
        this.message = message;
        this.readableCode = readableCode;
    }

    public static InfoCode of(String code) {
        return INFO_CODE_MAP.get(code);
    }

    public String getMessage() {
        return message;
    }

    public String getReadableCode() {
        return readableCode;
    }
}
