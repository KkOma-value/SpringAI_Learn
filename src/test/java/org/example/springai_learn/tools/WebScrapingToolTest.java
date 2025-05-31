package org.example.springai_learn.tools;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class WebScrapingToolTest {

    @Test
    void scrapeWebPage() {
        WebScrapingTool tool = new WebScrapingTool();
        String URL ="https://baomidou.com/";
        String result = tool.scrapeWebPage(URL);
        assertNotNull(result);
    }
}