/**
 * Copyright 2013 BancVue, LTD
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.bancvue.gradle.test

import com.bancvue.gradle.GradlePluginMixin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.testing.Test

@Mixin(GradlePluginMixin)
class NamedTestConfigurationPlugin implements Plugin<Project> {

	protected Project project
	protected String configurationName

	NamedTestConfigurationPlugin(String configurationName) {
		this.configurationName = configurationName
	}

	public void apply(Project project) {
		this.project = project
		if (configurationExistsAsSrcDir()) {
			initializeNamedTestConfiguration()
		}
	}

	private boolean configurationExistsAsSrcDir() {
		project.file("src/${configurationName}").exists()
	}

	protected void initializeNamedTestConfiguration() {
		project.apply(plugin: 'java')
		addTestConfiguration()
		addTestSourceSet()
		addNamedTestTask()
	}

	private void addTestConfiguration() {
		createNamedConfigurationExtendingFrom(configurationName, 'test')
	}

	private void addTestSourceSet() {
		project.sourceSets {
			"${configurationName}" {
				compileClasspath = main.output + test.compileClasspath - test.output +
						project.configurations."${configurationName}Compile" +
						project.configurations."${configurationName}CompileOnly"
				runtimeClasspath = project.sourceSets."${configurationName}".output + main.output +
						test.runtimeClasspath - test.output + project.configurations."${configurationName}Runtime"
			}
		}
	}

	private void addNamedTestTask() {
		createAndConfigureNamedTestTask()
		configureNamedTestToRunAfterUnitTest()
	}

	private void createAndConfigureNamedTestTask() {
		Task testTask = project.tasks.create(configurationName, Test)
		testTask.configure {
			description = "Runs the ${configurationName} tests."
			group = TestExtPlugin.VERIFICATION_GROUP_NAME
		}
 		testTask.conventionMapping.with {
			classpath = { project.sourceSets."${configurationName}".runtimeClasspath }
			testClassesDir = { project.sourceSets."${configurationName}".output.classesDir }
		}
		testTask.reports.html.conventionMapping.map("destination", {
			new File(project.reporting.baseDir, "${configurationName}s")
		})
		testTask.reports.junitXml.conventionMapping.map("destination", {
			new File(project.buildDir, "${configurationName}-results")
		})
		testTask
	}

	private void configureNamedTestToRunAfterUnitTest() {
		Task namedTest = project.tasks.getByName(configurationName)
		Task unitTest = project.tasks.getByName('test')

		namedTest.mustRunAfter(unitTest)
	}

}
