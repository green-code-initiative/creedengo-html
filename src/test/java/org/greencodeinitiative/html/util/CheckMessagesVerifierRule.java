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
package org.greencodeinitiative.html.util;

import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.sonar.plugins.html.checks.HtmlIssue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CheckMessagesVerifierRule implements AfterEachCallback {

    private final List<CheckMessagesVerifier> verifiers = new ArrayList<>();

    public CheckMessagesVerifier verify(Collection<HtmlIssue> messages) {
        CheckMessagesVerifier verifier = CheckMessagesVerifier.verify(messages);
        verifiers.add(verifier);
        return verifier;
    }

    protected void verify() {
        for (CheckMessagesVerifier verifier : verifiers) {
            verifier.noMore();
        }
    }

    @Override
    public void afterEach(ExtensionContext extensionContext) {
        verify();
    }
}
