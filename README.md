## How to start the server

Navigate to the `src` folder. Download the `jetty-all-7.0.2.v20100331.jar` and `servlet-api-2.5.jar` files to that folder.
```
JETTY_VERSION=7.0.2.v20100331
javac -cp servlet-api-2.5.jar:jetty-all-$JETTY_VERSION.jar "main/code/ContinuousIntegrationServer.java" "main/code/Functions.java"
java -cp .:servlet-api-2.5.jar:jetty-all-$JETTY_VERSION.jar main.code.ContinuousIntegrationServer
```