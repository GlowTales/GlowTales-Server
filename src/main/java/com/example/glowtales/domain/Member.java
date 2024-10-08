package com.example.glowtales.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private Integer age;
    private String provider;
    private String providerId;
    private String loginId;
    private String email;
    private String roles; //USER

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LearningLanguage> learningLanguageList = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Tale> taleList = new ArrayList<>();

    public List<String> getRoleList() {
        if(!this.roles.isEmpty()) {
            return Arrays.asList(this.roles);
        }
        return new ArrayList<>();
    }

    @Builder
    public Member(String name, Integer age, String provider, String providerId, String loginId, String email, String roles) {
        this.name = name;
        this.age = age;
        this.provider = provider;
        this.providerId = providerId;
        this.loginId = loginId;
        this.email = email;
        this.roles = roles;
    }

    public void updateAge(Integer age) {
        this.age = age;
    }
}
