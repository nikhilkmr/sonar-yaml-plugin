package org.sonar.plugins.yaml.checks;

import java.util.List;

import com.google.common.collect.ImmutableList;

public class CheckRepository {

    public static final String REPOSITORY_KEY = "yaml";
    public static final String REPOSITORY_NAME = "SonarQube";
    public static final String SONAR_WAY_PROFILE_NAME = "Sonar way";

    private CheckRepository() {
    }

    public static List<AbstractYamlCheck> getChecks() {
	return ImmutableList.of(new NodeExist(), new SimpleYamlMatchCheck());
    }

    public static List<Class> getCheckClasses() {
	ImmutableList.Builder<Class> builder = ImmutableList.builder();

	for (AbstractYamlCheck check : getChecks()) {
	    builder.add(check.getClass());
	}

	return builder.build();
    }

}
