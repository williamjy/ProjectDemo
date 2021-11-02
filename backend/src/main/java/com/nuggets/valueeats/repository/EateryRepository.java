package com.nuggets.valueeats.repository;

import com.nuggets.valueeats.entity.Eatery;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EateryRepository extends UserRepository<Eatery> {
    boolean existsById(Long id);

    @Query(value = "select * from Cuisines", nativeQuery = true)
    List<Object> findAllCuisines();

    @Query("select e from Eatery e where e.id not in (:list) order by id")
    List<Eatery> findAllEateriesNotInList(@Param("list") List<Long> list);

    List<Eatery> findAllByOrderByIdDesc();

    List<Eatery> findAllByOrderByLazyRatingDesc();

    @Query(value = "select count(*) from (select * from Cuisines where eatery_id = ?1 and cuisine in (select cuisine from Cuisines where eatery_id in ?2))",
            nativeQuery = true)
    Integer dinerHadCuisineBefore(Long eateryId, List<Long> list);
}
