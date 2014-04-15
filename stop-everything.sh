#!/bin/bash

#Cd to folder
echo "Everything is Awesome!!!!"
cd /home/aerobal/capstone/icom5047-server/

#start aerobal
./stop-aerobal.sh 

#start webhook
./stop-webhook.sh 

echo "==Killing Everythin=="
