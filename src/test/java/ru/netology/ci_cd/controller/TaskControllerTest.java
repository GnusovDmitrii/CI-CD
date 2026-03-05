package ru.netology.ci_cd.controller;

import ru.netology.ci_cd.model.Task;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testCreateTask() throws Exception {
        // Создаем задачу
        Task task = new Task("Купить молоко");

        // Отправляем POST запрос
        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(task)))
                .andExpect(status().isCreated())  // Ожидаем статус 201
                .andExpect(jsonPath("$.title").value("Купить молоко"))  // Проверяем название
                .andExpect(jsonPath("$.completed").value(false));  // Проверяем статус
    }

    @Test
    public void testGetAllTasks() throws Exception {
        // Сначала создадим задачу
        Task task = new Task("Купить хлеб");
        mockMvc.perform(post("/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(task)));

        // Получаем все задачи
        mockMvc.perform(get("/tasks"))
                .andExpect(status().isOk())  // Ожидаем статус 200
                .andExpect(jsonPath("$[0].title").value("Купить хлеб"));
    }

    @Test
    public void testUpdateTask() throws Exception {
        // Создаем задачу
        Task task = new Task("Купить масло");
        String response = mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(task)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // Получаем ID созданной задачи
        Task createdTask = objectMapper.readValue(response, Task.class);

        // Обновляем задачу
        createdTask.setTitle("Купить масло и сыр");
        createdTask.setCompleted(true);

        mockMvc.perform(put("/tasks/" + createdTask.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createdTask)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Купить масло и сыр"))
                .andExpect(jsonPath("$.completed").value(true));
    }

    @Test
    public void testDeleteTask() throws Exception {
        // Создаем задачу
        Task task = new Task("Купить сахар");
        String response = mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(task)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Task createdTask = objectMapper.readValue(response, Task.class);

        // Удаляем задачу
        mockMvc.perform(delete("/tasks/" + createdTask.getId()))
                .andExpect(status().isNoContent());  // Ожидаем статус 204

        // Проверяем, что задача действительно удалена
        mockMvc.perform(get("/tasks/" + createdTask.getId()))
                .andExpect(status().isNotFound());  // Ожидаем статус 404
    }

    @Test
    public void testCreateTaskWithInvalidData() throws Exception {
        // Пытаемся создать задачу с пустым названием
        Task task = new Task("");

        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(task)))
                .andExpect(status().isBadRequest());  // Ожидаем статус 400
    }
}