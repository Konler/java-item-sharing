package ru.practicum.shareit.messages;

public enum LogMessages {
    GET_BOOKINGS("Запрос на получение бронирования со статусом {}, id пользователя={}, from={}, size={}"),
    BOOK_ITEM("Запрос на создание бронирования {}, id пользователя={}"),
    ADD_ITEM("Запрос на создание вещи {}, id пользователя={}"),
    ADD_USER("Запрос на создание пользователя, id пользователя={}"),
    ADD_ITEM_REQUEST_REQUEST("Запрос на добавление нового запроса вещи {}, id пользователя={}"),
    GET_BOOKING("Запрос на получение бронирования {}, id пользователя={}"),
    GET_REQUEST("Запрос на получение запроса с id {}, id пользователя={}"),
    GET_REQUESTS("Запрос на получение запросов пользователя, id пользователя={}"),
    GET_OTHER_REQUESTS("Запрос на получение запросов других пользователей, id пользователя={}, from={}, size={}"),
    GET_ITEM_BY_ID("Запрос на получение вещи {}, id пользователя={}"),
    GET_USER_BY_ID("Запрос на получение пользователя, id пользователя={}"),
    GET_ALL_USERS("Запрос на получение списка всех пользователей"),
    GET_ALL_REQUEST("Запрос владельцем списка всех его вещей, id пользователя={}, from={}, size={}"),
    SEARCH_REQUEST("Запрос на поиск вещи по слову {}, id пользователя={}, from={}, size={}"),
    GET_ALL_BY_OWNER("Запрос на получение списка бронирований всех предметов со статусом {}, id пользователя={}, from={}, size={}"),
    RENEWAL_BOOKING("Запрос на подтверждение/отклонение бронирования с id {}, id пользователя={}"),
    RENEWAL_ITEM("Запрос на обровление вещи с id {}, id пользователя={}"),
    RENEWAL_USER("Запрос на обровление пользователя, id пользователя={}"),
    COMMENT_REQUEST("Запрос на добавление комментария вещи с id {}, id пользователя={}"),
    REMOVE_REQUEST("Запрос на удаление пользователя, id пользователя={}"),
    BAD_REQUEST_STATUS("Ошибка 400!"),
    INTERNAL_SERVER_ERROR_STATUS("Ошибка 500! {}");

    private final String messageText;

    LogMessages(String messageText) {
        this.messageText = messageText;
    }

    public String toString() {
        return messageText;
    }
}