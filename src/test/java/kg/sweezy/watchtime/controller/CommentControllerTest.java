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
    void deleteCommentById() throws Exception {
        String jwtToken = getJwtToken("Sweezy", "qweqwe");
        addCommentById();

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/comment/delete-by-id")
                .queryParam("parentCommentId", "1")
                .queryParam("commentId", "4")
                .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/video/get-all-comments-by-video-id")
                .queryParam("videoId", "3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].comments").isEmpty());
    }

    @DirtiesContext
    @Test
    void deleteCommentByWrongId() throws Exception {
        String jwtToken = getJwtToken("Sweezy", "qweqwe");
        addCommentById();

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/comment/delete-by-id")
                        .queryParam("parentCommentId", "1")
                        .queryParam("commentId", "50")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isNotFound());

    }

    @DirtiesContext
    @Test
    void likeCommentById() throws Exception {
        String jwtToken = getJwtToken("Sweezy", "qweqwe");
        mockMvc.perform(MockMvcRequestBuilders.post("/api/comment/like-by-id")
                .queryParam("commentId", "1")
                .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/video/get-all-comments-by-video-id")
                .queryParam("videoId", "3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].likes").value("1"));
    }

    @DirtiesContext
    @Test
    void removeLikeWithSameMethod() throws Exception {
        String jwtToken = getJwtToken("Sweezy", "qweqwe");
        likeCommentById();
        mockMvc.perform(MockMvcRequestBuilders.post("/api/comment/like-by-id")
                        .queryParam("commentId", "1")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/video/get-all-comments-by-video-id")
                .queryParam("videoId", "3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].likes").value("0"));
    }

    @DirtiesContext
    @Test
    void removeDislikeAndLikeVideo() throws Exception {
        String jwtToken = getJwtToken("Sweezy", "qweqwe");
        dislikeCommentById();
        likeCommentById();

        mockMvc.perform(MockMvcRequestBuilders.get("/api/video/get-all-comments-by-video-id")
                .queryParam("videoId", "3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].likes").value("1"))
                .andExpect(jsonPath("$[0].dislikes").value("0"));
    }

    @DirtiesContext
    @Test
    void dislikeCommentById() throws Exception {
        String jwtToken = getJwtToken("Sweezy", "qweqwe");
        mockMvc.perform(MockMvcRequestBuilders.post("/api/comment/dislike-by-id")
                .queryParam("commentId", "1")
                .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/video/get-all-comments-by-video-id")
                .queryParam("videoId", "3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].dislikes").value("1"));
    }

    @DirtiesContext
    @Test
    void removeDislikeWithSameMethod() throws Exception {
        String jwtToken = getJwtToken("Sweezy", "qweqwe");
        dislikeCommentById();

        mockMvc.perform(MockMvcRequestBuilders.post("/api/comment/dislike-by-id")
                        .queryParam("commentId", "1")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/video/get-all-comments-by-video-id")
                        .queryParam("videoId", "3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].dislikes").value("0"));
    }

    @DirtiesContext
    @Test
    void removeLikeAndDislikeVideo() throws Exception {
        String jwtToken = getJwtToken("Sweezy", "qweqwe");
        likeCommentById();
        dislikeCommentById();
        mockMvc.perform(MockMvcRequestBuilders.get("/api/video/get-all-comments-by-video-id")
                .queryParam("videoId", "3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].likes").value("0"))
                .andExpect(jsonPath("$[0].dislikes").value("1"));
    }

    @DirtiesContext
    @Test
    void addCommentById() throws Exception {
        String jwtToken = getJwtToken("Sweezy", "qweqwe");
        String commentBody = """
                {
                   "content": "new test comment"
                }
                """;

        mockMvc.perform(MockMvcRequestBuilders.post("/api/comment/add-comment-by-id")
                .queryParam("commentId", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(commentBody)
                .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("new test comment"));
    }

    @DirtiesContext
    @Test
    void addCommentByEmptyBody() throws Exception {
        String jwtToken = getJwtToken("Sweezy", "qweqwe");
        String commentBody = """
                {
                   "content": "      "
                }
                """;

        mockMvc.perform(MockMvcRequestBuilders.post("/api/comment/add-comment-by-id")
                        .queryParam("commentId", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(commentBody)
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isBadRequest());
    }
}