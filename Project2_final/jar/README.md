# Map-Reduce

## docker image

`sudo docker pull yiping999/ub-jdk8:1.1`

## disable SSH check

it seems this command doesn't work as expected  
`ssh -o "StrictHostKeyChecking=no" worker1`

## usage

compile BASIC.jar MASTER.jar SLAVE.jar  
`sh deploy.sh compile`

run BASIC.jar on a given directory  
`sh deploy.sh basic dirName`

run MASTER.jar on a given directory  
`sh deploy.sh master dirName`

delete intermediate files inside containers  
`sh clean.sh container`

delete intermediate and final files on the host  
`sh clean.sh host`

stop all containers  
`sh clean.sh stop`
