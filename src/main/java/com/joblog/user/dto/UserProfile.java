package com.joblog.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserProfile {
    private Long id;
    private String email;
    private String nickname;
}
