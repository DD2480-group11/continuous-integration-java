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
        System.out.println(target);

        // Convert the JSON payload into a String
        String JSONstring = Functions.JSONtoString(request);

        // Depending on what request has come in, call different functions
        if (!JSONstring.equals("/favicon.ico")) {
            handleWebsiteVisit(target, baseRequest, request, response, JSONstring);
        }
        else {
            // Extract branch name and email from webhook message
        String branchName = Functions.getBranchName(JSONstring);
        String email = Functions.getEmail(JSONstring);

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

        // --- FOR DEBUGGING PURPOSES ---

        // Add branch name of the commit to email message.
        message.append("Branch name of commit: " + branchName);
        System.out.println(message.toString());
        Functions.sendFromServer(email,message.toString());
        response.getWriter().println("CI job done");
        }
    }

    // This function is called when someone visits the public forwarding URL specified by ngrok
    // (e.g. http://d47a-92-34-27-8.ngrok.io)
    public void handleWebsiteVisit(String target,
                                   Request baseRequest,
                                   HttpServletRequest request,
                                   HttpServletResponse response,
                                   String JSONstring)
        throws IOException, ServletException 
    {
        response.getWriter().println("Later, this page will have info about previous builds");
    }

    public void handleNewCommit(String target,
                                Request baseRequest,
                                HttpServletRequest request,
                                HttpServletResponse response,
                                String JSONstring)
        throws IOException, ServletException 
    {
        // Extract branch name and email from webhook message
        String branchName = Functions.getBranchName(JSONstring);
        String email = Functions.getEmail(JSONstring);

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

        // --- FOR DEBUGGING PURPOSES ---

        // Add branch name of the commit to email message.
        message.append("Branch name of commit: " + branchName);
        System.out.println(message.toString());
        Functions.sendFromServer(email,message.toString());
        response.getWriter().println("CI job done");
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
