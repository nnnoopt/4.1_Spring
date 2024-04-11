package com.itm.space.backendresources.controller;


import com.itm.space.backendresources.BaseIntegrationTest;
import com.itm.space.backendresources.api.request.UserRequest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserControllerTest extends BaseIntegrationTest {
    @Autowired
    private Keycloak keycloak;

    @Test
    @WithMockUser(roles = "MODERATOR")
    void createUserTest() throws Exception {
        UserRequest userRequest = new UserRequest("Adam",
                "adam@gmail.com", "Adam123", "Adam", "Port");
        MockHttpServletRequestBuilder mockHttpServletRequest = requestWithContent(post("/api/users"), userRequest);
        mvc.perform(mockHttpServletRequest)
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "MODERATOR")
    void getUserByIdTest() throws Exception {
        String id = keycloak.realm("ITM").users().search("Adam").get(0).getId();
        mvc.perform(get("/api/users/{id}", id))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("adam@gmail.com"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.firstName").value("Adam"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.lastName").value("Port"));
    }

    @Test
    @WithMockUser(roles = "MODERATOR")
    void helloTest() throws Exception {
        mvc.perform(get("/api/users/hello")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @AfterAll
    void clean() {
        UserRepresentation userRepresentation = keycloak.realm("ITM").users().search("Adam").get(0);
        keycloak.realm("ITM").users().get(userRepresentation.getId()).remove();
    }

}