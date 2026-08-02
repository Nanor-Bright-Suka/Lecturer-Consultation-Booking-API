package com.backend.lcbapi.awmodule.repo;

import com.backend.lcbapi.awmodule.entity.BookableSlotEntity;
import com.backend.lcbapi.awmodule.enums.BookableSlotStatusEnum;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface BookableSlotRepo extends JpaRepository<BookableSlotEntity, UUID> {
    long countByAvailabilityWindowId(UUID availabilityWindowId);
    List<BookableSlotEntity> findAllByAvailabilityWindowId(UUID availabilityWindowId);
    void deleteAllByAvailabilityWindowIdAndStatus(UUID availabilityWindowId, BookableSlotStatusEnum status);
    boolean existsByAvailabilityWindowIdAndStatusNot(UUID availabilityWindowId, BookableSlotStatusEnum status);


}
