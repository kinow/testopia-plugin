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
package jenkins.plugins.testopia.result;

import java.util.Date;

import org.mozilla.testopia.model.TestCase;

/**
 * Wrapper for Testopia test case.
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 0.1
 */
public class TestCaseWrapper extends TestCase {
	/**
	 * serialVersionUID.
	 */
	private static final long serialVersionUID = -8109674018485577268L;
	/**
	 * Run ID.
	 */
	private Integer runId;
	/**
	 * Build ID.
	 */
	private Integer buildId;
	/**
	 * Environment ID.
	 */
	private Integer envId;
	/**
	 * No args constructor.
	 */
	public TestCaseWrapper() {
		super();
	}
	/**
	 * Constructor with args.
	 * @param id
	 * @param statusId
	 * @param categoryId
	 * @param priorityId
	 * @param authorId
	 * @param defaultTesterId
	 * @param creationDate
	 * @param estimatedTime
	 * @param automated
	 * @param sortKey
	 * @param script
	 * @param arguments
	 * @param summary
	 * @param requirement
	 * @param alias
	 */
	public TestCaseWrapper(Integer id, Integer statusId, Integer categoryId,
			Integer priorityId, Integer authorId, Integer defaultTesterId,
			Date creationDate, Date estimatedTime, Boolean automated,
			String sortKey, String script, String arguments, String summary,
			String requirement, String alias) {
		super(id, statusId, categoryId, priorityId, authorId, defaultTesterId,
				creationDate, estimatedTime, automated, sortKey, script, arguments,
				summary, requirement, alias);
	}
	/**
	 * Constructor with args.
	 * @param id
	 * @param statusId
	 * @param categoryId
	 * @param priorityId
	 * @param authorId
	 * @param defaultTesterId
	 * @param creationDate
	 * @param estimatedTime
	 * @param automated
	 * @param sortKey
	 * @param script
	 * @param arguments
	 * @param summary
	 * @param requirement
	 * @param alias
	 * @param runId
	 * @param buildId
	 * @param envId
	 */
	public TestCaseWrapper(Integer id, Integer statusId, Integer categoryId,
			Integer priorityId, Integer authorId, Integer defaultTesterId,
			Date creationDate, Date estimatedTime, Boolean automated,
			String sortKey, String script, String arguments, String summary,
			String requirement, String alias,
			Integer runId, Integer buildId, Integer envId) {
		super(id, statusId, categoryId, priorityId, authorId, defaultTesterId,
				creationDate, estimatedTime, automated, sortKey, script, arguments,
				summary, requirement, alias);
		this.runId = runId;
		this.buildId = buildId;
		this.envId = envId;
	}
	/**
	 * @param tc
	 * @param runId
	 * @param buildId
	 * @param envId
	 */
	public TestCaseWrapper(TestCase tc, 
			Integer runId, Integer buildId, Integer envId) {
		super(tc.getId(), 
			  tc.getStatusId(), 
			  tc.getCategoryId(), 
			  tc.getPriorityId(), 
			  tc.getAuthorId(), 
			  tc.getDefaultTesterId(),
			  tc.getCreationDate(), 
			  tc.getEstimatedTime(), 
			  tc.getAutomated(), 
			  tc.getSortKey(), 
			  tc.getScript(), 
			  tc.getArguments(),
			  tc.getSummary(), 
			  tc.getRequirement(), 
			  tc.getAlias());
		this.runId = runId;
		this.buildId = buildId;
		this.envId = envId;
	}
	/**
	 * 
	 * @param tc
	 */
	public TestCaseWrapper(TestCase tc) {
		super(tc.getId(), 
			  tc.getStatusId(), 
			  tc.getCategoryId(), 
			  tc.getPriorityId(), 
			  tc.getAuthorId(), 
			  tc.getDefaultTesterId(),
			  tc.getCreationDate(), 
			  tc.getEstimatedTime(), 
			  tc.getAutomated(), 
			  tc.getSortKey(), 
			  tc.getScript(), 
			  tc.getArguments(),
			  tc.getSummary(), 
			  tc.getRequirement(), 
			  tc.getAlias());
	}
	/**
	 * @return the runId
	 */
	public Integer getRunId() {
		return runId;
	}
	/**
	 * @param runId the runId to set
	 */
	public void setRunId(Integer runId) {
		this.runId = runId;
	}
	/**
	 * @return the buildId
	 */
	public Integer getBuildId() {
		return buildId;
	}
	/**
	 * @param buildId the buildId to set
	 */
	public void setBuildId(Integer buildId) {
		this.buildId = buildId;
	}
	/**
	 * @return the envId
	 */
	public Integer getEnvId() {
		return envId;
	}
	/**
	 * @param envId the envId to set
	 */
	public void setEnvId(Integer envId) {
		this.envId = envId;
	}
}
