#!/bin/sh

basedir=$(cd "$(dirname "$0")"; pwd)
cd ${basedir}

#JAVA_HOME=/usr/lib/jvm/java-1.7.0-openjdk.x86_64
#JAVA_HOME=/usr/lib/jvm/java-1.7.0-openjdk-1.7.0.75.x86_64
SERVER_NAME=`pwd`/run.pid

#Safe close server servlet linsten port
#CLOSE_WEB_LISTEN_PORT=7070
CLOSE_WEB_LISTEN_PORT=`cat ${basedir}/conf/application.properties |grep server.port |awk -F '=' '{print $2}'|sed 's/^[[:space:]]\+//'|sed 's/\r//'`
CLOSE_WEB_URL="http://127.0.0.1:${CLOSE_WEB_LISTEN_PORT}/dal/closeGame?validateCode=abf0d19cbb424f4cb0ef812a814c335d"
#CLOSE_WEB_URL="http://127.0.0.1:${CLOSE_WEB_LISTEN_PORT}/closeGame"



#标准输出和标准错误输出
#STDOUT=`pwd`/stdout.log
#STDERR=`pwd`/stderr.log
STDOUT=/dev/null
STDERR=/dev/null

#参数
ARGS=application.properties

#运行的主名，写类的全称，即包括包名
CLASS_NAME=com.Application

#pid文件路径
PID=`pwd`/run.pid

LIB=`pwd`/lib

#类包的路径
JARS=(`ls lib`)
DIRNUM=${#JARS[@]}

CLASS_PATH=./:./conf

index=0
while [ $index -lt $DIRNUM ]
do
  CLASS_PATH=$CLASS_PATH:`pwd`/${JARS[$index]}
  let "index= $index + 1"
done

JAVA_OPTS="-Xmx4096m -Xms512m -Xss256k -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=./oom.hprof -XX:+UnlockExperimentalVMOptions -XX:+UseG1GC -XX:MaxGCPauseMillis=50 -XX:GCPauseIntervalMillis=200 -XX:SurvivorRatio=6"

#程序运行环境
#export LANG=zh_CN
#export LC_ALL=zh_CN

#=============================================================================
start()
{
if test -e $PID
        then
                echo
                echo The $SERVER_NAME Server already Started!
                echo
        else
                echo
                echo Start The $SERVER_NAME Server....
                echo 
                echo $CLASS_PATH
                java -Djava.nio.channels.spi.SelectorProvider=sun.nio.ch.EPollSelectorProvider $JAVA_OPTS -classpath $CLASS_PATH $CLASS_NAME $ARGS >system.out 2>&1 &
                echo $!>$PID
                sleep 2
                STATUS=`ps -p $!|grep java |awk '{print $1}'`
                if test $STATUS
                        then
                                echo The $SERVER_NAME Server Started!
                                echo
                        else
                                rm $PID
                echo The $SERVER_NAME Server Start Failed
                                echo please Check the system
                                echo
                fi
fi
}

stop()
{
if test -e $PID
        then
                TPID=`cat $PID`
                echo
        
        STATUS=`ps -p $TPID |grep java | awk '{print $1}'`
                if test $STATUS 
        then
            echo Safe Stop The $SERVER_NAME Server [${TPID}]....            
        else
            echo "Server is already stoped."
            rm $PID
            return  
        fi

                echo
                
                wget ${CLOSE_WEB_URL} -t 1 -O safe_close.html

                closeResult=`cat safe_close.html`

                echo "wget result : ${closeResult} "

                SUCCESS_TEXT="OK" 

                if [ "$closeResult" = "$SUCCESS_TEXT" ]; then
                    sleep 2
                    while test `ps -p $TPID |grep java | awk '{print $1}'`
                    do
                        echo "waiting for server close proccess job......."
                        sleep 5
                    done
    
                    echo The $SERVER_NAME Server Stoped
                    echo
                    rm $PID
                else
                    echo "close request fail : ${closeResult}"
                fi

                
        else
                echo
                echo The $SERVER_NAME Server already Stoped!
                echo
fi
}


_kill()
{
if test -e $PID
        then
                echo
                echo Kill The $SERVER_NAME Server....
                echo
                TPID=`cat $PID`
                kill -9 $TPID
                sleep 1
                STATUS=`ps -p $TPID |grep java | awk '{print $1}'`
                if test $STATUS
                        then
                                echo The $SERVER_NAME Server NOT Stoped!
                                echo please Check the system
                                echo
                        else
                                echo The $SERVER_NAME Server Stoped
                                echo
                                rm $PID
                fi
        else
                echo
                echo The $SERVER_NAME Server already Stoped!
                echo
fi
}

status()
{
echo
if test -e $PID
        then
                TPID=`cat $PID`
                STATUS=`ps -p $TPID|grep java | awk '{print $1}'`
                if test $STATUS
                        then
                                echo "The $SERVER_NAME Server Running($TPID)!"
                                echo
                        else
                                echo The $SERVER_NAME Server NOT Running!
                                rm $PID
                                echo
                fi
        else
                echo
                echo The $SERVER_NAME Server NOT Running!
                echo
fi
}

case "$1" in
'start')
                start
        ;;
'stop')
                stop
        ;;
'kill')
                _kill
        ;;
'restart')
        stop
        start
    ;;
'status')
                status
        ;;
*)
        echo
        echo
        echo "Usage: $0 {status | start | stop | kill}"
        echo
        echo Status of $SERVER_NAME Servers ......
                status
        ;;
esac
exit 0


