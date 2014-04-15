#!/bin/bash

echo "==Stopping AeroBal=="

cd /home/aerobal/capstone/icom5047-server/aerobal-server/target/universal/stage/

#Killing Pid

echo | kill  $(cat RUNNING_PID)

echo "==AeroBal Server Stop=="
