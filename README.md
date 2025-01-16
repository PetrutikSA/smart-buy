## Меню бота
### /start 
Начало работы с ботом. 

Результат: активация бота вывод на экран сообщение.
Приветственное сообщение, кратко описание возможностей, призыв к первому действию. 
> Приветствую, умный покупатель! Я помогу отслеживать в гипермаркетах цены на интересующие Вас товары. Просто введи поисковый запрос, который я буду каждый день проверять в гипермаркетах и при снижении цены отправлю тебе ссылку. Хватит переплачивать, мы поймаем нужную цену!  
Попробуйте добавить свой первый запрос, выберите в меню пункт добавить и начните покупать выгоднее!

При первом получении команды, происходит регистрация пользователя, при повторном - только выводится справка (аналогично __/help__)

### /add
Добавление запроса. После ввода команды предстоит ввести сам запрос.

_После ввода запроса будет запрошена максимальная стоимость с которой стоит выводить сообщения. Если введен 0,то ограничения не будет установлено._ 

Данный запрос будет направлен в сервис реквестов, зарегистрирован, и направле в парсер для первого получения информации 

### /list
Показать пронумерованный список всех запросов пользователя

### /show
Показать последний сохраненный результат. 

Выводится список запросов и необходимо написать номер по которому необходимо получить информацию.

### /remove
Удалить зарегистрированный запрос.

Выводится список запросов и необходимо написать номер по которому необходимо получить информацию.

### /remove_all
Удалить все запросы пользователя