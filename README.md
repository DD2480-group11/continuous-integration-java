# continuous-integration-java

This is a continuous integration github-webhook-server, written in Java. 

## How to start the server

Navigate to the `src` folder. Make sure that the files `jetty-all-7.0.2.v20100331.jar` and `servlet-api-2.5.jar` are in that folder. Then run the following commands:
```
JETTY_VERSION=7.0.2.v20100331
javac -cp servlet-api-2.5.jar:jetty-all-$JETTY_VERSION.jar:javax.mail.jar:jakarta.activation.jar "main/code/ContinuousIntegrationServer.java" "main/code/Functions.java"
java -cp .:servlet-api-2.5.jar:jetty-all-$JETTY_VERSION.jar:javax.mail.jar:jakarta.activation.jar main.code.ContinuousIntegrationServer
```

The first command makes sure the correct version of Jetty is used. The second command compiles the relevant Server files, with some .jar files. The third command starts the server.

## How to get a public URL for your server

Open another window and navigate to the src foler. Make sure the `ngrok` file is in that folder. Start ngrok with the following command.

```
./ngrok http 8011
```

Copy and paste the relevant public URL to a JSON github webhook which activates on push events. 

## Tests

### How to run the tests

Navigate to the `src` folder. Make sure the `junit.jar` and `hamcrest.jar` files are in that folder.
```
JETTY_VERSION=7.0.2.v20100331
javac -cp ".:hamcrest.jar:junit.jar:servlet-api-2.5.jar:jakarta.activation.jar:javax.mail.jar:jetty-all-$JETTY_VERSION.jar" "main/serverTests/Tests.java"
java -cp ".:hamcrest.jar:junit.jar:servlet-api-2.5.jar:jakarta.activation.jar:javax.mail.jar:jetty-all-$JETTY_VERSION.jar" org.junit.runner.JUnitCore "main.serverTests.Tests"

```

The first command makes sure the correct version of Jetty is used. The second line compiles the Tests file. The third line runs the tests with junit. Notice that each `.jar` file is separated by a colon. If more `.jar` files are required, they can be added in a similar fashion.

### How to interepret the results of the tests
If `OK` is printed, then all tests passed. Otherwise, information about the failed test(s) will be printed.
