package com.structurizr.dsl.plugins.plantuml;

import com.structurizr.Workspace;
import com.structurizr.documentation.Format;
import com.structurizr.documentation.Section;
import com.structurizr.dsl.StructurizrDslPlugin;
import com.structurizr.dsl.StructurizrDslPluginContext;

public class PlantUMLEncoderPlugin implements StructurizrDslPlugin {

    private static final String MARKDOWN_IMAGE_TEMPLATE = "![](%s/%s/%s)";
    private static final String ASCIIDOC_IMAGE_TEMPLATE = "image::%s/%s/%s[]";

    private static final String PLANTUML_FORMAT = "svg";

    @Override
    public void run(StructurizrDslPluginContext context) {
        String url = context.getParameter("plantuml.url", "https://www.plantuml.com/plantuml");

        try {
            Workspace workspace = context.getWorkspace();
            for (Section section : workspace.getDocumentation().getSections()) {
                String content = section.getContent();

                StringBuilder buf = new StringBuilder();
                String[] lines = content.split("\\r?\\n");
                StringBuilder rawPlantUML = null;
                for (String line : lines) {
                    line = line.trim();

                    if (line.equals("```plantuml")) {
                        rawPlantUML = new StringBuilder();
                    } else if (rawPlantUML != null && line.equals("```")) {
                        String encodedPlantUML = new PlantUMLEncoder().encode(rawPlantUML.toString());

                        if (section.getFormat() == Format.AsciiDoc) {
                            buf.append(String.format(ASCIIDOC_IMAGE_TEMPLATE, url, PLANTUML_FORMAT, encodedPlantUML));
                        } else {
                            buf.append(String.format(MARKDOWN_IMAGE_TEMPLATE, url, PLANTUML_FORMAT, encodedPlantUML));
                        }

                        buf.append(System.lineSeparator());
                        rawPlantUML = null;
                    } else if (rawPlantUML != null) {
                        rawPlantUML.append(line);
                        rawPlantUML.append(System.lineSeparator());
                    } else {
                        buf.append(line);
                        buf.append(System.lineSeparator());
                    }
                }

                section.setContent(buf.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}