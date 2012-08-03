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

import java.io.Serializable;

import org.kohsuke.stapler.DataBoundConstructor;

/**
 * Testopia installation. Has the parameters necessary for connecting 
 * and logging in to Testopia.
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 0.1
 */
public class TestopiaInstallation implements Serializable {

	private static final long serialVersionUID = -2968094597929236867L;
	
	private final String name;
	private final String username;
	private final String password;
	private final String url;
	private final String properties;
	
	/**
	 * @param name
	 * @param username
	 * @param password
	 * @param url
	 * @param properties
	 */
	@DataBoundConstructor
	public TestopiaInstallation(String name, String username, String password, String url, String properties) {
		super();
		this.name = name;
		this.username = username;
		this.password = password;
		this.url = url;
		this.properties = properties;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}
	
	/**
	 * @return the properties
	 */
	public String getProperties() {
		return properties;
	}
	
}
