package com.qingluan.aigent.tools;

import cn.hutool.core.io.FileUtil;
import cn.hutool.http.HttpUtil;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

import java.io.File;
import static com.qingluan.aigent.tools.FileConstant.FILE_SAVE_DIR;

public class ResourceDownLoad {

    @Tool(description = "Download a resource from a given url")
    public String resourcedownload(@ToolParam(description = "URL of the resource to download") String url,
                                   @ToolParam(description = "Name of the save the redource download") String filename
    ) {
        String filepath = FILE_SAVE_DIR + "/" +"download" + "/" +filename;

        try {
            FileUtil.mkdir(filepath);
            HttpUtil.downloadFile(url,new File(filepath));
            return "Success to download to:" + filepath;
        } catch (Exception e) {
            return "Error to download:" + e.getMessage();
        }

    }
}
