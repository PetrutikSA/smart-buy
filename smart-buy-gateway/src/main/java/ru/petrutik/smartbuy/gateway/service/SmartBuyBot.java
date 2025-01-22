package ru.petrutik.smartbuy.gateway.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.petrutik.smartbuy.gateway.config.BotMenuCommand;

import java.util.ArrayList;
import java.util.List;

@Service
public class SmartBuyBot extends TelegramLongPollingBot {
    private final String botName;
    private final UserRequestService userRequestService;

    public SmartBuyBot(@Value("${smartbuy.bot.name}") String botName, @Value("${smartbuy.bot.token}") String botToken,
                       UserRequestService userRequestService) {
        super(botToken);
        this.botName = botName;
        this.userRequestService = userRequestService;
        List<BotCommand> listOfCommands = new ArrayList<>();
        for (BotMenuCommand command : BotMenuCommand.values()) {
            listOfCommands.add(new BotCommand(command.getCommand(), command.getDescription()));
        }
        try {
            this.execute(new SetMyCommands(listOfCommands, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getBotUsername() {
        return botName;
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
                    case "/add":
                        addCommandReceived(chatId);
                        break;
                    case "/list":
                        listCommandReceived(chatId);
                        break;
                    case "/show":
                        showCommandReceived(chatId);
                        break;
                    case "/remove":
                        removeCommandReceived(chatId);
                        break;
                    case "/remove_all":
                        removeAllCommandReceived(chatId);
                        break;
                    default:
                        sendText(chatId, "Приношу извинения, данная команда не предусмотрена :(");
                }
            }
        }
    }

    private void startCommandReceived(Long chatId, String firstName) {
        String answer = "Приветствую, " + firstName + "! " +
                "Я помогу отслеживать в гипермаркетах цены на интересующие Вас товары. " +
                "Просто введи поисковый запрос, который я буду каждый день проверять в гипермаркетах" +
                " и при снижении цены отправлю тебе ссылку. Хватит переплачивать, мы поймаем нужную цену! " +
                "Попробуйте добавить свой первый запрос, выберите в меню пункт добавить и начните покупать выгоднее!";
        userRequestService.registerUser(chatId);
        sendText(chatId, answer);
    }

    private void addCommandReceived(Long chatId) {
        //Check request limit
        sendText(chatId, "Введите, пожалуйста, следующее:");
        sendText(chatId, "Поисковый запрос который необходимо отслеживать");
        // change status ADD1, break
        sendText(chatId, "Не дороже какой цены должен быть товар");
        // change status ADD2, break
        sendText(chatId, "Отлично, как найду, пришлю ссылку :)");
        // change status NEW
    }

    private void listCommandReceived(Long chatId) {
        sendText(chatId, "Список всех действующих запросов");
        // change status NEW
    }

    private void showCommandReceived(Long chatId) {
        sendText(chatId, "Список всех действующих запросов");
        sendText(chatId, "Напишите, пожалуйста, номер запрос по которому хотите получить информацию");
        // change status SHOW, break
        sendText(chatId, "Информация по запросу:");
        // change status NEW
    }

    private void removeCommandReceived(Long chatId) {
        sendText(chatId, "Список всех действующих запросов");
        sendText(chatId, "Напишите, пожалуйста, номер запрос который хотите удалить");
        // change status DELETE, break
        sendText(chatId, "Запрос удален");
        // change status NEW
    }

    private void removeAllCommandReceived(Long chatId) {
        sendText(chatId, "Вы действительно хотите удалить все запросы? Если да, напишите \"да\":");
        // change status DELETE_ALL, break
        sendText(chatId, "Все запросы удалены");
        // change status NEW
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
