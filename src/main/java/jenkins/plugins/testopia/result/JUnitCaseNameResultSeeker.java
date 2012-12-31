/*
 * The MIT License
 *
 * Copyright (c) <2011> <Bruno P. Kinoshita>
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
package jenkins.plugins.testopia.result;

import hudson.Extension;
import hudson.Launcher;
import hudson.model.BuildListener;
import hudson.model.Result;
import hudson.model.AbstractBuild;
import hudson.tasks.junit.JUnitParser;
import hudson.tasks.junit.SuiteResult;
import hudson.tasks.junit.TestResult;
import hudson.tasks.junit.CaseResult;

import java.io.IOException;

import jenkins.plugins.testopia.TestopiaSite;
import jenkins.plugins.testopia.util.Messages;

import org.kohsuke.stapler.DataBoundConstructor;
import org.mozilla.testopia.model.Status;

/**
 * <p>Seeks for test results matching each JUnit Case Result name with the key 
 * custom field.</p>
 * 
 * <p>Skips JUnit Case Results that were disabled.</p>
 * 
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 1.3
 */
public class JUnitCaseNameResultSeeker extends ResultSeeker {

	private static final long serialVersionUID = 2278496777245515704L;

	/**
	 * @param includePattern Include pattern used when looking for results
	 */
	@DataBoundConstructor
	public JUnitCaseNameResultSeeker(String includePattern) {
		super(includePattern);
	}

	@Extension
	public static class DescriptorImpl extends ResultSeekerDescriptor {
		/*
		 * (non-Javadoc)
		 * 
		 * @see hudson.model.Descriptor#getDisplayName()
		 */
		@Override
		public String getDisplayName() {
			return Messages.Testopia_JUnit_CaseName();
		}
	}

	@Override
	public void seek(TestCaseWrapper[] automatedTestCases, AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener, TestopiaSite testopia) throws ResultSeekerException {
		listener.getLogger().println( Messages.Testopia_JUnit_LookingForTestCases() ); // i18n
		try {
			final JUnitParser parser = new JUnitParser(false);
			final TestResult testResult = parser.parse(this.includePattern, build, launcher, listener);
			
			for(SuiteResult suiteResult : testResult.getSuites()) {
				for(CaseResult caseResult : suiteResult.getCases()) {
					for(TestCaseWrapper automatedTestCase : automatedTestCases) {
						if(! caseResult.isSkipped() && caseResult.getName().equals(automatedTestCase.getAlias())) {
							Status status = this.getStatus(caseResult);
							automatedTestCase.setStatusId(status.getValue());
							try {
								//listener.getLogger().println( Messages.Testopia_ResultSeeker_UpdateAutomatedTestCases() );
								testopia.updateTestCase(automatedTestCase);
							} catch (RuntimeException e) {
								build.setResult(Result.UNSTABLE);
								e.printStackTrace(listener.getLogger());
							}
						}
					}
				}
			}
		} catch (IOException e) {
			throw new ResultSeekerException(e);
		} catch (InterruptedException e) {
			throw new ResultSeekerException(e);
		}
	}

	private Status getStatus(CaseResult caseResult) {
		if (caseResult.getStatus() == CaseResult.Status.PASSED) {
			return Status.PASSED;
		}
		if (caseResult.getStatus() == CaseResult.Status.FAILED) {
			return Status.FAILED;
		}
		if (caseResult.getStatus() == CaseResult.Status.SKIPPED) {
			return Status.BLOCKED;
		}
		return Status.IDLE;
	}

}
