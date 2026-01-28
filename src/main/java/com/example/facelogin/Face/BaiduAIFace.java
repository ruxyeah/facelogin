package com.example.facelogin.Face;

import com.alibaba.fastjson.JSONObject;
import com.baidu.aip.util.Base64Util;
import com.example.facelogin.SetingModel.Setingmodel;
import com.example.facelogin.Utils.GsonUtils;
import com.example.facelogin.Utils.HttpUtil;
import com.example.facelogin.config.BaiduFaceProperties;
import com.example.facelogin.Service.BaiduTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 百度AI人脸识别服务
 * 提供人脸注册、搜索、检测等功能
 */
@Component
public class BaiduAIFace {

    @Autowired
    private BaiduFaceProperties properties;

    @Autowired
    private BaiduTokenService tokenService;

    /**
     * 获取访问令牌
     */
    private String getToken() {
        return tokenService.getAccessToken();
    }

    /**
     * 人脸注册
     * @param setting 参数设置
     * @return 注册结果
     */
    public Map<String, Object> registerFace(Setingmodel setting) throws IOException {
        return addOrUpdateFace(setting, properties.getRegisterUrl());
    }

    /**
     * 人脸更新
     * @param setting 参数设置
     * @return 更新结果
     */
    public Map<String, Object> updateFace(Setingmodel setting) throws IOException {
        return addOrUpdateFace(setting, properties.getUpdateUrl());
    }

    /**
     * 人脸注册/更新通用方法
     */
    private Map<String, Object> addOrUpdateFace(Setingmodel setting, String url) throws IOException {
        byte[] bytes = Files.readAllBytes(Paths.get(setting.getImgpath()));
        String imageBase64 = Base64Util.encode(bytes);

        Map<String, Object> params = new HashMap<>();
        params.put("image", imageBase64);
        params.put("group_id", setting.getGroupID());
        params.put("user_id", setting.getUserID());
        params.put("liveness_control", setting.getLiveness_Control());
        params.put("image_type", setting.getImage_Type());
        params.put("quality_control", setting.getQuality_Control());

        try {
            String result = HttpUtil.post(url, getToken(), "application/json", GsonUtils.toJson(params));
            return GsonUtils.fromJson(result, Map.class);
        } catch (Exception e) {
            System.err.println("人脸注册/更新失败: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 查询用户人脸列表
     * @param setting 参数设置
     * @return 人脸列表
     */
    public Map<String, Object> getUserFaceList(Setingmodel setting) {
        Map<String, Object> params = new HashMap<>();
        params.put("group_id", setting.getGroupID());
        params.put("user_id", setting.getUserID());

        try {
            String result = HttpUtil.post(properties.getFaceListUrl(), getToken(),
                    "application/json", GsonUtils.toJson(params));
            return GsonUtils.fromJson(result, Map.class);
        } catch (Exception e) {
            System.err.println("查询用户人脸列表失败: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 查询分组用户列表
     * @param setting 参数设置
     * @return 用户列表
     */
    public Map<String, Object> getGroupUserList(Setingmodel setting) {
        Map<String, Object> params = new HashMap<>();
        params.put("group_id", setting.getGroupID());

        try {
            String result = HttpUtil.post(properties.getGroupUsersUrl(), getToken(),
                    "application/json", GsonUtils.toJson(params));
            return GsonUtils.fromJson(result, Map.class);
        } catch (Exception e) {
            System.err.println("查询分组用户列表失败: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 删除用户人脸
     * @param setting 参数设置
     * @return 删除结果
     */
    public Map<String, Object> deleteUserFace(Setingmodel setting) {
        Map<String, Object> faceList = getUserFaceList(setting);
        if (faceList == null) {
            return null;
        }

        try {
            String resultStr = GsonUtils.toJson(faceList.get("result"));
            JSONObject resultJson = JSONObject.parseObject(resultStr);
            String faceListStr = resultJson.getString("face_list");
            // 解析 face_token
            String faceToken = faceListStr.substring(2, faceListStr.length() - 2).split("\"")[7];

            Map<String, Object> params = new HashMap<>();
            params.put("group_id", setting.getGroupID());
            params.put("user_id", setting.getUserID());
            params.put("face_token", faceToken);

            String result = HttpUtil.post(properties.getDeleteUrl(), getToken(),
                    "application/json", GsonUtils.toJson(params));
            return GsonUtils.fromJson(result, Map.class);
        } catch (Exception e) {
            System.err.println("删除用户人脸失败: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 人脸搜索
     * @param setting 参数设置
     * @return 搜索结果
     */
    public Map<String, Object> searchFace(Setingmodel setting) throws IOException {
        Map<String, Object> params = new HashMap<>();
        params.put("image", setting.getImgpath());
        params.put("liveness_control", setting.getLiveness_Control());
        params.put("group_id_list", setting.getGroupID());
        params.put("image_type", setting.getImage_Type());
        params.put("quality_control", setting.getQuality_Control());
        params.put("face_type", setting.getFaceType());

        try {
            String result = HttpUtil.post(properties.getSearchUrl(), getToken(),
                    "application/json", GsonUtils.toJson(params));

            JSONObject jsonResult = JSONObject.parseObject(result);
            int errorCode = jsonResult.getIntValue("error_code");
            System.out.println("人脸搜索错误码: " + errorCode);

            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("error_code", errorCode);
            resultMap.put("error_msg", jsonResult.getString("error_msg"));

            if (errorCode == 0) {
                JSONObject resultObj = jsonResult.getJSONObject("result");
                if (resultObj != null) {
                    String faceToken = resultObj.getString("face_token");
                    JSONObject userInfo = resultObj.getJSONArray("user_list").getJSONObject(0);

                    resultMap.put("face_token", faceToken);
                    resultMap.put("group_id", userInfo.getString("group_id"));
                    resultMap.put("user_id", userInfo.getString("user_id"));
                    resultMap.put("user_info", userInfo.getString("user_info"));
                    resultMap.put("score", String.valueOf(userInfo.getDoubleValue("score")));
                    resultMap.put("mask_mode", setting.isMaskMode());

                    System.out.println("识别成功 - user_id: " + userInfo.getString("user_id")
                            + ", score: " + userInfo.getDoubleValue("score"));
                }
            } else {
                System.out.println("人脸搜索失败，错误码: " + errorCode);
            }

            return resultMap;
        } catch (Exception e) {
            System.err.println("人脸搜索异常: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 人脸检测（检测口罩状态）
     * @param setting 参数设置
     * @return 检测结果
     */
    public Map<String, Object> detectFace(Setingmodel setting) {
        Map<String, Object> params = new HashMap<>();
        params.put("image", setting.getImgpath());
        params.put("image_type", setting.getImage_Type());
        params.put("face_field", "mask,quality,face_type");

        try {
            String result = HttpUtil.post(properties.getDetectUrl(), getToken(),
                    "application/json", GsonUtils.toJson(params));
            System.out.println("人脸检测结果: " + result);
            return GsonUtils.fromJson(result, Map.class);
        } catch (Exception e) {
            System.err.println("人脸检测失败: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 解析口罩状态
     * @param detectResult 检测结果
     * @return true-戴口罩，false-未戴口罩
     */
    @SuppressWarnings("unchecked")
    public boolean parseMaskStatus(Map<String, Object> detectResult) {
        try {
            if (detectResult == null) {
                return false;
            }

            Map<String, Object> result = (Map<String, Object>) detectResult.get("result");
            if (result == null) {
                return false;
            }

            List<Map<String, Object>> faceList = (List<Map<String, Object>>) result.get("face_list");
            if (faceList == null || faceList.isEmpty()) {
                return false;
            }

            Map<String, Object> face = faceList.get(0);
            Map<String, Object> mask = (Map<String, Object>) face.get("mask");
            if (mask == null) {
                return false;
            }

            int maskType = ((Number) mask.get("type")).intValue();
            System.out.println("口罩检测结果: " + (maskType == 1 ? "戴口罩" : "未戴口罩"));
            return maskType == 1;
        } catch (Exception e) {
            System.err.println("解析口罩状态失败: " + e.getMessage());
            return false;
        }
    }

    /**
     * 带口罩检测的人脸搜索
     * @param setting 参数设置
     * @return 搜索结果（包含口罩状态）
     */
    public Map<String, Object> searchFaceWithMaskDetection(Setingmodel setting) throws IOException {
        // 1. 检测口罩状态
        Map<String, Object> detectResult = detectFace(setting);
        boolean hasMask = parseMaskStatus(detectResult);

        // 2. 根据口罩状态调整参数
        if (hasMask) {
            System.out.println("检测到口罩，启用口罩模式");
            setting.setMaskMode(true);
            setting.setFaceType("LIVE");
        }

        // 3. 执行人脸搜索
        Map<String, Object> result = searchFace(setting);
        if (result != null) {
            result.put("has_mask", hasMask);
            result.put("threshold", hasMask ? properties.getMaskThreshold() : properties.getNormalThreshold());
        }

        return result;
    }

    // ============ 新增方法：供 FaceController 使用 ============

    /**
     * 人脸注册（使用Base64图片）
     * @param setting 包含 image(base64), group_id, user_id 等参数
     * @return API响应JSON字符串
     */
    public String registerFace(com.example.facelogin.Model.Setingmodel setting) {
        Map<String, Object> params = new HashMap<>();
        params.put("image", setting.getImage());
        params.put("image_type", setting.getImage_type());
        params.put("group_id", setting.getGroup_id());
        params.put("user_id", setting.getUser_id());
        params.put("quality_control", setting.getQuality_control());
        params.put("liveness_control", setting.getLiveness_control());

        try {
            return HttpUtil.post(properties.getRegisterUrl(), getToken(),
                    "application/json", GsonUtils.toJson(params));
        } catch (Exception e) {
            System.err.println("人脸注册失败: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 人脸更新（使用Base64图片）
     * @param setting 包含 image(base64), group_id, user_id 等参数
     * @return API响应JSON字符串
     */
    public String updateFace(com.example.facelogin.Model.Setingmodel setting) {
        Map<String, Object> params = new HashMap<>();
        params.put("image", setting.getImage());
        params.put("image_type", setting.getImage_type());
        params.put("group_id", setting.getGroup_id());
        params.put("user_id", setting.getUser_id());
        params.put("quality_control", setting.getQuality_control());
        params.put("liveness_control", setting.getLiveness_control());

        try {
            return HttpUtil.post(properties.getUpdateUrl(), getToken(),
                    "application/json", GsonUtils.toJson(params));
        } catch (Exception e) {
            System.err.println("人脸更新失败: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取用户的人脸列表
     * @param groupId 分组ID
     * @param userId 用户ID
     * @return face_token列表
     */
    @SuppressWarnings("unchecked")
    public List<String> getFaceList(String groupId, String userId) {
        Map<String, Object> params = new HashMap<>();
        params.put("group_id", groupId);
        params.put("user_id", userId);

        try {
            String result = HttpUtil.post(properties.getFaceListUrl(), getToken(),
                    "application/json", GsonUtils.toJson(params));
            System.out.println("获取人脸列表: " + result);

            JSONObject json = JSONObject.parseObject(result);
            if (json.getIntValue("error_code") != 0) {
                return null;
            }

            JSONObject resultObj = json.getJSONObject("result");
            if (resultObj == null) {
                return null;
            }

            List<String> faceTokens = new java.util.ArrayList<>();
            com.alibaba.fastjson.JSONArray faceList = resultObj.getJSONArray("face_list");
            if (faceList != null) {
                for (int i = 0; i < faceList.size(); i++) {
                    faceTokens.add(faceList.getJSONObject(i).getString("face_token"));
                }
            }
            return faceTokens;

        } catch (Exception e) {
            System.err.println("获取人脸列表失败: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 删除指定人脸
     * @param groupId 分组ID
     * @param userId 用户ID
     * @param faceToken 人脸Token
     * @return API响应JSON字符串
     */
    public String deleteFace(String groupId, String userId, String faceToken) {
        Map<String, Object> params = new HashMap<>();
        params.put("group_id", groupId);
        params.put("user_id", userId);
        params.put("face_token", faceToken);

        try {
            return HttpUtil.post(properties.getDeleteUrl(), getToken(),
                    "application/json", GsonUtils.toJson(params));
        } catch (Exception e) {
            System.err.println("删除人脸失败: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取分组内所有用户ID
     * @param groupId 分组ID
     * @return 用户ID列表
     */
    @SuppressWarnings("unchecked")
    public List<String> getGroupUsers(String groupId) {
        Map<String, Object> params = new HashMap<>();
        params.put("group_id", groupId);

        try {
            String result = HttpUtil.post(properties.getGroupUsersUrl(), getToken(),
                    "application/json", GsonUtils.toJson(params));
            System.out.println("获取用户列表: " + result);

            JSONObject json = JSONObject.parseObject(result);
            if (json.getIntValue("error_code") != 0) {
                return new java.util.ArrayList<>();
            }

            JSONObject resultObj = json.getJSONObject("result");
            if (resultObj == null) {
                return new java.util.ArrayList<>();
            }

            List<String> userIds = new java.util.ArrayList<>();
            com.alibaba.fastjson.JSONArray userList = resultObj.getJSONArray("user_id_list");
            if (userList != null) {
                for (int i = 0; i < userList.size(); i++) {
                    userIds.add(userList.getString(i));
                }
            }
            return userIds;

        } catch (Exception e) {
            System.err.println("获取用户列表失败: " + e.getMessage());
            e.printStackTrace();
            return new java.util.ArrayList<>();
        }
    }
}
