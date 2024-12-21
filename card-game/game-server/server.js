require('dotenv').config();
const express = require("express");
const http = require("http");
const cors = require("cors");
const serverSocketIO = require("socket.io");
const fetch = require("node-fetch");


const app = express();
const server = http.createServer(app);
const gameSocket = serverSocketIO(server, {
  cors: {
    origin: "http://localhost:3000",
    methods: ["GET", "POST"],
  },
});
const port = 5000;


var fs = require('fs');
var questions = fs.readFileSync('questions.txt').toString().split("\n");
console.log("Questions:");
for(i in questions) {
  console.log(questions[i]);
};
const roomNumber = process.env.ROOM_ID ? process.env.ROOM_ID : 1;
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


const roomSrvUrl = process.env.WEBSOCKET_URL ? process.env.WEBSOCKET_URL : "http://localhost:3000";
fetch(`${roomSrvUrl}/game-room-service/api/v1/room/users`,
  {
      method: 'POST',
      body: JSON.stringify({
        gameId: roomNumber,
        roomId: roomNumber,
      }),
      headers: {
          'Content-type':
              'application/json; charset=UTF-8',
      },
  })
  // Parse JSON data
  .then((response) => response.json())
  // Showing response
  .then((json) => {
    console.log(json)
    if (json && Array.isArray(json)) {
      gameState.players = json.map((value) => value.username);
    } else {
      console.log("Room srv response bad format");
    }
  })
  .catch(err => {
    console.log("Room srv is unreachable");
    console.log(err);
  });


gameSocket.on("connection", (socket) => {
  console.log(`A user connected [${socket.id}]`);

  socket.on("join", () => {
    console.log(`Join request from ${socket.id}`);
    gameState.idArray.push(socket.id);
    if (gameState.idArray.length > 1 && gameState.status != "started") {
      gameState.status = "started"
      gameState.activePlayer = getRandomPlayer();
      gameState.question = getQuestion();
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
    }
    if (gameState.activePlayer == socket.id) {
      gameState.activePlayer = getRandomPlayer();
      gameState.question = getQuestion();
    }
    console.log(gameState);
  });
});


app.use(cors());
server.listen(port, () => {
  console.log(`Server running at http://localhost:${port}`);
});