package ru.practicum.shareit.request.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.request.dto.AddItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@WebMvcTest(ItemRequestController.class)
class ItemRequestControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private ItemRequestService itemRequestService;
    private AddItemRequestDto itemRequest;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");

    @BeforeEach
    void setUp() {
        LocalDateTime created = LocalDateTime.now();
        itemRequest = AddItemRequestDto.builder()
                .created(created)
                .requestor(1L)
                .description("Item request description")
                .build();
    }

    @Test
    public void shouldNotCreateItemRequestWithNotFoundUser() throws Exception {
        when(itemRequestService.addRequest(any(), anyLong())).thenThrow(new NotFoundException("Объект не найден {}"));
        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 99)
                        .content(objectMapper.writeValueAsString(itemRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldNotGetItemRequestByNotFoundId() throws Exception {
        when(itemRequestService.getItemRequestById(anyLong(), anyLong())).thenThrow(new NotFoundException("Объект не найден {}"));

        mockMvc.perform(get("/requests/{requestId}", 99L)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldNotGetItemRequestByIdWithNotFoundUser() throws Exception {
        when(itemRequestService.getItemRequestById(anyLong(), anyLong())).thenThrow(new NotFoundException("Объект не найден {}"));

        mockMvc.perform(get("/requests/{requestId}", 1L)
                        .header("X-Sharer-User-Id", 99))
                .andExpect(status().isNotFound());
    }

    @Test
    void getUserItemRequests() throws Exception {
        List<AddItemRequestDto> itemRequests = new ArrayList<>();
        itemRequests.add(itemRequest);
        when(itemRequestService.getUserRequests(anyLong())).thenReturn(itemRequests);

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(itemRequest.getId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(itemRequest.getDescription())))
                .andExpect(jsonPath("$[0].requestor", is(itemRequest.getRequestor()), Long.class));
    }

    @Test
    void getOtherUsersRequests() throws Exception {
        List<AddItemRequestDto> itemRequests = new ArrayList<>();
        itemRequests.add(itemRequest);
        when(itemRequestService.getOtherUsersRequests(anyLong(), anyInt(), anyInt())).thenReturn(itemRequests);

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1)
                        .param("from", "0")
                        .param("size", "10")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(itemRequest.getId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(itemRequest.getDescription())))
                .andExpect(jsonPath("$[0].requestor", is(itemRequest.getRequestor()), Long.class));
    }

    @Test
    public void shouldNotCreateWithEmptyDescription() throws Exception {
        itemRequest.setDescription("");
        when(itemRequestService.addRequest(any(), anyLong())).thenThrow(new ValidationException("Описание не может быть пустым"));
        mockMvc.perform(post("/requests")
                        .content(objectMapper.writeValueAsString(itemRequest))
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldNotCreateWithInvalidCreated() throws Exception {
        itemRequest.setCreated(LocalDateTime.of(2020, 12,15, 13,0));
        when(itemRequestService.addRequest(any(), anyLong())).thenThrow(new ValidationException("Дата создания запроса должна быть в прошлом или настоящем"));
        mockMvc.perform(post("/requests")
                        .content(objectMapper.writeValueAsString(itemRequest))
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}