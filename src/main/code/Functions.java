package main.code;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;

import java.io.IOException;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import java.util.stream.Collectors;

public class Functions {
    // Runs command and returns the output as a String.
    public static String runCommand(String command) throws IOException {
        ProcessBuilder pb = new ProcessBuilder();
	    pb.command("bash", "-c", command);
        Process process = pb.start();

        String result = new String(process.getInputStream().readAllBytes());
        return result;
    }

    public static void cloneThisRepo() throws IOException {
        runCommand("git clone git@github.com:DD2480-group11/continuous-integration-java.git");
    }

    public static void deleteClonedRepo() throws IOException {
        runCommand("rm -rf continuous-integration-java");
    }

    // Runs the bash script with given filename. The script should be located in the scripts folder.
    // Returns the output as a String.
    public static String runBashScript(String filename) throws IOException {
        return runCommand("bash main/code/scripts/" + filename);
    }

    // Tries to compile the cloned code, using the bash script "compilationCheck.sh".
    // Returns true if compilation was successful, otherwise false.
    public static boolean compilationCheck() throws IOException {
        String compilationResult = runBashScript("compilationCheck.sh");
        return compilationResult.equals("success\n");
    }

    // Turns a JSON HttpServletRequest object into a String.
    public static String JSONtoString(HttpServletRequest request) throws IOException {
        return request.getReader().lines().collect(Collectors.joining());
    }

    // Takes a JSON String with github commit information from a github webhook.
    // Extracts and returns the name of the branch that was pushed to.
    public static String getBranchName(String JSONstring) throws IOException  {
        String branchName = "";

        // Extract each character from the branch name.
        // The branch name starts at index 19 in the string, and ends with a quotation mark.
        int i = 19;
        char c = JSONstring.charAt(i);
        while (c != '"') {
            branchName += c;
            c = JSONstring.charAt(i);
            i++;
        }

        return branchName;
    }

    
}
