This is open source project for monitoring as visa slots.

Project is written in Java.

Used:
    Spring web
    Spring security (only for managing CORS)
    Spring data JPA
    PostgreSQL
    Flyway
    Lombok
    Telegram API
    Playwright (for monitoring website)
    2Captcha (service to pass reCAPTCHA)

User enters its personal data, which sends to spring boot service, which saves information about user in PostgreSQL database.
Server constantly monitors AS VISA website with using Playwright library for finding free slots.