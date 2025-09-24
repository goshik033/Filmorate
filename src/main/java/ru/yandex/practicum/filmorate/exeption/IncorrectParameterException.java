package ru.yandex.practicum.filmorate.exeption;

import lombok.Getter;

@Getter
public class IncorrectParameterException extends RuntimeException {
    String parameter;
    public IncorrectParameterException(String message, String parameter) {
        super(message);
        this.parameter = parameter;
    }
}
