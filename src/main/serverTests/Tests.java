package main.serverTests;

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
        String JSONstring = "{\"ref\":\"refs/heads/someBranchName\",\"before\":\"c747cb43fd0c8564151dc4d1bdbaf7a37cde2638\",\"after\":\"...}"

        // Act
        String extractedBranchName = Functions.getBranchName(JSONstring);

        // Assert
        String expectedBranchName = "someBranchName";
        assertTrue(extractedBranchName.equals(expectedBranchName)); 
    }

}
