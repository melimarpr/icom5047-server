#!/bin/bash

echo "==Stopping Webhook=="

cd /home/aerobal/capstone/icom5047-server/play-webhook/target/universal/stage/

#Killing Pid

echo | kill  $(cat RUNNING_PID)

echo "==Webhook Stop=="
