package ru.yandex.practicum.filmorate.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

import java.util.List;

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
    @GetMapping()
    public List<Review> getAllReviews() {
        return reviewService.getAllReviews();
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

}
