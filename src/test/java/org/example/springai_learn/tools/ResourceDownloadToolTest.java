package org.example.springai_learn.tools;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class ResourceDownloadToolTest {

    @Test
    public void testDownloadResource() {
        ResourceDownloadTool tool = new ResourceDownloadTool();
        String url = "https://plus.hutool.cn/apidocs/";
        String fileName = "apidocs.txt";
        String result = tool.downloadResource(url, fileName);
        assertNotNull(result);
    }
}
