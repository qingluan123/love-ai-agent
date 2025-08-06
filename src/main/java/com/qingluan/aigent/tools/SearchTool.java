package com.qingluan.aigent.tools;

import java.util.regex.Pattern;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;



public class SearchTool {

    private static final String SEARCH_URL = "https://www.searchapi.io/api/v1/search";

    private static final Pattern CHINESE_PATTERN = Pattern.compile("[\\u4e00-\\u9fa5]+");

    private final String apikey;

    public SearchTool(String apikey){
        this.apikey = apikey;
    }

    @Tool(description = "Search information from baidu engine")
    public String webSearch(@ToolParam(description = "Search query keyword") String query){
        //需要传入的参数
        Map<String,Object> mapparms = new HashMap<>();
        mapparms.put("q",query);
        mapparms.put("api_key",apikey);
        mapparms.put("engine","baidu");
        try {
            //发起http的get请求得到数据
            String respone = HttpUtil.get(SEARCH_URL, mapparms);
            //解析数据
            JSONObject jsonObject = JSONUtil.parseObj(respone);
            JSONArray organic_results = jsonObject.getJSONArray("organic_results");
            List<Object> objects = organic_results.subList(0, 5);

            StringBuilder resultBuilder = new StringBuilder();
            for (int i = 0; i < objects.size(); i++) {
                JSONObject tmpjson = (JSONObject) objects.get(i);

                // 提取中文标题
                String title = tmpjson.getStr("title", "");
                String chineseTitle = extractChineseText(title);

                // 提取链接
                String link = tmpjson.getStr("link", "");

                // 如果有内容就添加到结果中
                if (!chineseTitle.isEmpty() || !link.isEmpty()) {
                    String displayTitle = !chineseTitle.isEmpty() ? chineseTitle : "搜索结果";

                    resultBuilder.append(i + 1).append(". [")
                            .append(displayTitle).append("]");

                    if (!link.isEmpty()) {
                        resultBuilder.append("(").append(link).append(")");
                    }

                    resultBuilder.append("\n");
                }
            }

            String result = resultBuilder.toString().trim();
            return result;
        } catch (Exception e) {
            return "Search fail from Baidu" + e.getMessage();
        }
    }

    // 提取中文文本的辅助方法
    private String extractChineseText(String text) {
        if (text == null || text.trim().isEmpty()) {
            return "";
        }

        StringBuilder chineseText = new StringBuilder();
        String[] words = text.split("\\s+");

        for (String word : words) {
            if (CHINESE_PATTERN.matcher(word).find()) {
                chineseText.append(word).append(" ");
            }
        }

        return chineseText.toString().trim();
    }


}
