package com.qingluan.aigent.tools;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IORuntimeException;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;


public class FileOperationTool {

    private final String FILE_DIR = FileConstant.FILE_SAVE_DIR + "/file";

    @Tool(description = "Read content from a file")
    public String readFile(@ToolParam(description = "Name of the file to read") String filename){
        //获得该文件路径
        String filepath = FILE_DIR + "/" + filename;
        //通过hutool工具进行对文件的读操作
        try {
            return FileUtil.readUtf8String(filepath);
        } catch (Exception e) {
            return "Error read file :" + e.getMessage();
        }
    }

    @Tool(description = "write content to a file")
    public String writeFile(@ToolParam(description = "Name of the file to write") String filename,
                            @ToolParam(description = "Content to write to the file") String content){
        //写入文件的路径
        String filepath = FILE_DIR + "/" + filename;
        //通过hutool工具写文件
        try {
            //创建目录
            FileUtil.mkdir(filepath);
            FileUtil.writeUtf8String(content,filepath);
            return "File write success :" +  filepath;
        } catch (IORuntimeException e) {
            return "File write fail :" + e.getMessage();
        }
    }

}
