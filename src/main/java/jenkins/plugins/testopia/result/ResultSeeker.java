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
package jenkins.plugins.testopia.result;

import hudson.DescriptorExtensionList;
import hudson.Launcher;
import hudson.Util;
import hudson.model.BuildListener;
import hudson.model.Describable;
import hudson.model.AbstractBuild;
import hudson.model.Descriptor;
import hudson.model.Hudson;
import hudson.model.Node;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

import jenkins.plugins.testopia.TestopiaSite;
import jenkins.plugins.testopia.util.Messages;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.types.FileSet;

/**
 * Seeks for test results.
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 0.1
 */
public abstract class ResultSeeker implements Serializable, Describable<ResultSeeker>, Comparable<ResultSeeker> {
	/**
	 * serialVersionUID.
	 */
	private static final long serialVersionUID = -4649208585216986990L;
	/**
	 * Le logger.
	 */
	private static final Logger LOGGER = Logger.getLogger("jenkins.plugins.testopia");
	/**
	 * Include pattern used when looking for results.
	 */
	protected final String includePattern;
	/**
	 * Creates a result seeker passing a ant-like pattern to look for results.
	 * @param includePattern Include pattern when looking for results.
	 */
	public ResultSeeker(String includePattern) {
		super();
		this.includePattern = includePattern;
	}
	/**
	 * @return the includePattern
	 */
	public String getIncludePattern() {
		return includePattern;
	}
	/*
	 * (non-Javadoc)
	 * @see hudson.model.Describable#getDescriptor()
	 */
	public ResultSeekerDescriptor getDescriptor() {
		return (ResultSeekerDescriptor) Hudson.getInstance().getDescriptor(getClass());
	}
	public static DescriptorExtensionList<ResultSeeker, Descriptor<ResultSeeker>> all() {
		return Hudson.getInstance().<ResultSeeker, Descriptor<ResultSeeker>> getDescriptorList(ResultSeeker.class);
	}
	public static DescriptorExtensionList<ResultSeeker, Descriptor<ResultSeeker>> allExcept(
			Node current) {
		return Hudson.getInstance().<ResultSeeker, Descriptor<ResultSeeker>> getDescriptorList(ResultSeeker.class);
	}
	/**
	 * <p>Seeks for Test Results in a directory. It tries to match the
	 * includePattern with files in this directory.</p>
	 * 
	 * <p>It looks for results using the include pattern, but this value 
	 * is matched within the workspace. It means that your result files have 
	 * to be relative to your workspace.</p>
	 * 
	 * <p>For each result found, it is automatically updated in Testopia.</p>
	 * 
	 * @param automatedTestcases Automated test cases
	 * @param workspace Build workspace, used when looking for results using the include pattern
	 * @param listener Build listener for logging
	 * @param testopia Testopia site for updating test status
	 * @throws ResultSeekerException
	 */
	public abstract void seek(TestCaseWrapper[] automatedTestCases, AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener, TestopiaSite testopia) throws ResultSeekerException;
	/**
	 * Retrieves the file content encoded in Base64.
	 * 
	 * @param file
	 *            file to read the content.
	 * @return file content encoded in Base64.
	 * @throws IOException
	 */
	protected String getBase64FileContent(File file) throws IOException {
		byte[] fileData = FileUtils.readFileToByteArray(file);
		return Base64.encodeBase64String(fileData);
	}
	/**
	 * Scans a directory for files matching the includes pattern.
	 * 
	 * @param directory the directory to scan.
	 * @param includes the includes pattern.
	 * @param listener Hudson Build listener.
	 * @return array of strings of paths for files that match the includes pattern in the directory.
	 * @throws IOException
	 */
	protected String[] scan(final File directory, final String includes, final BuildListener listener) throws IOException {
		String[] fileNames = new String[0];

		if (StringUtils.isNotBlank(includes)) {
			FileSet fs = null;
			try {
				fs = Util.createFileSet(directory, includes);

				DirectoryScanner ds = fs.getDirectoryScanner();
				fileNames = ds.getIncludedFiles();
			} catch (BuildException e) {
				e.printStackTrace(listener.getLogger());
				throw new IOException(e);
			}
		}
		
		if(LOGGER.isLoggable(Level.FINE)) {
			for(String fileName : fileNames) {
				LOGGER.log(Level.FINE, Messages.Testopia_ResultSeeker_TestResultFound(fileName));
			}
		}
		return fileNames;
	}
	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(ResultSeeker o) {
		return o != null ? this.getDescriptor().getDisplayName().compareTo(o.getDescriptor().getDisplayName()) : 0;
	}

}
