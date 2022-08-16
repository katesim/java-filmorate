package ru.com.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.com.practicum.filmorate.exception.NotFoundException;
import ru.com.practicum.filmorate.model.MPA;
import ru.com.practicum.filmorate.storage.MPA.MPAStorage;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class MPAStorageTest {
    private final MPAStorage mpaStorage;

    @Test
    void getById_valid_id_ValidMPA() {
        Optional<MPA> mpaOptional = Optional.ofNullable(mpaStorage.getById(1L));
        assertThat(mpaOptional)
                .isPresent()
                .hasValueSatisfying(mpa ->
                        assertThat(mpa).hasFieldOrPropertyWithValue("id", 1L)
                );
    }

    @Test
    void getById_not_valid_id_empty() {
        assertThrows(NotFoundException.class, () -> mpaStorage.getById(10L));
    }

    @Test
    void getAll_execute_5Items() {
        List<MPA> mpa = mpaStorage.getAll();
        assertThat(mpa).hasSize(5);
    }

}
