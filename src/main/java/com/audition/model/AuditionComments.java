package com.audition.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuditionComments {

    private int postId;
    private int id;
    private String name;
    private String email;
    private String body;

}
