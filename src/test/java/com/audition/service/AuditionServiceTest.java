package com.audition.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.audition.integration.AuditionIntegrationClient;
import com.audition.model.AuditionComments;
import com.audition.model.AuditionPost;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.util.ResourceUtils;

@ExtendWith(MockitoExtension.class)
public class AuditionServiceTest {

    @InjectMocks
    private transient AuditionService auditionService;
    @Mock
    private transient AuditionIntegrationClient client;

    transient List<AuditionPost> auditionPostList = new ArrayList<>();

    transient List<AuditionComments> auditionCommentsList = new ArrayList<>();


    @BeforeEach
    public void setUp() throws IOException {
        auditionPostList = new ObjectMapper().readValue(
            ResourceUtils.getFile("classpath:posts_response.json"), new TypeReference<>() {
            });
        auditionCommentsList = new ObjectMapper()
            .readValue(ResourceUtils.getFile("classpath:comments_response.json"), new TypeReference<>() {
            });
    }

    @Test
    public void getPosts() {
        when(client.getPosts(anyString(), anyString())).thenReturn(auditionPostList);
        List auditionList = auditionService.getPosts("1", "1");
        assertEquals(auditionList.size(), 10);
    }

    @Test
    public void getPostById() {
        when(client.getPostById(anyString())).thenReturn(auditionPostList.get(0));
        AuditionPost auditionPost = auditionService.getPostById("1");
        assertEquals(auditionPost.getId(), 1);
        assertEquals(auditionPost.getUserId(), 1);
    }

    @Test
    public void getComments() {
        when(client.getCommentsByPostId(anyString(), anyString())).thenReturn(auditionCommentsList);
        List commentsList = auditionService.getCommentsByPostId("1", "1");
        assertEquals(commentsList.size(), 5);
    }

    @Test
    public void getCommentsByPostId() {
        when(client.getComments(anyString())).thenReturn(auditionCommentsList);
        List commentsList = auditionService.getComments("1");
        assertEquals(commentsList.size(), 5);
    }

}
