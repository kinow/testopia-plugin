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

import hudson.EnvVars;

import java.util.HashMap;
import java.util.Map;

import org.mozilla.testopia.model.TestCase;

/**
 * Utility methods.
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 0.1
 */
public class Utils {
	// Empty constructor, as this is an util class.
	private Utils() {};
	// Environment Variables names.
	private static final String TESTOPIA_TESTCASE_ID_ENVVAR = "TESTOPIA_TESTCASE_ID";
	private static final String TESTOPIA_TESTCASE_SCRIPT_ENVVAR = "TESTOPIA_TESTCASE_SCRIPT";
	/**
	 * Creates EnvVars for a Test Case.
	 * @param testCase test Case
	 * @return EnvVars (environment variables)
	 */
	public static EnvVars buildTestCaseEnvVars(TestCase testCase) {
		// Build environment variables list
		Map<String, String> envVars = createEnvironmentVariables(testCase);
		final EnvVars buildEnvironment = new EnvVars(envVars);
		return buildEnvironment;
	}
	/**
	 * Creates a Map (name, value) of environment variables for a Test Case.
	 * @param testCase test Case.
	 * @return Map (name, value) of environment variables.
	 */
	public static Map<String, String> createEnvironmentVariables(TestCase testCase) {
		Map<String, String> envVar = new HashMap<String, String>();
		envVar.put(TESTOPIA_TESTCASE_ID_ENVVAR, ""+testCase.getId() );
		envVar.put(TESTOPIA_TESTCASE_SCRIPT_ENVVAR, ""+testCase.getScript());
		return envVar;
	}
}
