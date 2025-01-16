package ru.petrutik.smartbuy.gateway.config;

public enum BotMenuCommand {
    START("/start", "Приветственное сообщение"),
    ADD("/add", "Добавить новый поиск товара"),
    SHOW("/show", "Показать последний найденный результат для выбранного товара"),
    LIST("/list", "Показать все зарегистрированные запросы"),
    REMOVE("/remove", "Удалить выбранный поиск"),
    REMOVE_ALL("/remove_all", "Удалить все запросы");

    private final String command;
    private final String description;

    BotMenuCommand(String command, String description) {
        this.command = command;
        this.description = description;
    }

    public String getCommand() {
        return command;
    }

    public String getDescription() {
        return description;
    }
}
