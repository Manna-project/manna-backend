package com.manna.auth.entity;

public enum LoginType {
    GOOGLE("google"),
    KAKAO("kakao");

    private final String loginType;


    LoginType(String loginType) {
        this.loginType = loginType;
    }

    public String getLoginType() {
        return loginType;
    }
}
