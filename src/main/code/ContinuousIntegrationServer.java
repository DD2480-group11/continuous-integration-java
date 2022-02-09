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

        // here you do all the continuous integration tasks
        // for example
        // 1st clone your repository
        // 2nd compile the code
        System.out.println(Functions.runCommand("pwd"));
        Functions.deleteClonedRepo();
        Functions.cloneThisRepo();
        if (Functions.compilationCheck()) {
            System.out.println("Code compiled succesfully.");
        }
        else {
            System.out.println("Code compilation failed.");
        }

        //System.out.println(JSONtoString(request));
        String branchName = getBranchName(request);
        System.out.println("Branch name of commit: " + branchName);


        //String result = Functions.runCommand("bash script.sh");
        //System.out.println(result);

        response.getWriter().println("CI job done");
    }

    // Takes a HttpServletRequest object with JSON data about a github commit/push.
    // Extracts and returns the name of the branch that was pushed to.
    public static String getBranchName(HttpServletRequest request) throws IOException  {
        String strResponse = JSONtoString(request);
        String branchName = "";

        // Extract each character from the branch name.
        // The branch name starts at index 19 in the string, and ends with a quotation mark.
        int i = 19;
        char c = strResponse.charAt(i);
        while (c != '"') {
            branchName += c;
            c = strResponse.charAt(i);
            i++;
        }

        return branchName;
    }

    // Turns the JSON request and turns into a String.
    public static String JSONtoString(HttpServletRequest request) throws IOException {
        return request.getReader().lines().collect(Collectors.joining());
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