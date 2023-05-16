package com.ttran.gcp.enumerations;

public enum ErrorCode {
    MDM_ERROR_100(""), // this is default error -> will put original exception message
    MDM_ERROR_101("Data not found for %s"),
    MDM_ERROR_102("Unable to update schema"),
    MDM_ERROR_103("Unable to update event"),
    MDM_ERROR_104("Provided version pre-dates Lumi's available data"),
    MDM_ERROR_105("Unable to update schema"),

    /* Generic error */
    MDM_ERROR_201("Illegal argument exception"),
    MDM_ERROR_202("Un-support operation exception"),
    MDM_ERROR_203("Illegal state exception"),
    ;

    private final String message;

    ErrorCode(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
