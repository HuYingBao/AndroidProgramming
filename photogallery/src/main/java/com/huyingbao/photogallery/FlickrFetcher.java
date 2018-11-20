package com.huyingbao.photogallery;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 基本网络连接代码
 * Created by liujunfeng on 2018/11/20.
 */
public class FlickrFetcher {
    public byte[] getUrlBytes(String urlSpec) throws IOException {
        //创建Url对象
        URL url = new URL(urlSpec);
        //创建一个指向要访问URL的连接对象
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            //只有在调用getInputStream()方法时，才会真正连接到指定的URL地址
            //如果是POST请求，则调用getOutputStream()方法
            InputStream inputStream = connection.getInputStream();
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new IOException(connection.getResponseCode() + ": with" + urlSpec);
            }
            int bytesRead = 0;
            byte[] buffer = new byte[1024];
            while ((bytesRead = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, bytesRead);
            }
            outputStream.close();
            return outputStream.toByteArray();
        } finally {
            connection.disconnect();
        }
    }

    /**
     * 将getUrlBytes(String)方法获取的字节数据转换为String。
     * @param urlSpec
     * @return
     * @throws IOException
     */
    public String getUrlString(String urlSpec) throws IOException {
        return new String(getUrlBytes(urlSpec));
    }
}
