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
package org.greencodeinitiative.html.checks;

import org.sonar.check.Rule;
import org.sonar.plugins.html.checks.AbstractPageCheck;
import org.sonar.plugins.html.node.TagNode;
import org.sonarsource.analyzer.commons.annotations.DeprecatedRuleKey;

@Rule(key = AvoidAutoplayCheck.KEY)
@DeprecatedRuleKey(repositoryKey = "ecocode-html", ruleKey = "EC8000")
public class AvoidAutoplayCheck extends AbstractPageCheck {

    public static final String KEY = "GCI8000";

    @Override
    public void startElement(TagNode node) {
        if (isAudioTag(node) && hasAutoplayAttribute(node)) {
            createViolation(node, "Avoid using autoplay attribute in audio element");
        } else if (isVideoTag(node) && hasAutoplayAttribute(node)) {
            createViolation(node, "Avoid using autoplay attribute in video element");
        }
    }

    private static boolean isAudioTag(TagNode node) {
        return "AUDIO".equalsIgnoreCase(node.getNodeName());
    }

    private static boolean isVideoTag(TagNode node) {
        return "VIDEO".equalsIgnoreCase(node.getNodeName());
    }

    private static boolean hasAutoplayAttribute(TagNode node) {
        return node.hasProperty("AUTOPLAY");
    }

}
