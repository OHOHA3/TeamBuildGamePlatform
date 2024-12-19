# Card-game

Dockerfile запускает game-server (backend) и game-client (frontend) в одном контейнере.

Ожидаются переменные окружения:

- **WEBSOCKET_URL** - для подключения к сервису комнат
- **ROOM_ID** - необходима сервису комнат для идентификации игры.
