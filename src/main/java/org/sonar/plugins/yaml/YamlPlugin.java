package org.sonar.plugins.yaml;

import java.util.Arrays;
import java.util.List;

import org.sonar.api.Properties;
import org.sonar.api.Property;
import org.sonar.api.SonarPlugin;
import org.sonar.plugins.yaml.batch.YamlIssueSensor;

@Properties({ @Property(key = YamlPlugin.FILE_SUFFIXES_KEY, defaultValue = YamlLanguage.DEFAULT_SUFFIXES, name = "File suffixes", description = "Comma-separated list of suffixes for files to analyze.", global = true, project = false) })
public final class YamlPlugin extends SonarPlugin {

    public static final String MY_PROPERTY = "sonar.example.myproperty";

    public static final String FILE_SUFFIXES_KEY = "sonar-Yaml-plugin.file.suffixes";

    // This is where you're going to declare all your SonarQube extensions
    @Override
    public List getExtensions() {
	return Arrays.asList(YamlIssueSensor.class, YamlLanguage.class,
		YamlRulesDefinition.class);
    }
}
