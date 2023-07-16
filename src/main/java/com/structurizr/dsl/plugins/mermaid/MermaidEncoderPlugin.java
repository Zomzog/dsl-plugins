package com.structurizr.dsl.plugins.mermaid;

import com.structurizr.Workspace;
import com.structurizr.documentation.*;
import com.structurizr.dsl.StructurizrDslPlugin;
import com.structurizr.dsl.StructurizrDslPluginContext;
import com.structurizr.model.SoftwareSystem;

public class MermaidEncoderPlugin implements StructurizrDslPlugin {

    private static final String MARKDOWN_IMAGE_TEMPLATE = "![](%s/%s/%s)";
    private static final String ASCIIDOC_IMAGE_TEMPLATE = "image::%s/%s/%s[]";

    private static final String MERMAID_FORMAT = "svg";

    @Override
    public void run(StructurizrDslPluginContext context) {
        System.out.println("Start mermaid encoder plugin");
        String rootUrl = context.getParameter("mermaid.url", "https://mermaid.ink");
        try {
            Workspace workspace = context.getWorkspace();
            updateDocuments(workspace, rootUrl);
            for (SoftwareSystem softwareSystem : workspace.getModel().getSoftwareSystems()) {
                updateDocuments(softwareSystem, rootUrl);
                softwareSystem.getContainers().forEach(container -> {
                    updateDocuments(container, rootUrl);
                    container.getComponents().forEach(component -> {
                        updateDocuments(component, rootUrl);
                    });
                });

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

   private void updateDocuments(Documentable item, String mermaidUrl) {
        item.getDocumentation() .getSections()
                .forEach(section -> updateDocuments(section, mermaidUrl));
        item.getDocumentation().getDecisions()
                .forEach(decision -> updateDocuments(decision, mermaidUrl));
   }

   private void updateDocuments(DocumentationContent content, String mermaidUrl) {
        content.setContent(encodeMermaid(mermaidUrl, content.getContent(), content.getFormat()));
   }

    private static String SCRIPT = """
    <script id="rendered-js" type="module">
        import mermaid from "https://cdn.skypack.dev/mermaid@10";
        mermaid.init({noteMargin: 10}, ".mermaid");
    </script>
        
        """;
    
    private String encodeMermaid(String url, String content, Format format) {

        StringBuilder buf = new StringBuilder();
        String[] lines = content.split("\\r?\\n");
        StringBuilder rawMermaid = null;
        for (String line : lines) {
            line = line.trim();

            if (line.equals("```mermaid")) {
                System.out.println("Start new mermaid");
                rawMermaid = new StringBuilder();
                rawMermaid.append("<div class=\"mermaid\">");
            } else if (rawMermaid != null && line.equals("```")) {
                rawMermaid.append("</div>");
                System.out.println(rawMermaid.toString());
                buf.append(rawMermaid.toString());
                buf.append(System.lineSeparator());
                rawMermaid = null;
            } else if (rawMermaid != null && !line.isBlank()) {
                rawMermaid.append(line);
                rawMermaid.append(System.lineSeparator());
            } else {
                buf.append(line);
                buf.append(System.lineSeparator());
            }
        }

        buf.append(SCRIPT);
        return buf.toString();
    }

}
