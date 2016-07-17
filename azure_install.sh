#!/bin/bash
set -e
#Dirty script to deploy on azure. Should replace with ansible and or docker build



echo "install deps"

	sudo yum -y install git maven tomcat  httpd mod_ssl mod_proxy_html

	sudo  systemctl enable httpd
	sudo  systemctl enable tomcat
	sudo  systemctl enable firewalld

	sudo  systemctl start firewalld

echo "setup firewall"

	sudo firewall-cmd --zone=public --add-port=80/tcp --permanent
	sudo firewall-cmd --zone=public --add-port=443/tcp --permanent
	sudo firewall-cmd --reload


echo "setup reverse proxy"

cat << EOF > /tmp/reverse-proxy.conf

ProxyRequests Off
ProxyPass / http://localhost:8080/ connectiontimeout=5 timeout=30
ProxyPassReverse / http://localhost:8080/

EOF

	sudo cp /tmp/reverse-proxy.conf /etc/httpd/conf.d/reverse-proxy.conf

	sudo cp /usr/share/doc/httpd-2.4.6/proxy-html.conf /etc/httpd/conf.d/

	sudo setsebool -P httpd_can_network_connect 1



echo "getting latest version of app"

	export WORKING_DIR=$(mktemp -d)


	cd ${WORKING_DIR}
	git clone https://github.com/awalker125/training.git

	cd training
	mvn clean install

	sudo cp ${WORKING_DIR}/training/target/training.war /var/lib/tomcat/webapps/training.war
	sudo chown root:tomcat /var/lib/tomcat/webapps/training.war
	sudo chmod 750 /var/lib/tomcat/webapps/training.war

	rm -rf ${WORKING_DIR}

	sudo  systemctl start httpd
	sudo  systemctl start tomcat