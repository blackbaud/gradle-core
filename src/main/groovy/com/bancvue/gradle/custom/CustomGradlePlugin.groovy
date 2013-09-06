/*
 * Copyright 2013 BancVue, LTD
 *
 *      Licensed under the Apache License, Version 2.0 (the "License");
 *      you may not use this file except in compliance with the License.
 *      You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 *      Unless required by applicable law or agreed to in writing, software
 *      distributed under the License is distributed on an "AS IS" BASIS,
 *      WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *      See the License for the specific language governing permissions and
 *      limitations under the License.
 */
package com.bancvue.gradle.custom

import com.bancvue.gradle.maven.MavenPublishExtPlugin
import com.bancvue.gradle.maven.MavenRepositoryProperties
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.tasks.bundling.Zip
import org.gradle.api.tasks.wrapper.Wrapper

class CustomGradlePlugin implements Plugin<Project> {

	static final String PLUGIN_NAME = 'custom-gradle'

	private Project project

	public void apply(Project project) {
		this.project = project
		applyJavaPlugin()
		applyMavenPublishExtPlugin()
		addBuildCustomGradleDistroTask()
		addMavenPublication()
		addCustomWrapperTask()
	}

	private void applyJavaPlugin() {
		project.apply(plugin: 'java')
	}

	private void applyMavenPublishExtPlugin() {
		project.apply(plugin: MavenPublishExtPlugin.PLUGIN_NAME)
	}

	private DownloadGradle addDownloadGradleTask() {
		CustomGradleProperties gradleProperties = new CustomGradleProperties(project)
		DownloadGradle downloadGradleTask = project.tasks.create('downloadGradle', DownloadGradle)
		downloadGradleTask.configure {
			description = 'Download Gradle version from Gradle distributions website'
			gradleVersion = gradleProperties.baseVersion
			destinationDir = project.file("${project.buildDir}/gradle-downloads")
		}
		downloadGradleTask
	}

	private void addBuildCustomGradleDistroTask() {
		CustomGradleProperties gradleProperties = new CustomGradleProperties(project)
		DownloadGradle downloadGradleTask = addDownloadGradleTask()
		Task buildCustomGradleDistroTask = project.tasks.create('buildCustomGradleDistro', Zip)
		buildCustomGradleDistroTask.configure {
			dependsOn { downloadGradleTask }
			doFirst { println "configure internal zip" }
			group = 'Build'
			description = 'Add extra files to company Gradle distribution'
			baseName = gradleProperties.artifactId
			version = gradleProperties.version
			classifier = 'bin'
			from project.zipTree(downloadGradleTask.destinationFile)
			into(downloadGradleTask.distributionNameBase) {
				into('init.d') {
					from createGradleInitializationScript()
				}
			}
		}
	}

	private File createGradleInitializationScript() {
		File gradleInitializationScript = new File(project.buildDir, 'tmp/customized.gradle')
		gradleInitializationScript.parentFile.mkdirs()
		gradleInitializationScript.write(getGradleInitializationScriptContent())
		gradleInitializationScript
	}

	private String getGradleInitializationScriptContent() {
		MavenRepositoryProperties repositoryProperties = new MavenRepositoryProperties(project)
		"""
println "Using the BancVue gradle"

allprojects {
    buildscript {
        repositories {
            mavenLocal()
            maven {
                url "${repositoryProperties.publicUrl}"
            }
        }
    }

    project.ext {
        repositoryName = "${repositoryProperties.name}"
        repositoryPublicUrl = "${repositoryProperties.publicUrl}"
        repositorySnapshotUrl = "${repositoryProperties.snapshotUrl}"
        repositoryReleaseUrl = "${repositoryProperties.releaseUrl}"
    }
}
"""
	}

	private void addMavenPublication() {
		CustomGradleProperties customGradle = new CustomGradleProperties(project)
		project.publishing {
			Task buildCustomGradleDistroTask = project.tasks.getByName('buildCustomGradleDistro')
			publications {
				"customGradleDistro"(MavenPublication) {
					artifact buildCustomGradleDistroTask
					artifactId = customGradle.artifactId
					version = customGradle.version
				}
			}
		}
	}

	private void addCustomWrapperTask() {
		Task wrapper = project.tasks.create('customWrapper', Wrapper)
		wrapper.group = 'Utilities'
		// TODO: add description
		wrapper.distributionUrl = createDistributionUrl()
	}

	private void createDistributionUrl() {
		MavenRepositoryProperties repository = new MavenRepositoryProperties(project)
		CustomGradleProperties customGradle = new CustomGradleProperties(project)
		// TODO: will fail if customGradle.groupName is null, need test and need to figure out what to do in that case
		"${repository.releaseUrl}/" +
				"${customGradle.groupName.replaceAll('.', '/')}/" +
				"${customGradle.artifactId}/${customGradle.version}/" +
				"${customGradle.artifactId}-${customGradle.version}-bin.zip"
	}
}