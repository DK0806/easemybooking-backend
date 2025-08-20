package com.easemybooking.booking.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Entity @Table(name="daily_inventory")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@IdClass(DailyInventoryId.class)
public class DailyInventoryEntity {
    @Id @Column(name="place_id", columnDefinition="uuid") private UUID placeId;
    @Id @Column(name="for_date") private LocalDate forDate;

    @Column(nullable=false) private Integer capacity;
    @Column(nullable=false) private Integer reserved;
}
