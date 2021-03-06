package org.gradle.testkit.functional;

import org.gradle.testkit.functional.internal.DefaultGradleRunner;
import org.gradle.testkit.functional.internal.GradleHandleFactory;
import org.gradle.testkit.functional.internal.classpath.ClasspathInjectingGradleHandleFactory;
import org.gradle.testkit.functional.internal.toolingapi.ToolingApiGradleHandleFactory;

public class GradleRunnerFactory {

    public static GradleRunner create() {
        GradleHandleFactory toolingApiHandleFactory = new ToolingApiGradleHandleFactory();

        // TODO: Which class would be attached to the right classloader? Is using something from the test kit right?
        ClassLoader sourceClassLoader = GradleRunnerFactory.class.getClassLoader();
        GradleHandleFactory classpathInjectingHandleFactory = new ClasspathInjectingGradleHandleFactory(sourceClassLoader, toolingApiHandleFactory);

        return new DefaultGradleRunner(classpathInjectingHandleFactory);
    }

}
