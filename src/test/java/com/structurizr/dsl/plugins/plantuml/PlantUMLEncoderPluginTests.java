package com.structurizr.dsl.plugins.plantuml;

import com.structurizr.Workspace;
import com.structurizr.documentation.Format;
import com.structurizr.documentation.Section;
import com.structurizr.documentation.StructurizrDocumentationTemplate;
import com.structurizr.dsl.StructurizrDslPluginContext;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PlantUMLEncoderPluginTests {

    @Test
    public void test_run() {
        Workspace workspace = new Workspace("Name", "Description");

        StructurizrDocumentationTemplate template = new StructurizrDocumentationTemplate(workspace);
        Section section = template.addContextSection(null, Format.Markdown, "## Context\n" +
                "\n" +
                "```plantuml\n" +
                "@startuml\n" +
                "Bob -> Alice : hello\n" +
                "@enduml\n" +
                "```");

        Map<String,String> parameters = new HashMap<>();
        StructurizrDslPluginContext context = new StructurizrDslPluginContext(workspace, parameters);
        new PlantUMLEncoderPlugin().run(context);

        assertEquals("## Context\n" +
                "\n" +
                "![](https://www.plantuml.com/plantuml/svg/SoWkIImgAStDuNBAJrBGjLDmpCbCJbMmKiX8pSd9vt98pKi1IG80)\n", section.getContent());
    }

}
