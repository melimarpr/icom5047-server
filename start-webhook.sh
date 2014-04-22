#!/bin/bash

echo  "==Start Play WebHook=="

cd /home/aerobal/capstone/icom5047-server/play-webhook

play stage


cd /home/aerobal/capstone/icom5047-server/play-webhook/target/universal/stage/bin/

./play-webhook -Dhttp.port=9005

echo "==Started Webhook=="
