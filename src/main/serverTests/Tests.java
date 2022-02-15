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
    public void test_getCommitHash() throws IOException {
        // Arrange
        String JSONstring = "{\"ref\":\"refs/heads/someBranchName\",\"before\":\"c747cb43fd0c8564151dc4d1bdbaf7a37cde2638\",\"after\":\"9c9ebf3cd4262d2de0d94f73e4bb4ea0ecf7d228\",\"repository\": {}\"";

        // Act
        String extractedCommitHash = Functions.getCommitHash(JSONstring);

        // Assert
        String expectedCommitHash = "9c9ebf3cd4262d2de0d94f73e4bb4ea0ecf7d228";
        assertTrue(extractedCommitHash.equals(expectedCommitHash));
    }

    @Test
    public void test_getCommitTimestamp() throws IOException {
        // Arrange
        String JSONstring = "{\"ref\":\"refs/heads/someBranchName\"..." +
                            "commits\":[{\"id\":\"bd58a21891ad0ce4f3cc1f303b32383f654cb7b3\",\"tree_id\":\"8574623057713cf6bf8aedcb76c063714abd8a10\n" +
                            "distinct\":true,\"message\":\"Fixed #93. localhost:8011 has links to each build, (#94)\n\nwith test-results info.\"," +
                            "\"timestamp\":\"2022-02-15T15:53:54+01:00\",\"url\":\"...";

        // Act
        String extractedTimestamp = Functions.getCommitTimestamp(JSONstring);

        // Assert
        String expectedTimestamp = "2022-02-15T15:53:54+01:00";
        assertTrue(extractedTimestamp.equals(expectedTimestamp));
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
    public void test_sendFromServer_invalidEmail() throws IOException {
        // Arrange: Add an invalid email as recipient
        String email = "invalid email";

        assertFalse(Functions.sendFromServer(email, "text"));
    }

    @Test
    public void test_readAndWriteToFile() throws IOException {
        Functions.writeToFile("main/serverTests/testFile.txt", "some text");

        String actual = Functions.readFile("main/serverTests/testFile.txt");
        String expected = "some text";

        assertTrue(actual.equals(expected));
    }

    @Test
    public void test_appendToFile() throws IOException {
        Functions.writeToFile("main/serverTests/testFile.txt", "some text");
        Functions.appendToFile("main/serverTests/testFile.txt", " and some more");

        String actual = Functions.readFile("main/serverTests/testFile.txt");
        String expected = "some text and some more";
        
        assertTrue(actual.equals(expected));
    }
}
