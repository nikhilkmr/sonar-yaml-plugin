package org.sonar.plugins.yaml.checks;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.junit.Test;

public class NodeExistTest extends AbstractCheckTester {

    @Test
    public void simpleCase_successfulMatch() throws IOException {
	// Set up
	super.createFileSystem();
	File tempFile1 = new File("/home/nikhil/workspace_plugin/snakeYaml/src/main/java/snakeYaml/batch-api.yaml");
	
	NodeExist check = new NodeExist();
	
	

	// Run
	YamlSourceFile result = parseAndCheck(tempFile1, check,
		"com.mycorp.projectA.service:service-do-X");

	check.setYamlSourceFile(result);
	// Check
	List<YamlIssue> issuesFound = result.getYamlIssues();
	//assertTrue(issuesFound.size() == 2);

	
    }

    

    private int countTextIssuesFoundAtLine(final int lineNumber,
	    final List<YamlIssue> list) {
	int countFound = 0;
	for (YamlIssue currentIssue : list) {
	    if (currentIssue.getLine() == lineNumber) {
		countFound++;
	    }
	}
	return countFound;
    }

}
