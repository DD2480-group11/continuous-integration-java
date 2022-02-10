package main.code;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;

import java.io.IOException;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import java.util.stream.Collectors;

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
        System.out.println(email);

        // Delete the old cloned repo, and clone the branch of the new commit.
        Functions.deleteClonedRepo();
        Functions.cloneBranch(branchName);


        // Check if compilation of the server of the cloned repo is successful.
        if (Functions.compilationCheck()) {
            Functions.sendFromServer(email, "Code compilation worked!");
            System.out.println("Code compiled succesfully.");
        }
        else {
            Functions.sendFromServer(email, "Code compilation didn't work.");
            System.out.println("Code compilation failed.");
        }

        // --- FOR DEBUGGING PURPOSES ---

        // Print branch name of the commit
        System.out.println("Branch name of commit: " + branchName);


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