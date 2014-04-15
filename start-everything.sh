#!/bin/bash

#Cd to folder
echo "Everything is Awesome!!!!"
cd /home/aerobal/capstone/icom5047-server

#start aerobal
./start-aerobal.sh &

echo "==AeroBal Started=="
#start webhook
./start-webhook.sh &
