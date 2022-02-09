## Tests

### How to run the tests

Navigate to the `src` folder
```
JETTY_VERSION=7.0.2.v20100331
javac -cp ".:hamcrest.jar:junit.jar" "main/serverTests/Tests.java"
java -cp ".:hamcrest.jar:junit.jar" org.junit.runner.JUnitCore "main.serverTests.Tests"
```

### How to interepret the results of the tests
If `OK` is printed, then all tests passed. Otherwise, information about the failed test(s) will be printed.