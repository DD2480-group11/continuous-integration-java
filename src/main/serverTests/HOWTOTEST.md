## Tests

### How to run the tests

Navigate to the `src` folder. Add the `hamcrest.junit` and `hamcrest.jar` files to that folder.
```
JETTY_VERSION=7.0.2.v20100331
javac -cp ".:hamcrest.jar:junit.jar:servlet-api-2.5.jar:jetty-all-$JETTY_VERSION.jar" "main/serverTests/Tests.java"
java -cp ".:hamcrest.jar:junit.jar:servlet-api-2.5.jar:jetty-all-$JETTY_VERSION.jar" org.junit.runner.JUnitCore "main.serverTests.Tests"
```

The first line compiles the Tests file. The second line runs the tests with junit. Notice that each `.jar` file is separated by a colon. If more `.jar` files are required, they can be added in a similar fashion.

### How to interepret the results of the tests
If `OK` is printed, then all tests passed. Otherwise, information about the failed test(s) will be printed.