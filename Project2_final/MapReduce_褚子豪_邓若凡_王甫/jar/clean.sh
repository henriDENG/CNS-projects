#!/bin/bash
sudo docker --version

if [ $1 = "container" ]
then
	echo "delete container files"
	for i in `seq 3`
	do
	{
		sudo docker start worker$i
		sudo docker exec -i worker$i /bin/bash -c "cd tmp && rm -r SLAVE.jar Sx$i.txt SM SS"
	}&
	done
	wait
elif [ $1 = "host" ]
then
	echo "delete host files"
	rm -rf Sx RM result.txt
elif [ $1 = "stop" ]
then
	echo "stop containers"
	for i in `seq 3`
	do
	{
		sudo docker stop worker$i
	}&
	done
	wait
fi