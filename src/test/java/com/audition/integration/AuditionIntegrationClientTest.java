package com.audition.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import com.audition.common.exception.SystemException;
import com.audition.common.logging.AuditionLogger;
import com.audition.model.AuditionComments;
import com.audition.model.AuditionPost;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.ResourceUtils;
import org.springframework.web.client.RestTemplate;

@ExtendWith(MockitoExtension.class)
public class AuditionIntegrationClientTest {

    @InjectMocks
    private transient AuditionIntegrationClient client;

    @Mock
    private transient AuditionLogger logger;

    @Mock
    private transient RestTemplate restTemplate;

    transient List<AuditionPost> auditionPostList = new ArrayList<>();

    transient List<AuditionComments> auditionCommentsList = new ArrayList<>();

    @BeforeEach
    public void setUp() throws IOException {
        ReflectionTestUtils.setField(client, "postsEndpoint", "https://jsonplaceholder.typicode.com/posts");
        ReflectionTestUtils.setField(client, "commentsEndpoint", "https://jsonplaceholder.typicode.com/comments");
        ReflectionTestUtils.setField(client, "postCommentsEndpoint", "https://jsonplaceholder.typicode.com/posts/{postId}/comments");

        auditionPostList = new ObjectMapper().readValue(ResourceUtils.getFile("classpath:posts_response.json"), new TypeReference<>(){});
        auditionCommentsList = new ObjectMapper().readValue(ResourceUtils.getFile("classpath:comments_response.json"), new TypeReference<>(){});

    }

    @Test
    public void testPosts_positive_flow() {
        when(restTemplate.exchange(anyString(), Mockito.any(HttpMethod.class), Mockito.any(HttpEntity.class), Mockito.any(
            ParameterizedTypeReference.class))).thenReturn(new ResponseEntity(auditionPostList, HttpStatus.OK));
        List<AuditionPost> auditionPostList = client.getPosts(null, "1");
        assertEquals(auditionPostList.size(), 10);
    }

    @Test
    public void testPosts_negative_flow() {
        when(restTemplate.exchange(anyString(), Mockito.any(HttpMethod.class), Mockito.any(HttpEntity.class), Mockito.any(
            ParameterizedTypeReference.class))).thenReturn(new ResponseEntity(new ArrayList<>(), HttpStatus.OK));
        SystemException systemException = Assertions.assertThrows(SystemException.class, () -> client.getPosts("345699", "9876"));
        assertEquals(systemException.getMessage(), "Cannot find any posts. Resource Not Found");
    }

    @Test
    public void testPosts_negative_flow_generic_exception() {

        doThrow(new RuntimeException("run time exception thrown unfortunately"))
            .when(restTemplate).exchange(anyString(), Mockito.any(HttpMethod.class), Mockito.any(HttpEntity.class), Mockito.any(
            ParameterizedTypeReference.class));
        RuntimeException runtimeException = Assertions.assertThrows(RuntimeException.class, () -> client.getPosts("345644", "5678"));
        assertEquals(runtimeException.getMessage(), "run time exception thrown unfortunately");
    }

/*
    @Test
    public void testPostsById_positive_flow() {
        doReturn(new ResponseEntity(AuditionPost.builder().build(), HttpStatus.OK)).when(restTemplate).exchange(Mockito.eq("https://jsonplaceholder.typicode.com/posts/1"), Mockito.any(HttpMethod.class), Mockito.any(HttpEntity.class), Mockito.any(
            ParameterizedTypeReference.class));
        AuditionPost auditionPost = client.getPostById("1");
        assertEquals(auditionPost.getBody(), null);
    }

    @Test
    public void testPostsById_negative_flow() {
        when(restTemplate.exchange(Mockito.eq("https://jsonplaceholder.typicode.com/posts/1"), Mockito.any(HttpMethod.class), Mockito.any(HttpEntity.class),
            Mockito.any(ParameterizedTypeReference.class))).thenReturn(new ResponseEntity(AuditionPost.builder().build(), HttpStatus.OK));
        SystemException systemException = Assertions.assertThrows(SystemException.class, () -> client.getPostById("3456"));
        assertEquals(systemException.getMessage(), "Cannot find any posts. Resource Not Found");
    }
*/

    @Test
    public void testComments_positive_flow() {
        doReturn(new ResponseEntity(auditionCommentsList, HttpStatus.OK))
            .when(restTemplate).exchange(Mockito.any(URI.class), Mockito.any(HttpMethod.class), Mockito.any(HttpEntity.class), Mockito.any(
            ParameterizedTypeReference.class));
        List<AuditionComments> auditionCommentsList = client.getComments("1");
        assertEquals(auditionCommentsList.size(), 5);
    }

    @Test
    public void testComments_negative_flow() {
        doReturn(new ResponseEntity(new ArrayList<>(), HttpStatus.OK))
            .when(restTemplate).exchange(Mockito.any(URI.class), Mockito.any(HttpMethod.class), Mockito.any(HttpEntity.class), Mockito.any(
            ParameterizedTypeReference.class));
        SystemException systemException = Assertions.assertThrows(SystemException.class, () -> client.getComments("123456"));
        assertEquals(systemException.getMessage(), "Cannot find any comments. Resource Not Found");
    }

    @Test
    public void testComments_negative_flow_generic_exception() {

        doThrow(new RuntimeException("run time exception thrown here"))
            .when(restTemplate).exchange(Mockito.any(URI.class), Mockito.any(HttpMethod.class), Mockito.any(HttpEntity.class), Mockito.any(
            ParameterizedTypeReference.class));
        RuntimeException runtimeException = Assertions.assertThrows(RuntimeException.class, () -> client.getComments("333456"));
        assertEquals(runtimeException.getMessage(), "run time exception thrown here");
    }

    @Test
    public void testCommentsByPostId_positive_flow() {
        doReturn(new ResponseEntity(auditionCommentsList, HttpStatus.OK))
            .when(restTemplate).exchange(anyString(), Mockito.any(HttpMethod.class), Mockito.any(HttpEntity.class), Mockito.any(
            ParameterizedTypeReference.class));
        List<AuditionComments> auditionCommentsList = client.getCommentsByPostId("1", "1");
        assertEquals(auditionCommentsList.size(), 5);
    }

    @Test
    public void testCommentsByPostId_negative_flow() {
        doReturn(new ResponseEntity(new ArrayList<>(), HttpStatus.OK))
            .when(restTemplate).exchange(anyString(), Mockito.any(HttpMethod.class), Mockito.any(HttpEntity.class), Mockito.any(
            ParameterizedTypeReference.class));
        SystemException systemException = Assertions.assertThrows(SystemException.class, () -> client.getCommentsByPostId("3456", null));
        assertEquals(systemException.getMessage(), "Cannot find any comments. Resource Not Found");
    }

    @Test
    public void testCommentsByPostId_negative_flow_generic_exception() {

        doThrow(new RuntimeException("run time exception thrown"))
            .when(restTemplate).exchange(anyString(), Mockito.any(HttpMethod.class), Mockito.any(HttpEntity.class), Mockito.any(
            ParameterizedTypeReference.class));
        RuntimeException runtimeException = Assertions.assertThrows(RuntimeException.class, () -> client.getCommentsByPostId("3456", null));
        assertEquals(runtimeException.getMessage(), "run time exception thrown");
    }

}
