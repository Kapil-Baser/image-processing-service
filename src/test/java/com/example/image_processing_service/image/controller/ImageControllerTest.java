package com.example.image_processing_service.image.controller;

import com.example.image_processing_service.image.entity.Image;
import com.example.image_processing_service.image.service.ImageOrchestrator;
import com.example.image_processing_service.image.service.ImageService;
import com.example.image_processing_service.user.entity.AppUser;
import com.example.image_processing_service.user.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(ImageController.class)
class ImageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ImageService service;

    @MockitoBean
    private ImageOrchestrator imageOrchestrator;

    @MockitoBean
    private UserService userService;

    @Test
    void uploadImage_authenticatedRequest_returnsCreated() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "image",
                "fake image content".getBytes()
        );

        AppUser user = new AppUser();

        Image image = new Image();
        image.setOriginalName("image_01.png");
        image.setUser(user);
        image.setStoragePath("https://www.supabase.com/storage/image_01.jpg");


        when(userService.getUserFromJWT(any(Jwt.class))).thenReturn(user);
        when(imageOrchestrator.upload(user, file)).thenReturn(image);


        mockMvc.perform(multipart("/api/v1/images")
                .file(file)
                .with(jwt()))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.url").value("https://www.supabase.com/storage/image_01.jpg"));

        verify(userService).getUserFromJWT(any(Jwt.class));
        verify(imageOrchestrator).upload(user, file);
    }

}