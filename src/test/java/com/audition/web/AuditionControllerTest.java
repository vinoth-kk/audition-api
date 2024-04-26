package com.audition.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.audition.configuration.ResponseHeaderInjector;
import com.audition.integration.AuditionIntegrationClient;
import com.audition.model.AuditionComments;
import com.audition.model.AuditionPost;
import com.audition.service.AuditionService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ResourceUtils;

@ExtendWith(MockitoExtension.class)
@SuppressFBWarnings
public class AuditionControllerTest {

    @InjectMocks
    private transient AuditionController auditionController;
    @Mock
    private transient AuditionService auditionService;
    @Mock
    private transient ResponseHeaderInjector responseHeaderInjector;

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
        when(auditionService.getPosts(anyString(), anyString())).thenReturn(auditionPostList);
        MultiValueMap<String, String> queryParam = new LinkedMultiValueMap<>();
        queryParam.add("id", "1");
        queryParam.add("userId", "1");
        ResponseEntity responseEntity = auditionController.getPosts(queryParam);
        List<AuditionPost> auditionPostList = (List<AuditionPost>) responseEntity.getBody();
        assertEquals(auditionPostList.size(), 10);
    }

    @Test
    public void getPostsById() {
        when(auditionService.getPostById(anyString())).thenReturn(auditionPostList.get(0));
        ResponseEntity responseEntity = auditionController.getPostsById("1");
        AuditionPost auditionPost = (AuditionPost) responseEntity.getBody();
        assertEquals(auditionPost.getUserId(), 1);
        assertEquals(auditionPost.getId(), 1);
    }

    @Test
    public void getComments() {
        when(auditionService.getComments(anyString())).thenReturn(auditionCommentsList);
        ResponseEntity responseEntity = auditionController.getComments("1");
        List<AuditionComments> auditionCommentsList = (List<AuditionComments>) responseEntity.getBody();
        assertEquals(auditionCommentsList.size(), 5);
    }

    @Test
    public void getCommentsByPostId() {
        when(auditionService.getCommentsByPostId(anyString(), anyString())).thenReturn(auditionCommentsList);
        MultiValueMap<String, String> queryParam = new LinkedMultiValueMap<>();
        queryParam.add("id", "1");
        queryParam.add("postId", "1");
        ResponseEntity responseEntity = auditionController.getCommentsByPostId(queryParam);
        List<AuditionComments> auditionCommentsList = (List<AuditionComments>) responseEntity.getBody();
        assertEquals(auditionCommentsList.size(), 5);
    }

}
