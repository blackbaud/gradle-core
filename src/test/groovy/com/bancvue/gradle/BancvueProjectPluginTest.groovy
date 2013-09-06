package com.bancvue.gradle

import com.bancvue.gradle.maven.MavenPublishExtPlugin
import com.bancvue.gradle.test.ComponentTestPlugin
import com.bancvue.gradle.test.TestExtPlugin
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException

class BancvueProjectPluginTest extends AbstractPluginTest {

	@Rule
	public ExpectedException exception = ExpectedException.none()

	BancvueProjectPluginTest() {
		super(BancvueProjectPlugin.PLUGIN_NAME)
	}

	@Before
	void setUp() {
		project.version = '1.0'
		setArtifactId('bancvue')
	}

	@Test
	void apply_ShouldApplyGroovyPlugin() {
		applyPlugin()

		assertNamedPluginApplied('groovy')
	}

	@Test
	void apply_ShouldFail_IfVersionNotDefined() {
		exception.expect(BancvueProjectPlugin.VersionNotDefinedException)
		project = ProjectBuilder.builder().withName('project').build()
		setArtifactId('bancvue')

		applyPlugin()
	}

	@Test
	void apply_ShouldFail_IfArtifactIdNotDefined() {
		exception.expect(BancvueProjectPlugin.ArtifactIdNotDefinedException)
		project = ProjectBuilder.builder().withName('project').build()
		project.version = '1.0'

		applyPlugin()
	}

	@Test
	void apply_ShouldApplyBancvueIdePlugin() {
		applyPlugin()

		assertNamedPluginApplied(IdeExtPlugin.PLUGIN_NAME)
	}

	@Test
	void apply_ShouldApplyBancvuePublishPlugin() {
		applyPlugin()

		assertNamedPluginApplied(MavenPublishExtPlugin.PLUGIN_NAME)
	}

	@Test
	void apply_ShouldApplyTestExtPlugin() {
		applyPlugin()

		assertNamedPluginApplied(TestExtPlugin.PLUGIN_NAME)
	}

	@Test
	void apply_ShouldApplyBancvueComponentTestPlugin() {
		applyPlugin()

		assertNamedPluginApplied(ComponentTestPlugin.PLUGIN_NAME)
	}

	@Test
	void apply_ShouldApplyBancvueUtilitiesPlugin() {
		applyPlugin()

		assertNamedPluginApplied(ProjectSupportPlugin.PLUGIN_NAME)
	}

	@Test
	void apply_ShouldApplyBancvueDefaultsPlugin() {
		applyPlugin()

		assertNamedPluginApplied(ProjectDefaultsPlugin.PLUGIN_NAME)
	}

}