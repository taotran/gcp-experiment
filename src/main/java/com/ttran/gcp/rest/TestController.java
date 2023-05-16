package com.ttran.gcp.rest;

import com.ttran.gcp.annotations.GCloudLog;
import com.ttran.gcp.exception.DataNotFoundException;
import com.ttran.gcp.service.Publisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/test")
public class TestController {

    @Autowired
    private Publisher publisher;

    @PostMapping("/publish")
    public ResponseEntity<?> publish(@RequestParam String message) {
        publisher.write(message);

        return ResponseEntity.ok("");
    }

    @PostMapping("/writeLogs")
    public ResponseEntity<?> testWriteLog(@RequestParam String text) {
        log.info("Param: {} - randomUUID: {}", text, UUID.randomUUID());
        if ("error".equalsIgnoreCase(text)) {
            throw new IllegalArgumentException("IllegalArgumentException thrown");
        }
        return ResponseEntity.ok("{\"status\": \"OK\"}");
    }

    @GCloudLog(code = "REMIGRATE_INFO", message = "nothiing")
    @PostMapping("/writeErrorLog")
    public ResponseEntity<?> testWriteErrorLog(@RequestParam String text) {
        log.info("Param: {} - randomUUID: {}", text, UUID.randomUUID());
        if ("error".equalsIgnoreCase(text)) {
            throw new DataNotFoundException("appFlow");
        }
        return ResponseEntity.ok("{\"status\": \"OK\"}");
    }
}
