package com.qingluan.aigent.tools;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


class ResourceDownLoadTest {

    @Test
    void resourcedownload() {
        ResourceDownLoad resourceDownLoad = new ResourceDownLoad();
        String url = "https://pic3.zhimg.com/v2-e52354ffdbd94a8e0a7649eacd34a788_r.jpg?source=1940ef5c";
        String filename = "logo.png";
        String result = resourceDownLoad.resourcedownload(url, filename);
        assertNotNull(result);
    }
}