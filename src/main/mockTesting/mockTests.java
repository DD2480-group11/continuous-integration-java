package main.mockTesting;
import org.junit.Test;
import static org.junit.Assert.*;
import java.beans.Transient;

import main.code.*;

import java.io.IOException;

// This class includes tests used to showcase that the testexecution of the CI server works correctly.
public class mockTests {

	@Test
    public void test1() throws IOException {
        // Arrange
        boolean tru = true;

        // Assert
        assertTrue(tru);
    }

	@Test
    public void test2() throws IOException {
        // Arrange
        boolean tru = true;

        // Assert
        assertFalse(!tru);
    }
}
