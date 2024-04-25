package com.audition.integration;

import com.audition.common.exception.SystemException;
import com.audition.common.logging.AuditionLogger;
import com.audition.configuration.LoggingInterceptor;
import com.audition.model.AuditionComments;
import com.audition.model.AuditionPost;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import edu.umd.cs.findbugs.annotations.SuppressWarnings;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.lang.reflect.ParameterizedType;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@SuppressFBWarnings
public class AuditionIntegrationClient {

    private static final String AUDITION_CLIENT = "auditionIntegrationClient";

    @Value("${management.endpoints.audition.posts}")
    private transient String postsEndpoint;

    @Value("${management.endpoints.audition.comments}")
    private transient String commentsEndpoint;

    @Value("${management.endpoints.audition.postComments}")
    private transient String postCommentsEndpoint;

    private static final Logger LOG = LoggerFactory.getLogger(AuditionIntegrationClient.class);

    @Autowired
    private transient AuditionLogger logger;

    @Autowired
    private transient RestTemplate restTemplate;

    @Retry(name = AUDITION_CLIENT)
    @RateLimiter(name = AUDITION_CLIENT)
    public List<AuditionPost> getPosts(String id, String userId) {
        try {
            logger.info(LOG, "getPosts(): Request Received");

            // Build uri with query params if available
            UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromUriString(postsEndpoint);
            // Update query params if available
            if (StringUtils.isNotEmpty(id)) {
                uriComponentsBuilder.queryParam("id", id);
            }
            if (StringUtils.isNotEmpty(userId)) {
                uriComponentsBuilder.queryParam("userId", userId);
            }

            logger.info(LOG, "getPosts(): URL to fetch the resource:" + uriComponentsBuilder.toUriString());

            HttpHeaders headers = new HttpHeaders();
            headers.set("accept", "application/json");
            HttpEntity requestEntity = new HttpEntity<>(null, headers);

            final ResponseEntity<List<AuditionPost>> auditionPostList =
                    restTemplate.exchange(uriComponentsBuilder.toUriString(), HttpMethod.GET,
                            requestEntity, new ParameterizedTypeReference<>(){});
            if (auditionPostList.getBody().isEmpty()) {
                throw new HttpClientErrorException(HttpStatus.NOT_FOUND, "Resource Not Found for the given search criteria");
            }
            logger.info(LOG, "getPosts(): Response successfully received");

            return auditionPostList.getBody();

        } catch (final HttpClientErrorException httpClientErrorException) {
            if (httpClientErrorException.getStatusCode() == HttpStatus.NOT_FOUND) {
                logger.error(LOG, "getPosts(): " + HttpStatus.NOT_FOUND + " " + httpClientErrorException.getMessage());
                throw new SystemException("Cannot find any posts. Resource Not Found", 404);
            } else {
                logger.error(LOG, "getPosts() downstream error stack: " + httpClientErrorException.getStackTrace());
                throw new SystemException("getPosts() exception:" + httpClientErrorException.getMessage() );
            }
        }
    }

    @Retry(name = AUDITION_CLIENT)
    @RateLimiter(name = AUDITION_CLIENT)
    public AuditionPost getPostById(final String id) {
        try {
            logger.info(LOG, "getPostById(): Request Received");
            logger.info(LOG, "getPostById(): URL to fetch the resource:" + postsEndpoint + "/" + id);
            final AuditionPost auditionPost = restTemplate.getForObject(postsEndpoint + "/" + id,
                    AuditionPost.class);
            logger.info(LOG, "getPostById(): Response successfully received");
            return auditionPost;
        } catch (final HttpClientErrorException httpClientErrorException) {
            if (httpClientErrorException.getStatusCode() == HttpStatus.NOT_FOUND) {
                logger.error(LOG, "getPostById(): " + HttpStatus.NOT_FOUND + " " + httpClientErrorException.getMessage());
                throw new SystemException("Cannot find a Post with id " + id, "Resource Not Found",
                    404);
            } else {
                // TODO Find a better way to handle the exception so that the original error message is not lost. Feel free to change this function.
                logger.error(LOG, "getPostById() downstream error stack: " + httpClientErrorException.getStackTrace());
                throw new SystemException("getPostById() exception:" + httpClientErrorException.getMessage() );
            }
        }
    }

    @Retry(name = AUDITION_CLIENT)
    @RateLimiter(name = AUDITION_CLIENT)
    public List<AuditionComments> getComments(String postId) {
        try {
            logger.info(LOG, "getComments(): Request Received");
            Map<String, String> params = new HashMap<>();
            // Update path params if available
            if (StringUtils.isNotEmpty(postId)) {
                params.put("postId", postId);
            }
            // Build uri with query params if available
            URI uri = UriComponentsBuilder.fromUriString(postCommentsEndpoint).buildAndExpand(params).toUri();
            logger.info(LOG, "getComments(): URL to fetch the resource:" + uri.toString());

            HttpHeaders headers = new HttpHeaders();
            headers.set("accept", "application/json");
            HttpEntity requestEntity = new HttpEntity<>(null, headers);

            final ResponseEntity<List<AuditionComments>> auditionCommentsList =
                    restTemplate.exchange(uri, HttpMethod.GET, requestEntity, new ParameterizedTypeReference<>(){});
            if (auditionCommentsList.getBody() !=null && auditionCommentsList.getBody().isEmpty()) {
                throw new HttpClientErrorException(HttpStatus.NOT_FOUND, "Resource Not Found for the given search criteria");
            }
            logger.info(LOG, "getComments(): Response successfully received");

            return auditionCommentsList.getBody();

        } catch (final HttpClientErrorException httpClientErrorException) {
            if (httpClientErrorException.getStatusCode() == HttpStatus.NOT_FOUND) {
                logger.error(LOG, "getComments(): " + HttpStatus.NOT_FOUND + " " + httpClientErrorException.getMessage());
                throw new SystemException("Cannot find any comments. Resource Not Found", 404);
            } else {
                logger.error(LOG, "getComments() downstream error stack: " + httpClientErrorException.getStackTrace());
                throw new SystemException("getComments() exception:" + httpClientErrorException.getMessage() );
            }
        }
    }

    @Retry(name = AUDITION_CLIENT)
    @RateLimiter(name = AUDITION_CLIENT)
    public List<AuditionComments> getCommentsByPostId(String id, String postId) {
        try {
            logger.info(LOG, "getCommentsByPostId(): Request Received");
            // Build uri with query params if available
            UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromUriString(commentsEndpoint);
            // Update query params if available
            if (StringUtils.isNotEmpty(postId)) {
                uriComponentsBuilder.queryParam("postId", postId);
            }
            if (StringUtils.isNotEmpty(id)) {
                uriComponentsBuilder.queryParam("id", id);
            }

            logger.info(LOG, "getCommentsByPostId(): URL to fetch the resource:" + uriComponentsBuilder.toUriString());

            HttpHeaders headers = new HttpHeaders();
            headers.set("accept", "application/json");
            HttpEntity requestEntity = new HttpEntity<>(null, headers);

            final ResponseEntity<List<AuditionComments>> auditionCommentsList =
                    restTemplate.exchange(uriComponentsBuilder.toUriString(), HttpMethod.GET,
                            requestEntity, new ParameterizedTypeReference<>(){});
            if (auditionCommentsList.getBody() != null && auditionCommentsList.getBody().isEmpty()) {
                throw new HttpClientErrorException(HttpStatus.NOT_FOUND, "Resource Not Found for the given search criteria");
            }
            logger.info(LOG, "getCommentsByPostId(): Response successfully received");

            return auditionCommentsList.getBody();

        } catch (final HttpClientErrorException httpClientErrorException) {
            if (httpClientErrorException.getStatusCode() == HttpStatus.NOT_FOUND) {
                logger.error(LOG, "getCommentsByPostId(): " + HttpStatus.NOT_FOUND + " " + httpClientErrorException.getMessage());
                throw new SystemException("Cannot find any comments. Resource Not Found", 404);
            } else {
                // TODO Find a better way to handle the exception so that the original error message is not lost. Feel free to change this function.
                logger.error(LOG, "getCommentsByPostId() downstream error stack: " + httpClientErrorException.getStackTrace());
                throw new SystemException("getCommentsByPostId() exception:" + httpClientErrorException.getMessage() );
            }
        }
    }
}
