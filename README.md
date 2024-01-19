# Cloud service

Это REST-сервис, который предоставляет REST-интерфейс для загрузки файлов и вывода списка уже загруженных файлов пользователя.<br>
Спецификация для разработки сервиса находится в по пути `./specs/CloudServiceSpecification.yaml`

## Запуск приложения

Приложение работает на порту, заданном в application.properties.<br>
Перед запуском необходимо выполнить команду `gradle build` для сборки jar файла приложения.<br> 
После этого в корне проекта необходимо выполнить команду `docker-compose up` в результате чего запустится Backend и база данных<br>
PostgresSQL в которой хранится информация о пользователях и файлах<br>
Для хранения баз данных по пути настроено монтирование папок из Docker контейнера с PostgresSQL на локальный компьютер. Базы данных
хранятся по пути `./db`.<br>
Backend будет доступен по адресу http://localhost:5500<br>
Далее скачиваем и запускаем Frontend по инструкции из [гит репозитория frontend](https://github.com/netology-code/jd-homeworks/tree/master/diploma/netology-diplom-frontend).

## Логирование
Операции логируются в файле app.log в корне проекта.

## Тестирование
Для создания тестовых пользователей в базе можно использовать контроллер UsersController.

### Ссылка на задачу на разработку:
https://github.com/netology-code/jd-homeworks/blob/master/diploma/cloudservice.md

### Список используемой литературы:
- [JavaRush - CORS](https://javarush.com/quests/lectures/questspring.level04.lecture25)
- [Habr - Java Records (JEP 359)](https://habr.com/ru/articles/487308/)
- [Baeldung - Defining Unique Constraints in JPA](https://www.baeldung.com/jpa-unique-constraints)
- [How to Read HTTP Headers in Spring REST Controllers - Defining Unique Constraints in JPA](https://www.baeldung.com/spring-rest-http-headers)
- [Generate the MD5 Checksum for a File in Java](https://www.baeldung.com/java-md5-checksum-file)