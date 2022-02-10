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
//test
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

        // Extract the branch name of the github commit
        String JSONstring = Functions.JSONtoString(request);

        String branchName = Functions.getBranchName(JSONstring);

        String email = Functions.getEmail(JSONstring);

        // Delete the old cloned repo, and clone the branch of the new commit.
        Functions.deleteClonedRepo();
        Functions.cloneBranch(branchName);
        StringBuilder message = new StringBuilder();
        String codeCompilationResult = Functions.compilationCheck();
        String testCompilationResult = Functions.compileTestsCheck();
        // Check if compilation of the server is successful.
        if (codeCompilationResult == "") {
            message.append("The code compilation worked!\n");
            System.out.println("Server compiled succesfully.");
        }
        else {
            //TODO: add compilation errors
            message.append(codeCompilationResult);
            System.out.print(codeCompilationResult);
            System.out.println("Server compilation failed.");
        }

        // Check if tests compilation is successful.
        if (testCompilationResult == "") {
            message.append("Tests compiled succesfully.\n");
            //If tests compile, run the tests and print the result.
            String testResults = Functions.runTests();
            System.out.println(testResults);
            message.append(testResults+"\n");
        }
        else {
            //TODO: Add test compilation issues to message.
            System.out.println(testCompilationResult);
            message.append(testCompilationResult);
            System.out.println("Tests compilation failed.");

        }

        // --- FOR DEBUGGING PURPOSES ---

        // Add branch name of the commit to email message.
        message.append("Branch name of commit: " + branchName);
        System.out.pringln(message.toString());
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
