package com.example.facelogin.Model;

/**
 * Face API Setting Model (for FaceController)
 * Used for face registration, update operations with Base64 image
 */
public class Setingmodel {

    private String image;           // Base64 encoded image
    private String image_type;      // Image type: BASE64
    private String group_id;        // Face group ID
    private String user_id;         // User ID
    private String quality_control; // Quality control: NONE, LOW, NORMAL, HIGH
    private String liveness_control; // Liveness control: NONE, LOW, NORMAL, HIGH

    // Getters and Setters

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getImage_type() {
        return image_type;
    }

    public void setImage_type(String image_type) {
        this.image_type = image_type;
    }

    public String getGroup_id() {
        return group_id;
    }

    public void setGroup_id(String group_id) {
        this.group_id = group_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getQuality_control() {
        return quality_control;
    }

    public void setQuality_control(String quality_control) {
        this.quality_control = quality_control;
    }

    public String getLiveness_control() {
        return liveness_control;
    }

    public void setLiveness_control(String liveness_control) {
        this.liveness_control = liveness_control;
    }
}
