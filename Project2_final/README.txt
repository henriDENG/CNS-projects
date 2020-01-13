DEMO ( on Ubuntu )
=====================================================================

Preparations need to be done in advance. ( ref.  Report section "How to build/run" )

########################  Pull image needed or Use an image with java and scp service

docker image
$: sudo docker pull yiping999/ub-jdk8:1.1

########################  Commands and Usage

compile BASIC.jar MASTER.jar SLAVE.jar
$: sh deploy.sh compile

run BASIC.jar on a given directory
$: sh deploy.sh basic dirName

run MASTER.jar on a given directory
$: sh deploy.sh master dirName

delete intermediate files inside containers
$: sh clean.sh container

delete intermediate and final files on the host
$: sh clean.sh host

stop all containers
$: sh clean.sh stop

########################  Test files

folder: test ( 117B )
        test2 ( 8.2MB )
        corpus ( 193.8MB )
