#!/bin/bash

#Cd to folder
cd /home/mmarquez/capstone/

#start aerobal
./start-aerobal.sh &

echo "==AeroBal Started=="
#start webhook
./start-webhook.sh &
