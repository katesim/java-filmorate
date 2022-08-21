package ru.com.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;

@Data
public class Review { // модель отзыва пользователя к фильму
    private long reviewId;
    @NotBlank
    private String content;
    @NotNull
    private Boolean isPositive;
    @NotNull
    private Long userId;
    @NotNull
    private Long filmId;
    private long useful;
    private final Set<Long> likes = new HashSet<>(); // список reviewId пользователей для хранения лайков
    private final Set<Long> dislikes = new HashSet<>(); // список reviewId пользователей для хранения дизлайков
    /* с помощью списков likes и dislikes контролируем возможность пользователя проставить реакцию один раз и тем
       самым исключаем возможность манипуляций с рейтингом */

    public Review(long id, String content, Boolean isPositive, Long userId, Long filmId) {
        this.reviewId = id;
        this.content = content;
        this.isPositive = isPositive;
        this.userId = userId;
        this.filmId = filmId;
    }
}