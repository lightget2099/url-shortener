## URL Shortener REST API

Сервіс скорочення URL-адрес із JWT-авторизацією, аналітикою переходів та валідацією терміну дії посилань.

## Стек та Особливості тестування
Технології: Java 21, Spring Boot 4.1.0, Spring Security (JWT), Spring Data JPA, PostgreSQL 15, Flyway, Docker.
Документування: Swagger UI (Springdoc OpenAPI).
Тестування: JUnit 5, Mockito та Testcontainers.
Автоматизація (CI Pipeline): Налаштовано CI Pipeline (GitHub Actions), який автоматично запускається при кожному пуші або пулл-реквесті, збирає проєкт за допомогою Gradle, проганяє тести та перевіряє рівень покриття коду.
Testcontainers: Інтеграційні тести бази даних та перевірка шару репозиторіїв використовують Testcontainers. 
Під час запуску тестів автоматично піднімається реальний, повністю ізольований контейнер PostgreSQL у Docker.
Покриття коду: JaCoCo перевіряє покриття бізнес-логіки (мінімальний поріг — 80% line coverage).

## Налаштування оточення (.env)
Створіть файл .env у кореневій директорії проєкту та заповніть його:
DB_USERNAME=your_DATABASE_name (Логін для підключення до бази даних PostgreSQL)
DB_PASSWORD=your_DATABASE_password (Пароль для підключення до вашої локальної бази даних PostgreSQL)
JWT_SECRET=your_jwt_secret_key(Секретний ключ для підпису та перевірки JWT-токенів, мінімум 32 символи)

## Передача JWT токену
Токен необхідно передавати у HTTP-заголовку: Authorization: Bearer ваш_токен

## Налаштування базової адреси (Base URL)
За замовчуванням додаток генерує короткі посилання, використовуючи локальну адресу `http://localhost:8080`. 
Якщо ви розгортаєте сервіс на віддаленому сервері (Staging/Production), у Docker-контейнері або за Reverse Proxy (Nginx), вам необхідно змінити базову адресу.

Для цього використовується змінна оточення **`APP_BASE_URL`**.

## Спосіб 1: Запуск через Docker 
При запуску контейнера передайте змінну за допомогою прапорця `-e`:
docker run -p 8080:8080 -e APP_BASE_URL=[https://ваша-назва-сайту.com](https://ваша-назва-сайту.com) назва-образу

## Спосіб 2: Через Docker Compose
У нашому `docker-compose.yml` у блоці `environment` для сервісу `app` уже налаштовано гнучку змінну:
- APP_BASE_URL=${APP_BASE_URL:http://localhost:8080}
Щоб задати власну адресу, додайте у файл .env змінну: APP_BASE_URL=[https://ваша-назва-сайту.com](https://ваша-назва-сайту.com)
При запуску команди docker compose up --build -d Docker автоматично підставить адресу з .env файлу.
## Варіанти запуску

## Запуск через Docker Compose
**Збірка проєкту та підняття всієї інфраструктури (додаток + БД)**
docker-compose up --build
**Зупинка та видалення контейнерів**
docker-compose down

## Запуск локально (через Термінал / IDE)
Запустіть у Docker Dekstop вашу базу даних
Запустіть Spring Boot додаток:
./gradlew bootRun
При запуску через IntelliJ IDEA увімкніть плагін EnvFile у конфігурації AppLauncher та вкажіть шлях до .env файлу.

## Як запускати тести
Запуск всіх тестів та генерація звіту JaCoCo:
./gradlew clean test
Звіт про покриття доступний за шляхом: build/reports/jacoco/test/html/index.html
Щоб відкрити його натисніть правою кнопкою миші по файлу -> Open In -> Browser -> і оберіть браущер

## Документація API (Swagger UI)
Після запуска додатка інтерактивна специфікація доступна за адресою:
http://localhost:8080/swagger-ui/index.html

## Структура ендпоінтів: Public vs Protected
Публічні ендпоінти:
POST /api/v1/users/register — Реєстрація користувача.
POST /api/v1/users/login — Аутентифікація та отримання токена.
GET /r/{code} — Редірект за коротким кодом посилання.

## Захищені ендпоінти (JWT токен)
Токен необхідно передавати у HTTP-заголовку: Authorization: Bearer ваш_токен
POST /api/v1/urls — Створення короткого посилання.
GET /api/v1/urls/active — Отримання лише активних посилань користувача.
PATCH /api/v1/urls/{code} — Редагування оригінального URL.

## Приклади запитів
## Реєстрація нового користувача (Register)
Тип запиту: POST
Адреса: http://localhost:8080/api/v1/users/register
Заголовки: Content-Type: application/json
Тіло запиту:
{
"username": "test_user",
"password": "securePassword123"
}

## Вхід та отримання токена (Login)
Тип запиту: POST
Адреса: http://localhost:8080/api/v1/users/login
Заголовки: Content-Type: application/json
Тіло запиту:
{
"username": "test_user",
"password": "securePassword123"
}

## Створення короткого посилання (Create)
Тип запиту: POST
Адреса: http://localhost:8080/api/v1/urls
Заголовки:
Content-Type: application/json
Authorization: Bearer <YOUR_JWT_TOKEN>
Тіло запиту:
{
"originalUrl": "https://github.com/spring-projects/spring-boot"
}


## Перехід за посиланням (Redirect)
Тип запиту: GET
Адреса: http://localhost:8080/r/abcdef
Заголовки: Не потрібні (публічний доступ)
Результат: Автоматичний редірект (код відповіді 302) на оригінальну сторінку.