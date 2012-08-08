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

import hudson.model.Action;
import hudson.model.AbstractBuild;

import java.io.Serializable;

import org.kohsuke.stapler.StaplerProxy;

/**
 * Testopia build action.
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 0.1
 */
public class TestopiaBuildAction implements Action, Serializable, StaplerProxy {
	/**
	 * serialVersionUID.
	 */
	private static final long serialVersionUID = -3802972892292646194L;
	public static final String DISPLAY_NAME = "Testopia";
	public static final String ICON_FILE_NAME = "/plugin/testopia/icons/testopia-24x24.png";
	public static final String URL_NAME = "testopiaResult";

	private AbstractBuild<?, ?> build;
	private TestopiaResult result;

	public TestopiaBuildAction(AbstractBuild<?, ?> build, TestopiaResult result) {
		this.build = build;
		this.result = result;
	}

	public String getDisplayName() {
		return DISPLAY_NAME;
	}

	public String getIconFileName() {
		return ICON_FILE_NAME;
	}

	public String getUrlName() {
		return URL_NAME;
	}

	public Object getTarget() {
		return this.result;
	}

	public AbstractBuild<?, ?> getBuild() {
		return build;
	}

	/**
	 * @return Testopia job execution result
	 */
	public TestopiaResult getResult() {
		return this.result;
	}

	/**
	 * @return Previous Testopia report
	 */
	private Report getPreviousReport() {
		TestopiaResult previousResult = this.getPreviousResult();
		Report previousReport = null;
		if (previousResult != null) {
			previousReport = previousResult.getReport();
		}
		return previousReport;
	}

	/**
	 * @return Previous Testopia job execution result
	 */
	public TestopiaResult getPreviousResult() {
		TestopiaBuildAction previousAction = this.getPreviousAction();
		TestopiaResult previousResult = null;
		if (previousAction != null) {
			previousResult = previousAction.getResult();
		}
		return previousResult;
	}

	/**
	 * @return Previous Build Action
	 */
	public TestopiaBuildAction getPreviousAction() {
		if (this.build != null) {
			AbstractBuild<?, ?> previousBuild = this.build.getPreviousBuild();
			if (previousBuild != null) {
				return previousBuild.getAction(TestopiaBuildAction.class);
			}
		}
		return null;
	}

	/**
	 * @return Report summary
	 */
	public String getSummary() {
		return Utils.createReportSummary(result.getReport(), this.getPreviousReport());
	}

	/**
	 * @return Detailed Report summary
	 */
	public String getDetails() {
		return Utils.createReportSummaryDetails(result.getReport(), this.getPreviousReport());
	}

}
