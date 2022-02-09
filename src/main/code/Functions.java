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
}
