package com.example.facelogin.Service;

import com.example.facelogin.config.BaiduFaceProperties;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 百度API Token服务
 * 负责获取和管理百度云API访问令牌
 */
@Service
public class BaiduTokenService {

    @Autowired
    private BaiduFaceProperties properties;

    private String accessToken;

    /**
     * 初始化时获取Token
     */
    @PostConstruct
    public void init() {
        this.accessToken = fetchAccessToken();
    }

    /**
     * 获取当前Token
     * @return 访问令牌
     */
    public String getAccessToken() {
        if (accessToken == null) {
            accessToken = fetchAccessToken();
        }
        return accessToken;
    }

    /**
     * 刷新Token
     * @return 新的访问令牌
     */
    public String refreshToken() {
        this.accessToken = fetchAccessToken();
        return this.accessToken;
    }

    /**
     * 从百度云获取访问令牌
     * @return 访问令牌
     */
    private String fetchAccessToken() {
        String tokenUrl = properties.getTokenUrl()
                + "?grant_type=client_credentials"
                + "&client_id=" + properties.getApiKey()
                + "&client_secret=" + properties.getSecretKey();

        try {
            URL url = new URL(tokenUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()));
            StringBuilder result = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }
            reader.close();

            JSONObject jsonObject = new JSONObject(result.toString());
            String token = jsonObject.getString("access_token");
            System.out.println("百度API Token获取成功");
            return token;

        } catch (Exception e) {
            System.err.println("获取百度API Token失败: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}
