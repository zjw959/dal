# -*- coding: utf-8 -*-

import os
import subprocess
import check

PID_FILE = 'server.pid'
JVM_MEMORY = 1024
RUN_COMMAND = "nohup java -server" \
              " -Xms" + str(JVM_MEMORY) +"m" \
              " -Xmx" + str(JVM_MEMORY) + "m" \
              " -Xmn" + str(JVM_MEMORY/2) + "m"\
              " -XX:MaxDirectMemorySize=" + str(int(JVM_MEMORY/110) if int(JVM_MEMORY/100) > 512 else 512) + "m"\ 
              " -XX:+UseConcMarkSweepGC " \
              " -XX:+UseCMSCompactAtFullCollection" \
              " -XX:CMSFullGCsBeforeCompaction=3" \
              " -XX:+UseParNewGC " \
              " -XX:SurvivorRatio=8" \
              " -XX:TargetSurvivorRatio=90" \
              " -verbose:gc " \
              " -XX:+PrintGC -XX:+PrintGCDateStamps" \
              " -XX:+PrintGCDetails " \
              " -XX:+PrintGCApplicationStoppedTime" \
              " -Xloggc:logs/gc.log" \
              " -jar @start@.jar &"

def runServer():
    os.environ["PATH"] += ":/usr/java/jdk1.8.0_92"
    envcp = os.environ.copy()
    envcp["JAVA_HOME"] = "/usr/java/jdk1.8.0_92"
    envcp["PATH"] += ":/usr/java/jdk1.8.0_92/bin"
    #subprocess.Popen("java -version", env = envcp, shell=True)
    print(str(RUN_COMMAND))
    subprocess.Popen(RUN_COMMAND, env = envcp, shell=True)

def main():
    pid = check.getProcessPid();
    print(pid)
    if (check.processorExist(pid) == True):
        print("server running, pid:" + str(pid))
    else:
        runServer()
        print("server start" )


#" -XX:+AggressiveOpts -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=9850" \


if __name__ == '__main__':
    main()
