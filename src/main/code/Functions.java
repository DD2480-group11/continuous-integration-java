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

/**
 * A class containing functions needed to run the Continuous Integration Server
 */
public class Functions {
    /**
     * Runs a string command and returns the output as a String
     *
     * @param command
     * @return result
     * @throws IOException
     */
    public static String runCommand(String command) throws IOException {
        ProcessBuilder pb = new ProcessBuilder();
	    pb.command("bash", "-c", command);
        Process process = pb.start();

        String result = new String(process.getInputStream().readAllBytes());
        return result;
    }

    /**
     * Clones the git repo into the folder src/continuous-integration-java
     *
     * @throws IOException
     */
    public static void cloneThisRepo() throws IOException {
        runCommand("git clone git@github.com:DD2480-group11/continuous-integration-java.git");
    }

    /**
     * Clones the given branch of the git repo into the folder src/continuous-integration-java
     *
     * @param branchName
     * @throws IOException
     */
    public static void cloneBranch(String branchName) throws IOException {
        runCommand("git clone -b " + branchName + " git@github.com:DD2480-group11/continuous-integration-java.git");
    }

    /**
     * Deletes the repo src/continuous-integration-java
     *
     * @throws IOException
     */
    public static void deleteClonedRepo() throws IOException {
        runCommand("rm -rf continuous-integration-java");
    }

    /**
     * Runs the bash script located in the script folder with given filename
     *
     * @param filename
     * @return runCommand("bash main/code/scripts/" + filename) Launches runCommand which ouputs the result as a string
     * @throws IOException
     */
    public static String runBashScript(String filename) throws IOException {
        return runCommand("bash main/code/scripts/" + filename);
    }

    /**
     * Tries to compile the server of the cloned repo, using the bash script "compilationCheck.sh"
     *
     * @return compilationResult.equals("success\n")
     * @throws IOException
     */
    //
    // Returns true if compilation was successful, otherwise false.
    public static boolean compilationCheck() throws IOException {
        String compilationResult = runBashScript("compilationCheck.sh");
        return compilationResult.equals("success\n");
    }

    /**
     * Tries to compile the tests of the cloned repo, using the bash script "compileTestsCheck.sh"
     *
     * @return compilationResult.equals("success\n")
     * @throws IOException
     */

    // Returns true if compilation was successful, otherwise false.
    public static boolean compileTestsCheck() throws IOException {
        String compilationResult = runBashScript("compileTestsCheck.sh");
         return compilationResult.equals("success\n");
    }

    /**
     * Runs the tests in main/serverTests.Tests.java and launches runBashScript which
     * returns the output of those tests
     *
     * @return runBashScript("runTests.sh")
     * @throws IOException
     */
    public static String runTests() throws IOException{
        return runBashScript("runTests.sh");
    }

    /**
     * Turns a JSON HttpServletRequest object into a String
     *
     * @param request A HttpServletRequest
     * @return request.getReader().lines().collect(Collectors.joining())
     * @throws IOException
     */
    public static String JSONtoString(HttpServletRequest request) throws IOException {
        return request.getReader().lines().collect(Collectors.joining());
    }

    /**
     * Takes a JSON String with github commit information from a github webhook and returns the name of the
     * branch that was pushed to
     *
     * @param JSONstring A string of a JSON object
     * @return branchName
     * @throws IOException
     */
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

    /**
     * Takes a JSON String with github commit information from a github webhook and returns
     * the email address of the committer
     *
     * @param JSONstring A string of a JSON object
     * @return email
     * @throws IOException
     */
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
     * Sends an email containing the text String given as input from the servers email address to given input email
     * to the given email address of the recipent
     *
     * @param recipient The recipents email address
     * @param text The text to send in an email
     * @return true or false Returns false if the recipients email address is faulty otherwise true
     */
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
     * Creates a file named as fileName and writes the text String given as input to it
     * If file already exists then it will be overwritten.
     *
     * @param fileName
     * @param text
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
     * Reads a file and returns its content as a String
     *
     * @param fileName
     * @return content
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
     * Adds a given text as a String to a given file if the file exists
     *
     * @param fileName
     * @param text
     */
    public static void appendToFile(String fileName, String text) {
        try {
            Files.write(Paths.get(fileName), text.getBytes(), StandardOpenOption.APPEND);
        } catch (IOException e) {
            System.out.println("Error: the file " + fileName + " does not exist.");
        }
    }
}

