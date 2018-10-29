#!/bin/bash

cd $(dirname $0)
nohup ./bin/weather-web >> weather.log 2>&1 &
