package ru.practicum.shareit.item.controller;

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
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@WebMvcTest(ItemController.class)
class ItemControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private ItemService itemService;
    private ItemDto itemDto;

    @BeforeEach
    void setUp() {
        itemDto = ItemDto.builder()
                .name("Item name")
                .description("Item description")
                .available(true)
                .build();
    }

    @Test
    public void shouldCreateItem() throws Exception {
        when(itemService.addItem(anyLong(), any())).thenReturn(itemDto);
        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1)
                        .content(objectMapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())));
    }

    @Test
    public void shouldNotCreateItemWithEmptyName() throws Exception {
        when(itemService.addItem(anyLong(), any())).thenThrow(new ValidationException("Имя не может быть пустым"));
        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1)
                        .content(objectMapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldNotCreateItemWithEmptyDescription() throws Exception {
        when(itemService.addItem(anyLong(), any())).thenThrow(new ValidationException("Описание не может быть пустым"));
        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1)
                        .content(objectMapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldNotCreateItemWithEmptyAvailable() throws Exception {
        when(itemService.addItem(anyLong(), any())).thenThrow(new ValidationException("Статус доступности вещи отсутствует"));
        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1)
                        .content(objectMapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldNotCreateItemWithNotFoundUser() throws Exception {
        when(itemService.addItem(anyLong(), any())).thenThrow(new NotFoundException("Объект не найден {}"));
        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 99)
                        .content(objectMapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void shouldRenewalItem() throws Exception {
        itemDto.setName("NewName");
        itemDto.setDescription("New item description");
        when(itemService.updateItem(anyLong(), any(), anyLong())).thenReturn(itemDto);
        mockMvc.perform(patch("/items/{itemId}", 1L)
                        .header("X-Sharer-User-Id", 1)
                        .content(objectMapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())));
    }

    @Test
    public void getItemById() throws Exception {
        when(itemService.getItemById(anyLong(), anyLong())).thenReturn(itemDto);
        mockMvc.perform(get("/items/{itemId}", 1L)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())));
    }

    @Test
    void shouldNotGetItemByNotFoundId() throws Exception {
        when(itemService.getItemById(anyLong(), anyLong())).thenThrow(new NotFoundException("Объект не найден {}"));
        mockMvc.perform(get("/items/{itemId}", 99L)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldNotGetItemByIdNotFoundUser() throws Exception {
        when(itemService.getItemById(anyLong(), anyLong())).thenThrow(new NotFoundException("Объект не найден {}"));
        mockMvc.perform(get("/items/{itemId}", 1L)
                        .header("X-Sharer-User-Id", 99))
                .andExpect(status().isNotFound());
    }

    @Test
    public void searchItemByText() throws Exception {
        when(itemService.searchItem(anyString(), anyInt(), anyInt())).thenReturn(List.of(itemDto));
        mockMvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", 1)
                        .param("text", "name")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(itemDto.getName())))
                .andExpect(jsonPath("$[0].description", is(itemDto.getDescription())));
    }

    @Test
    public void getPersonalItems() throws Exception {
        List<ItemDto> items = new ArrayList<>();
        items.add(itemDto);
        when(itemService.getItemsByUserId(anyLong(), anyInt(), anyInt())).thenReturn(items);
        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1)
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(itemDto.getName())))
                .andExpect(jsonPath("$[0].description", is(itemDto.getDescription())));
    }

    @Test
    public void shouldAddComment() throws Exception {
        CommentDto commentDto = CommentDto.builder()
                .text("Comment text")
                .build();
        when(itemService.addComment(anyLong(), anyLong(), any())).thenReturn(commentDto);
        mockMvc.perform(post("/items/{itemId}/comment", 1L)
                        .header("X-Sharer-User-Id", 1)
                        .content(objectMapper.writeValueAsString(commentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(commentDto.getId()), Long.class))
                .andExpect(jsonPath("$.text", is(commentDto.getText())))
                .andExpect(jsonPath("$.created", is(commentDto.getCreated())));
    }

    @Test
    public void shouldNotAddCommentWithEmptyText() throws Exception {
        CommentDto commentDto = CommentDto.builder()
                .text("")
                .build();
        when(itemService.addComment(anyLong(), anyLong(), any())).thenThrow(new ValidationException("Отсутствует текст поискового запроса"));
        mockMvc.perform(post("/items/{itemId}/comment", 1L)
                        .header("X-Sharer-User-Id", 1)
                        .content(objectMapper.writeValueAsString(commentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldNotAddCommentWithNotFoundItem() throws Exception {
        CommentDto commentDto = CommentDto.builder()
                .text("Comment text")
                .build();
        when(itemService.addComment(anyLong(), anyLong(), any())).thenThrow(new NotFoundException("Объект не найден {}"));
        mockMvc.perform(post("/items/{itemId}/comment", 99L)
                        .header("X-Sharer-User-Id", 1)
                        .content(objectMapper.writeValueAsString(commentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void shouldNotAddCommentWithNotFoundUser() throws Exception {
        CommentDto commentDto = CommentDto.builder()
                .text("Comment text")
                .build();
        when(itemService.addComment(anyLong(), anyLong(), any())).thenThrow(new NotFoundException("Объект не найден {}"));
        mockMvc.perform(post("/items/{itemId}/comment", 99L)
                        .header("X-Sharer-User-Id", 1)
                        .content(objectMapper.writeValueAsString(commentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}