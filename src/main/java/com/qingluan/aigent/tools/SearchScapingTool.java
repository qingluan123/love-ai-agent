package com.qingluan.aigent.tools;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

import java.io.IOException;

public class SearchScapingTool {

    @Tool(description = "Scrape the content of a web page")
    public String searchscap(@ToolParam(description = "url of the web page to scrape") String url){

        try {
            //抓取网页的信息
            Document document = Jsoup.connect(url).get();
            //返回网页的代码
            return document.html();
        } catch (IOException e) {
            return "searchscap fail :" + e.getMessage();
        }
    }
}
