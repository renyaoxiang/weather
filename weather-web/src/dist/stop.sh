#!/bin/bash

port=9900

function stop(){
	kill -9 $(netstat -nlp | grep :9900 | awk '{print $7}' | awk -F"/" '{ print $1 }')
}
stop
