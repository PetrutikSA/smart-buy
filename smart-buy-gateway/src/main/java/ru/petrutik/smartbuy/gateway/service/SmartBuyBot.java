package ru.petrutik.smartbuy.gateway.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import ru.petrutik.smartbuy.gateway.config.AppConfig;
import ru.petrutik.smartbuy.gateway.config.BotMenuCommand;
import ru.petrutik.smartbuy.gateway.model.ConversationStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class SmartBuyBot extends TelegramLongPollingBot {
    private final String botName;
    private final UserRequestService userRequestService;
    private final Logger logger;
    private final AppConfig appConfig;
    private final String operationInProcess = "Ваш запрос обрабатывается, пожалуйста, подождите";

    public SmartBuyBot(@Value("${smartbuy.bot.name}") String botName, @Value("${smartbuy.bot.token}") String botToken,
                       UserRequestService userRequestService, AppConfig appConfig) {
        super(botToken);
        this.botName = botName;
        this.userRequestService = userRequestService;
        this.appConfig = appConfig;
        logger = LoggerFactory.getLogger(SmartBuyBot.class);
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
            if (message.hasText()) {
                String textMessage = message.getText();
                switch (textMessage) {
                    case "/start" -> startCommandReceived(chatId, message.getChat().getFirstName());
                    case "/add" -> addCommandReceived(chatId, ConversationStatus.ADD0, textMessage);
                    case "/list" -> listCommandReceived(chatId);
                    case "/show" -> showCommandReceived(chatId, ConversationStatus.SHOW0, textMessage);
                    case "/remove" -> removeCommandReceived(chatId, ConversationStatus.DELETE0, textMessage);
                    case "/remove_all" -> removeAllCommandReceived(chatId, ConversationStatus.DELETE_ALL0, textMessage);
                    default -> {
                        ConversationStatus conversationStatus = userRequestService.checkConversationStatus(chatId);
                        switch (conversationStatus) {
                            case ADD1, ADD2 -> addCommandReceived(chatId, conversationStatus, textMessage);
                            case SHOW1, SHOW2 -> showCommandReceived(chatId, conversationStatus, textMessage);
                            case DELETE1, DELETE2 -> removeCommandReceived(chatId, conversationStatus, textMessage);
                            case DELETE_ALL1, DELETE_ALL2 ->
                                    removeAllCommandReceived(chatId, conversationStatus, textMessage);
                            default -> sendText(chatId, "Приношу извинения, данная команда не предусмотрена :(");
                        }
                    }
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
        userRequestService.registerUserOrSetStatusToNew(chatId);
        sendText(chatId, answer);
    }

    private void addCommandReceived(Long chatId, ConversationStatus conversationStatus, String clientMessage) {
        switch (conversationStatus) {
            case ADD0 -> {
                if (userRequestService.isRequestLimitReached(chatId)) {
                    sendText(chatId, "К сожаление достигнут лимит запросов :(");
                    sendText(chatId, "Для добавления нового запроса, пожалуйста, удалите один из существующих");
                    return;
                }
                sendText(chatId, "Введите, пожалуйста, следующее:");
                sendText(chatId, "Поисковый запрос который необходимо отслеживать");
                userRequestService.addRequest(chatId, conversationStatus, clientMessage);
            }
            case ADD1 -> {
                sendText(chatId, "Не дороже какой цены должен быть товар (только цифры, без копеек)");
                userRequestService.addRequest(chatId, conversationStatus, clientMessage);
            }
            case ADD2 -> {
                sendText(chatId, "Отлично, как найду, пришлю ссылку :)");
                userRequestService.addRequest(chatId, conversationStatus, clientMessage);
            }
        }
    }

    private void listCommandReceived(Long chatId) {
        listAllRequests(chatId, ConversationStatus.LIST);
    }

    private void listAllRequests(Long chatId, ConversationStatus conversationStatus) {
        ConversationStatus status = userRequestService.checkConversationStatus(chatId);
        if (status == conversationStatus) {
            sendText(chatId, operationInProcess);
            return;
        }
        sendText(chatId, "Список всех действующих запросов");
        userRequestService.listOfAllRequests(chatId, conversationStatus); //TODO in handler make status new
    }

    private void showCommandReceived(Long chatId, ConversationStatus conversationStatus, String clientMessage) {
        switch (conversationStatus) {
            case SHOW0 -> {
                sendText(chatId, "Напишите, пожалуйста, номер запроса, по которому " +
                        "хотите получить информацию (только цифры)");
                listAllRequests(chatId, ConversationStatus.SHOW1);
            }
            case SHOW1 -> {
                Optional<Integer> optionalRequestNumber = parseRequestNumber(chatId, clientMessage);
                if (optionalRequestNumber.isPresent()) {
                    sendText(chatId, "Информация по запросу:");
                    userRequestService.showRequest(chatId, optionalRequestNumber.get()); //TODO in handler make status new
                }
            }
            case SHOW2 -> sendText(chatId, operationInProcess);
        }
    }

    private void removeCommandReceived(Long chatId, ConversationStatus conversationStatus, String clientMessage) {
        switch (conversationStatus) {
            case DELETE0 -> {
                sendText(chatId, "Напишите, пожалуйста, номер запрос который хотите удалить");
                listAllRequests(chatId, ConversationStatus.DELETE1);
            }
            case DELETE1 -> {
                Optional<Integer> optionalRequestNumber = parseRequestNumber(chatId, clientMessage);
                if (optionalRequestNumber.isPresent()) {
                    userRequestService.removeRequest(chatId, optionalRequestNumber.get());
                    sendText(chatId, "Запрос удален"); //TODO in handler make status new, change requestsAdded
                }
            }
            case DELETE2 -> sendText(chatId, operationInProcess);
        }
    }

    private Optional<Integer> parseRequestNumber(Long chatId, String clientMessage) {
        int number;
        try {
            number = Integer.parseInt(clientMessage);
        } catch (NumberFormatException e) {
            logger.error("Error when parsing number of request: {}", clientMessage, e);
            number = -1;
        }
        if (number < 1 && number > appConfig.getRequestsPerUserLimit()) {
            sendText(chatId, "Введено некорректное значение. Попробуйте, пожалуйста, снова (только цифры)");
            return Optional.empty();
        }
        return Optional.of(number);
    }

    private void removeAllCommandReceived(Long chatId, ConversationStatus conversationStatus, String clientMessage) {
        switch (conversationStatus) {
            case DELETE_ALL0 -> {
                sendText(chatId, "Вы действительно хотите удалить все запросы? Если да, напишите слово \"да\":");
                userRequestService.removeAll(chatId, conversationStatus);
            }
            case DELETE_ALL1 -> {
                if (clientMessage.equalsIgnoreCase("да")) {
                    userRequestService.removeAll(chatId, conversationStatus);
                    sendText(chatId, "Все запросы удалены"); //TODO in handler make status new, change requestsAdded
                } else {
                    userRequestService.makeConversationStatusNew(chatId);
                    sendText(chatId, "Подтверждение не получено, операция отменена");
                }
            }
            case DELETE_ALL2 -> sendText(chatId, operationInProcess);
        }
    }

    public void sendText(Long chatId, String text) {
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
