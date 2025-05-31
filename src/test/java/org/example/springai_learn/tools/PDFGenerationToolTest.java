package org.example.springai_learn.tools;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class PDFGenerationToolTest {

    @Test
    public void testGeneratePDF() {
        PDFGenerationTool tool = new PDFGenerationTool();
        String fileName = "测试文本.pdf";
        String content = "https://doc.xiaominfo.com/docs/quick-start";
        String result = tool.generatePDF(fileName, content);
        assertNotNull(result);
    }
}
