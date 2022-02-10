package main.serverTests;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.Properties;
import java.util.Scanner;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;


import org.junit.Test;
import static org.junit.Assert.*;
import java.beans.Transient;

import main.code.*;

import java.io.IOException;
import java.io.File; 

// This class includes tests for various functions.
public class Tests {

    @Test 
    public void test_runCommand() throws IOException {
        // Arrange
        String actualOutput = Functions.runCommand("echo testOutput");

        // Assert
        String expectedOutput = "testOutput\n";
        assertTrue(actualOutput.equals(expectedOutput)); 
    }

    
    @Test 
    public void test_getBranchName() throws IOException {
        // Arrange
        String JSONstring = "{\"ref\":\"refs/heads/someBranchName\",\"before\":\"c747cb43fd0c8564151dc4d1bdbaf7a37cde2638\",\"after\":\"...}";

        // Act
        String extractedBranchName = Functions.getBranchName(JSONstring);
        
        // Assert
        String expectedBranchName = "someBranchName";
        assertTrue(extractedBranchName.equals(expectedBranchName)); 
    }

    @Test 
    public void test_cloneBranch() throws IOException {
        // Arrange: Delete the old cloned repo
        Functions.deleteClonedRepo();

        // Act: Clone the main branch of the repo
        Functions.cloneBranch("main");

        // Assert: Check if there actually is a cloned repo, with the correct name.
        File f = new File("continuous-integration-java");
        boolean repoExists = f.exists() && f.isDirectory();
        assertTrue(repoExists);
    }

    @Test 
    public void test_sendFromServer() throws MessagingException {
        // Arrange: Send an email which does not have a correct structure
        String falseEmail = "xxxxxxx";

        try {
            Functions.sendFromServer(falseEmail, "text");
            fail("Expected the email address to be invalid.");

        } catch(MessagingException e) {
            // pass
         }
    }

}
