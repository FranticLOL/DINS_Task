# DINS_Task

## Руководство по запуску:

### Предварительная подготовка

1. Проверить файл docker/database.env и настроить под себя необходимые конфигурации.
2. Запустить PostgreSQL и Kafka командой: docker-compose up -d
3. Проверить название контейнера с помощью команды: docker ps
4. Запустить командную строку Kafka : docker exec -it $название контейнера$ bash
5. Создать топик alerts: kafka-topics.sh --create --zookeeper zookeeper:2181 --replication-factor 1 --partitions 1 --topic alerts
6. Просматривать получаемые сообщение можно с помощью команды: kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic alerts
7. Запустить скрипты по начальной инициализации и наполнению базы данных (resources/sql/schema.sql и data.sql)

### Сборка и деплой приложения

1. Собрать приложение можно с помощью команды: mvn package (для этого должен быть устрановлен maven)
2. Деплой приложения происходит с помощью команды: sudo ./spark-submit --class App path/DINS/target/uber-DINS-1.0-SNAPSHOT.jar (Опционально можно указать аргумент - название IP адреса)
