package org.sonar.plugins.yaml.batch;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.Sensor;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.rule.CheckFactory;
import org.sonar.api.batch.rule.Checks;
import org.sonar.api.component.ResourcePerspectives;
import org.sonar.api.issue.Issuable;
import org.sonar.api.resources.Project;
import org.sonar.plugins.yaml.checks.AbstractCrossFileCheck;
import org.sonar.plugins.yaml.checks.AbstractYamlCheck;
import org.sonar.plugins.yaml.checks.CheckRepository;
import org.sonar.plugins.yaml.checks.CrossFileScanPrelimIssue;
import org.sonar.plugins.yaml.checks.YamlIssue;
import org.sonar.plugins.yaml.checks.YamlSourceFile;
import org.yaml.snakeyaml.Yaml;

import com.google.common.collect.Maps;

public class YamlIssueSensor implements Sensor {
    private final Logger LOG = LoggerFactory.getLogger(YamlIssueSensor.class);

    private final Checks<Object> checks;
    private final FileSystem fs;
    private final ResourcePerspectives resourcePerspectives;
    private final Project project;
    final Map<InputFile, List<CrossFileScanPrelimIssue>> crossFileChecksRawResults;

    /**
     * Use of IoC to get FileSystem
     */
    public YamlIssueSensor(final FileSystem fs,
	    final ResourcePerspectives perspectives,
	    final CheckFactory checkFactory, final Project project) {
	this.checks = checkFactory.create(CheckRepository.REPOSITORY_KEY)
		.addAnnotatedChecks(CheckRepository.getCheckClasses());
	this.fs = fs;
	this.resourcePerspectives = perspectives;
	this.project = project;

	// This data structure is shared across all cross-file checks so they
	// can see each others' data.
	// Each file with any trigger or disallow match gets a listitem
	// indicating the specifics of the Check that matched including line
	// number. This object reference stays with the check and gets
	// referenced later inside the "raiseIssuesAfterScan()" method call.
	this.crossFileChecksRawResults = Maps.newHashMap();
    }

    /**
     * This sensor is executed only when there are "yaml" language files present
     * in the project.
     *
     * Consider in future versions: Will all users want this behavior? This
     * plugin now scans files from other languages when the rule's ant-style
     * file path pattern directs it to...
     */
    @Override
    public boolean shouldExecuteOnProject(final Project project) {
	return fs.hasFiles(fs.predicates().hasLanguage("yaml"));
    }

    @Override
    public void analyse(final Project project, final SensorContext sensorContext) {

	for (InputFile inputFile : fs.inputFiles(fs.predicates().hasType(
		InputFile.Type.MAIN))) {
	    try {
		File file = inputFile.file();
		if (!(file.isFile() && file.getName().endsWith(".yaml"))){
		    continue;
		}
		FileReader reader = new FileReader(file);
		Yaml yaml = new Yaml();
		Object obj = yaml.compose(reader);
		analyseIndividualFile(inputFile, obj);
	    } catch (FileNotFoundException e) {
		e.printStackTrace();
	    }
	}

	raiseCrossFileCheckIssues();

    }

    private void analyseIndividualFile(final InputFile inputFile, final Object obj) {
	
	YamlSourceFile yamlSourceFile = new YamlSourceFile(inputFile, obj);

	for (Object check : checks.all()) {
	    try {
		// if (check instanceof AbstractCrossFileCheck) {
		// Calls to cross-file checks need to pass in the data structure
		// used to collect match data
		// AbstractCrossFileCheck crossFileCheck =
		// (AbstractCrossFileCheck) check;
		// crossFileCheck.setRuleKey(checks.ruleKey(check));
		// crossFileCheck.validate(crossFileChecksRawResults,
		// textSourceFile, project.getKey());
		// } else {
		AbstractYamlCheck textCheck = (AbstractYamlCheck) check;
		textCheck.setRuleKey(checks.ruleKey(check));
		textCheck.validate(yamlSourceFile, project.getKey());
		// }
	    } catch (Exception e) {
		e.printStackTrace();
		LOG.warn(
			"Check for rule \"{}\" choked on file {}. Continuing the scan. Skipping evaluation of just this one rule against this one file.",
			((AbstractYamlCheck) check).getRuleKey(), inputFile
				.file().getAbsolutePath());
		LOG.warn("Brief failure cause info: " + e.toString());
		LOG.warn("Full failure details can be exposed by enabling debug logging on 'org.sonar.plugins.text.batch.TextIssueSensor'.");
		LOG.debug("Check failure details:", e);
	    }
	}

	saveIssues(yamlSourceFile.getYamlIssues(),
		yamlSourceFile.getInputFile());
    }

    private void raiseCrossFileCheckIssues() {
	for (Object check : checks.all()) {
	    if (check instanceof AbstractCrossFileCheck) {
		List<YamlSourceFile> textSourceFiles = ((AbstractCrossFileCheck) check)
			.raiseIssuesAfterScan();

		for (YamlSourceFile file : textSourceFiles) {
		    saveIssues(file.getYamlIssues(), file.getInputFile());
		}
	    }
	}
    }

    private void saveIssues(final List<YamlIssue> issuesList,
	    final InputFile againstThisFile) {
	for (YamlIssue issue : issuesList) {
	    Issuable issuable = resourcePerspectives.as(Issuable.class,
		    againstThisFile);

	    if (issuable != null) {
		issuable.addIssue(issuable.newIssueBuilder()
			.ruleKey(issue.getRuleKey()).line(issue.getLine())
			.message(issue.getMessage()).build());
	    }
	}
    }

}
