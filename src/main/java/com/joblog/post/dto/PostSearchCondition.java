package com.joblog.post.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostSearchCondition {
    private String title;
    private String authorEmail;
}
