#!/bin/bash

echo "==Starting AeroBal Server=="


cd /home/mmarquez/capstone/aerobal-server/target/universal/stage/bin/

#Run Command
./aerobal-server -Dhttp.port=9000

echo "==Starting AeroBal Server"
