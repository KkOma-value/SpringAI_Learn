package org.example.springai_learn.tools;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FileOperationToolTest {

    @Test
    void readFile() {
        FileOperationTool tool = new FileOperationTool();
        String filename = "test.txt";
        String result = tool.ReadFile(filename);
        System.out.println(result);
    }

    @Test
    void writeFile() {
        FileOperationTool tool = new FileOperationTool();
        String filename = "test.txt";
        String content = "I Love Java";
        String result = tool.WriteFile(filename, content);
        assertNotNull(result);
    }
}