# bin/bash
# usage: ./start.sh mobile-push-${version}.jar
# start mobile-push-1.0.jar
# log file dir (/var/log/tomcat/mobile-push)

rootPath=/appdeploy/api-scaffold
#jar_lib
out_jar_lib=$rootPath/lib
config_path=$rootPath/config/application-prod.yml
# jar file name
jarfile=$(ls $rootPath/*.jar);

if [ -z "$jarfile" ]; then
    echo "no jar file found, exit"
    exit 0
fi

echo "starting "$jarfile

# start jar
nohup java -server -Xms64m -Xmx512m -XX:MaxDirectMemorySize=256m -Dloader.path=$out_jar_lib -Djava.io.tmpdir=/var/tmp -jar $jarfile >$rootPath/nohup.out &

# write pid into file current.pid
echo $! > $rootPath/current.pid

echo $jarfile" is running"
exit
