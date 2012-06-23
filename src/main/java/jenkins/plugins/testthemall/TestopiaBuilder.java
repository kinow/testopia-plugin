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
package jenkins.plugins.testthemall;

import hudson.AbortException;
import hudson.EnvVars;
import hudson.Extension;
import hudson.Launcher;
import hudson.model.Action;
import hudson.model.BuildListener;
import hudson.model.EnvironmentContributingAction;
import hudson.model.Result;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.tasks.BuildStep;
import hudson.tasks.Builder;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import org.kohsuke.stapler.DataBoundConstructor;
import org.mozilla.testopia.model.TestCase;
import org.mozilla.testopia.service.xmlrpc.XmlRpcMiscService;
import org.mozilla.testopia.service.xmlrpc.XmlRpcTestRunService;
import org.mozilla.testopia.transport.TestopiaXmlRpcClient;

/**
 * Testopia Builder.
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 0.1
 */
public class TestopiaBuilder extends Builder {
	/**
	 * Testopia installation name.
	 */
	protected final String testopiaInstallationName;
	/**
	 * Testopia test run ID.
	 */
	protected final Integer testRunId;
	/**
	 * List of build steps that are executed only once per job execution. 
	 */
	protected final List<BuildStep> singleBuildSteps;
	/**
	 * List of build steps that are executed before iterating all test cases.
	 */
	protected final List<BuildStep> beforeIteratingAllTestCasesBuildSteps;
	/**
	 * List of build steps that are executed for each test case.
	 */
	protected final List<BuildStep> iterativeBuildSteps;
	/**
	 * List of build steps that are executed after iterating all test cases.
	 */
	protected final List<BuildStep> afterIteratingAllTestCasesBuildSteps;
	/**
	 * Le descriptor.
	 */
	@Extension
	public static final TestopiaBuilderDescriptor DESCRIPTOR = new TestopiaBuilderDescriptor();
	/**
	 * This constructor is bound to a stapler request. The parameters are 
	 * passed from Jenkins UI.
	 * @param testopiaInstallationName
	 * @param testRunId
	 * @param singleBuildSteps
	 * @param beforeIteratingAllTestCasesBuildSteps
	 * @param iterativeBuildSteps
	 * @param afterIteratingAllTestCasesBuildSteps
	 */
	@DataBoundConstructor
	public TestopiaBuilder(String testopiaInstallationName, 
			Integer testRunId,
			List<BuildStep> singleBuildSteps, 
			List<BuildStep> beforeIteratingAllTestCasesBuildSteps, 
			List<BuildStep> iterativeBuildSteps,
			List<BuildStep> afterIteratingAllTestCasesBuildSteps) {
		this.testopiaInstallationName = testopiaInstallationName;
		this.testRunId = testRunId;
		this.singleBuildSteps = singleBuildSteps;
		this.beforeIteratingAllTestCasesBuildSteps = beforeIteratingAllTestCasesBuildSteps;
		this.iterativeBuildSteps = iterativeBuildSteps;
		this.afterIteratingAllTestCasesBuildSteps = afterIteratingAllTestCasesBuildSteps;
	}
	/**
	 * @return the testopiaInstallationName
	 */
	public String getTestopiaInstallationName() {
		return testopiaInstallationName;
	}
	/**
	 * @return the testRunId
	 */
	public Integer getTestRunId() {
		return testRunId;
	}
	/**
	 * @return the singleBuildSteps
	 */
	public List<BuildStep> getSingleBuildSteps() {
		return singleBuildSteps;
	}
	/**
	 * @return the beforeIteratingAllTestCasesBuildSteps
	 */
	public List<BuildStep> getBeforeIteratingAllTestCasesBuildSteps() {
		return beforeIteratingAllTestCasesBuildSteps;
	}
	/**
	 * @return the iterativeBuildSteps
	 */
	public List<BuildStep> getIterativeBuildSteps() {
		return iterativeBuildSteps;
	}
	/**
	 * @return the afterIteratingAllTestCasesBuildSteps
	 */
	public List<BuildStep> getAfterIteratingAllTestCasesBuildSteps() {
		return afterIteratingAllTestCasesBuildSteps;
	}
	/* (non-Javadoc)
	 * @see hudson.tasks.BuildStepCompatibilityLayer#getProjectAction(hudson.model.AbstractProject)
	 */
	@Override
	public Action getProjectAction(AbstractProject<?, ?> project) {
		return new TestopiaProjectAction(project);
	}
	/**
	 * {@inheritDoc}
	 * 
	 * Executes Testopia automated tests.
	 */
	@Override
	public boolean perform(AbstractBuild<?, ?> build, Launcher launcher,
			BuildListener listener) throws InterruptedException, IOException {
		listener.getLogger().println("Connecting to Testopia to retrieve automated test cases.");
		TestopiaInstallation installation = DESCRIPTOR.getInstallationByName(this.testopiaInstallationName);
		if(installation == null) {
			throw new AbortException("Invalid Testopia installation.");
		}
		URL url = new URL(installation.getUrl());
        TestopiaXmlRpcClient xmlRpcClient = new TestopiaXmlRpcClient(url);
        XmlRpcMiscService misc = new XmlRpcMiscService(xmlRpcClient);
        XmlRpcTestRunService testRunSvc = new XmlRpcTestRunService(xmlRpcClient);
		try {
			misc.login(installation.getUsername(), installation.getPassword());
		} catch (Exception e) {
			e.printStackTrace(listener.getLogger());
			throw new AbortException(e.getMessage());
		}
		//TestRun testRun = testRunSvc.get(this.getTestRunId());
		TestCase[] testCases = testRunSvc.getTestCases(this.getTestRunId());
		listener.getLogger().println("Executing single build steps");
		this.executeSingleBuildSteps(build, launcher, listener);
		listener.getLogger().println("Executing iterative build steps");
		this.executeIterativeBuildSteps(testCases, build, launcher, listener);
		//TODO: look for results
		//TODO: create report
		//TODO: create graphs
		return Boolean.TRUE;
	}
	/**
	 * Executes the list of single build steps.
	 * 
	 * @param build
	 *            Jenkins build.
	 * @param launcher
	 * @param listener
	 * @throws IOException
	 * @throws InterruptedException
	 */
	protected void executeSingleBuildSteps(AbstractBuild<?, ?> build,
			Launcher launcher, BuildListener listener) throws IOException,
			InterruptedException {
		if (singleBuildSteps != null) {
			for (BuildStep b : singleBuildSteps) {
				boolean success = b.perform(build, launcher, listener);
				if(!success) {
					build.setResult(Result.UNSTABLE);
				}
			}
		}
	}
	/**
	 * <p>
	 * Executes iterative build steps. For each automated test case found in the
	 * array of automated test cases, this method executes the iterative builds
	 * steps using Jenkins objects.
	 * </p>
	 * 
	 * @param testCases
	 *            array of automated test cases
	 * @param launcher
	 * @param listener
	 * @throws InterruptedException
	 * @throws IOException
	 */
	protected void executeIterativeBuildSteps(TestCase[] testCases,
			AbstractBuild<?, ?> build,
			Launcher launcher, BuildListener listener) throws IOException,
			InterruptedException {
		if (beforeIteratingAllTestCasesBuildSteps != null) {
			for (BuildStep b : beforeIteratingAllTestCasesBuildSteps) {
				final boolean success = b.perform(build, launcher, listener);
				if(!success) {
					build.setResult(Result.UNSTABLE);
				}
			}
		}
		for (TestCase automatedTestCase : testCases) {
			if (iterativeBuildSteps != null) {
				final EnvVars iterativeEnvVars = Utils.buildTestCaseEnvVars(automatedTestCase);

				build.addAction(new EnvironmentContributingAction() {
					public void buildEnvVars(AbstractBuild<?, ?> build,
							EnvVars env) {
						env.putAll(iterativeEnvVars);
					}

					public String getUrlName() {
						return null;
					}

					public String getIconFileName() {
						return null;
					}

					public String getDisplayName() {
						return null;
					}
				});
				for (BuildStep b : iterativeBuildSteps) {
					final boolean success = b
							.perform(build, launcher, listener);
					if(!success) {
						build.setResult(Result.UNSTABLE);
					}
				}
			}
		}
		if (afterIteratingAllTestCasesBuildSteps != null) {
			for (BuildStep b : afterIteratingAllTestCasesBuildSteps) {
				final boolean success = b.perform(build, launcher, listener);
				if(!success) {
					build.setResult(Result.UNSTABLE);
				}
			}
		}
	}
}
