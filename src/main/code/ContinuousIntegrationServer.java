package main.code;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;

import java.io.IOException;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.Properties;
import java.util.Scanner;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import java.util.stream.Collectors;
import java.lang.StringBuilder;

/**
 Skeleton of a ContinuousIntegrationServer which acts as webhook
 See the Jetty documentation for API documentation of those classes.
*/
public class ContinuousIntegrationServer extends AbstractHandler
{

    /**
     * Called each time the github webhook is activated. The branch of the new commit will be cloned,
     * compiled, and tested. Finally, the user who made the commit will be notified of the results.
     *
     * @param target
     * @param baseRequest
     * @param request
     * @param response
     * @throws IOException
     * @throws ServletException
     */
    public void handle(String target,
                       Request baseRequest,
                       HttpServletRequest request,
                       HttpServletResponse response)
        throws IOException, ServletException
    {
        response.setContentType("text/html;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        baseRequest.setHandled(true);

        // Convert the JSON payload into a String
        String JSONstring = Functions.JSONtoString(request);


        // Depending on what request has come in, call different functions
        if (JSONstring.equals("")) {
            handleWebsiteVisit(target, baseRequest, request, response, JSONstring);
        }
        else if (Functions.newCommitWasMade(JSONstring)) {
            handleNewCommit(target, baseRequest, request, response, JSONstring);
        }
    }

    /**
     * Called when someone visits the page localhost:8011, when the server is running.
     *
     * @param target
     * @param baseRequest
     * @param request
     * @param response
     * @param JSONstring
     * @throws IOException
     * @throws ServletException
     */

    public void handleWebsiteVisit(String target,
                                   Request baseRequest,
                                   HttpServletRequest request,
                                   HttpServletResponse response,
                                   String JSONstring)
        throws IOException, ServletException
    {

        // Print HTML with links to all builds
        String HTML = Functions.getLinksToBuildsHTML();
        response.getWriter().println(HTML);

        // If a link to a build is clicked; process the request and showcase the correct build info.
        String requestedURL = request.getRequestURL().toString();
        Functions.processURLrequest(requestedURL, response);
    }

    /**
     * Called everytime a new commit is pushed to the github repository. Checks if the server code and the tests
     * are compilable. Sends an email with the results of the compilations to the committer.
     *
     * @param target
     * @param baseRequest
     * @param request
     * @param response
     * @param JSONstring
     * @throws IOException
     * @throws ServletException
     */
    public void handleNewCommit(String target,
                                Request baseRequest,
                                HttpServletRequest request,
                                HttpServletResponse response,
                                String JSONstring)
        throws IOException, ServletException
    {
        // Extract branch name, email and commitHash from webhook message
        String branchName = Functions.getBranchName(JSONstring);
        String email = Functions.getEmail(JSONstring);
        String commitHash = Functions.getCommitHash(JSONstring);
        String commitTimestamp = Functions.getCommitTimestamp(JSONstring);

        if (!commitHash.equals("0000000000000000000000000000000000000000")) {
            // Delete the old cloned repo, and clone the branch of the new commit.
            Functions.deleteClonedRepo();
            Functions.cloneBranch(branchName);

            // Construct the email with compilation and test results
            StringBuilder message = new StringBuilder();
            boolean codeCompiled = Functions.compilationCheck("compilationCheck.sh");
            //String codeCompilationResult = Functions.compilationCheck();
            boolean  testsCompiled = Functions.compilationCheck("compileTestsCheck.sh");
            //String testCompilationResult = Functions.compileTestsCheck();
            boolean testsPassed = true;

            message.append("--- Test summary --- \n");//
            // Check if compilation of the server is successful
            if (codeCompiled) {
                message.append("Code compiled succesfully\n");
            }
            else {
                //TODO: add compilation errors
                //message.append(codeCompilationResult);
                message.append("Code compilation failed.");
                //System.out.print(codeCompilationResult);
            }

            // Check if tests compilation is successful.
            if (testsCompiled) {
                message.append("Tests compiled succesfully.\n");

                //If tests compile, run the tests.
                String testResults = Functions.runTests("runTests.sh");
                //Tests failed
                if(testResults.contains("Failures: ")){
                    testsPassed = false;
                    message.append("Tests failed.\n");
                }//Tests passed
                else{
                    message.append("Tests passed.\n");
                }
                message.append("\n--- Specific test info --- \n");
                message.append(testResults+"\n");
            }
            else {
                //TODO: Add test compilation issues to message.
                //System.out.println(testCompilationResult);
                //message.append(testCompilationResult);
                message.append("Tests compilation failed.");

            }

            // Add branch name, timestamp, and commit hash to the test results message.
            message.append("--- Commit specifics ---");
            message.append("\nBranch name:\t" + branchName);
            message.append("\nTimestamp:\t" + commitTimestamp);
            message.append("\nHash:\t\t" + commitHash);

            // Convert test results to String
            String messageStr = message.toString();

            //Construct buildresult based on if code and tests compiled and if tests passed.
            String buildResult = "";
            if(codeCompiled && testsCompiled && testsPassed){
                buildResult = "Build successful";
            }else{
                buildResult = "Build failed";
            }

            // The test results will be printed to terminal, sent via email to commiter, and written to a build file.
            System.out.println(messageStr);
            Functions.sendFromServer(email, buildResult, messageStr);
            Functions.writeToFile("main/builds/" + commitTimestamp + commitHash + ".txt", messageStr);

            // Repond to github webhook.
            response.getWriter().println("CI job done");
        }
    }

    /**
     * Used to start the ContinuousIntegrationServer in command line
     *
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception
    {
        Server server = new Server(8011);

        server.setHandler(new ContinuousIntegrationServer());
        server.start();
        server.join();
    }
}
