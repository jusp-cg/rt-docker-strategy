#!/bin/sh
# chkconfig: - 64 36
# Default-Start: 2 3 4 5
# Default-Stop: 0 1 2 3 4 6
# Required-Start:
# pidfile: none
# lockfile: /var/lock/subsys/example
# Source function library.
#source /etc/rc.d/init.d/functions
#source /etc/init.d/functions

# Source networking configuration.
source /etc/sysconfig/network

# Check that networking is up.
[ "$NETWORKING" = "no" ] && exit 0

USER="webapp"
APPNAME="dcip"
APPBIN="/home/webapp/dcip/{{env}}/bin/dcip-main.jar"
APPARGS="java -Xmx12g -jar"
APPPROP="/home/webapp/dcip/{{env}}/config/application_release.properties"
LOGPATH="/home/webapp/dcip/{{env}}/log"
LOGFILE="$LOGPATH/dcip-main.log"
LOCKFILE="/var/lock/subsys/$APPNAME"
LOGPATH=$(dirname $LOGFILE)
APPDAGENT="-javaagent:/users/appdynamics/appagent/javaagent.jar"
APPDAPP="-Dappdynamics.agent.applicationName=DCIP"
APPDTIER="-Dappdynamics.agent.tierName=Tomcat"
APPDNODE="-Dappdynamics.agent.nodeName=x123456"
APPDLOGS="-Dappdynamics.agent.logs.dir=$LOGPATH"

# Start with empty APPD parameters, only set if APPD dictionary flag is set to 'true'
APPDARGS=""
if [[ "{{appd.enable}}" == "true" ]]; then
        APPDARGS="$APPDAGENT $APPDAPP $APPDTIER $APPDNODE $APPDLOGS"
fi

start() {
        [ -x $prog ] || exit 5
        [ -d $LOGPATH ] || mkdir $LOGPATH
        [ -f $LOGFILE ] || touch $LOGFILE

        echo -n -e $"Starting $APPNAME:\n"
        PID=$(ps -ef | grep "${LIB_HOME}/dcip-main.jar"| grep -v grep | awk '{print $2}')
        if [[ $PID != "" ]];then
                echo -n -e "dcip service already running (PID:$PID) \n"
                exit
        fi
#       daemon --user=$USER "$APPARGS $APPBIN --spring.config.location=file:$APPPROP > $LOGFILE &"
		chown $USER:$USER $LOGFILE
		chmod 775 $LOGFILE
        su $USER -c "$APPARGS $APPDARGS $APPBIN --spring.config.location=file:$APPPROP > $LOGFILE &"
#       su $USER -c "$APPARGS $APPBIN > $LOGFILE &"
        for i in {1..20}
		do
            sleep 5
            grep "Started Application in" $LOGFILE
            if [[ $? == "0" ]]; then
                echo -n -e "process started successfully \n"
                PID=$(ps -ef | grep "${LIB_HOME}/dcip-main.jar"| grep -v grep | awk '{print $2}')
                echo -n -e "dcip service PID:$PID \n"
                RETVAL=$?
                #echo -n $RETVAL
                [ $RETVAL -eq 0 ] && touch $LOCKFILE
                flag=1
                exit 0
            fi
        done
        if [[ $flag -ne "1" ]]; then
                awk '/Error/' $LOGFILE
                echo -n -e "\n failed to start service, refer to $LOGFILE for more details \n"
                exit 1
        fi
}

stop() {
       PID=$(ps -ef | grep "${LIB_HOME}/dcip-main.jar"| grep -v grep | awk '{print $2}')
       if [[ $PID == "" ]]; then
           echo -n -e "no service is running \n"
           return
		   #exit
       fi
       echo -n -e "Stopping dcip service (PID:$PID) \n"
       kill -9 $PID
       sleep 3
       PID=$(ps -ef | grep "${LIB_HOME}/dcip-main.jar"| grep -v grep | awk '{print $2}')
       if [[ $PID == "" ]]; then
           echo -n -e "dcip service stopped \n"
           RETVAL=$?
           echo -n -e "Lockfile deleted \n"
           [ $RETVAL -eq 0 ] && rm -f $LOCKFILE
           #return $RETVAL
       else
           echo -n -e "dcip service failed to stop (PID:$PID) \n"
		   exit 1
       fi
}

status() {
         PID=$(ps -ef | grep "${LIB_HOME}/dcip-main.jar"| grep -v grep | awk '{print $2}')
         if [[ $PID == "" ]]; then
             echo -n -e "no service is running \n"
         else
             echo -n -e "service is running (PID:$PID) \n"
         fi
}

restart() {
        stop
        start
}

rh_status() {
        status $prog
}

rh_status_q() {
        rh_status >/dev/null 2>&1
}

case "$1" in
        start)
              # rh_status_q && exit 0
                $1
        ;;
        stop)
               # rh_status_q || exit 0
                $1
        ;;
        restart)
                $1
        ;;
        status)
               # rh_status
                $1
        ;;
        *)
                echo $"Usage: $0 {start|stop|status|restart}"
                exit 2
esac
