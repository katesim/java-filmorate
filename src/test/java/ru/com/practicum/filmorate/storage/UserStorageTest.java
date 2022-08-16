package ru.com.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.com.practicum.filmorate.exception.NotFoundException;
import ru.com.practicum.filmorate.model.User;
import ru.com.practicum.filmorate.storage.user.DBUserStorage;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserStorageTest {
    private final DBUserStorage userStorage;

    @Test
    void getById_valid_id_ValidGenre() {
        User testUser = User.builder()
                .email("myname@ya.ru")
                .login("login")
                .name("MyName")
                .birthday("1999-01-01")
                .build();

        Long userId = userStorage.add(testUser).getId();

        Optional<User> userOptional = Optional.ofNullable(userStorage.getById(userId));
        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("id", 1L)
                );
    }

    @Test
    void getById_not_valid_id_empty() {
        assertThrows(NotFoundException.class, () -> userStorage.getById(10L));
    }
}
