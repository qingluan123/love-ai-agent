package com.qingluan.aigent.rag;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.markdown.MarkdownDocumentReader;
import org.springframework.ai.reader.markdown.config.MarkdownDocumentReaderConfig;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class LoveAppDocumentLoader {

    private final ResourcePatternResolver resourcePatternResolver;

    public LoveAppDocumentLoader(ResourcePatternResolver resourcePatternResolver){
        this.resourcePatternResolver = resourcePatternResolver;
    }

    public List<Document> documentloader() {
        List<Document> documents = new ArrayList<>();
        try {
            //加载路径的资源
            Resource[] resources = resourcePatternResolver.getResources("classpath:document/*.md");
            for(Resource resource : resources){
                String filename = resource.getFilename();
                String status = filename.substring(filename.length()-6,filename.length()-4);
                //文件读取配置
                MarkdownDocumentReaderConfig documentReaderConfig = MarkdownDocumentReaderConfig.builder()
                        .withHorizontalRuleCreateDocument(true)
                        .withIncludeBlockquote(false)
                        .withIncludeCodeBlock(false)
                        .withAdditionalMetadata("filename", filename)
                        .withAdditionalMetadata("status",status)
                        .build();
                //构建文件读取对象来加载读取文件
                MarkdownDocumentReader markdownDocumentReader = new MarkdownDocumentReader(resource,documentReaderConfig);
                documents.addAll(markdownDocumentReader.get());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return documents;
    }
}
