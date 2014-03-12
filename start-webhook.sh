#!/bin/bash

echo  "==Start Play WebHook=="

cd /home/mmarquez/capstone/play-webhook/target/universal/stage/bin/

./play-webhook -Dhttp.port=9005

echo "==Started Webhook=="
