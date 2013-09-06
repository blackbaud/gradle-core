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
package com.bancvue.gradle.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.TaskAction


class PrintClasspath extends DefaultTask {

	{
		description = 'Print classpaths of each source set to console (use -Pfullpath=1 to output complete directory paths)'
	}

	@TaskAction
	void printClasspathsForAllSourceSets() {
		project.sourceSets.each { SourceSet sourceSet ->
			printClasspathsForSourceSet(sourceSet)
		}
	}

	private void printClasspathsForSourceSet(SourceSet sourceSet) {
		println ""
		println "******************************* SourceSet ${sourceSet.name} *******************************"
		printClasspathForSourceSet(sourceSet, 'compileClasspath')
		printClasspathForSourceSet(sourceSet, 'runtimeClasspath')
	}

	private void printClasspathForSourceSet(SourceSet sourceSet, String classpathName) {
		List pathsToPrint = getPathsToPrintForSourceSet(sourceSet, classpathName)

		println "${sourceSet.name}.${classpathName}"
		pathsToPrint.each { String pathToPrint ->
			println "  > ${pathToPrint}"
		}
	}

	private boolean shouldPrintFullPath() {
		project.hasProperty('fullpath')
	}

	private List getPathsToPrintForSourceSet(SourceSet sourceSet, String classpathName) {
		boolean printFullPath = shouldPrintFullPath()

		sourceSet."${classpathName}".findAll { File file ->
			file.exists()
		}.collect { File file ->
			(printFullPath || file.isDirectory()) ? file.absolutePath : file.name
		}
	}

}