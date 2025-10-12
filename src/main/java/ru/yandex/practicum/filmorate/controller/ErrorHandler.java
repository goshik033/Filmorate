package ru.yandex.practicum.filmorate.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.filmorate.exception.*;

@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(FilmNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFound(FilmNotFoundException e) {
        return new ErrorResponse("Фильм не найден", e.getMessage());
    }
    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFound(UserNotFoundException e) {
        return new ErrorResponse("Пользователь не найден", e.getMessage());
    }
    @ExceptionHandler(GenreNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFound(GenreNotFoundException e) {
        return new ErrorResponse("Жанр не найден", e.getMessage());
    }
    @ExceptionHandler(MpaNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFound(MpaNotFoundException e) {
        return new ErrorResponse("Mpa не найден", e.getMessage());
    }
    @ExceptionHandler(UserAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleConflict(UserAlreadyExistsException e) {
        return new ErrorResponse("Пользователь уже создан", e.getMessage());
    }
    @ExceptionHandler(IncorrectParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleIncorrect(IncorrectParameterException e) {
        return new ErrorResponse(String.format("Некорректный параметр %s", e.getParameter()), e.getMessage());
    }
}
