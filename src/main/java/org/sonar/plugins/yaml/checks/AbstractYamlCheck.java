package org.sonar.plugins.yaml.checks;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.rule.RuleKey;
import org.sonar.api.utils.WildcardPattern;
import org.sonar.check.RuleProperty;

public abstract class AbstractYamlCheck {

  private RuleKey ruleKey;
  private YamlSourceFile yamlSourceFile;

  @RuleProperty(key = "doNotFireForTheseProjectKeys", type = "TEXT", defaultValue = "", description = "Use to exclude certain projects from this rule. Sample RegEx patterns: '^someMavenGroupIdPrefix' or 'someArtifactIdEndingDenotingSpecialProjectsToBeExcludedFromRule$'")
  private String doNotFireForProjectKeysRegex;

  @RuleProperty(key = "doNotFireForTheseFileNames", type = "TEXT", defaultValue = "", description = "Use to exclude certain file names from this rule. Sample RegEx pattern: '^(local\\.properties|README.txt)$'")
  private String doNotFireForTheseFileNamesRegex;

  protected final void createViolation(final Integer linePosition, final String message) {
      yamlSourceFile.addViolation(new YamlIssue(ruleKey, linePosition, message));
  }

  /**
   * Apply the Ant style file pattern to decide if the file is included
   */
  protected boolean isFileIncluded(final String filePattern) {
    return (filePattern == null) ? true :  WildcardPattern.create(filePattern).match(yamlSourceFile.getLogicalPath());
  }

  protected boolean shouldFireForProject(final String currentProjectKey) {
    if (doNotFireForProjectKeysRegex == null || "".equals(doNotFireForProjectKeysRegex.trim())) {
      return true;
    } else {
  	  Pattern regexp = Pattern.compile(doNotFireForProjectKeysRegex);
      Matcher matcher = regexp.matcher(currentProjectKey);
      return !matcher.find();
    }
  }

  protected boolean shouldFireOnFile(final InputFile currentFile) {
	  if (doNotFireForTheseFileNamesRegex == null || "".equals(doNotFireForTheseFileNamesRegex.trim())) {
	    return true;
	  } else {
	    Pattern regexp = Pattern.compile(doNotFireForTheseFileNamesRegex);
	    Matcher matcher = regexp.matcher(currentFile.file().getName());
	    return !matcher.find();
	  }
  }

  public final void setRuleKey(final RuleKey ruleKey) {
    this.ruleKey = ruleKey;
  }

  public RuleKey getRuleKey() {
    return this.ruleKey;
  }

  public void setDoNotFireForProjectKeysRegex(final String doNotFireForProjectKeysRegex) {
    this.doNotFireForProjectKeysRegex = doNotFireForProjectKeysRegex;
  }

  public void setDoNotFireForTheseFileNamesRegex(final String doNotFireForTheseFileNamesRegex) {
    this.doNotFireForTheseFileNamesRegex = doNotFireForTheseFileNamesRegex;
  }

  protected void setYamlSourceFile(final YamlSourceFile sourceFile) {
    this.yamlSourceFile = sourceFile;
  }

  public abstract void validate(YamlSourceFile sourceFile, String projectKey);

  protected YamlSourceFile getYamlSourceFile() {
    return yamlSourceFile;
  }

}
