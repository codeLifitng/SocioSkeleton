package com.SocioSkeleton.notfication_service.repository;

import com.SocioSkeleton.notfication_service.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
}
