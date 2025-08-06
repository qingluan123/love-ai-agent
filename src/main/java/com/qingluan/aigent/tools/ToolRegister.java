package com.qingluan.aigent.tools;

import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbacks;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ToolRegister {

    @Value("${search-api.api-key}")
    private String apikey;

    @Bean
    public ToolCallback[] alltools(){
        SearchTool searchTool = new SearchTool(apikey);
        FileOperationTool fileOperationTool = new FileOperationTool();
        PdfGenerateTool pdfGenerateTool = new PdfGenerateTool();
        ResourceDownLoad resourceDownLoad = new ResourceDownLoad();
        SearchScapingTool searchScapingTool = new SearchScapingTool();
        TerminateTool terminateTool = new TerminateTool();
        return ToolCallbacks.from(
                searchTool,
                fileOperationTool,
                pdfGenerateTool,
                resourceDownLoad,
                searchScapingTool,
                terminateTool
                );
    }
}
