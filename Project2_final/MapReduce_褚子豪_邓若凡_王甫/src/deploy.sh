#!/bin/bash
t1=$(date +%s%N)
if [ $1 = "compile" ]
then
	javac SLAVE.java
	jar -cvfm SLAVE.jar META-INF/MANIFEST_SLAVE.MF SLAVE.class
	javac MASTER.java
	jar -cvfm MASTER.jar META-INF/MANIFEST_MASTER.MF MASTER.class MASTER\$1.class MASTER\$Word.class MASTER\$Record.class
	javac BASIC.java
	jar -cvfm BASIC.jar META-INF/MANIFEST_BASIC.MF BASIC.class BASIC\$1.class BASIC\$Word.class BASIC\$Record.class
	rm *.class
elif [ $1 = "basic" ]
then
	java -jar BASIC.jar $2
elif [ $1 = "master" ]
then
	sudo docker --version

	# distribute
	java -jar MASTER.jar $2

	# prepare worker
	for i in `seq 3`
	do
	{
		sudo docker start worker$i
		sudo docker cp Sx/Sx$i.txt worker$i:/tmp
		sudo docker exec -i worker$i /bin/bash -c "cd tmp && mkdir -p SM SS"
		sudo docker cp SLAVE.jar worker$i:/tmp
	}&
	done
	wait

	# map
	for i in `seq 3`
	do
	{
		sudo docker exec -i worker$i /bin/bash -c "source ~/.bashrc && cd tmp && java -jar SLAVE.jar $i 1"
	}&
	done
	wait

	# reduce
	mkdir -p RM
	for i in `seq 3`
	do
	{
		sudo docker exec -i worker$i /bin/bash -c "source ~/.bashrc && cd tmp && java -jar SLAVE.jar $i 2"
		sudo docker cp worker$i:/tmp/SS/RM$i.txt RM
	}&
	done
	wait

	# collect
	java -jar MASTER.jar
fi
t2=$(date +%s%N)
echo `expr $t2 / 1000000 - $t1 / 1000000` "ms"