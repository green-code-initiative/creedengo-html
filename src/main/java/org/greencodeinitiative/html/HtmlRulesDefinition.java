/*
 * creedengo HTML plugin - Provides rules to reduce the environmental footprint of your HTML programs
 * Copyright © 2024 Green Code Initiative (https://green-code-initiative.org)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.greencodeinitiative.html;

import org.sonar.api.SonarRuntime;
import org.sonar.api.server.rule.RulesDefinition;
import org.sonar.plugins.html.api.HtmlConstants;
import org.sonarsource.analyzer.commons.RuleMetadataLoader;

import java.util.Collections;

public class HtmlRulesDefinition implements RulesDefinition {

    public static final String REPOSITORY_KEY = "creedengo-html";

    private static final String METADATA_LOCATION = "org.green-code-initiative.rules.html";

    private final SonarRuntime sonarRuntime;

    public HtmlRulesDefinition(SonarRuntime sonarRuntime) {
        this.sonarRuntime = sonarRuntime;
    }

    @Override
    public void define(Context context) {
        NewRepository repository = context
                .createRepository(REPOSITORY_KEY, HtmlConstants.LANGUAGE_KEY)
                .setName(HtmlPlugin.NAME);

        RuleMetadataLoader ruleMetadataLoader = new RuleMetadataLoader(METADATA_LOCATION, sonarRuntime);

        ruleMetadataLoader.addRulesByAnnotatedClass(
                repository,
                Collections.unmodifiableList(CheckList.getChecks())
        );

        repository.done();
    }

}
