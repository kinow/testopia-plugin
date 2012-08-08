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

import java.util.ArrayList;
import java.util.List;

import jenkins.plugins.testopia.result.TestCaseWrapper;

import org.mozilla.testopia.TestopiaAPI;
import org.mozilla.testopia.model.Status;
import org.mozilla.testopia.model.TestCase;
import org.mozilla.testopia.model.TestRun;

/**
 * Testopia site.
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 0.1
 */
public class TestopiaSite {
	/**
	 * Testopia API.
	 */
	private final TestopiaAPI api;
	/**
	 * Report.
	 */
	protected final Report report;
	/**
	 * Constructor with args.
	 * @param api
	 */
	public TestopiaSite(TestopiaAPI api) {
		this.api = api;
		this.report = new Report();
	}
	/**
	 * @return the api
	 */
	public TestopiaAPI getApi() {
		return api;
	}
	/**
	 * @return the report
	 */
	public Report getReport() {
		return report;
	}
	/**
	 * Filter an array of test cases for automated test cases only.
	 * @param testCaseRun 
	 * @param testCases array of test cases
	 * @return filtered array of automated test cases
	 */
	public TestCaseWrapper[] getTestCases(TestRun testCaseRun, TestCase[] testCases) {
		List<TestCaseWrapper> automatedTestCases = new ArrayList<TestCaseWrapper>();
		if(testCases != null) {
			for(TestCase testCase : testCases) {
				if(testCase != null && testCase.getAutomated()) {
					TestCaseWrapper tcw = new TestCaseWrapper(testCase);
					tcw.setBuildId(Integer.parseInt(testCaseRun.getBuild()));
					tcw.setEnvId(Integer.parseInt(testCaseRun.getEnvironment()));
					tcw.setRunId(testCaseRun.getId());
					automatedTestCases.add(tcw);
				} // else drop it
			}
		}
		return automatedTestCases.toArray(new TestCaseWrapper[0]);
	}
	/**
	 * @param tc
	 */
	public void updateTestCase(TestCaseWrapper tc) {
		if(tc.getStatusId() != Status.IDLE.getValue()) {
			this.api.update(tc, tc.getRunId(), tc.getBuildId(), tc.getEnvId());
			report.addTestCase(tc);
		}
	}
}
