## How to start the server

Navigate to the `src` folder. Make sure that the files `jetty-all-7.0.2.v20100331.jar` and `servlet-api-2.5.jar` are present in that folder. Then run the following commands:
```
JETTY_VERSION=7.0.2.v20100331
javac -cp servlet-api-2.5.jar:jetty-all-$JETTY_VERSION.jar "main/code/ContinuousIntegrationServer.java" "main/code/Functions.java"
java -cp .:servlet-api-2.5.jar:jetty-all-$JETTY_VERSION.jar main.code.ContinuousIntegrationServer
```

The first command makes sure the correct version of Jetty is used. The second command compiles the relevant Server files, with some .jar files. The third command starts the server.