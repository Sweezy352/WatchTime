package kg.sweezy.watchtime.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Sql(scripts = {"/data/insertRoles.sql", "/data/insertUsers.sql", "/data/insertVideo.sql", "/data/insertComments.sql"})
class CommentControllerTest {
    @Autowired
    private MockMvc mockMvc;

    private String getJwtToken(String username, String password) throws Exception {
        String jwtToken = mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/login")
                        .param("username", username)
                        .param("password", password))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertNotNull(jwtToken);
        return jwtToken;
    }

    @DirtiesContext
    @Test
    void updateCommentById() throws Exception {
        String jwtToken = getJwtToken("Sweezy", "qweqwe");
        String commentBody = """
                {
                   "content": "new test comment"
                }
                """;

        mockMvc.perform(MockMvcRequestBuilders.post("/api/comment/update-comment-by-id")
                .queryParam("commentId", "1")
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(commentBody))
                .andExpect(status().isOk());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/video/get-all-comments-by-video-id")
                .queryParam("videoId", "3")
                .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].content").value("new test comment"));
    }

    @DirtiesContext
    @Test
    void updateCommentByEmptyBody() throws Exception {
        String jwtToken = getJwtToken("Sweezy", "qweqwe");
        String commentBody = """
                {
                   "content": "     "
                }
                """;

        mockMvc.perform(MockMvcRequestBuilders.post("/api/comment/update-comment-by-id")
                        .queryParam("commentId", "1")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(commentBody))
                .andExpect(status().isBadRequest());

    }

    @DirtiesContext
    @Test
    void deleteCommentById() {
    }

    @DirtiesContext
    @Test
    void likeCommentById() {
    }

    @DirtiesContext
    @Test
    void dislikeCommentById() {
    }

    @DirtiesContext
    @Test
    void addCommentById() {
    }
}