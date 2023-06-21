package ru.practicum.shareit.messages;

public interface ValidationMessages {
    String EMPTY_NAME = "Имя не может быть пустым";
    String EMPTY_DESCRIPTION = "Описание не может быть пустым";
    String END_DATA = "Дата конца бронирования должна быть в настоящем или будущем";
    String START_DATA = "Дата начала бронирования должна быть в настоящем или будущем";
    String REQUEST_CREATED_DATA = "Дата создания запроса должна быть в прошлом или настоящем";
    String INCORRECT_EMAIL = "Некорректный email";
    String EMPTY_EMAIL = "email не может быть пустым";
    String AVAILABLE_NULL = "Статус доступности вещи отсутствует";
    String EMPTY_TEXT = "Отсутствует текст поискового запроса";
}