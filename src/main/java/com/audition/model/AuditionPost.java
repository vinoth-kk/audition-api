package com.audition.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuditionPost {

    private int userId;
    private int id;
    private String title;
    private String body;

}
