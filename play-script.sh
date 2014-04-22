#!/bin/bash

echo "==Git Hook Trigger=="

#Cd to Folder Aerobal
cd /home/aerobal/capstone/icom5047-server

echo "==Stopping Server=="
#Stop Server 
./stop-aerobal.sh 

echo "==Git Pull=="
#Pull
git pull 

echo "==Start Server=="
#Start Server
./start-aerobal.sh & 

