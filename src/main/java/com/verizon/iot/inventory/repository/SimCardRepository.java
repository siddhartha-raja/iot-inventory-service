package com.verizon.iot.inventory.repository;

import com.verizon.iot.inventory.entity.SimCard;
import com.verizon.iot.inventory.entity.SimStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SimCardRepository extends JpaRepository<SimCard, String> {

    @Query("select s from SimCard s where s.simType = :simType and s.region = :region "
            + "and s.status = com.verizon.iot.inventory.entity.SimStatus.AVAILABLE")
    List<SimCard> findAvailable(@Param("simType") String simType,
                                @Param("region") String region,
                                Pageable pageable);

    long countBySimTypeAndRegionAndStatus(String simType, String region, SimStatus status);

    List<SimCard> findByReservationId(String reservationId);
}
