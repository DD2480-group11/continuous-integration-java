package main.code;

import java.io.IOException;

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

    // Runs the bash script with given filename. The script should be located in the src folder.
    // Returns the output as a String
    public static String runBashScript(String filename) throws IOException {
        return runCommand("bash " + filename);
    }

    // Tries to compile the cloned code, using the bash script "compilationCheck.sh".
    // Returns true if compilation was successful, otherwise false.
    public static boolean compilationCheck() throws IOException {
        String compilationResult = runBashScript("compilationCheck.sh");
        return compilationResult.equals("success\n");
    }
}
