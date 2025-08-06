package com.qingluan.aigent.tools;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Slf4j
class PdfGenerateToolTest {

    @Test
    void generatePdf() {
        PdfGenerateTool pdfGenerateTool = new PdfGenerateTool();
        String filename = "Mylove.pdf";
        String content = "帮我生成一份七夕约会计划的pdf文档";
        String s = pdfGenerateTool.generatePDF(filename, content);
        Assertions.assertNotNull(s);
    }
}