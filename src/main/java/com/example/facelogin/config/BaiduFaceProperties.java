package com.example.facelogin.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Baidu Face Recognition API Configuration
 * Config prefix: baidu.face
 */
@Component
@ConfigurationProperties(prefix = "baidu.face")
public class BaiduFaceProperties {

    /**
     * Baidu Cloud API Key
     */
    private String apiKey;

    /**
     * Baidu Cloud Secret Key
     */
    private String secretKey;

    /**
     * Face Group ID
     */
    private String groupId = "1121";

    /**
     * Baidu API Base URL
     */
    private String baseUrl = "https://aip.baidubce.com";

    /**
     * Face API Path Prefix
     */
    private String faceApiPrefix = "/rest/2.0/face/v3";

    /**
     * Token API Path
     */
    private String tokenPath = "/oauth/2.0/token";

    /**
     * Face Detect API Path (relative to faceApiPrefix)
     */
    private String detectPath = "/detect";

    /**
     * Face Search API Path (relative to faceApiPrefix)
     */
    private String searchPath = "/search";

    /**
     * Face Register API Path (relative to faceApiPrefix)
     */
    private String registerPath = "/faceset/user/add";

    /**
     * Face Update API Path (relative to faceApiPrefix)
     */
    private String updatePath = "/faceset/user/update";

    /**
     * Face Delete API Path (relative to faceApiPrefix)
     */
    private String deletePath = "/faceset/face/delete";

    /**
     * Face List API Path (relative to faceApiPrefix)
     */
    private String faceListPath = "/faceset/face/getlist";

    /**
     * Group Users API Path (relative to faceApiPrefix)
     */
    private String groupUsersPath = "/faceset/group/getusers";

    /**
     * Mask Mode Recognition Threshold (default: 70)
     */
    private int maskThreshold = 70;

    /**
     * Normal Mode Recognition Threshold (default: 80)
     */
    private int normalThreshold = 80;

    // ============ URL Getters (construct full URL) ============

    public String getTokenUrl() {
        return baseUrl + tokenPath;
    }

    public String getDetectUrl() {
        return baseUrl + faceApiPrefix + detectPath;
    }

    public String getSearchUrl() {
        return baseUrl + faceApiPrefix + searchPath;
    }

    public String getRegisterUrl() {
        return baseUrl + faceApiPrefix + registerPath;
    }

    public String getUpdateUrl() {
        return baseUrl + faceApiPrefix + updatePath;
    }

    public String getDeleteUrl() {
        return baseUrl + faceApiPrefix + deletePath;
    }

    public String getFaceListUrl() {
        return baseUrl + faceApiPrefix + faceListPath;
    }

    public String getGroupUsersUrl() {
        return baseUrl + faceApiPrefix + groupUsersPath;
    }

    // ============ Basic Getters and Setters ============

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getFaceApiPrefix() {
        return faceApiPrefix;
    }

    public void setFaceApiPrefix(String faceApiPrefix) {
        this.faceApiPrefix = faceApiPrefix;
    }

    public String getTokenPath() {
        return tokenPath;
    }

    public void setTokenPath(String tokenPath) {
        this.tokenPath = tokenPath;
    }

    public String getDetectPath() {
        return detectPath;
    }

    public void setDetectPath(String detectPath) {
        this.detectPath = detectPath;
    }

    public String getSearchPath() {
        return searchPath;
    }

    public void setSearchPath(String searchPath) {
        this.searchPath = searchPath;
    }

    public String getRegisterPath() {
        return registerPath;
    }

    public void setRegisterPath(String registerPath) {
        this.registerPath = registerPath;
    }

    public String getUpdatePath() {
        return updatePath;
    }

    public void setUpdatePath(String updatePath) {
        this.updatePath = updatePath;
    }

    public String getDeletePath() {
        return deletePath;
    }

    public void setDeletePath(String deletePath) {
        this.deletePath = deletePath;
    }

    public String getFaceListPath() {
        return faceListPath;
    }

    public void setFaceListPath(String faceListPath) {
        this.faceListPath = faceListPath;
    }

    public String getGroupUsersPath() {
        return groupUsersPath;
    }

    public void setGroupUsersPath(String groupUsersPath) {
        this.groupUsersPath = groupUsersPath;
    }

    public int getMaskThreshold() {
        return maskThreshold;
    }

    public void setMaskThreshold(int maskThreshold) {
        this.maskThreshold = maskThreshold;
    }

    public int getNormalThreshold() {
        return normalThreshold;
    }

    public void setNormalThreshold(int normalThreshold) {
        this.normalThreshold = normalThreshold;
    }
}
