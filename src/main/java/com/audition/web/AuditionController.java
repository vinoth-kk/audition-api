package com.audition.web;

import com.audition.model.AuditionComments;
import com.audition.model.AuditionPost;
import com.audition.service.AuditionService;
import jakarta.websocket.server.PathParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class AuditionController {

    @Autowired
    private transient AuditionService auditionService;

    @RequestMapping(value = "/posts", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody List<AuditionPost> getPosts(@RequestParam @Validated final MultiValueMap<String, String> queryParam) {

        String id = StringUtils.isNotEmpty(queryParam.getFirst("id")) ? queryParam.getFirst("id") : null;
        String userId = StringUtils.isNotEmpty(queryParam.getFirst("userId")) ? queryParam.getFirst("userId") : null;
        return auditionService.getPosts(id, userId);
    }

    @RequestMapping(value = "/posts/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody AuditionPost getPosts(@PathVariable("id") @Validated final String postId) {
        return auditionService.getPostById(postId);
    }

    @RequestMapping(value = "/posts/{postId}/comments", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody List<AuditionComments> getComments(@PathVariable("postId") @Validated final String postId) {

        return auditionService.getComments(postId);
    }

    @RequestMapping(value = "/comments", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody List<AuditionComments>  getCommentsByPostId(@RequestParam @Validated final MultiValueMap<String, String> queryParam) {
        String id = StringUtils.isNotEmpty(queryParam.getFirst("id")) ? queryParam.getFirst("id") : null;
        String postId = StringUtils.isNotEmpty(queryParam.getFirst("postId")) ? queryParam.getFirst("postId") : null;
        return auditionService.getCommentsByPostId(id, postId);
    }

}
