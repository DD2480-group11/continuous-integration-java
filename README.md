# continuous-integration-java

This is a continuous integration github-webhook-server, written in Java.

## How to start the server

Navigate to the `src` folder and run the following commands.
```
JETTY_VERSION=7.0.2.v20100331
javac -cp servlet-api-2.5.jar:jetty-all-$JETTY_VERSION.jar:javax.mail.jar:jakarta.activation.jar "main/code/ContinuousIntegrationServer.java" "main/code/Functions.java"
java -cp .:servlet-api-2.5.jar:jetty-all-$JETTY_VERSION.jar:javax.mail.jar:jakarta.activation.jar main.code.ContinuousIntegrationServer
```

The first command makes sure the correct version of Jetty is used. The second command compiles the relevant Server files, with some .jar files. The third command starts the server.

## How to access the build list

Start the server and visit http://localhost:8011/ in your browser. Here, a build list will be available.

The builds are sorted by commit timestamps in descending order (most recent one at the top). If you click on the links you will get more specific test results. The builds test results are saved as text files in the folder `src/main/builds`. Their filenames are a combination of their commit timestamps and their commit hashes.

## How to get a public URL for your server

Navigate to a folder where you have the `ngrok` file. ngrok download instructions are available [here](https://github.com/KTH-DD2480/smallest-java-ci). Start ngrok with the following command.

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

###Compilation and testexecution unittesting
Navigate to the `src` folder. Make sure the `junit.jar` and `hamcrest.jar` files are in that folder.
```
JETTY_VERSION=7.0.2.v20100331
javac -cp ".:hamcrest.jar:junit.jar:servlet-api-2.5.jar:jakarta.activation.jar:javax.mail.jar:jetty-all-$JETTY_VERSION.jar" "main/mockTesting/featureTests.java" "main/mockTesting/mockTests.java"
java -cp ".:hamcrest.jar:junit.jar:servlet-api-2.5.jar:jakarta.activation.jar:javax.mail.jar:jetty-all-$JETTY_VERSION.jar" org.junit.runner.JUnitCore "main.mockTesting.featureTests"

```

### How to interepret the results of the tests
If `OK` is printed, then all tests passed. Otherwise, information about the failed test(s) will be printed.
