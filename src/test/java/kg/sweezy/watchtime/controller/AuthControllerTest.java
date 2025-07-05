package kg.sweezy.watchtime.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import kg.sweezy.watchtime.security.JwtHandler;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Sql(scripts = {"/data/insertRoles.sql", "/data/insertUsers.sql"})
class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @DirtiesContext
    @Test
    void login() throws Exception {
        String login = "Sweezy";
        String password = "qweqwe";

        String tokenJson = mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/login")
                .param("username", login)
                .param("password", password))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertNotNull(tokenJson);
    }

    @DirtiesContext
    @Test
    void loginWithWrongPassword() throws Exception {
        String login = "Sweezy";
        String password = "123456";

        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/login")
                .param("username", login)
                .param("password", password))
                .andExpect(status().isBadRequest());
    }

    @DirtiesContext
    @Test
    void getCurrent() throws Exception {
        String login = "Sweezy";
        String password = "qweqwe";

        String tokenJson = mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/login")
                        .param("username", login)
                        .param("password", password))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertNotNull(tokenJson);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/auth/get-current").header("Authorization", "Bearer " + tokenJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value(login));
    }
}