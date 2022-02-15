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
    // This function is called each time the github webhook is activated.
    // The branch of the new commit will be cloned, compiled, and tested.
    // Finally, the user who made the commit will be notified of the results.
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
        // System.out.println(">" + JSONstring + "<");

        // Depending on what request has come in, call different functions
        if (JSONstring.equals("")) {
            handleWebsiteVisit(target, baseRequest, request, response, JSONstring);
        }
        else {
            handleNewCommit(target, baseRequest, request, response, JSONstring);
        }
    }
    
    // This function is called when someone visits the page localhost:8011, when the server is running.
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
            boolean codeCompiled = Functions.compilationCheck();
            //String codeCompilationResult = Functions.compilationCheck();
            boolean  testsCompiled = Functions.compileTestsCheck();
            //String testCompilationResult = Functions.compileTestsCheck();

            // Check if compilation of the server is successful
            if (codeCompiled) {
                message.append("Code compiled succesfully\n");
                System.out.println("Server compiled succesfully.");
            }
            else {
                //TODO: add compilation errors
                //message.append(codeCompilationResult);
                message.append("Code compilation failed.");
                //System.out.print(codeCompilationResult);
                System.out.println("Server compilation failed.");
            }

            // Check if tests compilation is successful.
            if (testsCompiled) {
                message.append("Tests compiled succesfully.\n");
                //If tests compile, run the tests and print the result.
                String testResults = Functions.runTests();
                System.out.println(testResults);
                message.append("Testresults: \n");//
                message.append(testResults+"\n");
            }
            else {
                //TODO: Add test compilation issues to message.
                //System.out.println(testCompilationResult);
                //message.append(testCompilationResult);
                message.append("Tests compilation failed.");
                System.out.println("Tests compilation failed.");

            }

            // Add branch name, timestamp, and commit hash to the test results message.
            message.append("Commit branch name: " + branchName);
            message.append("\nCommit timestamp: " + commitTimestamp);
            message.append("\nCommit hash: " + commitHash);

            // Convert test results to String
            String messageStr = message.toString();

            // The test results will be printed to terminal, sent via email to commiter, and written to a build file.
            System.out.println(messageStr);
            Functions.sendFromServer(email, messageStr);
            Functions.writeToFile("main/builds/" + commitTimestamp + commitHash + ".txt", messageStr);

            // Repond to github webhook.
            response.getWriter().println("CI job done");
        }
    }

    // used to start the CI server in command line
    public static void main(String[] args) throws Exception
    {
        Server server = new Server(8011);

        server.setHandler(new ContinuousIntegrationServer());
        server.start();
        server.join();
    }
}
