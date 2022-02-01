package com.structurizr.dsl.plugins.plantuml;

import com.structurizr.Workspace;
import com.structurizr.documentation.Section;
import com.structurizr.dsl.StructurizrDslPlugin;
import com.structurizr.dsl.StructurizrDslPluginContext;

public class PlantUMLEncoderPlugin implements StructurizrDslPlugin {

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
                        buf.append(String.format("![](%s/%s/%s)", url, "svg", encodedPlantUML));
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