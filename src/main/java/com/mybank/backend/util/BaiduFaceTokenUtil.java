package com.mybank.backend.util;

import com.alibaba.fastjson2.JSONObject;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.stereotype.Component;

@Component
public class BaiduFaceTokenUtil {
    private static final String API_KEY = "ik5NvVMCWLwqRCB6tzfr36wO"; // 你的API Key
    private static final String SECRET_KEY = "yvN8BvHJJ83WQJLi47APycVCPfpxqo3r"; // 你的Secret Key
    private static String accessToken = null;
    private static long expireTime = 0;

    public synchronized String getAccessToken() {
        if (accessToken != null && System.currentTimeMillis() < expireTime) {
            return accessToken;
        }
        try {
            String url = "https://aip.baidubce.com/oauth/2.0/token?grant_type=client_credentials"
                    + "&client_id=" + API_KEY
                    + "&client_secret=" + SECRET_KEY;
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url(url).build();
            Response response = client.newCall(request).execute();
            String res = response.body().string();
            JSONObject json = JSONObject.parseObject(res);
            accessToken = json.getString("access_token");
            int expiresIn = json.getInteger("expires_in");
            expireTime = System.currentTimeMillis() + (expiresIn - 60) * 1000L; // 提前一分钟过期
            return accessToken;
        } catch (Exception e) {
            throw new RuntimeException("获取百度access_token失败", e);
        }
    }
}