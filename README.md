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

## How to set up the webhook

Navigate to a folder where you have the `ngrok` file. ngrok download instructions and more specific webhook instructions are available [here](https://github.com/KTH-DD2480/smallest-java-ci). Start ngrok with the following command.

```
./ngrok http 8011
```

Copy and paste the relevant public URL to a JSON github webhook which activates on push events.

## Feature implementation and testing

When a new commit has been pushed to github, the branch of the commit will be cloned by the server. The server will try to compile the server code and test code, and run the tests. The compilation and test results are printed in the terminal and sent via email to the committer, and then finally saved as a text file in `src/main/builds` (the file is used for the build list). The subject line in the email will clearly convey if everything worked correctly.

### Compilation
The compilation has been implemented with the use of shell scripts that compile a specific set of files. The script that contains the files that are to be compiled is passed to the `compilationCheck()` method by the server. compilationCheck() then returns *”success”* if the code compiled successfully or *“fail”* if this was not the case. Compilation has been unittested with the help of mock testing, where the `compilationCheck()` method is asked to compile two trivial files.

### Test execution
The test execution has been implemented with the use of shell scripts that run a specific test file with junit. The script that contains the test file that is to be run is passed to the `runTests()` method by the server. The complete results of the tests are then passed back to the server and analysed to determine whether they passed or failed. Test execution has been unittested with the help of mock testing, where the `runTests()` method is asked to run a small set of trivial tests.

### Email notification
The email notification has been implemented using Gmail SMTP to send an email from a Gmail email address exclusive to the server to a given receiver. The unit-test is checking whether the receiver's email address is in a valid format or not.

## Test

### How to run the tests

If you want to run the tests manually, you can do the following. Navigate to the `src` folder. Run the following command in order to compile all tests.

```
JETTY_VERSION=7.0.2.v20100331
javac -cp ".:hamcrest.jar:junit.jar:servlet-api-2.5.jar:jakarta.activation.jar:javax.mail.jar:jetty-all-$JETTY_VERSION.jar" "main/serverTests/Tests.java" "main/mockTesting/mockTests.java" "main/mockTesting/featureTests.java"
```

The following command runs the same tests that the server itself executes automatically when it receives a new commit. I.e. the tests for the server code.
```
java -cp ".:hamcrest.jar:junit.jar:servlet-api-2.5.jar:jakarta.activation.jar:javax.mail.jar:jetty-all-$JETTY_VERSION.jar" org.junit.runner.JUnitCore "main.serverTests.Tests"
```

The following command executes tests that validate the compilation and test execution features via mock testing. These tests are not run by the server itself.
```
java -cp ".:hamcrest.jar:junit.jar:servlet-api-2.5.jar:jakarta.activation.jar:javax.mail.jar:jetty-all-$JETTY_VERSION.jar" org.junit.runner.JUnitCore "main.mockTesting.featureTests"

```

### How to interpret the results of the tests
If `OK` is printed, then all tests passed. Otherwise, information about the failed test(s) will be printed.

## Statement of contributions

Magnus made the initial compilation checks, and the webpage with a log of previous builds. He also made the initial setup for running bash scripts. Marcus made the test execution along with mock testing for both compilation and test execution as well as added build status to the email in the subject. Beatrice made the send email functionality, added javadocs for most of the methods and classes and made the javadoc browsable.

Each person made tests for their respective contributions.

## Assessment of Teams Alpha

We think we are in the state Performing. We worked well as a team, and used branches and issues effectively, which gave us a very clear log of commit messages. Since we decided the overall structure of the project from the beginning, we managed to avoid backtracking.

One of the points which could be seen as controversial is the one regarding wasted work. For example, we were unsure if we actually needed to add mock testing, however we decided to do that regardless. Perhaps we should have asked the TA’s more questions when we were unsure about a requirement.

In order to get to the next state, Adjourned, we could try to be more specific in dividing up the work. When we initially divided up the work, we could have made smaller and more specific issues, instead of a few big ones.

## How to access the build list (P+ feature)

Start the server and visit http://localhost:8011/ in your browser. Here, a build list will be available.

The builds are sorted by commit timestamps in descending order (most recent one at the top). If you click on the links you will get more specific test results. The builds test results are saved as text files in the folder `src/main/builds`. Their filenames are a combination of their commit timestamps and their commit hashes.

### Example of build list and test log

*Below is an example of the web page build list, available at http://localhost:8011/. In this example, there are four links to different builds.*

```
Build links
The builds are sorted by their commit timestamps, with the most recent one at the top.
Each file name is a combination of a timestamp and commit hash.
2022-02-16T11;54;08+01;00f1d523387415c690e9a4aa8b6b0c49623ab87aa0.txt
2022-02-16T11;53;32+01;00a2be575399800e32c44e0ac18965de8b9a2a802c.txt
2022-02-16T11;51;14+01;002c6d3f81afd49037ff31fa7e517a27f0f35298dc.txt
2022-02-16T11;50;46+01;0018e51ad89bb552721eb59ebccdfa29b630ac63fc.txt
```


After clicking one of the build links, the following web page is shown. This build log will also be sent via email to the committer, and it will also be printed in the server terminal. The first line is a button which takes you back to the build line. The second line summarizes whether or not the build was successful.
```
Go to build list
Build successful
--- Build summary --- 
Code compiled successfully
Tests compiled successfully.
Tests passed.

--- Specific test info --- 
JUnit version 4.12
..........
Time: 5.342

OK (10 tests)


--- Commit specifics ---
Branch name:	main
Timestamp:	2022-02-16T11:51:14+01:00
Hash:		2c6d3f81afd49037ff31fa7e517a27f0f35298dc

```

We changed the oracle in one of the tests, so that it would fail. This resulted in the following test results. The first line says “Build failed”, meaning that something went wrong.

```
Go to build list
Build failed
--- Build summary ---
Code compiled successfully
Tests compiled successfully.
Tests failed.
--- Specific test info ---
JUnit version 4.12
...E.......
Time: 3.11
There was 1 failure:
1) test_newCommitWasMade_true(main.serverTests.Tests)
java.lang.AssertionError
at org.junit.Assert.fail(Assert.java:86)
at org.junit.Assert.assertTrue(Assert.java:41)
at org.junit.Assert.assertFalse(Assert.java:64)
at org.junit.Assert.assertFalse(Assert.java:74)
at main.serverTests.Tests.test_newCommitWasMade_true(Tests.java:135)
at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
at java.base/jdk.internal.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
at java.base/java.lang.reflect.Method.invoke(Method.java:566)
at org.junit.runners.model.FrameworkMethod$1.runReflectiveCall(FrameworkMethod.java:50)
at org.junit.internal.runners.model.ReflectiveCallable.run(ReflectiveCallable.java:12)
at org.junit.runners.model.FrameworkMethod.invokeExplosively(FrameworkMethod.java:47)
at org.junit.internal.runners.statements.InvokeMethod.evaluate(InvokeMethod.java:17)
at org.junit.runners.ParentRunner.runLeaf(ParentRunner.java:325)
at org.junit.runners.BlockJUnit4ClassRunner.runChild(BlockJUnit4ClassRunner.java:78)
at org.junit.runners.BlockJUnit4ClassRunner.runChild(BlockJUnit4ClassRunner.java:57)
at org.junit.runners.ParentRunner$3.run(ParentRunner.java:290)
at org.junit.runners.ParentRunner$1.schedule(ParentRunner.java:71)
at org.junit.runners.ParentRunner.runChildren(ParentRunner.java:288)
at org.junit.runners.ParentRunner.access$000(ParentRunner.java:58)
at org.junit.runners.ParentRunner$2.evaluate(ParentRunner.java:268)
at org.junit.runners.ParentRunner.run(ParentRunner.java:363)
at org.junit.runners.Suite.runChild(Suite.java:128)
at org.junit.runners.Suite.runChild(Suite.java:27)
at org.junit.runners.ParentRunner$3.run(ParentRunner.java:290)
at org.junit.runners.ParentRunner$1.schedule(ParentRunner.java:71)
at org.junit.runners.ParentRunner.runChildren(ParentRunner.java:288)
at org.junit.runners.ParentRunner.access$000(ParentRunner.java:58)
at org.junit.runners.ParentRunner$2.evaluate(ParentRunner.java:268)
at org.junit.runners.ParentRunner.run(ParentRunner.java:363)
at org.junit.runner.JUnitCore.run(JUnitCore.java:137)
at org.junit.runner.JUnitCore.run(JUnitCore.java:115)
at org.junit.runner.JUnitCore.runMain(JUnitCore.java:77)
at org.junit.runner.JUnitCore.main(JUnitCore.java:36)
FAILURES!!!
Tests run: 10, Failures: 1
--- Commit specifics ---
Branch name: shouldFailBranch
Timestamp: 2022-02-16T11:48:27+01:00
Hash: 6649fcc2b906cb9c34712bc1e3e682c9247410b8
```


