#!/bin/bash
#
BootDir=$(cd `dirname $0`; pwd)
Boot_JAVA_HOME=/data/jdk
Boot_Jar=movieCalendar-0.0.1-SNAPSHOT.jar
Boot_Class_Args=
Jvm_Args="-server -verbose:gc -Xloggc:logs/gc.log -Xms64m -Xmx2048m -XX:+UseConcMarkSweepGC -XX:+UseParNewGC"
LogFile=$BootDir/console.out

if [ "$Boot_JAVA_HOME" != "" ]; then
  JAVA_HOME=$Boot_JAVA_HOME
fi
JAVA_EXEC=$JAVA_HOME/bin/java

CLASSPATH=$JAVA_HOME/lib/tools.jar:$BootDir/bin:$BootDir/lib
# add libs to CLASSPATH
for f in $BootDir/lib/*.jar; do
  CLASSPATH=${CLASSPATH}:$f;
done
rm -rf nohup.out

# run it

echo Using JAVA_HOME: $JAVA_HOME
echo Using CLASSPATH: $CLASSPATH
echo Using Jvm_Args: $Jvm_Args
echo Using Boot_Jar: $Boot_Jar
echo Using Boot_Class_Args: $Boot_Class_Args

# log config
if [ "$LOG_DIR" = "" ]; then
  LOG_DIR="$BootDir/logs"
fi
if [ "$LOG_FILE" = "" ]; then
  LOG_FILE='run.log'
fi

LOG_OPTS="-Dlog.dir=$LOG_DIR -Dlog.file=$LOG_FILE"

num=0
while [ $num -lt 1 ]
do
if [ -f "error" ];then
num=1
else
nohup "$JAVA_EXEC" $Jvm_Args $LOG_OPTS -classpath "$CLASSPATH" -jar $Boot_Jar $Boot_Class_Args>$LogFile &
num=`expr $num + 1`
sleep 2
fi
done