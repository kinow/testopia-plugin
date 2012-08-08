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

import hudson.model.Action;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.util.ChartUtil;
import hudson.util.DataSetBuilder;

import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.jfree.chart.JFreeChart;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

/**
 * Testopia project action. This changes projects that are configured to run 
 * Testopia integration.
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 0.1
 */
public class TestopiaProjectAction implements Action {

	private static final int DEFAULT_GRAPH_WIDTH  = 500;
	private static final int DEFAULT_GRAPH_HEIGHT = 200;
	
	public static final String ICON_FILE_NAME = "/plugin/testopia/icons/testopia-24x24.png";
	public static final String URL_NAME = "testopiaResult";
	public static final String DISPLAY_NAME = "Testopia";

	/**
	 * Used to figure out if we need to regenerate the graphs or not. Only used
	 * in newGraphNotNeeded() method. Key is the request URI and value is the
	 * number of builds for the project.
	 */
	private transient Map<String, Integer> requestMap = new HashMap<String, Integer>();
	
	protected AbstractProject<?, ?> project;
	
	public TestopiaProjectAction(AbstractProject<?, ?> project) {
		this.project = project;
	}
	/**
	 * @return the project
	 */
	public AbstractProject<?, ?> getProject() {
		return project;
	}
	/**
	 * Gets the build action class.
	 * @return build action class
	 */
	protected Class<TestopiaBuildAction> getBuildActionClass() {
		return TestopiaBuildAction.class;
	}
	/**
	 * Returns the last build action.
	 * 
	 * @return the last build action or <code>null</code> if there is no such
	 *         build action.
	 */
	public TestopiaBuildAction getLastBuildAction() {
		AbstractBuild<?, ?> lastBuild = getLastBuildWithTestopia();
		TestopiaBuildAction action = null;
		if (lastBuild != null) {
			action = lastBuild.getAction(getBuildActionClass());
		}
		return action;
	}
	/**
	 * Retrieves the last build with Testopia in the project.
	 * 
	 * @return Last build with Testopia in the project or <code>null</code>, 
	 * 		   if there is no build with Testopia in the project.
	 */
	private AbstractBuild<?, ?> getLastBuildWithTestopia() {
		AbstractBuild<?, ?> lastBuild = (AbstractBuild<?, ?>) project
				.getLastBuild();
		while (lastBuild != null
				&& lastBuild.getAction(getBuildActionClass()) == null) {
			lastBuild = lastBuild.getPreviousBuild();
		}
		return lastBuild;
	}
	/**
	 * 
	 * Show report f the latest build. If no builds are associated with
	 * Testopia, returns info page.
	 * 
	 * @param req
	 *            Stapler request
	 * @param res
	 *            Stapler response
	 * @throws IOException
	 *             in case of an error
	 */
	public void doIndex(final StaplerRequest req, final StaplerResponse res)
			throws IOException {
		AbstractBuild<?, ?> lastBuild = getLastBuildWithTestopia();
		if (lastBuild == null) {
			res.sendRedirect2("nodata");
		} else {
			int buildNumber = lastBuild.getNumber();
			res.sendRedirect2(String.format("../%d/%s", buildNumber,
					TestopiaBuildAction.URL_NAME));
		}
	}

	public void doGraph(final StaplerRequest req, StaplerResponse res)
			throws IOException {
		if (ChartUtil.awtProblemCause != null) {
			res.sendRedirect2(req.getContextPath() + "/images/headless.png");
			return;
		}

		if (newGraphNotNeeded(req, res)) {
			return;
		}

		final DataSetBuilder<String, ChartUtil.NumberOnlyBuildLabel> dataSetBuilder = new DataSetBuilder<String, ChartUtil.NumberOnlyBuildLabel>();

		populateDataSetBuilder(dataSetBuilder);
		new hudson.util.Graph(-1, getGraphWidth(), getGraphHeight()) {
			protected JFreeChart createGraph() {
				return GraphHelper.createChart(req, dataSetBuilder.build());
			}
		}.doPng(req, res);
	}

	public void doGraphMap(final StaplerRequest req, StaplerResponse res)
			throws IOException {
		if (newGraphNotNeeded(req, res)) {
			return;
		}

		final DataSetBuilder<String, ChartUtil.NumberOnlyBuildLabel> dataSetBuilder = new DataSetBuilder<String, ChartUtil.NumberOnlyBuildLabel>();

		populateDataSetBuilder(dataSetBuilder);
		new hudson.util.Graph(-1, getGraphWidth(), getGraphHeight()) {
			protected JFreeChart createGraph() {
				return GraphHelper.createChart(req, dataSetBuilder.build());
			}
		}.doMap(req, res);
	}

	/**
	 * Checks if it should display graph.
	 * 
	 * @return <code>true</code> if it should display graph and
	 *         <code>false</code> otherwise.
	 */
	public final boolean isDisplayGraph() {
		return project.getBuilds().size() > 0;
	}

	/**
	 * If number of builds hasn't changed and if checkIfModified() returns true,
	 * no need to regenerate the graph. Browser should reuse it's cached image
	 * 
	 * @param req
	 * @param res
	 * @return true, if new image does NOT need to be generated, false otherwise
	 */
	private boolean newGraphNotNeeded(final StaplerRequest req,
			StaplerResponse res) {
		boolean newGraphNotNeeded = false;
		Calendar t = getProject().getLastCompletedBuild().getTimestamp();
		Integer prevNumBuilds = requestMap.get(req.getRequestURI());
		int numBuilds = getProject().getBuilds().size();

		// change null to 0
		prevNumBuilds = prevNumBuilds == null ? 0 : prevNumBuilds;
		if (prevNumBuilds != numBuilds) {
			requestMap.put(req.getRequestURI(), numBuilds);
		}

		if (requestMap.keySet().size() > 10) {
			// keep map size in check
			requestMap.clear();
		}

		if (prevNumBuilds == numBuilds && req.checkIfModified(t, res)) {
			/*
			 * checkIfModified() is after '&&' because we want it evaluated only
			 * if number of builds is different
			 */
			newGraphNotNeeded = true;
		}

		return newGraphNotNeeded;
	}

	protected void populateDataSetBuilder(
			DataSetBuilder<String, ChartUtil.NumberOnlyBuildLabel> dataset) {
		for (AbstractBuild<?, ?> build = getProject().getLastBuild(); build != null; build = build
				.getPreviousBuild()) {
			ChartUtil.NumberOnlyBuildLabel label = new ChartUtil.NumberOnlyBuildLabel(
					build);
			TestopiaBuildAction action = build.getAction(getBuildActionClass());
			if (action != null) {
				final TestopiaResult result = action.getResult();
				final Report report = result.getReport();
				dataset.add(report.getBlocked(), "Blocked", label);
				dataset.add(report.getFailed(), "Failed", label);
				dataset.add(report.getNotRun(), "Not Run", label);
				dataset.add(report.getPassed(), "Passed", label);
			}
		}
	}

	/**
	 * Getter for property 'graphWidth'.
	 * 
	 * @return Value for property 'graphWidth'.
	 */
	public int getGraphWidth() {
		return DEFAULT_GRAPH_WIDTH;
	}

	/**
	 * Getter for property 'graphHeight'.
	 * 
	 * @return Value for property 'graphHeight'.
	 */
	public int getGraphHeight() {
		return DEFAULT_GRAPH_HEIGHT;
	}
	/* (non-Javadoc)
	 * @see hudson.model.Action#getIconFileName()
	 */
	public String getIconFileName() {
		return ICON_FILE_NAME;
	}

	/* (non-Javadoc)
	 * @see hudson.model.Action#getDisplayName()
	 */
	public String getDisplayName() {
		return DISPLAY_NAME;
	}

	/* (non-Javadoc)
	 * @see hudson.model.Action#getUrlName()
	 */
	public String getUrlName() {
		return URL_NAME;
	}

}
