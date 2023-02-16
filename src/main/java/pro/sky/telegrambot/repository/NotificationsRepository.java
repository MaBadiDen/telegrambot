package pro.sky.telegrambot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pro.sky.telegrambot.entity.Notification;

import java.time.LocalDateTime;
import java.util.List;

public interface NotificationsRepository extends JpaRepository<Notification, Long> {
    List<Notification> findAllByDataTime(LocalDateTime dateTime);
}
