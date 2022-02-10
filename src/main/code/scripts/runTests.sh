cd continuous-integration-java/src
JETTY_VERSION=7.0.2.v20100331
java -cp ".:hamcrest.jar:junit.jar:servlet-api-2.5.jar:jakarta.activation.jar:javax.mail.jar:jetty-all-$JETTY_VERSION.jar" org.junit.runner.JUnitCore "main.serverTests.Tests"
