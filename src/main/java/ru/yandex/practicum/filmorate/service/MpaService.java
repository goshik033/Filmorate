package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.IncorrectParameterException;
import ru.yandex.practicum.filmorate.exception.MpaNotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;

import java.util.List;

@Service
public class MpaService {
    private final MpaStorage mpaStorage;

    public MpaService(MpaStorage mpaStorage) {
        this.mpaStorage = mpaStorage;
    }

    public Mpa getMpa(long id) {
        if (id <= 0) {
            throw new IncorrectParameterException("id должен быть положительным. Текущее значение: " + id, "mpaId");
        }
        return mpaStorage.getMpa(id).orElseThrow(() -> new MpaNotFoundException(id));
    }

    public List<Mpa> getAllMpa() {
        return mpaStorage.getAllMpa();
    }

}
