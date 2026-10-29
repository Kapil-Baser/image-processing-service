package com.example.image_processing_service.transform.controller;

import com.example.image_processing_service.image.dto.ImageDto;
import com.example.image_processing_service.image.service.ImageOrchestrator;
import com.example.image_processing_service.transform.dto.Crop;
import com.example.image_processing_service.transform.dto.Filter;
import com.example.image_processing_service.transform.dto.Resize;
import com.example.image_processing_service.transform.dto.TransformRequest;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;
import java.util.UUID;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TransformationController.class)
class TransformationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ImageOrchestrator imageOrchestrator;

    @Test
    void transformAuthenticatedRequest_returnsCreated() throws Exception {
        UUID imageId = UUID.randomUUID();

        TransformRequest transformRequest = new TransformRequest(
                new Resize(1200, 800),
                new Crop(1024, 400, 0, 0),
                90,
                false,
                false,
                "png",
                new Filter(false, false)
        );

        ObjectMapper objectMapper = new ObjectMapper();

        UUID id = UUID.randomUUID();


        ImageDto expectedResponse = ImageDto.builder()
                .id(id)
                .storagePath("http://supabase/storage/image.png")
                .originalName("Image_1.jpg")
                .mimeType("image/png")
                .width(1200)
                .height(800)
                .build();

        Mockito.when(imageOrchestrator.getOrCreateTransformation(imageId, transformRequest)).thenReturn(expectedResponse);

        mockMvc.perform(post("/api/v1/images/{id}/transform", imageId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(transformRequest))
                .with(jwt())).andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.storagePath").value("http://supabase/storage/image.png"));
    }

}