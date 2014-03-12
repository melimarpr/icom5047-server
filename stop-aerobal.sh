#!/bin/bash

echo "==Stopping AeroBal=="

cd /home/mmarquez/capstone/aerobal-server/target/universal/stage/

#Killing Pid

echo | kill  $(cat RUNNING_PID)

echo "==AeroBal Server Stop=="
