package org.example.springai_learn.tools;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Bing 搜索引擎工具类
 * 提供简单的网络搜索功能
 */
public class WebSearchTool {

    // SearchAPI 接口地址
    private static final String SEARCH_API_URL = "https://www.searchapi.io/api/v1/search";

    private final String apiKey;  // API 密钥

    /**
     * 构造函数
     * @param apiKey SearchAPI 的认证密钥
     */
    public WebSearchTool(String apiKey) {
        this.apiKey = apiKey;
    }

    /**
     * 使用 Bing 搜索引擎进行网络搜索
     * @param query 搜索关键词
     * @return 格式化后的搜索结果字符串
     */
    @Tool(description = "Search for information from Bing Search Engine")
    public String searchBing(
            @ToolParam(description = "Search query keyword") String query) {

        // 构建请求参数
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("q", query);          // 搜索关键词
        paramMap.put("api_key", apiKey);  // API 密钥
        paramMap.put("engine", "bing");    // 指定搜索引擎为 Bing

        // 设置默认参数
        paramMap.put("num", 5);           // 默认返回5条结果
        paramMap.put("safe_search", "moderate");  // 默认中等安全搜索

        try {
            // 发送 HTTP GET 请求
            String response = HttpUtil.get(SEARCH_API_URL, paramMap);

            // 解析 JSON 响应
            JSONObject jsonObject = JSONUtil.parseObj(response);
            JSONArray organicResults = jsonObject.getJSONArray("organic_results");

            // 格式化搜索结果
            String formattedResults = organicResults.stream()
                    .limit(5)  // 限制最多5条结果
                    .map(obj -> {
                        JSONObject result = (JSONObject) obj;
                        return String.format("标题: %s\n链接: %s\n摘要: %s",
                                result.getStr("title"),
                                result.getStr("link"),
                                result.getStr("snippet"));
                    })
                    .collect(Collectors.joining("\n\n"));

            return formattedResults.isEmpty() ? "No return" : formattedResults;

        } catch (Exception e) {
            return "Error " + e.getMessage();
        }
    }
}