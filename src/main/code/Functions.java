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
        return runCommand("java -cp \".:hamcrest.jar:junit.jar:servlet-api-2.5.jar:jakarta.activation.jar:javax.mail.jar:jetty-all-$JETTY_VERSION.jar\" org.junit.runner.JUnitCore \"/continuous-integration-java/src/main/serverTests/Tests\"");
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
}
