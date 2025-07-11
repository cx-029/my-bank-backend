package com.mybank.backend.service.impl;

import com.alibaba.fastjson2.JSONObject;
import com.mybank.backend.service.FaceRecognitionService;
import com.mybank.backend.util.BaiduFaceTokenUtil;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FaceRecognitionServiceImpl implements FaceRecognitionService {
    // 百度人脸库分组名（group_id），和人脸注册时一致
    private static final String GROUP_ID = "mybank_users";

    @Autowired
    private BaiduFaceTokenUtil tokenUtil;

    @Override
    public String recognize(String base64Image) {
        if (base64Image != null && base64Image.startsWith("data:image")) {
            base64Image = base64Image.substring(base64Image.indexOf(",") + 1);
        }

        String url = "https://aip.baidubce.com/rest/2.0/face/v3/search?access_token=" + tokenUtil.getAccessToken();

        JSONObject params = new JSONObject();
        params.put("image", base64Image);
        params.put("image_type", "BASE64");
        params.put("group_id_list", GROUP_ID);

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
            JSONObject json = JSONObject.parseObject(res);
            JSONObject result = json.getJSONObject("result");
            if (result != null && result.getJSONArray("user_list") != null) {
                JSONObject user = result.getJSONArray("user_list").getJSONObject(0);
                double score = user.getDouble("score");
                if (score > 80) {
                    return user.getString("user_id");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}