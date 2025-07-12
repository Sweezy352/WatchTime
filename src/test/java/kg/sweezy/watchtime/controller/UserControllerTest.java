package kg.sweezy.watchtime.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Sql(scripts = {"/data/insertRoles.sql", "/data/insertUsers.sql", "/data/insertSubscriptions.sql"})
class UserControllerTest {

    private String getJwtToken(String username, String password) throws Exception {
        String tokenJson = mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/login")
        .param("username", username)
        .param("password", password))
        .andExpect(status().isOk())
        .andReturn()
        .getResponse()
        .getContentAsString();
        assertNotNull(tokenJson);
        return tokenJson;
    }

    @Autowired
    private MockMvc mockMvc;

    @DirtiesContext
    @Test
    void register() throws Exception {

        var response = mockMvc.perform(multipart("/api/users/register")
                .param("username", "Aziret")
                .param("password", "qweqwe")
                .param("email", "aziret@gmail.com")
                .contentType(MediaType.MULTIPART_FORM_DATA)
        ).andExpect(status().isOk()).andExpect(jsonPath("$.username").value("Aziret"))
                .andExpect(jsonPath("$.email").value("aziret@gmail.com"))
                .andReturn();

        assertEquals(200, response.getResponse().getStatus());
    }

    @DirtiesContext
    @Test
    void registerEmptyBody() throws Exception {
        var response = mockMvc.perform(multipart("/api/users/register")
                        .param("username", "")
                        .param("password", "")
                        .param("email", "")
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                ).andExpect(status().isBadRequest()).andReturn();
    }

    @DirtiesContext
    @Test
    void registerWithEmptySpaces() throws Exception {
        var response = mockMvc.perform(multipart("/api/users/register")
                .param("username", "  ")
                .param("password", "   ")
                .param("email", "     ")
                .contentType(MediaType.MULTIPART_FORM_DATA)
        ).andExpect(status().isBadRequest()).andReturn();
    }

    @DirtiesContext
    @Test
    void getById() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/users/get-by-id/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("Sweezy"))
                .andExpect(jsonPath("$.email").value("sweezy@gmail.com"));
    }

    @DirtiesContext
    @Test
    void getByWrongId() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/users/get-by-id/50")).andExpect(status().isNotFound());
    }

    @DirtiesContext
    @Test
    void getAll() throws Exception {
        var result = mockMvc.perform(MockMvcRequestBuilders.get("/api/users/get-all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id").value(1))
                .andExpect(jsonPath("$.[1].id").value(2))
                .andExpect(jsonPath("$.[2].id").value(3));
    }

    @DirtiesContext
    @Test
    void getSubscriptionChannels() throws Exception {
        String jwtToken = getJwtToken("Sweezy", "qweqwe");

        mockMvc.perform(MockMvcRequestBuilders.get("/api/users/get-subscription-channels")
                .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id").value(3))
                .andExpect(jsonPath("$.[0].username").value("user"));
    }
}