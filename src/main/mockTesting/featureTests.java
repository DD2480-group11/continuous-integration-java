package main.mockTesting;
import org.junit.Test;
import static org.junit.Assert.*;
import java.beans.Transient;

import main.code.*;

import java.io.IOException;

// This class includes tests that showcase that the testexecution and compilation of the CI server works correctly.
public class featureTests {

	@Test
    public void testCompilationSuccess() throws IOException {
        // Arrange
        String script = "compileSuccess.sh";

		//Act
		boolean result = Functions.compilationCheck(script);

        // Assert
        assertTrue(result);
    }

	@Test
    public void testCompilationFail() throws IOException {
        // Arrange
        String script = "compileFail.sh";

		//Act
		boolean result = Functions.compilationCheck(script);

        // Assert
        assertFalse(result);
    }

	@Test
    public void test_testExcecution() throws IOException {
        // Arrange
       String mockTestScript = "runMockTests.sh";
       // Assert
       String testResults = Functions.runTests(mockTestScript);
       //System.out.println("Results: "+testResults + "End of results");
      // String exitcode = Character.toString(testResults.charAt(testResults.length()-1));
      // System.out.println("exit:" + exitcode);
       assertFalse(testResults.contains("Failures: "));//exitcode.equals("0")
    }
}
