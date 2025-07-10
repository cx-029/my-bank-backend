package com.mybank.backend.service;

import com.alibaba.fastjson2.JSONObject;
import com.mybank.backend.util.BaiduFaceTokenUtil;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FaceRecognitionService {
    // 百度人脸库分组名（group_id），和人脸注册时一致
    private static final String GROUP_ID = "mybank_users";

    @Autowired
    private BaiduFaceTokenUtil tokenUtil;

    public String recognize(String base64Image) {
        // 1. 去掉BASE64前缀
        if (base64Image != null && base64Image.startsWith("data:image")) {
            base64Image = base64Image.substring(base64Image.indexOf(",") + 1);
        }

        System.out.println("FaceRecognitionService.recognize called! base64Image.length=" + (base64Image == null ? "null" : base64Image.length()));
        String url = "https://aip.baidubce.com/rest/2.0/face/v3/search?access_token=" + tokenUtil.getAccessToken();

        JSONObject params = new JSONObject();
        params.put("image", base64Image);
        params.put("image_type", "BASE64");
        params.put("group_id_list", GROUP_ID);

        System.out.println("请求URL: " + url);
        System.out.println("请求参数(前100): " + params.toString().substring(0, Math.min(100, params.toString().length())));

        OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(params.toString(), MediaType.parse("application/json"));
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .addHeader("Content-Type", "application/json")
                .build();
        try {
            Response response = client.newCall(request).execute();
            String res = response.body().string();
            System.out.println("百度API响应: " + res);
            JSONObject json = JSONObject.parseObject(res);
            JSONObject result = json.getJSONObject("result");
            if (result != null && result.getJSONArray("user_list") != null) {
                JSONObject user = result.getJSONArray("user_list").getJSONObject(0);
                double score = user.getDouble("score");
                if (score > 80) { // 推荐阈值80分以上
                    return user.getString("user_id"); // 返回用户名
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}