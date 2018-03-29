package org.sonar.plugins.yaml.checks;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;

import org.apache.commons.io.FileUtils;
import org.junit.rules.TemporaryFolder;
import org.sonar.api.batch.fs.internal.DefaultFileSystem;
import org.sonar.api.batch.fs.internal.DefaultInputFile;
import org.yaml.snakeyaml.Yaml;

public abstract class AbstractCheckTester {

    @org.junit.Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    protected static final String INCORRECT_NUMBER_OF_VIOLATIONS = "Incorrect number of violations";

    protected YamlSourceFile parseAndCheck(final File file,
	    final AbstractYamlCheck check, final String projectKey) {
	Yaml yaml = new Yaml();
	Object ob = null;
	try {
	    ob = yaml.compose(new FileReader(file));
	    System.out.println("");
	} catch (Exception  e) {
	    e.printStackTrace();
	}
	YamlSourceFile textSourceFile = new YamlSourceFile(
		new DefaultInputFile(file.getPath()).setAbsolutePath(file
			.getAbsolutePath()), ob);
	check.setYamlSourceFile(textSourceFile);
	check.validate(textSourceFile, projectKey);
	return textSourceFile;
    }

    protected DefaultFileSystem createFileSystem() {
	File workDir;
	try {
	    workDir = temporaryFolder.newFolder("temp");
	} catch (IOException e) {
	    throw new RuntimeException(e);
	}

	DefaultFileSystem fs = new DefaultFileSystem();
	fs.setEncoding(Charset.defaultCharset());
	fs.setWorkDir(workDir);

	return fs;
    }

    protected File createTempFile(final String content) throws IOException {
	File f = temporaryFolder.newFile("file.xml");
	FileUtils.write(f, content);

	return f;
    }

}
