package com.joblog.auth.oauth;

import com.joblog.user.entity.User;
import com.joblog.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.DelegatingOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        Map<String, Object> attributes = oAuth2User.getAttributes();

        OAuthProvider provider = OAuthProvider.from(registrationId); // enum 변환

        String email = null;
        switch (provider) {
            case GOOGLE -> email = (String) attributes.get("email");
            case NAVER -> {
                Map<String, Object> response = (Map<String, Object>) attributes.get("response");
                email = (String) response.get("email");
                attributes = response; // 💡 이후에도 통일되게 사용하기 위해 response를 기준으로 덮어씀
            }
            case KAKAO -> {
                Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
                email = (String) kakaoAccount.get("email");
                attributes = kakaoAccount; // email 등 접근을 통일하기 위해
            }
            default -> throw new OAuth2AuthenticationException("Unsupported provider: " + provider);
        }

        if (email == null) {
            throw new OAuth2AuthenticationException("이메일을 가져올 수 없습니다.");
        }

        final String finalEmail = email;
        User user = userRepository.findByEmailForOAuth(email)
                .orElseGet(() -> userRepository.save(User.createOAuthUser(finalEmail,"소셜 사용자")));

        return new CustomOAuth2User(user, attributes);
    }
}
