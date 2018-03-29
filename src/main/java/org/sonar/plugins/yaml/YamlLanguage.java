package org.sonar.plugins.yaml;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.sonar.api.config.Settings;
import org.sonar.api.resources.AbstractLanguage;

import com.google.common.collect.Lists;

public class YamlLanguage extends AbstractLanguage {

  protected static final String DEFAULT_SUFFIXES = ".yaml";

  public static final String LANGUAGE_KEY = "yaml";

  private static final String TEXT_LANGUAGE_NAME = "yaml";

  private final Settings settings;

  public YamlLanguage(final Settings settings) {
    super(LANGUAGE_KEY, TEXT_LANGUAGE_NAME);
    this.settings = settings;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String[] getFileSuffixes() {
    String[] suffixes = filterEmptyStrings(settings.getStringArray(YamlPlugin.FILE_SUFFIXES_KEY));
    if (suffixes.length == 0) {
      suffixes = YamlLanguage.DEFAULT_SUFFIXES.split("\\s*,\\s*");
    }
    return suffixes;
  }

  private static String[] filterEmptyStrings(final String[] stringArray) {
    List<String> nonEmptyStrings = Lists.newArrayList();
    for (String string : stringArray) {
      if (StringUtils.isNotBlank(string.trim())) {
        nonEmptyStrings.add(string.trim());
      }
    }
    return nonEmptyStrings.toArray(new String[nonEmptyStrings.size()]);
  }

}
