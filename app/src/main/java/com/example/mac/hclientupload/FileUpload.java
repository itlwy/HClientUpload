package com.example.mac.hclientupload;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by mac on 16/4/2.
 */
public class FileUpload {
    private static final int TIME_OUT = 10 * 10000000; // 超时时间
    private static final String CHARSET = "utf-8"; // 设置编码
    public static final String SUCCESS = "1";
    public static final String FAILURE = "0";
    public static String RequestURL1 = "http://192.168.1.101:8080/UploadFilePro/UploadServlet";
    public static String RequestURL2 = "http://192.168.1.101:8080/UploadFilePro/HClientUpload";

    /**
     * URLConnection upload files
     * @param requestURL
     * @param files
     * @param keyNames
     * @param fileNames
     * @return
     */
    public static String uploadFile(String requestURL,List<File> files,String[] keyNames,String[] fileNames) {
        String BOUNDARY = UUID.randomUUID().toString(); // 边界标识 随机生成
        String PREFIX = "--", LINE_END = "\r\n";
        String CONTENT_TYPE = "multipart/form-data"; // 内容类型
        try {
            URL url = new URL(requestURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(TIME_OUT);
            conn.setConnectTimeout(TIME_OUT);
            conn.setDoInput(true); // 允许输入流
            conn.setDoOutput(true); // 允许输出流
            conn.setUseCaches(false); // 不允许使用缓存
            conn.setRequestMethod("POST"); // 请求方式
            conn.setRequestProperty("Charset", CHARSET); // 设置编码
            conn.setRequestProperty("connection", "keep-alive");
            conn.setRequestProperty("Content-Type", CONTENT_TYPE + ";boundary="
                    +BOUNDARY);
            OutputStream outputSteam = conn.getOutputStream();
            DataOutputStream dos = new DataOutputStream(outputSteam);
            if (files != null) {
                for (int i = 0; i < files.size(); i++) {
                    /**
                     * 当文件不为空，把文件包装并且上传
                     */
                    StringBuffer sb = new StringBuffer();
                    sb.append(PREFIX);
                    sb.append(BOUNDARY);
                    sb.append(LINE_END);
                    /**
                     * 这里重点注意： name里面的值为服务器端需要key 只有这个key 才可以得到对应的文件
                     * filename是文件的名字，包含后缀名的 比如:abc.png
                     */

                    sb.append("Content-Disposition: form-data; name=\""+keyNames[i]+"\"; filename=\""
                            + fileNames[i] + "\"" + LINE_END);
                    sb.append("Content-Type: application/octet-stream; charset="
                            + CHARSET + LINE_END);
                    sb.append(LINE_END);
                    dos.write(sb.toString().getBytes());
                    InputStream is = new FileInputStream(files.get(i));
                    byte[] bytes = new byte[1024];
                    int len = 0;
                    while ((len = is.read(bytes)) != -1) {
                        dos.write(bytes, 0, len);
                    }
                    is.close();
                    dos.write(LINE_END.getBytes());
                    if (i < files.size()-1) {  //last one skip
                        byte[] end_data = (PREFIX + BOUNDARY  + LINE_END)
                                .getBytes();
                        dos.write(end_data);
                    }
                }
                byte[] end_data = (PREFIX + BOUNDARY  + PREFIX + LINE_END)  //last divider
                        .getBytes();
                dos.write(end_data);
                dos.flush();
                /**
                 * 获取响应码 200=成功 当响应成功，获取响应的流
                 */
                int res = conn.getResponseCode();
                if (res == 200) {
                    return SUCCESS;
                }
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return FAILURE;
    }


    public static String uploadFilesByHClient(String url,Map<String, String> params,List<File> files)
            throws ClientProtocolException, IOException {
        HttpClient client=new DefaultHttpClient();// 开启一个客户端 HTTP 请求
        HttpPost post = new HttpPost(url);//创建 HTTP POST 请求
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
//      builder.setCharset(Charset.forName("uft-8"));//设置请求的编码格式
        builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);//设置浏览器兼容模式
        int count=0;
        for (File file:files) {
//          FileBody fileBody = new FileBody(file);//把文件转换成流对象FileBody
//          builder.addPart("file"+count, fileBody);
            builder.addBinaryBody("file"+count, file);
            count++;
        }
        builder.addTextBody("method", params.get("method"));//设置请求参数
        String types = params.get("fileTypes");
        builder.addTextBody("fileTypes", types);//设置请求参数
        HttpEntity entity = builder.build();// 生成 HTTP POST 实体
        post.setEntity(entity);//设置请求参数
        HttpResponse response = client.execute(post);// 发起请求 并返回请求的响应
        if (response.getStatusLine().getStatusCode()==200) {
            return SUCCESS;
        }
        return FAILURE;
    }

}

