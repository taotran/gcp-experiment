package com.ttran.gcp.config.filter;

import com.google.cloud.logging.LogEntry;
import com.google.cloud.logging.Payload;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class RequestInterceptor extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final Map<String, Object> testPayload = new HashMap<>();
        testPayload.put("testProp", "testing");
        Payload payload = Payload.JsonPayload.of(testPayload);
        LogEntry logEntry = LogEntry.newBuilder(payload).build();
        this.doFilter(request, response, filterChain);
        log.info("after filter");
    }
}
