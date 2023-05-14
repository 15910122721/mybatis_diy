package com.wxy.io;

import java.io.InputStream;

public class Resources {

    /**
     * 根据配置文件路径，加载成字节输入流并存放到内存中
     * @param path
     * @return
     */
    public static InputStream getResourceAsStream(String path){
        return Resources.class.getClassLoader().getResourceAsStream(path);
    }
}
