package com.ttran.gcp.rest;

import com.ttran.gcp.service.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
}
