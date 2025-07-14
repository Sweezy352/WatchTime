package kg.sweezy.watchtime.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.multipart.MultipartFile;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Sql(scripts = {"/data/insertRoles.sql", "/data/insertUsers.sql", "/data/insertVideo.sql", "/data/insertComments.sql"})
class VideoControllerTest {
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

    //Будет работать с включенным MinIo
    @DirtiesContext
    @Test
    void uploadVideo() throws Exception {
        String jwtToken = getJwtToken("Sweezy", "qweqwe");

        MockMultipartFile videoFile = new MockMultipartFile("videoFile", "video.mp4", "video/mp4", "test".getBytes());
        MockMultipartFile videoPreviewFile = new MockMultipartFile("videoPreviewFile", "preview.jpg", "image/jpg", "test".getBytes());

        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/video/upload-video")
                .file(videoFile)
                .file(videoPreviewFile)
                .param("title", "Good video")
                .param("description", "Nice video")
                .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(4))
                .andExpect(jsonPath("$.title").value("Good video"))
                .andExpect(jsonPath("$.description").value("Nice video"))
                .andExpect(jsonPath("$.fileName").value("videoFileName"));
    }

    @DirtiesContext
    @Test
    void uploadEmptyVideo() throws Exception {
        String jwtToken = getJwtToken("Sweezy", "qweqwe");

        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/video/upload-video")
                        .param("title", "   ")
                        .param("description", "   ")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isBadRequest());
    }

    @DirtiesContext
    @Test
    void getVideoById() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/video/get-by-id/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("good video"))
                .andExpect(jsonPath("$.description").value("nice video"))
                .andExpect(jsonPath("$.fileName").value("name1"));
    }

    @DirtiesContext
    @Test
    void getVideoByWrongId() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/video/get-by-id").queryParam("videoId", "50"))
                .andExpect(status().isNotFound());
    }

    @DirtiesContext
    @Test
    void getAllVideo() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/video/get-all")).andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].title").value("good video"))
                .andExpect(jsonPath("$[0].fileName").value("name1"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].title").value("great video"))
                .andExpect(jsonPath("$[1].fileName").value("name2"))
                .andExpect(jsonPath("$[2].id").value(3))
                .andExpect(jsonPath("$[2].title").value("hey video"))
                .andExpect(jsonPath("$[2].fileName").value("name3"));
    }

    @DirtiesContext
    @Test
    void getAllVideoByChannelId() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/video/get-all-by-channel-id").queryParam("channelId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].title").value("good video"))
                .andExpect(jsonPath("$[0].fileName").value("name1"));
    }

    @DirtiesContext
    @Test
    void getAllVideoByWrongChannelId() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/video/get-all-by-channel-id").queryParam("channelId", "50"))
                .andExpect(status().isNotFound());
    }


    //Будет работать с включенным MinIo
    @DirtiesContext
    @Test
    void deleteVideoById() throws Exception {
        String jwtToken = getJwtToken("Sweezy", "qweqwe");

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/video/delete-by-id")
                        .queryParam("videoId", "1")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk());
    }

    @DirtiesContext
    @Test
    void addCommentById() throws Exception {
        String jwtToken = getJwtToken("Sweezy", "qweqwe");
        String commentBody = """
                {
                   "content": "test comment"
                }
                """;

        mockMvc.perform(MockMvcRequestBuilders.post("/api/video/add-comment-by-id")
                .queryParam("videoId", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(commentBody)
                .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(4))
                .andExpect(jsonPath("$.content").value("test comment"));
    }

    @DirtiesContext
    @Test
    void addCommentByWrongId() throws Exception {
        String jwtToken = getJwtToken("Sweezy", "qweqwe");
        String commentBody = """
                {
                   "content": "test comment"
                }
                """;

        mockMvc.perform(MockMvcRequestBuilders.post("/api/video/add-comment-by-id")
                        .queryParam("videoId", "50")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(commentBody)
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isNotFound());
    }

    @DirtiesContext
    @Test
    void addEmptyCommentById() throws Exception {
        String jwtToken = getJwtToken("Sweezy", "qweqwe");
        String commentBody = """
                {
                   "content": "   "
                }
                """;

        mockMvc.perform(MockMvcRequestBuilders.post("/api/video/add-comment-by-id")
                        .queryParam("videoId", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(commentBody)
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isBadRequest());
    }

    @DirtiesContext
    @Test
    void likeVideoById() throws Exception {
        String jwtToken = getJwtToken("Sweezy", "qweqwe");
        mockMvc.perform(MockMvcRequestBuilders.post("/api/video/like-video-by-id")
                .queryParam("videoId", "1")
                .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/video/get-liked-videos")
                .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].title").value("good video"));
    }

    @DirtiesContext
    @Test
    void likeVideoByWrongId() throws Exception {
        String jwtToken = getJwtToken("Sweezy", "qweqwe");
        mockMvc.perform(MockMvcRequestBuilders.post("/api/video/like-video-by-id")
                .queryParam("videoId", "50")
                .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isNotFound());
    }

    @DirtiesContext
    @Test
    void unlikeVideoById() throws Exception {
        likeVideoById();

        String jwtToken = getJwtToken("Sweezy", "qweqwe");
        mockMvc.perform(MockMvcRequestBuilders.post("/api/video/like-video-by-id")
                        .queryParam("videoId", "1")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/video/get-liked-videos")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @DirtiesContext
    @Test
    void dislikeVideoById() throws Exception {
        String jwtToken = getJwtToken("Sweezy", "qweqwe");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/video/dislike-video-by-id")
                .queryParam("videoId", "1")
                .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk());
    }

    @DirtiesContext
    @Test
    void dislikeWithLikedVideo() throws Exception {
        String jwtToken = getJwtToken("Sweezy", "qweqwe");
        likeVideoById();
        dislikeVideoById();

        mockMvc.perform(MockMvcRequestBuilders.get("/api/video/get-liked-videos")
                .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @DirtiesContext
    @Test
    void likeWithDislikedVideo() throws Exception {
        dislikeVideoById();
        likeVideoById();

        String jwtToken = getJwtToken("Sweezy", "qweqwe");
        mockMvc.perform(MockMvcRequestBuilders.get("/api/video/get-liked-videos")
        .header("Authorization", "Bearer " + jwtToken))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].id").value(1))
        .andExpect(jsonPath("$[0].title").value("good video"));
    }

    @DirtiesContext
    @Test
    void addToPlayListById() throws Exception {
        String jwtToken = getJwtToken("Sweezy", "qweqwe");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/video/add-to-play-list-by-id")
                .queryParam("videoId", "1")
                .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/video/get-play-list")
                .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].title").value("good video"));
    }

    @DirtiesContext
    @Test
    void addToPlayListByWrongId() throws Exception {
        String jwtToken = getJwtToken("Sweezy", "qweqwe");
        mockMvc.perform(MockMvcRequestBuilders.post("/api/video/add-to-play-list-by-id")
                .queryParam("videoId", "50")
                .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isNotFound());
    }

    @DirtiesContext
    @Test
    void removeFromPlayListBySameMethod() throws Exception {
        String jwtToken = getJwtToken("Sweezy", "qweqwe");
        addToPlayListById();
        mockMvc.perform(MockMvcRequestBuilders.post("/api/video/add-to-play-list-by-id")
                .queryParam("videoId", "1")
                .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/video/get-play-list")
                .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @DirtiesContext
    @Test
    void removeFromPlayListById() throws Exception {
        String jwtToken = getJwtToken("Sweezy", "qweqwe");
        addToPlayListById();

        mockMvc.perform(MockMvcRequestBuilders.post("/api/video/remove-from-play-list-by-id")
                .queryParam("videoId", "1")
                .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/video/get-play-list")
                .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @DirtiesContext
    @Test
    void getVideosByTitle() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/video/get-videos-by-title")
                .queryParam("title", "g"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].title").value("good video"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].title").value("great video"));
    }

    @DirtiesContext
    @Test
    void deleteCommentFromVideo() throws Exception {
        String jwtToken = getJwtToken("Sweezy", "qweqwe");
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/video/delete-comment-from-video")
                .queryParam("videoId", "3")
                .queryParam("commentId", "1")
                .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/video/get-all-comments-by-video-id")
                .queryParam("videoId", "3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }
}