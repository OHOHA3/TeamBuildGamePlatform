'use client';
import './App.css';
import React, { useState, useEffect } from 'react';
import List from '@mui/material/List';
import ListItem from '@mui/material/ListItem';
import { Stack } from "@mui/material";
import { io } from "socket.io-client";
import { OutlinedButton } from './components/OutlinedButton';
import AvatarBox from './components/AvatarBox';
import { QuestionBox } from './components/QuestionBox';
import QuestionButton from './components/QuestionButton';

// "http://localhost:5000"
const socket = io("http://194.226.49.153:5000");

function App() {
  const [id, setId] = useState("");
  const [gameState, setGameState] = useState({
    question: "",
    activePlayer: "",
    players: [],
    idArray: [],
    status: ""
  });
  const [roomNumber, setRoomNumber] = useState("2021");

  function getQuestionLabelText() {
    switch (gameState.status) {
      case "started": return gameState.question;
      case "ended": return "Игра окончена";
      default: return "Ожидание игроков";
    }
  }

  useEffect(() => {
    socket.on("connect", () => {
      console.log("Connected");
      socket.emit("join");
    });

    socket.on("joinAck", (res) => {
      setId(res.id);
      setRoomNumber(res.roomNumber);
      console.log(`Assigned id: ${res.id}`);
      console.log(`Room number: ${res.roomNumber}`);
    });

    socket.on("gameState", (newGameState) => {
      setGameState(newGameState);
      console.log(newGameState);
    });

    socket.on("disconnect", () => {
      console.log("Disconnected from server");
    });

    return () => {
      socket.off("connect");
      socket.off("joinAck");
      socket.off("gameState");
      socket.off("disconnect");
    };
  }, []); 

  function onQuestionButtonClick() {
    var nextPlayer = id;
    while (nextPlayer === id) {
      nextPlayer = gameState.idArray[gameState.idArray.length * Math.random() | 0]
    }
    console.log(`Choose player [${nextPlayer}]`)
    socket.emit("chooseActivePlayer", nextPlayer);
  }

  function onExitButtonCLick() {
    window.location.replace(`http://194.226.49.153:4000/select/${roomNumber}`);
  }

  var isActivePlayer = (id && id === gameState.activePlayer);

  return (
    <div className="app-box">
      <header className="app-header">
        <img className="logo" src={"/logo.png"} alt={"logo"} />
      </header>
      <div className="game-parent">
        <div className="game-box">
          <div className="game-header">
            <OutlinedButton variant="outlined" onClick={onExitButtonCLick}>
              Выйти из игры
            </OutlinedButton>
            <p className="room-number-label">
              {`Комната ${roomNumber}`}
            </p>
            <OutlinedButton variant="outlined">
              Правила
            </OutlinedButton>
          </div>
          <div className="question-parent">
            <QuestionBox 
              elevation={24} 
              sx={{
                position: 'absolute', 
                zIndex: '1', 
                margin: '1em 2em 0 0',
                background: "linear-gradient(150deg, #0142ba, #0073dd)",
              }}
            />
            <QuestionBox 
              elevation={12} 
              sx={{
                position: 'absolute', 
                zIndex: '2', 
                margin: '0.5em 1em 0 0',
                background: "linear-gradient(150deg, #0051e1, #0073dd)",
              }}
            />
            <QuestionButton 
              disabled={!isActivePlayer} 
              text={getQuestionLabelText()}
              onClick={onQuestionButtonClick}
            />
          </div>
          <div className="user-list-parent">
            <List 
              component={Stack} 
              direction="row" 
              sx={{ 
                width: 'fit-content', 
                maxWidth: '100%', 
                bgcolor: 'background.paper',
              }}
            >
              {gameState.players
                .filter((_, index) => { return index < gameState.idArray.length; })
                .map((value, index) => {
                return (
                  <ListItem key={value} spacing={1} >
                    <AvatarBox userName={value} n={index} active={isActivePlayer}/>
                  </ListItem>
                );
              })}
            </List>
          </div>
        </div>
      </div>
    </div>
  );
}

export default App;
