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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sonar.api.SonarProduct;
import org.sonar.api.SonarRuntime;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.fs.internal.DefaultInputFile;
import org.sonar.api.batch.fs.internal.TestInputFileBuilder;
import org.sonar.api.batch.rule.ActiveRules;
import org.sonar.api.batch.rule.CheckFactory;
import org.sonar.api.batch.rule.internal.DefaultActiveRules;
import org.sonar.api.batch.rule.internal.NewActiveRule;
import org.sonar.api.batch.sensor.SensorDescriptor;
import org.sonar.api.batch.sensor.internal.SensorContextTester;
import org.sonar.api.rule.RuleKey;
import org.sonar.api.server.rule.RulesDefinition;
import org.sonar.api.utils.Version;
import org.sonar.plugins.html.api.HtmlConstants;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class HtmlSensorTest {

    private static final Path TEST_DIR = Paths.get("src/test/resources/files");

    private SonarRuntime sonarRuntime;

    private HtmlSensor htmlSensor;

    private SensorContextTester tester;

    @BeforeEach
    public void setup() {
        this.sonarRuntime = mock(SonarRuntime.class);
        when(sonarRuntime.getProduct()).thenReturn(SonarProduct.SONARQUBE);
        when(sonarRuntime.getApiVersion()).thenReturn(Version.create(9, 9));

        HtmlRulesDefinition rulesDefinition = new HtmlRulesDefinition(sonarRuntime);
        RulesDefinition.Context context = new RulesDefinition.Context();
        rulesDefinition.define(context);
        RulesDefinition.Repository repository = context.repository(HtmlRulesDefinition.REPOSITORY_KEY);

        List<NewActiveRule> ar = new ArrayList<>();
        for (RulesDefinition.Rule rule : repository.rules()) {
            ar.add(new NewActiveRule.Builder().setRuleKey(RuleKey.of(HtmlRulesDefinition.REPOSITORY_KEY, rule.key())).build());
        }
        ActiveRules activeRules = new DefaultActiveRules(ar);
        CheckFactory checkFactory = new CheckFactory(activeRules);

        htmlSensor = new HtmlSensor(checkFactory, sonarRuntime);
        tester = SensorContextTester.create(TEST_DIR).setRuntime(sonarRuntime);
    }

    @Test
    public void processesFilesIndependently() {
        SensorDescriptor descriptor = mock(SensorDescriptor.class);
        when(descriptor.name(any())).thenReturn(descriptor);
        this.htmlSensor.describe(descriptor);
        verify(descriptor).name(HtmlConstants.LANGUAGE_NAME);
        verify(descriptor).processesFilesIndependently();
    }

    @Test
    public void notProcessesFilesIndependentlyIfSonarLint() {
        SensorDescriptor descriptor = mock(SensorDescriptor.class);
        doReturn(descriptor).when(descriptor).name(any());
        doReturn(SonarProduct.SONARLINT).when(this.sonarRuntime).getProduct();
        this.htmlSensor.describe(descriptor);
        verify(descriptor, never()).processesFilesIndependently();
    }

    @Test
    public void cancellation() throws Exception {
        DefaultInputFile inputFile = createInputFile("example.vue");
        tester.fileSystem().add(inputFile);
        tester.setCancelled(true);
        htmlSensor.execute(tester);
        assertThat(tester.allIssues()).isEmpty();
    }

    @Test
    public void vueFile() throws Exception {
        DefaultInputFile inputFile = createInputFile("example.vue");
        tester.fileSystem().add(inputFile);
        htmlSensor.execute(tester);
        assertThat(tester.allIssues()).hasSize(1);
        assertThat(tester.allAnalysisErrors()).isEmpty();
    }

    private DefaultInputFile createInputFile(String fileName) throws IOException {
        return new TestInputFileBuilder("key", fileName)
                .setModuleBaseDir(TEST_DIR)
                .setLanguage(HtmlConstants.LANGUAGE_KEY)
                .setType(InputFile.Type.MAIN)
                .initMetadata(Files.readString(TEST_DIR.resolve(fileName)))
                .setCharset(StandardCharsets.UTF_8)
                .build();
    }

}
