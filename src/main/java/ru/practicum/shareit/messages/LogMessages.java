package ru.practicum.shareit.messages;

public enum LogMessages {
    ADD_REQUEST("Запрос на добавление объекта {}"),
    RENEWAL_REQUEST("Запрос на обновление объекта {}"),
    GET_BY_ID_REQUEST("Запрос на получение объекта по id {}"),
    GET_ALL_REQUEST("Запрос владельцем списка всех его вещей"),
    GET_ALL_USERS("Запрос списка всех пользователей"),
    SEARCH_REQUEST("Запрос на поиск вещи"),
    REMOVE_REQUEST("Запрос на удаление пользователя {} "),
    ADD_ITEMREQUEST_REQUEST("Запрос на добавление нового запроса вещи"),
    BAD_REQUEST_STATUS("Ошибка 400!"),
    NOT_FOUND_STATUS("Ошибка 404!"),
    INTERNAL_SERVER_ERROR_STATUS("Ошибка 500! {}"),
    ALREADY_EXIST_ERROR_STATUS("Ошибка 409!"),
    ILLEGAL_ACCESS_ERROR_STATUS("Ошибка 403!"),
    ALREADY_EXIST("Такой объект {} уже есть"),
    BLANK_TEXT("Задан пустой поисковый запрос"),
    NOT_FOUND("Объект не найден {} "),
    ILLEGAL_ACCESS("Данный пользователь не оболадает правами доступа к вещи"),
    COMMENT_REQUEST("Запрос на добавление комментария к {} от {}"),
    REQUEST_EXCEPTION("Пользователь {} не может оставить комментрарий, аренда не закончилась"),
    BOOKING_REQUEST("Запрос от пользователя {} на бронирование {}"),
    BOOKING_RENEWAL_REQUEST("Запрос на подтверждение/отклонение бронирования {}"),
    GET_BOOKING_REQUEST("Запрос от {} на получение бронирования {}"),
    GET_BOOKING_REQUEST_STATUS("Запрос от {} на получение бронирования со статусом {} "),
    GET_ALL_BOOKING_REQUEST_STATUS("Запрос от {} на получение списка бронирования всех предметов со статусом {} "),
    BOOKING_START_DATE("Дата начала бронирования не может быть позднее даты его начала"),
    BOOKING_START_DATE_EQUAL("Дата начала бронирования не может быть равна дате его начала"),
    BOOKING_NOT_AVAILABLE("Предмет с id {} не доступен для бронирования"),
    BOOKING_BY_OWNER("Бронирование собственного предмета недоступно"),
    BOOKING_INVALID_ID("Нет прав на подтверждение бронирования. " +
            "Пользователь {} не является собственником предмета"),
    BOOKING_APPROVED("Данное бронирование уже было подтверждено"),
    BOOKING_GET_BY_ID("Пользователь {} не обладает правами для просмотра данных о бронировании"),
    UNSUPPORTED_STATUS("Unknown state: ");


    private final String messageText;

    LogMessages(String messageText) {
        this.messageText = messageText;
    }

    public String toString() {
        return messageText;
    }
}