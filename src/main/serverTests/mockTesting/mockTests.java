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
    public void test1() throws IOException {
        // Arrange
        boolean true = true;

        // Assert
        assertTrue(true);
    }
	
	@Test
    public void test1() throws IOException {
        // Arrange
        boolean true = true;

        // Assert
        assertFalse(!true);
    }
}
