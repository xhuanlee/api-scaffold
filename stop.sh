# stop current.pid

rootPath=/appdeploy/api-scaffold
pid=$(cat $rootPath/current.pid)

if [ ! -n "$pid" ]; then
    echo "current.pid not found, exit"
    exit 1
fi

kill -9 $pid

echo "current.pid killed"

rm -rf $rootPath/current.pid

exit
