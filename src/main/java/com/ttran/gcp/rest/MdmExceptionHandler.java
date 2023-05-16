package com.ttran.gcp.rest;

import com.google.cloud.logging.Logging;
import com.google.cloud.logging.LoggingOptions;
import com.ttran.gcp.exception.BaseMdmException;
import com.ttran.gcp.logging.GcpLoggingManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@ControllerAdvice
public class MdmExceptionHandler extends ResponseEntityExceptionHandler {

    private final Logging logging = LoggingOptions.getDefaultInstance().getService();

//    @ExceptionHandler({Exception.class})
//    public ResponseEntity<Object> handleAccessDeniedException(
//            Exception ex, WebRequest request) {
//
//        LogEntry entry =
//                LogEntry.newBuilder(Payload.StringPayload.of("This is test log"))
//                        .setLogName("TestLogName")
//                        .setSeverity(Severity.ERROR)
//                        .build();
//
//        logging.write(Collections.singletonList(entry));
//
//        log.info("Into the error logging");
//        return new ResponseEntity<Object>(
//                "Access denied message here", new HttpHeaders(), HttpStatus.FORBIDDEN);
//    }

    @Autowired
    private GcpLoggingManager loggingManager;

    @ExceptionHandler({Exception.class})
    public ResponseEntity<Object> handleAccessDeniedException(
            Exception ex, WebRequest request) throws Exception {

        if (ex instanceof BaseMdmException) {
            final BaseMdmException mdmException = (BaseMdmException) ex;
            loggingManager.writeMdmError((BaseMdmException) ex);
            return new ResponseEntity<>(String.format("{\"errorCode\": %s, \"message\": %s}",
                    mdmException.getErrorCode().name(), mdmException.getMessage()),
                    new HttpHeaders(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        } else {

        }

        return new ResponseEntity<>(
                "Access denied message here", new HttpHeaders(), HttpStatus.FORBIDDEN);
    }

}
