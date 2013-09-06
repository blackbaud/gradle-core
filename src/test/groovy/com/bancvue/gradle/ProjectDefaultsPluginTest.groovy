package com.bancvue.gradle

import org.gradle.api.tasks.TaskCollection
import org.gradle.api.tasks.compile.GroovyCompile
import org.gradle.api.tasks.compile.JavaCompile
import org.junit.Before
import org.junit.Test

class ProjectDefaultsPluginTest extends AbstractPluginTest {

	ProjectDefaultsPluginTest() {
		super(ProjectDefaultsPlugin.PLUGIN_NAME)
	}

	@Before
	void setUp() {
		project.version = '1.0'
		setArtifactId('bancvue')
	}

	@Test
	void apply_ShouldSetGroupNameToBancvue() {
		applyPlugin()

		project.group == 'com.bancvue'
	}

	@Test
	void apply_ShouldApplyJavaPluginAndSetCompatibility() {
		project.ext.defaultsJavaVersion = '1.8'

		applyPlugin()

		assertNamedPluginApplied('java')
		assert "${project.sourceCompatibility}" == '1.8'
		assert "${project.targetCompatibility}" == '1.8'
	}

	@Test
	void apply_ShouldSetCompilerEncodingToUtf8() {
		applyPlugin()

		TaskCollection tasks = project.tasks.withType(JavaCompile)
		assert tasks.size() > 0
		tasks.each { JavaCompile task ->
			assert task.options.encoding == 'UTF-8'
		}
	}

	@Test
	void apply_ShouldSetMemorySettingsForJavaAndGroovyCompileTasks() {
		project.ext.defaultsMinHeapSize = '16m'
		project.ext.defaultsMaxHeapSize = '24m'
		project.ext.defaultsMaxPermSize = '8m'

		project.apply(plugin: 'groovy')
		applyPlugin()

		TaskCollection javaTasks = project.tasks.withType(JavaCompile)
		assert javaTasks.size() > 0
		javaTasks.each { JavaCompile compile ->
			assert compile.options.forkOptions.memoryInitialSize == '16m'
			assert compile.options.forkOptions.memoryMaximumSize == '24m'
			assert compile.options.forkOptions.jvmArgs.contains('-XX:MaxPermSize=8m')
		}

		TaskCollection groovyTasks = project.tasks.withType(GroovyCompile)
		assert groovyTasks.size() > 0
		groovyTasks.each { GroovyCompile compile ->
			assert compile.groovyOptions.forkOptions.memoryInitialSize == '16m'
			assert compile.groovyOptions.forkOptions.memoryMaximumSize == '24m'
			assert compile.groovyOptions.forkOptions.jvmArgs.contains('-XX:MaxPermSize=8m')
		}
	}

	@Test
	void apply_ShouldSetHeapSizeForTestTasks() {
		project.ext.defaultsMinTestHeapSize = '17m'
		project.ext.defaultsMaxTestHeapSize = '23m'
		project.ext.defaultsMaxTestPermSize = '5m'

		applyPlugin()

		TaskCollection tasks = project.tasks.withType(org.gradle.api.tasks.testing.Test)
		assert tasks.size() > 0
		tasks.each { org.gradle.api.tasks.testing.Test test ->
			assert test.minHeapSize == '17m'
			assert test.maxHeapSize == '23m'
			assert test.jvmArgs.contains('-XX:MaxPermSize=5m')
		}
	}

	@Test
	void apply_ShouldAddBuildDateAndJdkToJarManifest() {
		String expectedJavaVersion = System.getProperty('java.version')

		applyPlugin()

		assert project.jar.manifest.attributes['Built-Date'] != null
		assert project.jar.manifest.attributes['Build-Jdk'] == expectedJavaVersion
	}

}