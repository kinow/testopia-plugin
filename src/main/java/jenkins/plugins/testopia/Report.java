/* 
 * The MIT License
 * 
 * Copyright (c) 2010 Bruno P. Kinoshita <http://www.kinoshita.eti.br>
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

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import jenkins.plugins.testopia.result.TestCaseWrapper;

/**
 * Build Report.
 * 
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 0.1
 */
public class Report implements Serializable {

	private static final long serialVersionUID = 3686192971774873173L;
	
	private int passed 	= 0;
	private int failed 	= 0;
	private int blocked	= 0;
	private int notRun	= 0;

	private final List<TestCaseWrapper> testCases;

	/**
	 * Default constructor.
	 */
	Report() {
		super();
		this.testCases = new LinkedList<TestCaseWrapper>();
	}

	/**
	 * @return the tests total
	 */
	public int getTestsTotal() {
		return passed + failed + blocked + notRun;
	}

	/**
	 * @return the passed
	 */
	public int getPassed() {
		return passed;
	}

	/**
	 * @param passed
	 *            the passed to set
	 */
	public void setPassed(int passed) {
		this.passed = passed;
	}

	/**
	 * @return the failed
	 */
	public int getFailed() {
		return failed;
	}

	/**
	 * @param failed
	 *            the failed to set
	 */
	public void setFailed(int failed) {
		this.failed = failed;
	}

	/**
	 * @return the blocked
	 */
	public int getBlocked() {
		return blocked;
	}

	/**
	 * @param blocked
	 *            the blocked to set
	 */
	public void setBlocked(int blocked) {
		this.blocked = blocked;
	}

	/**
	 * @return the notRun
	 */
	public int getNotRun() {
		return notRun;
	}

	/**
	 * @param notRun
	 *            the notRun to set
	 */
	public void setNotRun(int notRun) {
		this.notRun = notRun;
	}

	/**
	 * @return the buildId
	 */
	public int getBuildId() {
		return (this.testCases != null && this.testCases.size() > 0 ? this.testCases.get(0).getBuildId() : 0);
	}

	/**
	 * @return the envId
	 */
	public int getEnvId() {
		return (this.testCases != null && this.testCases.size() > 0 ? this.testCases.get(0).getEnvId() : 0);
	}
	
	/**
	 * @return the runId
	 */
	public int getRunId() {
		return (this.testCases != null && this.testCases.size() > 0 ? this.testCases.get(0).getRunId() : 0);
	}

	/**
	 * @param testCase
	 */
	public void addTestCase(TestCaseWrapper testCase) {
		this.testCases.add(testCase);
	}
	
	/**
	 * @return the testCases
	 */
	public List<TestCaseWrapper> getTestCases() {
		return testCases;
	}

}
