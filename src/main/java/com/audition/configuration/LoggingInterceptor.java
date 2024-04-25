package com.audition.configuration;

import com.audition.common.logging.AuditionLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.Charset;

@Component
public class LoggingInterceptor implements ClientHttpRequestInterceptor {

    private static final Logger LOG = LoggerFactory.getLogger(LoggingInterceptor.class);

    @Autowired
    private transient AuditionLogger logger;

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {

        logRequest(request, body);
        ClientHttpResponse response = execution.execute(request, body);
        logResponse(response);

        return response;
    }

    private void logRequest(HttpRequest request, byte[] body) throws IOException {
        logger.debug(LOG, "===log request start===");
        logger.debug(LOG, "URI: " + request.getURI());
        logger.debug(LOG, "Method: " + request.getMethod());
        logger.debug(LOG, "Headers: " + request.getHeaders());
        logger.debug(LOG, "Request body: " + new String(body, "UTF-8"));
        logger.debug(LOG, "===log request end===");

    }

    private void logResponse(ClientHttpResponse response) throws IOException {

        logger.debug(LOG, "===log response start===");
        logger.debug(LOG, "Status code: " + response.getStatusCode());
        logger.debug(LOG, "Status text: " + response.getStatusText());
        logger.debug(LOG, "Headers: " + response.getHeaders());
        logger.debug(LOG, "Response body: " +  StreamUtils.copyToString(response.getBody(), Charset.defaultCharset()));
        logger.debug(LOG, "===log response end===");
    }
}
