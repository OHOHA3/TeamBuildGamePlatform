#!/bin/sh

# Start backend
cd backend
node server.js &
cd ..

# Start frontend
serve -s ./frontend/build &

# Wait for any process to exit
wait -n

# Exit with status of process that exited first
exit $?