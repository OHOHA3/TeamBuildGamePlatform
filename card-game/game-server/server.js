require('dotenv').config();
const express = require("express");
const http = require("http");
const cors = require("cors");
const serverSocketIO = require("socket.io");
const Stomp = require("stompjs");
const SockJS = require("sockjs-client");

const app = express();
const server = http.createServer(app);
const gameSocket = serverSocketIO(server, {
  cors: {
    origin: "http://localhost:3000",
    methods: ["GET", "POST"],
  },
});
const port = 5000;
const sockJS = new SockJS(process.env.WEBSOCKET_URL);
stompClient = Stomp.over(sockJS);

var fs = require('fs');
var questions = fs.readFileSync('questions.txt').toString().split("\n");
console.log("Questions:");
for(i in questions) {
  console.log(questions[i]);
};
const roomNumber = process.env.ROOM_ID;
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


function sendGameState() {
  var state = {
    status: gameState.status,
  };
  stompClient.send("/app/game/status", {}, JSON.stringify(state));
}
function onRoomSrvMessageReceived(room) {
  console.log(room);
  gameState.players = room.map((value) => value.username);
}
function onRoomSrvConnected() {
  console.log("Connected to room service");
  stompClient.subscribe(
    `/game/${roomNumber}/queue/messages`,
    onRoomSrvMessageReceived
  );
  sendGameState();
  var roomRequest = {
    gameId: roomNumber,
    roomId: roomNumber,
  };
  stompClient.send("/app/room/users", {}, JSON.stringify(roomRequest));
}
function onRoomSrvError(err) {
  console,log(err);
}
function onRoomSrvDisconnect() {
  console.log("Disconnected from room service");
}
stompClient.connect({}, onRoomSrvConnected, onRoomSrvError, onRoomSrvDisconnect);


gameSocket.on("connection", (socket) => {
  console.log(`A user connected [${socket.id}]`);

  socket.on("join", () => {
    console.log(`Join request from ${socket.id}`);
    gameState.idArray.push(socket.id);
    if (gameState.players.length > 1 && gameState.status != "started") {
      gameState.status = "started"
      gameState.activePlayer = getRandomPlayer();
      gameState.question = getQuestion();
      sendGameState();
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
    if (gameState.players.length < 2) {
      gameState.status = "ended"
      sendGameState();
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