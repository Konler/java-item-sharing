package ru.practicum.shareit.booking;

public enum BookingState {
    ALL("Все"),
    CURRENT("Текущие"),
    PAST("Завершенные"),
    FUTURE("Будущие"),
    WAITING("Ожидающие подтверждения"),
    REJECTED("Отклоненные"),
    UNSUPPORTED_STATUS("Неподдерживаемый статус");

    public final String message;

    BookingState(String message) {
        this.message = message;
    }
}