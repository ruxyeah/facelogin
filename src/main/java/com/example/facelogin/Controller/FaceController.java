package com.example.facelogin.Controller;

import com.example.facelogin.Face.BaiduAIFace;
import com.example.facelogin.Model.Setingmodel;
import com.example.facelogin.config.BaiduFaceProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Face Management Controller
 * Provides APIs for face registration, update, delete, and user list
 */
@RestController
@RequestMapping("/face")
public class FaceController {

    @Autowired
    private BaiduAIFace baiduAIFace;

    @Autowired
    private BaiduFaceProperties properties;

    /**
     * Register new face
     */
    @PostMapping("/register")
    public Map<String, Object> registerFace(
            @RequestParam String userId,
            @RequestParam String imageBase64) {

        Map<String, Object> result = new HashMap<>();

        try {
            // Remove data URL prefix if present
            String base64Data = removeBase64Prefix(imageBase64);

            // Build setting model
            Setingmodel setting = new Setingmodel();
            setting.setImage(base64Data);
            setting.setImage_type("BASE64");
            setting.setGroup_id(properties.getGroupId());
            setting.setUser_id(userId);
            setting.setQuality_control("NORMAL");
            setting.setLiveness_control("NONE");

            // Call registration API
            String response = baiduAIFace.registerFace(setting);
            System.out.println("Face register response: " + response);

            if (response != null && response.contains("\"error_code\":0")) {
                result.put("success", true);
                result.put("message", "Face registered successfully");
            } else if (response != null && response.contains("already exist")) {
                result.put("success", false);
                result.put("message", "User already exists, please use update function");
            } else {
                result.put("success", false);
                result.put("message", "Registration failed: " + extractErrorMsg(response));
            }

        } catch (Exception e) {
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "Registration error: " + e.getMessage());
        }

        return result;
    }

    /**
     * Update existing face
     */
    @PostMapping("/update")
    public Map<String, Object> updateFace(
            @RequestParam String userId,
            @RequestParam String imageBase64) {

        Map<String, Object> result = new HashMap<>();

        try {
            String base64Data = removeBase64Prefix(imageBase64);

            Setingmodel setting = new Setingmodel();
            setting.setImage(base64Data);
            setting.setImage_type("BASE64");
            setting.setGroup_id(properties.getGroupId());
            setting.setUser_id(userId);
            setting.setQuality_control("NORMAL");
            setting.setLiveness_control("NONE");

            String response = baiduAIFace.updateFace(setting);
            System.out.println("Face update response: " + response);

            if (response != null && response.contains("\"error_code\":0")) {
                result.put("success", true);
                result.put("message", "Face updated successfully");
            } else {
                result.put("success", false);
                result.put("message", "Update failed: " + extractErrorMsg(response));
            }

        } catch (Exception e) {
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "Update error: " + e.getMessage());
        }

        return result;
    }

    /**
     * Delete user face
     */
    @PostMapping("/delete")
    public Map<String, Object> deleteFace(@RequestParam String userId) {

        Map<String, Object> result = new HashMap<>();

        try {
            // First get face list for this user
            List<String> faceList = baiduAIFace.getFaceList(properties.getGroupId(), userId);

            if (faceList == null || faceList.isEmpty()) {
                result.put("success", false);
                result.put("message", "User not found or has no face data");
                return result;
            }

            // Delete all faces for this user
            boolean allDeleted = true;
            for (String faceToken : faceList) {
                String response = baiduAIFace.deleteFace(properties.getGroupId(), userId, faceToken);
                if (response == null || !response.contains("\"error_code\":0")) {
                    allDeleted = false;
                }
            }

            if (allDeleted) {
                result.put("success", true);
                result.put("message", "Face deleted successfully");
            } else {
                result.put("success", false);
                result.put("message", "Some faces failed to delete");
            }

        } catch (Exception e) {
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "Delete error: " + e.getMessage());
        }

        return result;
    }

    /**
     * Get user list in group
     */
    @GetMapping("/userlist")
    public Map<String, Object> getUserList() {

        Map<String, Object> result = new HashMap<>();

        try {
            List<String> users = baiduAIFace.getGroupUsers(properties.getGroupId());

            if (users != null) {
                result.put("success", true);
                result.put("users", users);
                result.put("count", users.size());
            } else {
                result.put("success", true);
                result.put("users", new String[0]);
                result.put("count", 0);
            }

        } catch (Exception e) {
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "Failed to get user list: " + e.getMessage());
        }

        return result;
    }

    /**
     * Remove data URL prefix from base64 string
     */
    private String removeBase64Prefix(String base64) {
        if (base64 == null) return null;
        if (base64.contains(",")) {
            return base64.substring(base64.indexOf(",") + 1);
        }
        return base64;
    }

    /**
     * Extract error message from API response
     */
    private String extractErrorMsg(String response) {
        if (response == null) return "Unknown error";
        try {
            if (response.contains("error_msg")) {
                int start = response.indexOf("\"error_msg\":\"") + 13;
                int end = response.indexOf("\"", start);
                if (start > 12 && end > start) {
                    return response.substring(start, end);
                }
            }
        } catch (Exception e) {
            // ignore
        }
        return "API error";
    }
}
