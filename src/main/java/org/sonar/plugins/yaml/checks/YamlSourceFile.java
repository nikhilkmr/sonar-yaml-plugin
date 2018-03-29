package org.sonar.plugins.yaml.checks;

import java.util.ArrayList;
import java.util.List;

import org.sonar.api.batch.fs.InputFile;

public class YamlSourceFile {

  private final List<YamlIssue> textIssues = new ArrayList<>();

  private InputFile inputFile;
  private Object yamlTypeObj;

  /**
   * Used for tracking violations on each scanned file
 * @param obj 
   */
  public YamlSourceFile(InputFile file, Object yamlTypeObj) {
    this.inputFile = file;
    this.yamlTypeObj = yamlTypeObj;
  }

  public void addViolation(YamlIssue textIssue) {
    this.textIssues.add(textIssue);
  }

  public InputFile getInputFile() {
    return inputFile;
  }
  
  public Object getYamlTypeObj() {
      return yamlTypeObj;
  }

  public String getLogicalPath() {
    return inputFile.absolutePath();
  }

  public List<YamlIssue> getYamlIssues() {
    return textIssues;
  }

  @Override
  public String toString() {
    return inputFile.absolutePath();
  }
}
