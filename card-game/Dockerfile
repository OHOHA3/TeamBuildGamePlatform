# Fetching the latest node image on apline linux
FROM node:alpine

# Setting up the work directory
WORKDIR /app

ENV GAME_ENV="production"

# Setting up backend
RUN mkdir backend
COPY ./game-server/package.json ./backend/
RUN npm --prefix ./backend install
COPY ./game-server/. ./backend/

# Setting up frontend
RUN mkdir frontend
COPY ./game-client/package.json ./frontend/
RUN npm --prefix ./frontend install
RUN npm install -g serve
COPY ./game-client/. ./frontend/
RUN npm --prefix ./frontend run build

EXPOSE 3000
EXPOSE 5000 

COPY ./runner.sh .
RUN chmod +x runner.sh

CMD ./runner.sh
