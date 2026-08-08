package com.backend.lcbapi.awmodule.repo;

import com.backend.lcbapi.awmodule.entity.BookableSlotEntity;
import com.backend.lcbapi.awmodule.enums.BookableSlotStatusEnum;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BookableSlotRepo extends JpaRepository<BookableSlotEntity, UUID> {
    long countByAvailabilityWindowId(UUID availabilityWindowId);
    List<BookableSlotEntity> findAllByAvailabilityWindowId(UUID availabilityWindowId);
    void deleteAllByAvailabilityWindowIdAndStatus(UUID availabilityWindowId, BookableSlotStatusEnum status);
    boolean existsByAvailabilityWindowIdAndStatusNot(UUID availabilityWindowId, BookableSlotStatusEnum status);
    void deleteByAvailabilityWindowLecturerId(UUID lecturerId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<BookableSlotEntity> findById(UUID id);
}
