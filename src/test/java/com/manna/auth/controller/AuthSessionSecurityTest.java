package com.manna.auth.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
public class AuthSessionSecurityTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    void Access_Token이_없어도_CSRF_토큰이_있으면_로그아웃에_성공한다() throws Exception {
        mockMvc.perform(
            post("/api/v1/auth/logout").with(csrf())
        ).andExpect(status().isNoContent());
    }

    @Test
    void CSRF_토큰이_없으면_로그아웃을_거부한다() throws Exception {
        mockMvc.perform(
            post("/api/v1/auth/logout")
        ).andExpect(status().isForbidden());
    }
}
