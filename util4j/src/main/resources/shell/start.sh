#!/bin/bash
#
#jvm参数备注
#-XX:+UseParallelGC  关注吞吐量
#-XX:+UseParNewGC 关注响应时间
#1.吞吐量优先的并行收集器
#java -Xmx3800m -Xms3800m -Xmn2g -Xss128k -XX:UseParallelGC -XX:ParallelGCThreans = 20
#-Xmx3800m:最大堆大小
#-Xms3800m:初始堆大小,此值可以设置与-Xmx相同，以避免每次垃圾回收完成后JVM重新分配内存。
#-Xmn2g: 设置年轻代大小为2G。整个JVM内存大小=年轻代大小 + 年老代大小 + 持久代大小。持久代一般固定大小为64m，所以增大年轻代后，将会减小年老代大小。此值对系统性能影响较大，Sun官方推荐配置为整个堆的3/8。
#-Xss128k:设置每个线程的堆栈大小。JDK5.0以后每个线程堆栈大小为1M,以前每个线程堆栈大小为256K。
#-XX:+UseParallelGC:选择垃圾收集器为并行收集器。此配置仅对年轻代有效。即该配置下，年轻代使用并发收集，而年老代仍旧使用串行收集。
#-XX:ParallelGCThreans = 20：配置并行收集器的线程数，即：同时多少个线程一起进行垃圾回收。此值的配置最好与处理器数目相等。
#其它配置
#-XX:+UseParallelOldGC:配置老年代垃圾收集器为并行收集。JDK6.0支持对老年代并行收集。
#-XX:MaxGCPauseMillis = 100:设置每次年轻代垃圾回收的最长时间，如果无法满足此时间，JVM会自动调整年轻代大小，以满足此值。
#-XX:UseAdaptiveSizePolicy = 100:设置此项以后，并行收集器会自动选择年轻代大小和相应的Surivior区比例，以达到目标系统规定的最低响应时间或者收集频率等，此值建议使用并行收集器时一直打开。
#2.相应时间优先并发收集器
#java -Xmx3550m -Xmm3550m -Xmn2g -Xss128K -XX:ParallelGCThread = 20 -XX:+UseConcMarkSweepGC -XX:+UseParNewGC
#-XX:+UseConcMarkSweepGC：设置年老代为并发收集。
#-XX:+UseParNewGC：设置年轻代为并行收集。可以和CMS收集同时使用。JDK5.0以上，JVM会根据系统配置自行配置，所以无需再配置此值。
#java -Xmx3550m -Xms3550m -Xmn2g -Xss128k -XX:+UseConcMarkSweepGC -XX:CMSFULLGCsBeforCompaction=5 -XX:+UseCMSCompactAtFullCollection
#-XX:CMSFULLGCsBeforCompaction=5:由于并发收集器不对内粗空间进行压缩、整理，所以运行一段时间会产生“碎片”，使得运行效率低。此值设置运行多少次GC以后对内训空间进行压缩、整理。
#-XX:+UseCMSCompactAtFullCollection：打开对年老代的压缩。可能会影响性能，但是可以消除碎片。
#

BootDir=$(cd `dirname $0`; pwd)
Boot_JAVA_HOME=/data/jdk
Boot_Class=net.jueb.xx.MainClass
Boot_Class_Args=
Jvm_Args="-server -verbose:gc -Xloggc:logs/gc.log -Xms256m -Xmx1024m -XX:+UseConcMarkSweepGC -XX:+UseParNewGC"
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
echo Using Boot_Class: $Boot_Class
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
nohup "$JAVA_EXEC" $Jvm_Args $LOG_OPTS -classpath "$CLASSPATH" $Boot_Class $Boot_Class_Args>$LogFile &
num=`expr $num + 1`
sleep 2
fi
done