package com.nuggets.valueeats.repository.voucher;

import com.nuggets.valueeats.entity.voucher.Voucher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Optional;

@Repository
public interface VoucherRepository extends JpaRepository<Voucher, Long> {
    boolean existsById(Long id);

    Optional<Voucher> findById(Long id);

    @Query("select max(id) from Voucher")
    Long findMaxId();

    void deleteById(Long id);

    @Query(value = "select * from voucher where eatery_id = ?1 and is_active = true", nativeQuery = true)
    ArrayList<Voucher> findActiveByEateryId(Long eateryId);

    @Query(value = "select * from voucher where is_active = true", nativeQuery = true)
    ArrayList<Voucher> findAllActive();

    @Query(value = "select max(discount) from voucher where eatery_id = ?1 and is_active = true", nativeQuery = true)
    Long findMaxDiscountFromEatery(Long eateryId);
}
