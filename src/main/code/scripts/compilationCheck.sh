cd continuous-integration-java/src
JETTY_VERSION=7.0.2.v20100331
if javac -cp servlet-api-2.5.jar:jetty-all-$JETTY_VERSION.jar:javax.mail.jar:jakarta.activation.jar "main/code/ContinuousIntegrationServer.java" "main/code/Functions.java" "main/code/SendEmail.java"; then
    echo "success"
else
    echo "fail"
fi