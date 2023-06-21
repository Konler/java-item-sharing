package ru.practicum.shareit.booking;

public enum Status {
    WAITING("Ожидает одобрения"),
    APPROVED("Подтверждено владельцем"),
    REJECTED("Отклонено владельцем"),
    CANCELED("Отменено создателем");

    public final String statusDescription;

    Status(String message) {
        this.statusDescription = message;
    }
}