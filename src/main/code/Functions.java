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

import java.util.Formatter;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

public class Functions {
    // Runs command and returns the output as a String.
    public static String runCommand(String command) throws IOException {
        ProcessBuilder pb = new ProcessBuilder();
	    pb.command("bash", "-c", command);
        Process process = pb.start();

        String result = new String(process.getInputStream().readAllBytes());
        return result;
    }

    // Clones this git repo into the folder src/continuous-integration-java.
    public static void cloneThisRepo() throws IOException {
        runCommand("git clone git@github.com:DD2480-group11/continuous-integration-java.git");
    }

    // Clones the specified branch of this git repo into the folder src/continuous-integration-java.
    public static void cloneBranch(String branchName) throws IOException {
        runCommand("git clone -b " + branchName + " git@github.com:DD2480-group11/continuous-integration-java.git");
    }

    // Deletes the repo src/continuous-integration-java.
    public static void deleteClonedRepo() throws IOException {
        runCommand("rm -rf continuous-integration-java");
    }

    // Runs the bash script with given filename. The script should be located in the scripts folder.
    // Returns the output as a String.
    public static String runBashScript(String filename) throws IOException {
        return runCommand("bash main/code/scripts/" + filename);
    }

    // Tries to compile the server of the cloned repo, using the bash script "compilationCheck.sh".
    // Returns true if compilation was successful, otherwise false.
    public static boolean compilationCheck() throws IOException {
        String compilationResult = runBashScript("compilationCheck.sh");
        return compilationResult.equals("success\n");
    }

    // Tries to compile the tests of the cloned repo, using the bash script "compileTestsCheck.sh".
    // Returns true if compilation was successful, otherwise false.
    public static boolean compileTestsCheck() throws IOException {
        String compilationResult = runBashScript("compileTestsCheck.sh");
         return compilationResult.equals("success\n");//
    }

    // Runs the tests in main/serverTests.Tests.java and returns the output of those tests.
    public static String runTests() throws IOException{
        return runBashScript("runTests.sh");
    } 

    //java -cp ".:hamcrest.jar:junit.jar:servlet-api-2.5.jar:jakarta.activation.jar:javax.mail.jar:jetty-all-$JETTY_VERSION.jar" org.junit.runner.JUnitCore "continuous-integration-java.src.main.serverTests.Tests"

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
        i++;
        while (c != '"') {
            branchName += c;
            c = JSONstring.charAt(i);
            i++;
        }

        return branchName;
    }

    // Takes a JSON String with github commit information from a github webhook.
    // Extracts and returns the email of the committer.
    public static String getEmail(String JSONstring) throws IOException {
        String email = "";
        String comStr = "\"committer\":{";

        //get index of committer section
        int i = JSONstring.indexOf(comStr);

        String restJSON = JSONstring.substring(i+comStr.length(), JSONstring.length());
        String emailStr = "\"email\":\"";

        // get index of email under committer section
        int j = restJSON.indexOf(emailStr);

        int k = (j+emailStr.length());
        char c = restJSON.charAt(k);

        while (c != '"') {
                email += c;
                k++;
                c = restJSON.charAt(k);
        }

        return email;
    }

    /**
     * Takes a JSON String with github commit information from a github webhook.
     * Extracts and returns the commit hash of the committer.
     * @param JSONstring the String containg the JSON info
     * @return the commit hash of the commit
     * @throws IOException
     */
    public static String getCommitHash(String JSONstring) throws IOException {
        String commitHash = "";

        // Get index of "after" section, where the hash is located
        String afterStr = "\"after\":\"";
        int i = JSONstring.indexOf(afterStr);

        // Get index where the actual hash code starts
        i += afterStr.length();
        char c = JSONstring.charAt(i);

        // Extract all the chars from the hash
        while (c != '"') {
                commitHash += c;
                i++;
                c = JSONstring.charAt(i);
        }

        return commitHash;
    }


    public static boolean sendFromServer(String recipient, String text) {

        String sender = "ciserverupdate@gmail.com";
        String password = "skickamail1!";

        if(!(recipient.contains("@") && recipient.contains(".")) ){
            return false;
        }

        // setting up gmail smtp
        Properties properties = new Properties();
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");

        // authenticate to gmail
        Session session = Session.getInstance(properties,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(sender,password);
                    }
                });
        try
        {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(sender));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(recipient));
            message.setSubject("Result from CI Server");
            message.setText(text);
            Transport.send(message);
        }
        catch (MessagingException mex)
        {
            mex.printStackTrace();

        }
        return true;
    }

    /**
     * Creates a file, and writes a String to it.
     * If file already exists then it will be overwritten.
     * @param fileName the name of the file to write to
     * @param text the string to write to file
     */
    public static void writeToFile(String fileName, String text){ 
        Formatter formatter;
        try {
            formatter = new Formatter(fileName);  
            formatter.format("%s", text);
            formatter.close();
        }
        catch(Exception e) {     
            System.out.println("Error: file could not be created or written to.");
        }
    }

    /**
     * Reads a file and returns contents as a String.
     * @param fileName the file to read
     * @return the contents of the file
     */
    public static String readFile(String fileName) {
        String content = "";
        try {
            content = new String(Files.readAllBytes(Paths.get(fileName)));
        }
        catch (Exception e) {
            System.out.println("Error: could not read file " + fileName);
        }
        return content;
    }

    /**
     * Adds text to a file, if it already exists.
     * @param fileName file to add text to
     * @param text the text you want to add to the file
     */
    public static void appendToFile(String fileName, String text) {
        try {
            Files.write(Paths.get(fileName), text.getBytes(), StandardOpenOption.APPEND);
        } catch (IOException e) {
            System.out.println("Error: the file " + fileName + " does not exist.");
        }
    }
}

