package com.ppfuns.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 网络图片文件下载,需在线程中调用
 * Created by flyzebra on 2016/6/22.
 */
public class DownloadFiles {
    private static final int max_size = 8 * 1024*1024;
    private static final int CONNECT_TIME = 10000;//连接超时时间
    /**
     * 建立HTTP请求，并获取Bitmap对象。
     * @param imgUrl 图片的URL地址
     * @outputStream 保存图像的文件流
     */
    public static boolean downloadImgUrlToStream(String imgUrl, OutputStream outputStream) {
        HttpURLConnection urlConnection = null;
        BufferedOutputStream out = null;
        BufferedInputStream in = null;
        InputStream inputStream =null;
        boolean flag = false;
        try {
            final URL url = new URL(imgUrl);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setConnectTimeout(CONNECT_TIME);
            urlConnection.setReadTimeout(CONNECT_TIME);
            urlConnection.setDoInput(true);
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                in = new BufferedInputStream(inputStream, max_size);
                out = new BufferedOutputStream(outputStream,max_size);
                int b;
                while ((b = in.read()) != -1) {
                    out.write(b);
                }
                flag = true;
            }else{
                flag = false;
            }
        } catch (final IOException e) {
            flag = false;
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
                if(outputStream!=null){
                    outputStream.close();
                }
                if(inputStream!=null){
                    inputStream.close();
                }
            } catch (final IOException e) {
                e.printStackTrace();
            }
        }
        return flag;
    }

}
