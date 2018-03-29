package org.sonar.plugins.yaml.checks;

import java.util.Iterator;

import org.sonar.api.rule.RuleKey;
import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.squidbridge.annotations.RuleTemplate;
import org.yaml.snakeyaml.error.Mark;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.ScalarNode;

@Rule(key = "TagCheck", priority = Priority.MAJOR, name = "Check if Node Exist", description = "Simple node matcher.")
@RuleTemplate
public class NodeExist extends AbstractYamlCheck {

    @Override
    public void validate(YamlSourceFile sourceFile, String projectKey) {
	if (!(shouldFireForProject(projectKey) && shouldFireOnFile(sourceFile
		.getInputFile()))) {
	    return;
	}
	this.setYamlSourceFile(sourceFile);
	MappingNode node = (MappingNode) sourceFile.getYamlTypeObj();

	// get all paths.
	MappingNode paths = (MappingNode) getNode(node, "paths");

	// Iterate paths and get desired key one by one.
	for (Iterator<NodeTuple> iterator = paths.getValue().iterator(); iterator
		.hasNext();) {
	    NodeTuple tuple = iterator.next();
	    Node path = tuple.getKeyNode();
	    MappingNode valueNode = (MappingNode) tuple.getValueNode();

	    Node xWrfCusi = findFirstNode(valueNode, "x-wrf-cusi");
	    if (xWrfCusi == null) {
		final Mark pathMark = path.getStartMark();
		
		createViolation(pathMark.getLine(),
			"O Boy! this path didn't have x-wrf-cusi !!!");
		continue;
	    }

	    if (!((ScalarNode) xWrfCusi).getValue().startsWith("CUS")) {
		Mark startMark = xWrfCusi.getStartMark();
		
		createViolation(startMark.getLine(),
			"O Boy! this tag does not starts with 'CUS' !!!");
		continue;
	    }

	}

    }

    private static Node getNode(MappingNode node, String key) {
	for (Iterator<NodeTuple> iterator = node.getValue().iterator(); iterator
		.hasNext();) {
	    NodeTuple tuple = iterator.next();
	    Node keyNode = tuple.getKeyNode();
	    if (!(keyNode instanceof ScalarNode)) {
		return null;
	    }
	    if (key.equals(((ScalarNode) keyNode).getValue())) {
		return tuple.getValueNode();
	    }
	}
	return null;
    }

    private static Node findFirstNode(MappingNode node, String key) {
	Node resultedNode = null;
	for (Iterator<NodeTuple> iterator = node.getValue().iterator(); iterator
		.hasNext();) {
	    NodeTuple tuple = iterator.next();
	    Node keyNode = tuple.getKeyNode();
	    if (!(keyNode instanceof ScalarNode)) {
		return null;
	    }
	    if (key.equals(((ScalarNode) keyNode).getValue())) {
		resultedNode = tuple.getValueNode();
		break;
	    } else if (tuple.getValueNode() instanceof MappingNode) {
		resultedNode = findFirstNode(
			(MappingNode) tuple.getValueNode(), key);
	    }
	}
	return resultedNode;
    }
}
