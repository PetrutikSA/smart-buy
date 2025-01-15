package ru.petrutik.smartbuy.gateway.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Service
public class SmartBuyBot extends TelegramLongPollingBot {
    private final String botName;
    private final String botToken;

    public SmartBuyBot(@Value("${bot.name}") String botName, @Value("${bot.token}") String botToken) {
        this.botName = botName;
        this.botToken = botToken;
    }

    @Override
    public String getBotUsername() {
        return botName;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update != null && update.hasMessage()) {
            Message message = update.getMessage();
            long chatId = message.getChatId();
            if(message.hasText()) {
                String textMessage = message.getText();
                switch (textMessage) {
                    case "/start":
                        startCommandReceived(chatId, message.getChat().getFirstName());
                        break;
                    case "/stop":
                        sendText(chatId, "I'm unstoppable! :)");
                        break;
                    default:
                        sendText(chatId, "Sorry, command is not supported yet :(");
                }
            }
        }
    }

    private void startCommandReceived(Long chatId, String firstName) {
        String answer = "Hi, " + firstName + "! Nice to meet you!";
        sendText(chatId, answer);
    }

    private void sendText(Long chatId, String text) {
        SendMessage sm = SendMessage.builder()
                .chatId(chatId.toString())
                .text(text).build();
        try {
            execute(sm);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
}
