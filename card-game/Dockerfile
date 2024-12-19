# Fetching the latest node image on apline linux
FROM node:alpine

# Setting up the work directory
WORKDIR /app

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
RUN npm --prefix ./frontend run build
COPY ./game-client/. ./frontend/

EXPOSE 3000

# Starting backend
RUN node /backend/server.js &

# Starting frontend
CMD serve -s ./frontend/build
