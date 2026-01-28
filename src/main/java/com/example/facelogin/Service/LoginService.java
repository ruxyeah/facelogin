package com.example.facelogin.Service;

import com.example.facelogin.Face.BaiduAIFace;
import com.example.facelogin.SetingModel.Setingmodel;
import com.example.facelogin.config.BaiduFaceProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;

/**
 * 登录服务
 * 处理人脸登录相关业务逻辑
 */
@Service
public class LoginService {

    @Autowired
    private BaiduAIFace faceApi;

    @Autowired
    private Setingmodel setting;

    @Autowired
    private BaiduFaceProperties properties;

    /**
     * 人脸搜索（支持口罩检测）
     * @param imageBase64 Base64编码的图片数据
     * @return 搜索结果
     */
    public Map<String, Object> searchFace(StringBuffer imageBase64) throws IOException {
        // 去除 Base64 前缀 (data:image/png;base64,)
        String base64Data = imageBase64.substring(imageBase64.indexOf(",") + 1);

        setting.setImgpath(base64Data);
        setting.setGroupID(properties.getGroupId());

        System.out.println("开始人脸搜索（带口罩检测）...");
        return faceApi.searchFaceWithMaskDetection(setting);
    }
}
