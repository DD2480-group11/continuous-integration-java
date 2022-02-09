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
        String actualOutput = Functions.runCommand("echo testOutput");
        String expectedOutput = "testOutput\n";

        boolean outputWasCorrect = actualOutput.equals(expectedOutput);
        assertTrue(outputWasCorrect); 
    }
}
