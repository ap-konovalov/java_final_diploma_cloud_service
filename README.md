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
Для создания тестовых пользователей в базе можно использовать контроллер UsersController. <br>
Чтобы запустить тесты, необходимо собрать docker image с приложением, выполнив в терминале в корне проекта команды `gradle build` для 
сборки jar файла приложения и `docker-compose up` для сборки docker image.

### Ссылка на задачу на разработку сервиса:
https://github.com/netology-code/jd-homeworks/blob/master/diploma/cloudservice.md

### Список используемой литературы:
- [JavaRush - CORS](https://javarush.com/quests/lectures/questspring.level04.lecture25)
- [Habr - Java Records (JEP 359)](https://habr.com/ru/articles/487308/)
- [Baeldung - Defining Unique Constraints in JPA](https://www.baeldung.com/jpa-unique-constraints)
- [Baeldung - Hibernate One to Many Annotation Tutorial](https://www.baeldung.com/hibernate-one-to-many)
- [Baeldung - How to Read HTTP Headers in Spring REST Controllers - Defining Unique Constraints in JPA](https://www.baeldung.com/spring-rest-http-headers)
- [Baeldung - Generate the MD5 Checksum for a File in Java](https://www.baeldung.com/java-md5-checksum-file)
- [Baeldung - assertAll() vs Multiple Assertions in JUnit5](https://www.baeldung.com/junit5-assertall-vs-multiple-assertions)
- [Devwithus.com - Spring Boot Rest API Unit Testing with JUnit 5](https://devwithus.com/spring-boot-rest-api-unit-testing/)
- [Baeldung - Guide to @ConfigurationProperties in Spring Boot](https://www.baeldung.com/configuration-properties-in-spring-boot)
- [JavaRush - Зачем использовать SerialVersionUID внутри Serializable класса в Java](https://javarush.com/groups/posts/1034-zachem-ispoljhzovatjh-serialversionuid-vnutri-serializable-klassa-v-java)
- [Baeldung - Limiting Query Results With JPA and Spring Data JPA](https://www.baeldung.com/jpa-limit-query-results)
- [Baeldung - Using Transactions for Read-Only Operations](https://www.baeldung.com/spring-transactions-read-only)