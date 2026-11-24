package com.example.image_processing_service.image.controller;

import com.example.image_processing_service.auth.security.SecurityConfig;
import com.example.image_processing_service.image.entity.Image;
import com.example.image_processing_service.image.exception.ImageNotFoundException;
import com.example.image_processing_service.image.service.ImageOrchestrator;
import com.example.image_processing_service.image.service.ImageService;
import com.example.image_processing_service.user.entity.AppUser;
import com.example.image_processing_service.user.service.UserService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(ImageController.class)
@Import(SecurityConfig.class)
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

    @Test
    void getImages_withoutToken_returnsUnauthorized() throws Exception {
        mockMvc.perform(get("/api/v1/images"))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(userService, imageOrchestrator, service);
    }

    @Test
    void uploadImage_withoutToken_returnsUnauthorized() throws Exception {
        mockMvc.perform(multipart("/api/v1/images"))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(userService, imageOrchestrator, service);
    }

    @Test
    void deleteImage_withoutToken_returnsUnauthorized() throws Exception {
        mockMvc.perform(delete("/api/v1/images/{}", "image-uuid"))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(userService, imageOrchestrator, service);
    }

    @Test
    void uploadImage_withWrongParam_returnsBadRequest() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "not-image",
                "fake image content".getBytes()
        );

        mockMvc.perform((multipart("/api/v1/images")
                .file(file)
                .with(jwt())))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(userService, imageOrchestrator, service);
    }

    @Test
    void uploadImage_withJsonBody_returnsUnsupportedMediaType() throws Exception {
        String jsonPayload = """
                {
                    "name": "test-user",
                    "email": "test-user@email.com"
                }
                """;

        mockMvc.perform(post("/api/v1/images")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonPayload)
                .with(jwt())).andExpect(status().isUnsupportedMediaType());

        verifyNoInteractions(userService, imageOrchestrator, service);
    }

    @Test
    void deleteImage_withWrongUUID_returnsBadRequest() throws Exception {

        mockMvc.perform(delete("/api/v1/images/{id}", "not-a-uuid")
                .with(jwt()))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(userService, imageOrchestrator, service);
    }

    @Test
    void deleteImage_withValidUUID_returnsNoContent() throws Exception {
        UUID uuid = UUID.randomUUID();
        AppUser user = new AppUser();
        user.setId(1L);
        user.setUsername("test-user");

        when(userService.getUserFromJWT(any(Jwt.class))).thenReturn(user);

        mockMvc.perform(delete("/api/v1/images/{id}", uuid)
                .with(jwt()))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));

        verify(imageOrchestrator).delete(user, uuid);
    }

    @Test
    void getImages_returnsListOfImages() throws Exception {
        AppUser user = new AppUser();
        user.setId(1L);
        user.setUsername("test-user");

        UUID uuid1 = UUID.randomUUID();
        UUID uuid2 = UUID.randomUUID();

        Image image1 = new Image();
        image1.setId(uuid1);
        image1.setUser(user);
        image1.setStoragePath("https://www.supabase.com/storage/image_01.jpg");
        image1.setSizeBytes(3000L);
        image1.setOriginalName("image_1.jpeg");
        image1.setMimeType("image/jpeg");
        image1.setHeight(1200);
        image1.setWidth(800);
        image1.setCreatedAt(LocalDateTime.now());

        Image image2 = new Image();
        image2.setId(uuid2);
        image2.setUser(user);
        image2.setStoragePath("https://www.supabase.com/storage/image_02.jpg");
        image2.setSizeBytes(5000L);
        image2.setOriginalName("image_2.jpeg");
        image2.setMimeType("image/jpeg");
        image2.setHeight(1000);
        image2.setWidth(900);
        image2.setCreatedAt(LocalDateTime.now().plusSeconds(300));


        when(userService.getUserFromJWT(any(Jwt.class))).thenReturn(user);

        when(service.getImages(eq(user), any(Pageable.class))).thenReturn(List.of(image1, image2));


        mockMvc.perform(get("/api/v1/images")
                .with(jwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()")
                        .value(2))
                .andExpect(jsonPath("$.[0].id")
                        .value(uuid1.toString()))
                .andExpect(jsonPath("$.[1].storagePath")
                        .value("https://www.supabase.com/storage/image_02.jpg"));

        ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);

        verify(service).getImages(eq(user), captor.capture());

        var pageable = captor.getValue();

        Sort.Order order = pageable.getSort().getOrderFor("createdAt");

        assertThat(pageable).isNotNull();
        assertThat(pageable.getPageSize()).isEqualTo(10);
        assertThat(order).isNotNull();
        assertThat(order.getDirection()).isEqualTo(Sort.Direction.DESC);

    }

}