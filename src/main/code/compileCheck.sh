JETTY_VERSION=7.0.2.v20100331
if javac -cp servlet-api-2.5.jar:jetty-all-$JETTY_VERSION.jar "main/code/ContinuousIntegrationServer.java" "main/code/Functions.java"; then
    echo "yes"
else
    echo "no"
fi