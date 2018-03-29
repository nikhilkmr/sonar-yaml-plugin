package org.sonar.plugins.yaml;

import org.sonar.api.server.rule.RulesDefinition;
import org.sonar.plugins.yaml.checks.CheckRepository;
import org.sonar.squidbridge.annotations.AnnotationBasedRulesDefinition;

public final class YamlRulesDefinition implements RulesDefinition {

  @Override
  public void define(Context context) {
    NewRepository repository = context
      .createRepository(CheckRepository.REPOSITORY_KEY, YamlLanguage.LANGUAGE_KEY)
      .setName(CheckRepository.REPOSITORY_NAME);

    new AnnotationBasedRulesDefinition(repository, YamlLanguage.LANGUAGE_KEY).addRuleClasses(false, CheckRepository.getCheckClasses());

    repository.done();
  }

}
