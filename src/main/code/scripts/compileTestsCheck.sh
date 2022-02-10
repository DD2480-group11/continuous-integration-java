cd continuous-integration-java/src
JETTY_VERSION=7.0.2.v20100331
if javac -cp ".:hamcrest.jar:junit.jar:servlet-api-2.5.jar:jakarta.activation.jar:javax.mail.jar:jetty-all-$JETTY_VERSION.jar" "main/serverTests/Tests.java"; then
    echo "success"
else
    echo "fail"
fi