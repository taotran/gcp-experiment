package com.ttran.gcp.logging;

import com.google.cloud.logging.LogEntry;
import com.google.cloud.logging.LoggingEnhancer;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Enhancer implements LoggingEnhancer {

    @Override
    public void enhanceLogEntry(LogEntry.Builder logEntry) {
        // add additional labels
        log.info("inside enhanceLogEntry");
        logEntry.addLabel("levelValue", "6868");
    }
}