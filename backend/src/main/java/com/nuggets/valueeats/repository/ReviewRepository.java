package com.nuggets.valueeats.repository;

import com.nuggets.valueeats.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    @Query("select max(id) from Review")
    Long findMaxId();

    @Query(value = "select exists(select * from Review where diner_id = ?1 and eatery_id = ?2)", nativeQuery = true)
    int existsByDinerIdAndEateryId(Long dinerId, Long eateryId);

    @Query(value = "select exists(select * from Review where diner_id = ?1 and eatery_id = ?2 and id = ?3)",
            nativeQuery = true)
    int existsByDinerIdAndEateryIdAndReviewId(Long dinerId, Long eateryId, Long reviewId);

    @Query(value = "select * from Review where eatery_id = ?1", nativeQuery = true)
    List<Review> listReviewsOfEatery(Long eateryId);

    Optional<Review> findById(Long reviewId);

    List<Review> findByDinerId(Long id);

    void deleteById(Long reviewId);

    @Query(value = "select eatery_id from Review where diner_id = ?1 and rating < 3", nativeQuery = true)
    List<Long> listEateriesDinerDidNotEnjoy(Long dinerId);
}