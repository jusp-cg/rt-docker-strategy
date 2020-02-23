#!/bin/bash
LIB_HOME="/home/webapp/dcip/{{env}}/bin"
if  [[ -f ${LIB_HOME}/dcip-main.jar ]];then
    sudo service dcip stop
    sudo chkconfig dcip off
    sudo chkconfig dcip --del
    sudo chmod +x /etc/init.d/dcip
    sudo unlink /etc/init.d/dcip
    sudo unlink ${LIB_HOME}/dcip-main.jar
	rm -Rf /home/webapp/dcip/{{env}}/config/dcipctl.sh
	rm -Rf /home/webapp/dcip/{{env}}/config/application*.properties
fi
