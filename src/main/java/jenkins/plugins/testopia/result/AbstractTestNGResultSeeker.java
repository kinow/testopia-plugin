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

import com.tupilabs.testng.parser.TestNGParser;

/**
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 3.1
 */
public abstract class AbstractTestNGResultSeeker extends ResultSeeker {

	private static final long serialVersionUID = -1017414394764084125L;
	
	public static final String PASS = "PASS";
	public static final String FAIL = "FAIL";
	public static final String SKIP = "SKIP";
	
	public static final String TEXT_XML_CONTENT_TYPE = "text/xml";

	protected final TestNGParser parser = new TestNGParser();
	
	private boolean attachTestNGXML = false;
	
	private boolean markSkippedTestAsBlocked = false;
	
	public AbstractTestNGResultSeeker(String includePattern, boolean attachTestNGXML, boolean markSkippedTestAsBlocked) {
		super(includePattern);
		this.attachTestNGXML = attachTestNGXML;
		this.markSkippedTestAsBlocked = markSkippedTestAsBlocked;
	}

	public void setAttachTestNGXML(boolean attachTestNGXML) {
		this.attachTestNGXML = attachTestNGXML;
	}
	
	public boolean isAttachTestNGXML() {
		return attachTestNGXML;
	}
	
	public void setMarkSkippedTestAsBlocked(boolean markSkippedTestAsBlocked) {
		this.markSkippedTestAsBlocked = markSkippedTestAsBlocked;
	}
	
	public boolean isMarkSkippedTestAsBlocked() {
		return markSkippedTestAsBlocked;
	}
}
