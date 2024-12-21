# Card-game

Dockerfile запускает game-server (backend) и game-client (frontend) в одном контейнере.

Ожидаются переменные окружения:

- **WEBSOCKET_URL** - для подключения к сервису комнат
- **ROOM_ID** - необходима сервису комнат для идентификации игры.

## Docker

Build `sudo docker build . -t card-game`

Run `sudo docker run -p 3000:3000 -p 5000:5000 card-game`
