package com.audition.service;

import com.audition.integration.AuditionIntegrationClient;
import com.audition.model.AuditionComments;
import com.audition.model.AuditionPost;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuditionService {

    @Autowired
    private transient AuditionIntegrationClient auditionIntegrationClient;


    public List<AuditionPost> getPosts(String id, String userId) {
        return auditionIntegrationClient.getPosts(id, userId);
    }

    public AuditionPost getPostById(final String postId) {

        return auditionIntegrationClient.getPostById(postId);
    }

    public List<AuditionComments> getComments(String postId) {
        return auditionIntegrationClient.getComments(postId);
    }

    public List<AuditionComments> getCommentsByPostId(String id, String postId) {
        return auditionIntegrationClient.getCommentsByPostId(id, postId);
    }

}
