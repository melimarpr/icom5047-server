#!/bin/bash

echo "==Stopping Webhook=="

cd /home/mmarquez/capstone/play-webhook/target/universal/stage/

#Killing Pid

echo | kill  $(cat RUNNING_PID)

echo "==Webhook Stop=="
