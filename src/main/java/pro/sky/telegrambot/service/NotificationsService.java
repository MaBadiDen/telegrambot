package pro.sky.telegrambot.service;


import org.springframework.stereotype.Service;
import pro.sky.telegrambot.entity.Notification;
import pro.sky.telegrambot.repository.NotificationsRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
public class NotificationsService {
    private final NotificationsRepository notificationsRepository;

    public NotificationsService(NotificationsRepository notificationsRepository) {
        this.notificationsRepository = notificationsRepository;
    }

    @Transactional
    public void create(Long chatId, String message, LocalDateTime dataTime) {
        Notification notification = new Notification();
        notification.setUserId(chatId);
        notification.setDataTime(dataTime.truncatedTo(ChronoUnit.MINUTES));
        notification.setMessage(message);
        notificationsRepository.save(notification);
    }
}
