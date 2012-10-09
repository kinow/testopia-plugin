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
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.BuildListener;
import hudson.model.AbstractBuild;
import hudson.remoting.VirtualChannel;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import jenkins.plugins.testopia.TestopiaSite;
import jenkins.plugins.testopia.util.Messages;

import org.kohsuke.stapler.DataBoundConstructor;
import org.mozilla.testopia.model.Status;
import org.tap4j.consumer.TapConsumer;
import org.tap4j.consumer.TapConsumerFactory;
import org.tap4j.model.Directive;
import org.tap4j.model.TestResult;
import org.tap4j.model.TestSet;
import org.tap4j.util.DirectiveValues;

/**
 * <p>Seeks for test results matching each TAP file name with the key 
 * custom field.</p>
 * 
 * <p>Skips TAP Streams that were skipped.</p>
 * 
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 0.1
 */
public class TAPFileNameResultSeeker extends ResultSeeker {
	
	private static final long serialVersionUID = 3068999690225092293L;

	protected static final String TEXT_PLAIN_CONTENT_TYPE = "text/plain";
	/**
	 * @param includePattern
	 */
	@DataBoundConstructor
	public TAPFileNameResultSeeker(String includePattern) {
		super(includePattern);
	}

	@Extension
	public static class DescriptorImpl extends ResultSeekerDescriptor {
		/*
		 * (non-Javadoc)
		 * @see hudson.model.Descriptor#getDisplayName()
		 */
		@Override
		public String getDisplayName() {
			return Messages.Testopia_TAPResultSeeker_TAPFileName();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see jenkins.plugins.testopia.result.ResultSeeker#seek(org.mozilla.testopia.model.TestCase[], hudson.model.AbstractBuild, hudson.Launcher, hudson.model.BuildListener, jenkins.plugins.testopia.TestopiaSite)
	 */
	@Override
	public void seek(final TestCaseWrapper[] automatedTestCases, AbstractBuild<?, ?> build, Launcher launcher, final BuildListener listener, TestopiaSite testopia) throws ResultSeekerException {
		
		try {
			final Map<String, TestSet> testSets = build.getWorkspace().act(new FilePath.FileCallable<Map<String, TestSet>>() {
				private static final long serialVersionUID = 1L;

				private Map<String, TestSet> testSets;
				
				public Map<String, TestSet> invoke(File workspace, VirtualChannel channel)
						throws IOException, InterruptedException {
					final String[] tapFiles = TAPFileNameResultSeeker.this.scan(workspace, includePattern, listener);
					
					testSets = new HashMap<String, TestSet>(tapFiles.length);
					
					for(String tapFile : tapFiles) {
						final File input = new File(workspace, tapFile);
						final TapConsumer tapConsumer = TapConsumerFactory.makeTap13YamlConsumer();
						final TestSet testSet = tapConsumer.load(input);
						testSets.put(input.getName(), testSet);
					}
					
					return testSets;
				}
			});
			
			for(String key : testSets.keySet()) {
				for(TestCaseWrapper automatedTestCase : automatedTestCases) {
					String value = automatedTestCase.getAlias();
					String tapFileNameWithoutExtension = key;
					int extensionIndex = tapFileNameWithoutExtension.lastIndexOf('.');
					if ( extensionIndex != -1 ) {
						tapFileNameWithoutExtension = tapFileNameWithoutExtension.substring(0, tapFileNameWithoutExtension.lastIndexOf('.'));
					}
					if(tapFileNameWithoutExtension.equals(value)) {
						final Status status = this.getExecutionStatus(testSets.get(key));
						automatedTestCase.setStatusId(status.getValue());
						this.handleResult(automatedTestCase, build, listener, testopia, status, testSets, key);
					}
				}
			}
		} catch (IOException e) {
			throw new ResultSeekerException(e);
		} catch (InterruptedException e) {
			throw new ResultSeekerException(e);
		}
	}

	private void handleResult(TestCaseWrapper tc, final AbstractBuild<?, ?> build, BuildListener listener, TestopiaSite testopia, Status status, final Map<String, TestSet> testSets, final String key) {
		testopia.updateTestCase(tc);
	}

	/**
	 * Gets execution status for a Test Set.
	 * @param testSet
	 * @return
	 */
	protected Status getExecutionStatus(TestSet testSet) {
		Status status = Status.PASSED;
		if (isSkipped(testSet)) {
			status = Status.BLOCKED;
		} else if (isFailed(testSet)) {
			status = Status.FAILED;
		}
		return status;
	}
	
	/**
	 * Checks if a test set contains a plan with skip directive or any test case
	 * with the same.
	 */
	private boolean isSkipped(TestSet testSet) {
		boolean r = false;

		if (testSet.getPlan().isSkip()) {
			r = true;
		} else {
			for (TestResult testResult : testSet.getTestResults()) {
				final Directive directive = testResult.getDirective();
				if (directive != null
						&& directive.getDirectiveValue() == DirectiveValues.SKIP) {
					r = true;
					break;
				}
			}
		}
		return r;
	}

	/**
	 * Checks if a test set contains not ok's, bail out!'s or a TO-DO directive.
	 */
	private boolean isFailed(TestSet testSet) {
		boolean r = false;

		if (testSet.containsNotOk() || testSet.containsBailOut()) {
			r = true;
		} else {
			for (TestResult testResult : testSet.getTestResults()) {
				final Directive directive = testResult.getDirective();
				if (directive != null
						&& directive.getDirectiveValue() == DirectiveValues.TODO) {
					r = true;
					break;
				}
			}
		}

		return r;
	}
}