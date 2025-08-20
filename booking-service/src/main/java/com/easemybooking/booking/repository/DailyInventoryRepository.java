package com.easemybooking.booking.repository;

import com.easemybooking.booking.entity.DailyInventoryEntity;
import com.easemybooking.booking.entity.DailyInventoryId;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.UUID;

public interface DailyInventoryRepository extends JpaRepository<DailyInventoryEntity, DailyInventoryId> {

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Transactional
    @Query(value = """
      UPDATE daily_inventory
         SET reserved = reserved + :qty
       WHERE place_id = :placeId
         AND for_date = :forDate
         AND reserved + :qty <= capacity
      """, nativeQuery = true)
    int tryReserve(@Param("placeId") UUID placeId,
                   @Param("forDate") LocalDate forDate,
                   @Param("qty") int qty);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Transactional
    @Query(value = """
      UPDATE daily_inventory
         SET reserved = GREATEST(reserved - :qty, 0)
       WHERE place_id = :placeId
         AND for_date = :forDate
      """, nativeQuery = true)
    int release(@Param("placeId") UUID placeId,
                @Param("forDate") LocalDate forDate,
                @Param("qty") int qty);
}
