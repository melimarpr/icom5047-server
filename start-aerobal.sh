#!/bin/bash

echo "==Starting AeroBal Server=="

cd /home/aerobal/capstone/icom5047-server/aerobal-server/

play stage

cd /home/aerobal/capstone/icom5047-server/aerobal-server/target/universal/stage/bin/

#Run Command
./aerobal-server -Dhttp.port=9000

echo "==AeroBal Started=="
