package com.audition.configuration;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.opentelemetry.api.trace.Span;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

@Component
@SuppressFBWarnings
public class ResponseHeaderInjector {

    public HttpHeaders getTraceHeaders() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("traceId", Span.current().getSpanContext().getTraceIdBytes().toString());
        httpHeaders.add("spanId", Span.current().getSpanContext().getSpanIdBytes().toString());
        return httpHeaders;
    }

}
