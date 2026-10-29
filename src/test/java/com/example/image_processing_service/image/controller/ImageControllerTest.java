package com.example.image_processing_service.image.controller;

import com.example.image_processing_service.image.dto.ImageDetailsResponse;
import com.example.image_processing_service.image.dto.ImageMetadata;
import com.example.image_processing_service.image.service.ImageOrchestrator;
import com.example.image_processing_service.image.service.ImageService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;
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

    @Test
    void uploadImage_authenticatedRequest_returnsCreated() throws Exception {
        MockMultipartFile image = new MockMultipartFile(
                "image",
                "fake image content".getBytes()
        );

        ImageDetailsResponse expectedResponse = ImageDetailsResponse.builder()
                .url("https://www.supabase.com/storage/image_01.jpg")
                .metadata(new ImageMetadata("image_01.jpg", "image/jpg", 1858L, 1200, 1600))
                .build();

        when(service.uploadAndSave(any(Jwt.class), any(MultipartFile.class)))
                .thenReturn(expectedResponse);

        mockMvc.perform(multipart("/api/v1/images")
                .file(image)
                .with(jwt()))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.url").value("https://www.supabase.com/storage/image_01.jpg"));

    }

}