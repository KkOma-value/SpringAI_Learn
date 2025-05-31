package org.example.springai_learn.tools;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class WebSearchToolTest {

    @Value("${search-api.api-key}")
    private String api_key;

    @Test
    void searchBing() {
        WebSearchTool tool = new WebSearchTool(api_key);
        String query = "广州软件学院 李锦航";
        String result = tool.searchBing(query);
        assertNotNull(result);

    }
}