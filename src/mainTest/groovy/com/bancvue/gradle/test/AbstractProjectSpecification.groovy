/*
 * Copyright 2014 BancVue, LTD
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.bancvue.gradle.test

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

abstract class AbstractProjectSpecification extends Specification {

    @Rule
    public TemporaryFolder tmpFolder
    private File projectDir
    private Project aProject
    private ProjectFileSystem aProjectFS

    def setup() {
        projectDir = tmpFolder.root
        aProject = createProject()
        aProjectFS = new ProjectFileSystem(aProject.rootDir)
    }

    protected String getProjectName() {
        "root"
    }

    protected Project getProject() {
        aProject
    }

    protected void setProject(Project project) {
        aProject = project
    }

    protected ProjectFileSystem getProjectFS() {
        aProjectFS
    }

    protected void evaluateProject() {
        aProject.evaluate()
    }

    protected Project createProject() {
        ProjectBuilder.builder()
                .withName("${projectName}-project")
                .withProjectDir(projectDir)
                .build()
    }

    protected Project createSubProject(String subProjectName) {
        File subProjectDir = aProjectFS.file(subProjectName)

        ProjectBuilder.builder()
                .withName(subProjectName)
                .withProjectDir(subProjectDir)
                .withParent(aProject)
                .build()
    }

    protected void setArtifactId(String artifactId) {
        aProject.ext['artifactId'] = artifactId
    }
}
