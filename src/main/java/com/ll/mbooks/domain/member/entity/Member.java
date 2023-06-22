package com.ll.mbooks.domain.member.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ll.mbooks.base.entity.BaseEntity;
import com.ll.mbooks.domain.member.entity.emum.AuthLevel;
import com.ll.mbooks.util.Ut;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
public class Member extends BaseEntity {
    @Column(unique = true)
    private String username;
    @JsonIgnore
    private String password;
    private String email;
    private boolean emailVerified;
    private long restCash;
    private String nickname;
    private String avatarFileName;

    @Convert(converter = AuthLevel.Converter.class)
    private AuthLevel authLevel;

    @Column(columnDefinition = "TEXT")
    private String accessToken;

    public static Member fromMap(Map<String, Object> map) {
        long id = 0;

        if (map.get("id") instanceof Long) {
            id = (long) map.get("id");
        } else if (map.get("id") instanceof Integer) {
            id = (long) (int) map.get("id");
        }

        LocalDateTime createDate = null;
        LocalDateTime modifyDate = null;


        if (map.get("createDate") instanceof LocalDateTime) {
            createDate = (LocalDateTime) map.get("createDate");
        } else if (map.get("createDate") instanceof List) {
            createDate = Ut.date.bitsToLocalDateTime((List<Integer>) map.get("createDate"));
        }

        if (map.get("modifyDate") instanceof LocalDateTime) {
            modifyDate = (LocalDateTime) map.get("modifyDate");
        } else if (map.get("modifyDate") instanceof List) {
            modifyDate = Ut.date.bitsToLocalDateTime((List<Integer>) map.get("modifyDate"));
        }

        String username = (String) map.get("username");
        String email = (String) map.get("email");
        boolean emailVerified = (boolean) map.get("emailVerified");
        AuthLevel authLevel = (AuthLevel) map.get("authLevel");
        String accessToken = (String) map.get("accessToken");
        String nickname = (String) map.get("nickname");
        String avatarFileName = (String) map.get("avatarFileName");

        return Member
                .builder()
                .id(id)
                .createDate(createDate)
                .modifyDate(modifyDate)
                .username(username)
                .email(email)
                .emailVerified(emailVerified)
                .authLevel(authLevel)
                .accessToken(accessToken)
                .nickname(nickname)
                .avatarFileName(avatarFileName)
                .build();
    }

    public String getName() {
        if (nickname != null) {
            return nickname;
        }

        return username;
    }

    public Member(long id) {
        super(id);
    }

    public String getJdenticon() {
        return "member__" + getId();
    }

    public List<GrantedAuthority> getGrantedAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("MEMBER"));

        if (isAdmin()) {
            authorities.add(new SimpleGrantedAuthority("ADMIN"));
        }

        // 닉네임을 가지고 있다면 작가의 권한을 가진다.
        if (isAuthor()) {
            authorities.add(new SimpleGrantedAuthority("AUTHOR"));
        }

        return authorities;
    }

    @JsonIgnore
    public Map<String, Object> getAccessTokenClaims() {
        return Ut.mapOf(
                "id", getId(),
                "modifyDate", getModifyDate(),
                "username", getUsername()
        );
    }

    @JsonIgnore
    public Map<String, Object> toMap() {
        return Ut.mapOf(
                "id", getId(),
                "createDate", getCreateDate(),
                "modifyDate", getModifyDate(),
                "username", getUsername(),
                "email", getEmail(),
                "emailVerified", isEmailVerified(),
                "nickname", getNickname(),
                "authLevel", getAuthLevel(),
                "authorities", getGrantedAuthorities(),
                "accessToken", getAccessToken(),
                "avatarFileName", getAvatarFileName()
        );
    }

    public boolean isAdmin() {
        return getAuthLevel() == AuthLevel.ADMIN;
    }

    public boolean isAuthor() {
        return StringUtils.hasText(getNickname());
    }
}
