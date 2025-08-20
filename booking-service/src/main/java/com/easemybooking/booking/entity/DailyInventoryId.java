package com.easemybooking.booking.entity;

import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class DailyInventoryId implements Serializable {
    private UUID placeId;
    private LocalDate forDate;

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DailyInventoryId that)) return false;
        return Objects.equals(placeId, that.placeId) && Objects.equals(forDate, that.forDate);
    }
    @Override public int hashCode() { return Objects.hash(placeId, forDate); }
}
