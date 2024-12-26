require('dotenv').config();
const express = require("express");
const http = require("http");
const serverSocketIO = require("socket.io");
const fetch = require("node-fetch");
const cfgProvider = require("./config");


const cfg = cfgProvider.getCfg(process.env.GAME_ENV);
const app = express();
const server = http.createServer(app);
const gameSocket = serverSocketIO(server, {
  cors: {
    origin: cfg.frontendUrl,
    methods: ["GET", "POST"],
  },
});
const port = cfg.port;


var fs = require('fs');
var questions = fs.readFileSync('questions.txt').toString().split("\n");
console.log("Questions:");
for(i in questions) {
  console.log(questions[i]);
};
const roomNumber = process.env.ROOM_ID ? process.env.ROOM_ID : 1;
console.log(`Room id: ${roomNumber}`);
var gameState = {
  question: "",
  activePlayer: "",
  players: [],
  idArray: [],
  status: "wait players"
};
function getQuestion() {
  return questions[questions.length * Math.random() | 0];
}
function getRandomPlayer() {
  return gameState.idArray[gameState.idArray.length * Math.random() | 0];
}


var roomSrvUrl = cfg.roomSrvUrl;
async function updateRoom() {
  console.log(`Updating room`);
  const response = await fetch(`${roomSrvUrl}/game-room-service/api/v1/room/users`, {
    method: 'POST',
    body: JSON.stringify({
      gameId: roomNumber,
      roomId: roomNumber,
    }),
    headers: {
        'Content-type':
            'application/json; charset=UTF-8',
    },
  });
  if (response.ok) {
    const json = await response.json();
    console.log("Players in room:");
    console.log(json);
    if (json && Array.isArray(json)) {
      gameState.players = json.map((value) => value.login);
    } else {
      console.log("Room srv response bad format");
    }
  } else {
    console.log("Send game status failed");
    console.log("Room srv is unreachable");
    console.log(response.statusText);
  }
}
async function sendGameStatus() {
  console.log(`Sending game status [${gameState.status}]`);
  const response = await fetch(`${roomSrvUrl}/game-room-service/api/v1/game/status`, {
    method: 'POST',
    body: JSON.stringify({
      roomId: roomNumber,
      status: gameState.status
    }),
    headers: {
        'Content-type':
          'application/json; charset=UTF-8',
    },
  });
  if (response.ok) {
    console.log("Game status sended successfully");
  } else {
    console.log("Send game status failed");
    console.log("Room srv is unreachable");
    console.log(response.statusText);
  }
}

updateRoom();

gameSocket.on("connection", (socket) => {
  console.log(`A user connected [${socket.id}]`);
  updateRoom();

  socket.on("join", () => {
    console.log(`Join request from ${socket.id}`);
    gameState.idArray.push(socket.id);
    if (gameState.idArray.length > 1 && gameState.status != "started") {
      gameState.status = "started"
      gameState.activePlayer = getRandomPlayer();
      gameState.question = getQuestion();
      sendGameStatus();
    }
    var response = {
      id: socket.id,
      roomNumber: roomNumber,
    };
    socket.emit("joinAck", response);
    gameSocket.emit("gameState", gameState);
    console.log(gameState);
  });

  socket.on("chooseActivePlayer", (id) => {
    gameState.activePlayer = id;
    gameState.question = getQuestion();
    gameSocket.emit("gameState", gameState);
    console.log(gameState);
  });

  socket.on("disconnect", () => {
    console.log(`User ${socket.id} disconnected`);
    gameState.idArray = gameState.idArray.filter(e => e !== socket.id)
    if (gameState.idArray.length < 2) {
      gameState.status = "ended"
      sendGameStatus();
    }
    if (gameState.activePlayer == socket.id) {
      gameState.activePlayer = getRandomPlayer();
      gameState.question = getQuestion();
    }
    console.log(gameState);
  });
});


server.listen(port, () => {
  console.log(`Server listening port:${port}`);
});