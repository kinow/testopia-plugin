/*
 * The MIT License
 *
 * Copyright (c) <2012> <Bruno P. Kinoshita>
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package jenkins.plugins.testopia;

import hudson.EnvVars;

import java.util.HashMap;
import java.util.Map;

import jenkins.plugins.testopia.result.TestCaseWrapper;

import org.mozilla.testopia.model.Status;
import org.mozilla.testopia.model.TestCase;

/**
 * Utility methods.
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 0.1
 */
public class Utils {
	// Empty constructor, as this is an util class.
	private Utils() {};
	// Environment Variables names.
	private static final String TESTOPIA_TESTCASE_ID_ENVVAR = "TESTOPIA_TESTCASE_ID";
	private static final String TESTOPIA_TESTCASE_SCRIPT_ENVVAR = "TESTOPIA_TESTCASE_SCRIPT";
	private static final String TESTOPIA_TESTCASE_ALIAS_ENVVAR = "TESTOPIA_TESTCASE_ALIAS";
	/**
	 * Creates EnvVars for a Test Case.
	 * @param testCase test Case
	 * @return EnvVars (environment variables)
	 */
	public static EnvVars buildTestCaseEnvVars(TestCase testCase) {
		// Build environment variables list
		Map<String, String> envVars = createEnvironmentVariables(testCase);
		final EnvVars buildEnvironment = new EnvVars(envVars);
		return buildEnvironment;
	}
	/**
	 * Creates a Map (name, value) of environment variables for a Test Case.
	 * @param testCase test Case.
	 * @return Map (name, value) of environment variables.
	 */
	public static Map<String, String> createEnvironmentVariables(TestCase testCase) {
		Map<String, String> envVar = new HashMap<String, String>();
		envVar.put(TESTOPIA_TESTCASE_ID_ENVVAR, ""+testCase.getId() );
		envVar.put(TESTOPIA_TESTCASE_SCRIPT_ENVVAR, ""+testCase.getScript());
		envVar.put(TESTOPIA_TESTCASE_ALIAS_ENVVAR, ""+testCase.getAlias());
		return envVar;
	}
	/**
	 * @param report
	 * @param previous
	 * @return
	 */
	public static String createReportSummary(Report report, Report previous) {
		StringBuilder builder = new StringBuilder();
		builder.append("<p><b>Build "+report.getBuildId()+"</b></p>");
		builder.append("<p><b>Run "+report.getRunId()+"</b></p>");
		builder.append("<p><b>Environment "+report.getEnvId()+"</b></p>");
		builder.append("<p><a href=\"" + TestopiaBuildAction.URL_NAME + "\">");
		
		Integer total = report.getTestsTotal();
		Integer previousTotal = previous != null ? previous.getTestsTotal() : total;
		Integer passed = report.getPassed();
		Integer previousPassed = previous != null ? previous.getPassed() : passed;
		Integer failed = report.getFailed();
		Integer previousFailed = previous != null ? previous.getFailed() : failed;
		Integer blocked = report.getBlocked();
		Integer previousBlocked = previous != null ? previous.getBlocked() : blocked;
		Integer notRun = report.getNotRun();
		Integer previousNotRun = previous != null ? previous.getNotRun() : notRun;
		
		builder.append("Total of " +(total + getPlusSignal(total, previousTotal))+ " " +
				"tests</a>. Where "+(passed + getPlusSignal(passed, previousPassed))+" " +
				"passed, "+(failed + getPlusSignal(failed, previousFailed))+" failed, " +
				""+(blocked + getPlusSignal(blocked, previousBlocked))+" were blocked " +
				"and "+(notRun + getPlusSignal(notRun, previousNotRun))+" were not executed.");
		
        builder.append("</p>");
		
		return builder.toString();
	}
	/**
	 * Prints the difference between two int values, showing a plus sign if the 
	 * current number is greater than the previous. 
	 * @param current Current value
	 * @param previous Previous value
	 */
	public static String getPlusSignal(int current, int previous) {
		int difference = current - previous;

		if (difference > 0) {
			return " (+" + difference + ")";
		} else {
			return "";
		}

	}
	/**
	 * @param report
	 * @param previousReport
	 * @return
	 */
	public static String createReportSummaryDetails(Report report,
			Report previousReport) {
		final StringBuilder builder = new StringBuilder();

		builder.append("<p>Summary details</p>");
		builder.append("<table border=\"1\">\n");
		builder.append("<tr><th>");
		builder.append("Test Case ID");
		builder.append("</th><th>");
		builder.append("Status");
		builder.append("</th></tr>\n");
		
        for(TestCaseWrapper tc: report.getTestCases() )
        {
        	builder.append("<tr>\n");
        	
        	builder.append("<td>"+tc.getId()+"</td>");
    		builder.append("<td>"+Utils.getExecutionStatusTextColored(tc.getStatusId())+"</td>\n");
        	
        	builder.append("</tr>\n");
        }
        
        builder.append("</table>");
        return builder.toString();
	}
	/**
	 * @param statusId
	 * @return
	 */
	private static String getExecutionStatusTextColored(Integer statusId) {
		Status executionStatus = Status.get((statusId != null ? statusId
				: Status.IDLE.getValue()));
		String executionStatusTextColored = "Undefined";
		if (executionStatus == Status.FAILED) {
			executionStatusTextColored = "<span style='color: red'>Failed</span>";
		}
		if (executionStatus == Status.PASSED) {
			executionStatusTextColored = "<span style='color: green'>Passed</span>";
		}
		if (executionStatus == Status.BLOCKED) {
			executionStatusTextColored = "<span style='color: yellow'>Blocked</span>";
		}
		if (executionStatus == Status.IDLE) {
			executionStatusTextColored = "<span style='color: gray'>Not Run</span>";
		}
		return executionStatusTextColored;
	}
}
