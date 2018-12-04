package com.huyingbao.photogallery;

import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * 基本网络连接代码
 * Created by liujunfeng on 2018/11/20.
 */
public class FlickrFetcher {
    private static final String TAG = "FlickrFetcher";

    /**
     * 根据url获取对应的数据
     *
     * @param urlSpec
     * @return
     * @throws IOException
     */
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
//            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
//                throw new IOException(connection.getResponseCode() + ": with" + urlSpec);
//            }
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
     *
     * @param urlSpec
     * @return
     * @throws IOException
     */
    public String getUrlString(String urlSpec) throws IOException {
        return new String(getUrlBytes(urlSpec));
    }

    /**
     * 构建请求URL并获取内容
     */
    public List<GalleryItem> fetchItems() {
        List<GalleryItem> items = new ArrayList<>();
        //Uri.Builder可创建正确转义的参数化URL。
        //Uri.Builder.appendQueryParameter(String,String)可自动转义查询字符串
        try {
            String url = Uri.parse("https://gank.io/api/random/data/福利/20").toString();
            String jsonString = getUrlString(url);
            Log.i(TAG, "Received JSON: " + jsonString);
            JSONObject jsonObject = new JSONObject(jsonString);
            parseItems(items, jsonObject);
        } catch (IOException e) {
            Log.e(TAG, "Failed to fetch items", e);
        } catch (JSONException e) {
            Log.e(TAG, "Failed to parse JSON", e);
        }
        return items;
    }

    /**
     * 解析JSON数组
     *
     * @param items
     * @param jsonObject
     * @throws JSONException
     */
    private void parseItems(List<GalleryItem> items, JSONObject jsonObject) throws JSONException {
        JSONArray photoJSONArray = jsonObject.getJSONArray("results");
        for (int i = 0; i < photoJSONArray.length(); i++) {
            JSONObject photoJSONObject = photoJSONArray.getJSONObject(i);
            GalleryItem item = new GalleryItem();
            item.setId(photoJSONObject.getString("_id"));
            item.setCaption(photoJSONObject.getString("desc"));
            item.setUrl(photoJSONObject.getString("url"));
            items.add(item);
        }
    }
}
