#!/bin/bash
LIB_HOME="/home/webapp/dcip/{{env}}/bin"
if [ ! -d "/home/webapp/dcip/{{env}}/config" ];then
	mkdir /home/webapp/dcip/{{env}}/config
fi
mv ${LIB_HOME}/dcipctl.sh /home/webapp/dcip/{{env}}/config/
mv ${LIB_HOME}/application_release.properties /home/webapp/dcip/{{env}}/config/
dcip_jar=$(find ${LIB_HOME} -name "dcip-main-*.jar")
if  [[ -f ${LIB_HOME}/dcip-main.jar ]];then
	rm ${LIB_HOME}/dcip-main.jar
fi
ln -s $dcip_jar ${LIB_HOME}/dcip-main.jar

if [[ ! -f /etc/init.d/dcip ]];then
	sudo ln -s /home/webapp/dcip/{{env}}/config/dcipctl.sh /etc/init.d/dcip
	sudo chmod +x /etc/init.d/dcip
	sudo chkconfig dcip --add
fi
sudo systemctl daemon-reload
sudo service dcip start