package org.sonar.plugins.yaml.checks;

import org.sonar.api.rule.RuleKey;

public class YamlIssue {

  private final RuleKey ruleKey;
  private final int line;
  private final String message;

  public YamlIssue(final RuleKey ruleKey, final int line, final String message) {
    this.ruleKey = ruleKey;
    this.line = line;
    this.message = message;
  }

  public RuleKey getRuleKey() {
    return ruleKey;
  }

  public int getLine() {
    return line;
  }

  public String getMessage() {
    return message;
  }

  @Override
  public String toString() {
    return "YamlIssue [ruleKey=" + ruleKey + ", line=" + line + ", message=" + message + "]";
  }
}
