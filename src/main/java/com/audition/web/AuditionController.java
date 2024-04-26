package com.audition.web;

import com.audition.common.logging.AuditionLogger;
import com.audition.configuration.ResponseHeaderInjector;
import com.audition.integration.AuditionIntegrationClient;
import com.audition.service.AuditionService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuditionController {
    private static final Logger LOG = LoggerFactory.getLogger(AuditionController.class);

    @Autowired
    private transient AuditionLogger logger;

    @Autowired
    private transient AuditionService auditionService;

    @Autowired
    private transient ResponseHeaderInjector responseHeaderInjector;

    @RequestMapping(value = "/posts", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ResponseEntity getPosts(@RequestParam @Validated final MultiValueMap<String, String> queryParam) {
        logger.info(LOG, "AuditionController::getPosts(): Request Received. Processing Started");
        String id = StringUtils.isNotEmpty(queryParam.getFirst("id")) ? queryParam.getFirst("id") : null;
        String userId = StringUtils.isNotEmpty(queryParam.getFirst("userId")) ? queryParam.getFirst("userId") : null;
        return ResponseEntity.ok().headers(responseHeaderInjector.getTraceHeaders()).body(auditionService.getPosts(id, userId));
    }

    @RequestMapping(value = "/posts/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ResponseEntity getPostsById(@PathVariable("id") @Validated final String postId) {
        logger.info(LOG, "AuditionController::getPostsById(): Request Received. Processing Started");
        return ResponseEntity.ok().headers(responseHeaderInjector.getTraceHeaders()).body(auditionService.getPostById(postId));
    }

    @RequestMapping(value = "/posts/{postId}/comments", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ResponseEntity getComments(@PathVariable("postId") @Validated final String postId) {
        logger.info(LOG, "AuditionController::getComments(): Request Received. Processing Started");
        return ResponseEntity.ok().headers(responseHeaderInjector.getTraceHeaders()).body(auditionService.getComments(postId));
    }

    @RequestMapping(value = "/comments", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ResponseEntity  getCommentsByPostId(@RequestParam @Validated final MultiValueMap<String, String> queryParam) {
        logger.info(LOG, "AuditionController::getCommentsByPostId(): Request Received. Processing Started");
        String id = StringUtils.isNotEmpty(queryParam.getFirst("id")) ? queryParam.getFirst("id") : null;
        String postId = StringUtils.isNotEmpty(queryParam.getFirst("postId")) ? queryParam.getFirst("postId") : null;
        return ResponseEntity.ok().headers(responseHeaderInjector.getTraceHeaders()).body(auditionService.getCommentsByPostId(id, postId));
    }

}
