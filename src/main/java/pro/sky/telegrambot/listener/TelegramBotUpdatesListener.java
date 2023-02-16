package pro.sky.telegrambot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.repository.NotificationsRepository;
import pro.sky.telegrambot.service.NotificationsService;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class TelegramBotUpdatesListener implements UpdatesListener {

    private static final Pattern PATTERN = Pattern.compile("([0-9\\.\\:\\s]{16})(\\s)([\\W+]+)");
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
    private Logger LOGGER = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);

    public TelegramBotUpdatesListener(TelegramBot telegramBot, NotificationsService notificationsService) {
        this.telegramBot = telegramBot;
        this.notificationsService = notificationsService;
    }

    private final TelegramBot telegramBot;
    private final NotificationsService notificationsService;

    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(this);
    }

    @Override
    public int process(List<Update> updates) {
        updates.forEach(update -> {
            LOGGER.info("Processing update: {}", update);
            String userMessage = update.message().text();
            Long chatId = update.message().chat().id();

            if(userMessage.equals("/start")) {
                SendMessage hello = new SendMessage(chatId, "Приветствую, кожаный мешок, опять распланировать тебе задачи?");
                telegramBot.execute(hello);
            } else {
                Matcher matcher = PATTERN.matcher(userMessage);
                LocalDateTime dataTime;
                if(matcher.find() && (dataTime = parse(matcher.group(1))) != null) {
                    String message = matcher.group(3);
                    notificationsService.create(chatId, message, dataTime);
                    sendMessage(chatId, "Успешно!");
                } else {
                    sendMessage(chatId, "Ошибка в формате сообщения!");
                }
            }
        });

        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }
    public void sendMessage(Long chatId, String message) {
        SendMessage sendMessage = new SendMessage(chatId, message);
        SendResponse sendResponse = telegramBot.execute(sendMessage);
        if(!sendResponse.isOk()) {
            LOGGER.error("Ошибка тут");
        }
    }

    @Nullable
    private LocalDateTime parse(String dataTime) {
        try {
            return LocalDateTime.parse(dataTime, DATE_TIME_FORMATTER);
        } catch (DateTimeParseException e) {
            return null;
        }
    }
}
