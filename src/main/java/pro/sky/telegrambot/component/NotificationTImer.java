package pro.sky.telegrambot.component;

import com.pengrad.telegrambot.TelegramBot;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import pro.sky.telegrambot.listener.TelegramBotUpdatesListener;
import pro.sky.telegrambot.repository.NotificationsRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

@Component
public class NotificationTImer {
    private final NotificationsRepository notificationsRepository;
    private final TelegramBot telegramBot;
    private final TelegramBotUpdatesListener telegramBotUpdatesListener;

    public NotificationTImer(NotificationsRepository notificationsRepository, TelegramBot telegramBot, TelegramBotUpdatesListener telegramBotUpdatesListener) {
        this.notificationsRepository = notificationsRepository;
        this.telegramBot = telegramBot;
        this.telegramBotUpdatesListener = telegramBotUpdatesListener;
    }

    @Scheduled(fixedRate = 1, timeUnit = TimeUnit.MINUTES)
    @Transactional
    public void notificationTask() {
        notificationsRepository.findAllByDataTime(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES))
                .forEach(notification -> {
                    telegramBotUpdatesListener.sendMessage(notification.getUserId(), notification.getMessage());
                    notificationsRepository.delete(notification);
                });
    }
}
