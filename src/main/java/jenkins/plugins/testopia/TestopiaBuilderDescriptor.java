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

import hudson.CopyOnWrite;
import hudson.model.AbstractProject;
import hudson.model.Descriptor;
import hudson.model.FreeStyleProject;
import hudson.tasks.BuildStep;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import hudson.tasks.Publisher;
import hudson.util.FormValidation;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.QueryParameter;

/**
 * Descriptor for TestopiaBuilder extension point.
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 0.1
 */
public class TestopiaBuilderDescriptor extends Descriptor<Builder> {

	// exposed for Jelly
    public final Class<TestopiaBuilder> testopiaBuilderType	 = TestopiaBuilder.class;
    
	@CopyOnWrite
	private volatile TestopiaInstallation[] installations = new TestopiaInstallation[0];

	public TestopiaBuilderDescriptor() {
		super(TestopiaBuilder.class);
		load();
	}
	
	@Override
	public String getDisplayName() {
		return "Invoke Testopia";
	}

	/**
	 * @return the installations
	 */
	public TestopiaInstallation[] getInstallations() {
		return installations;
	}

	public TestopiaInstallation getInstallationByName(String name) {
		TestopiaInstallation installation = null;
		for (TestopiaInstallation ti : installations) {
			if (ti.getName().equals(name)) {
				installation = ti;
				break;
			}
		}
		return installation;
	}

	public boolean configure(org.kohsuke.stapler.StaplerRequest req,
			net.sf.json.JSONObject json) throws Descriptor.FormException {
		this.installations = req.bindParametersToList(
				TestopiaInstallation.class, "Testopia.").toArray(
				new TestopiaInstallation[0]);
		save();
		return Boolean.TRUE;
	};

	// exposed for Jelly
	public List<Descriptor<? extends BuildStep>> getApplicableBuildSteps(
			AbstractProject<?, ?> p) {
		return getBuildSteps();
	}

	public static List<Descriptor<? extends BuildStep>> getBuildSteps() {
		List<Descriptor<? extends BuildStep>> list = new ArrayList<Descriptor<? extends BuildStep>>();
		addTo(Builder.all(), list);
		addTo(Publisher.all(), list);
		return list;
	}

	private static void addTo(
			List<? extends Descriptor<? extends BuildStep>> source,
			List<Descriptor<? extends BuildStep>> list) {
		for (Descriptor<? extends BuildStep> d : source) {
			if (d instanceof BuildStepDescriptor) {
				BuildStepDescriptor<?> bsd = (BuildStepDescriptor<?>) d;
				if (bsd.isApplicable(FreeStyleProject.class)) {
					list.add(d);
				}
			}
		}
	}
	
	/* 
	 * --- Validation methods ---
	 */
	public FormValidation doCheckMandatory(@QueryParameter String value) {
		FormValidation returnValue = FormValidation.ok();
		if (StringUtils.isBlank( value )) {
			returnValue = FormValidation.error( "Required value." );
		}
		return returnValue;
	}

}
