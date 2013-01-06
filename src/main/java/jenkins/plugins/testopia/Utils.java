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
import jenkins.plugins.testopia.util.Messages;

import org.mozilla.testopia.model.Status;
import org.mozilla.testopia.model.TestRun;

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
	private static final String TESTOPIA_TESTCASE_RUN_ID_ENVVAR = "TESTOPIA_TESTCASE_RUN_ID";
	private static final String TESTOPIA_TESTCASE_BUILD_ID_ENVVAR = "TESTOPIA_TESTCASE_BUILD_ID";
	private static final String TESTOPIA_TESTCASE_SCRIPT_ENVVAR = "TESTOPIA_TESTCASE_SCRIPT";
	private static final String TESTOPIA_TESTCASE_ALIAS_ENVVAR = "TESTOPIA_TESTCASE_ALIAS";
	private static final String TESTOPIA_TESTCASE_ARGUMENTS_ENVVAR = "TESTOPIA_TESTCASE_ARGUMENTS";
	private static final String TESTOPIA_TESTCASE_REQUIREMENT_ENVVAR = "TESTOPIA_TESTCASE_REQUIREMENT";
	private static final String TESTOPIA_TESTCASE_SORT_KEY_ENVVAR = "TESTOPIA_TESTCASE_SORT_KEY";
	private static final String TESTOPIA_TESTCASE_SUMMARY_ENVVAR = "TESTOPIA_TESTCASE_SUMMARY";
	private static final String TESTOPIA_TESTCASE_AUTHOR_ID_ENVVAR = "TESTOPIA_TESTCASE_AUTHOR_ID";
	private static final String TESTOPIA_TESTCASE_CATEGORY_ID_ENVVAR = "TESTOPIA_TESTCASE_CATEGORY_ID";
	private static final String TESTOPIA_TESTCASE_DEFAULT_TESTER_ID_ENVVAR = "TESTOPIA_TESTCASE_DEFAULT_TESTER_ID";
	private static final String TESTOPIA_TESTCASE_PRIORITY_ID_ENVVAR = "TESTOPIA_TESTCASE_PRIORITY_ID";
	private static final String TESTOPIA_TESTCASE_STATUS_ID_ENVVAR = "TESTOPIA_TESTCASE_STATUS_ID";
	private static final String TESTOPIA_TESTCASE_AUTOMATED_ENVVAR = "TESTOPIA_TESTCASE_AUTOMATED";
	private static final String TESTOPIA_TESTCASE_CREATION_DATE_ENVVAR = "TESTOPIA_TESTCASE_CREATION_DATE";
	private static final String TESTOPIA_TESTCASE_ESTIMATED_TIME_ENVVAR = "TESTOPIA_TESTCASE_ESTIMATED_TIME";
	private static final String TESTOPIA_TESTCASE_ENV_ID_ENVVAR = "TESTOPIA_TESTCASE_ENV_ID";
	/* --- */
	private static final String TESTOPIA_TESTRUN_ID_ENVVAR = "TESTOPIA_TESTRUN_ID";
	private static final String TESTOPIA_TESTRUN_BUILD_ENVVAR = "TESTOPIA_TESTRUN_BUILD";
	private static final String TESTOPIA_TESTRUN_ENVIRONMENT_ENVVAR = "TESTOPIA_TESTRUN_ENVIRONMENT";
	private static final String TESTOPIA_TESTRUN_MANAGER_ENVVAR = "TESTOPIA_TESTRUN_MANAGER";
	private static final String TESTOPIA_TESTRUN_NOTES_ENVVAR = "TESTOPIA_TESTRUN_NOTES";
	private static final String TESTOPIA_TESTRUN_PRODUCT_VERSION_ENVVAR = "TESTOPIA_TESTRUN_PRODUCT_VERSION";
	private static final String TESTOPIA_TESTRUN_SUMMARY_ENVVAR = "TESTOPIA_TESTRUN_SUMMARY";
	private static final String TESTOPIA_TESTRUN_CASES_ENVVAR = "TESTOPIA_TESTRUN_CASES";
	private static final String TESTOPIA_TESTRUN_PLAN_ID_ENVVAR = "TESTOPIA_TESTRUN_PLAN_ID";
	private static final String TESTOPIA_TESTRUN_PLAN_TEXT_VERSION_ENVVAR = "TESTOPIA_TESTRUN_PLAN_TEXT_VERSION";
	private static final String TESTOPIA_TESTRUN_STATUS_ENVVAR = "TESTOPIA_TESTRUN_STATUS";
	private static final String TESTOPIA_TESTRUN_TARGET_COMPLETION_ENVVAR = "TESTOPIA_TESTRUN_TARGET_COMPLETION";
	private static final String TESTOPIA_TESTRUN_PLAN_TARGET_PASS_ENVVAR = "TESTOPIA_TESTRUN_PLAN_TARGET_PASS";
	/**
	 * Creates EnvVars for a Test Case.
	 * @param testCase test Case
	 * @return EnvVars (environment variables)
	 */
	public static EnvVars buildTestCaseEnvVars(TestCaseWrapper testCase) {
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
	public static Map<String, String> createEnvironmentVariables(TestCaseWrapper testCase) {
		Map<String, String> envVar = new HashMap<String, String>();
		envVar.put(TESTOPIA_TESTCASE_ID_ENVVAR, ""+testCase.getId() );
		envVar.put(TESTOPIA_TESTCASE_SCRIPT_ENVVAR, ""+testCase.getScript());
		envVar.put(TESTOPIA_TESTCASE_ALIAS_ENVVAR, ""+testCase.getAlias());
		envVar.put(TESTOPIA_TESTCASE_ARGUMENTS_ENVVAR, ""+testCase.getArguments());
		envVar.put(TESTOPIA_TESTCASE_REQUIREMENT_ENVVAR, ""+testCase.getRequirement());
		envVar.put(TESTOPIA_TESTCASE_SORT_KEY_ENVVAR, ""+testCase.getSortKey());
		envVar.put(TESTOPIA_TESTCASE_SUMMARY_ENVVAR, ""+testCase.getSummary());
		envVar.put(TESTOPIA_TESTCASE_AUTHOR_ID_ENVVAR, ""+testCase.getAuthorId());
		envVar.put(TESTOPIA_TESTCASE_CATEGORY_ID_ENVVAR, ""+testCase.getCategoryId());
		envVar.put(TESTOPIA_TESTCASE_DEFAULT_TESTER_ID_ENVVAR, ""+testCase.getDefaultTesterId());
		envVar.put(TESTOPIA_TESTCASE_PRIORITY_ID_ENVVAR, ""+testCase.getPriorityId());
		envVar.put(TESTOPIA_TESTCASE_STATUS_ID_ENVVAR, ""+testCase.getStatusId());
		envVar.put(TESTOPIA_TESTCASE_AUTOMATED_ENVVAR, ""+testCase.getAutomated());
		envVar.put(TESTOPIA_TESTCASE_CREATION_DATE_ENVVAR, ""+testCase.getCreationDate());
		envVar.put(TESTOPIA_TESTCASE_ESTIMATED_TIME_ENVVAR, ""+testCase.getEstimatedTime());
		envVar.put(TESTOPIA_TESTCASE_RUN_ID_ENVVAR, ""+testCase.getRunId());
		envVar.put(TESTOPIA_TESTCASE_BUILD_ID_ENVVAR, ""+testCase.getBuildId());
		envVar.put(TESTOPIA_TESTCASE_ENV_ID_ENVVAR, ""+testCase.getEnvId());
		TestRun testRun = testCase.getTestRun();
		envVar.put(TESTOPIA_TESTRUN_ID_ENVVAR, ""+testRun.getId());
		envVar.put(TESTOPIA_TESTRUN_BUILD_ENVVAR, ""+testRun.getBuild());
		envVar.put(TESTOPIA_TESTRUN_ENVIRONMENT_ENVVAR, ""+testRun.getEnvironment());
		envVar.put(TESTOPIA_TESTRUN_MANAGER_ENVVAR, ""+testRun.getManager());
		envVar.put(TESTOPIA_TESTRUN_NOTES_ENVVAR, ""+testRun.getNotes());
		envVar.put(TESTOPIA_TESTRUN_PRODUCT_VERSION_ENVVAR, ""+testRun.getProductVersion());
		envVar.put(TESTOPIA_TESTRUN_SUMMARY_ENVVAR, ""+testRun.getSummary());
		envVar.put(TESTOPIA_TESTRUN_CASES_ENVVAR, ""+testRun.getCases());
		envVar.put(TESTOPIA_TESTRUN_PLAN_ID_ENVVAR, ""+testRun.getPlanId());
		envVar.put(TESTOPIA_TESTRUN_PLAN_TEXT_VERSION_ENVVAR, ""+testRun.getPlanTextVersion());
		envVar.put(TESTOPIA_TESTRUN_STATUS_ENVVAR, ""+testRun.getStatus());
		envVar.put(TESTOPIA_TESTRUN_TARGET_COMPLETION_ENVVAR, ""+testRun.getTargetCompletion());
		envVar.put(TESTOPIA_TESTRUN_PLAN_TARGET_PASS_ENVVAR, ""+testRun.getTargetPass());
		return envVar;
	}
	/**
	 * @param report
	 * @param previous
	 * @return
	 */
	public static String createReportSummary(Report report, Report previous) {
		StringBuilder builder = new StringBuilder();
		builder.append(Messages.Testopia_Utils_Run(report.getRunId())); 
		builder.append(Messages.Testopia_Utils_Build(report.getBuildId()));
		builder.append(Messages.Testopia_Utils_Environment(report.getEnvId()));
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
		
		builder.append(Messages.Testopia_Utils_Totals(
				(total + getPlusSignal(total, previousTotal)),
				(passed + getPlusSignal(passed, previousPassed)), 
				(failed + getPlusSignal(failed, previousFailed)), 
				(blocked + getPlusSignal(blocked, previousBlocked)), 
				(notRun + getPlusSignal(notRun, previousNotRun))));
		
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

		builder.append(Messages.Testopia_Utils_Summary());
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
			executionStatusTextColored = Messages.Testopia_Utils_Failed();
		}
		if (executionStatus == Status.PASSED) {
			executionStatusTextColored = Messages.Testopia_Utils_Passed();
		}
		if (executionStatus == Status.BLOCKED) {
			executionStatusTextColored = Messages.Testopia_Utils_Blocked();
		}
		if (executionStatus == Status.IDLE) {
			executionStatusTextColored = Messages.Testopia_Utils_NotRun();
		}
		return executionStatusTextColored;
	}
}
