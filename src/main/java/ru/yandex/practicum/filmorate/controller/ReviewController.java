package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

import java.util.List;

@Validated
@RestController
@RequestMapping("/reviews")
public class ReviewController {
    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping("/{id}")
    public Review getReview(@PathVariable long id) {
        return reviewService.getReview(id);
    }

    @GetMapping
    public List<Review> getReviews(@RequestParam(required = false) Long filmId, @RequestParam(defaultValue = "10") @Min(1) @Max(100) int count

    ) {
        if (filmId == null) {
            return reviewService.getAllReviews();
        }
        return reviewService.getTopUsefulReviews(filmId, count);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Review addReview(@RequestBody Review review) {
        return reviewService.createReview(review);
    }

    @PutMapping("/{id}")
    public Review updateReview(@PathVariable long id, @RequestBody Review review) {
        review.setId(id);
        return reviewService.updateReview(review);

    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteReview(@PathVariable long id) {
        reviewService.deleteReview(id);
    }

    @PutMapping("/{id}/like/{userId}")
    public Review addLike(@PathVariable("id") @Min(1) long id, @PathVariable("userId") @Min(1) long userId) {
        return reviewService.addLike(id, userId);
    }

    @PutMapping("/{id}/dislike/{userId}")
    public Review addDislike(@PathVariable("id") @Min(1) long id, @PathVariable("userId") @Min(1) long userId) {
        return reviewService.addDislike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public ResponseEntity<Void> deleteLike(@PathVariable("id") @Min(1) long id, @PathVariable("userId") @Min(1) long userId) {
        reviewService.deleteLike(id, userId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}/dislike/{userId}")
    public ResponseEntity<Void> deleteDislike(@PathVariable("id") @Min(1) long id, @PathVariable("userId") @Min(1) long userId) {
        reviewService.deleteDislike(id, userId);
        return ResponseEntity.noContent().build();
    }

}
