package com.example.facelogin.Controller;

import com.example.facelogin.Service.LoginService;
import com.example.facelogin.Utils.GsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/login")
@SessionAttributes(value = "useinf")
public class LoginController {

    // Default admin credentials
    private static final String DEFAULT_USERNAME = "admin";
    private static final String DEFAULT_PASSWORD = "123123";

    @Autowired
    LoginService loginService = null;

    @RequestMapping("/jumpGetface")
    public String getface() {
        return "getface.html";
    }

    /**
     * Password login endpoint
     */
    @PostMapping("/password")
    @ResponseBody
    public Map<String, Object> passwordLogin(
            @RequestParam String username,
            @RequestParam String password,
            HttpServletRequest request) {

        Map<String, Object> result = new HashMap<>();

        if (DEFAULT_USERNAME.equals(username) && DEFAULT_PASSWORD.equals(password)) {
            HttpSession session = request.getSession();
            session.setAttribute("userinf", username);
            result.put("success", true);
            result.put("message", "Login successful");
            System.out.println("Password login successful: " + username);
        } else {
            result.put("success", false);
            result.put("message", "Invalid username or password");
            System.out.println("Password login failed: " + username);
        }

        return result;
    }

    @RequestMapping("/searchface")
    @ResponseBody
    public String searchface(@RequestBody @RequestParam(name = "imagebast64") StringBuffer imagebast64, Model model, HttpServletRequest request) throws IOException {
        Map<String, Object> result = loginService.searchFace(imagebast64);
        if (result == null || result.get("user_id") == null) {
            System.out.println("未找到匹配用户");
            return GsonUtils.toJson("fail");
        }

        String userId = result.get("user_id").toString();
        String scoreStr = result.get("score").toString();
        double score = Double.parseDouble(scoreStr);

        // 获取阈值（从后端返回，支持口罩模式动态阈值）
        int threshold = result.get("threshold") != null
                ? ((Number) result.get("threshold")).intValue()
                : 80;

        if (score > threshold) {
            model.addAttribute("userinf", userId);
            HttpSession session = request.getSession();
            session.setAttribute("userinf", userId);
            System.out.println("登录成功，用户ID: " + userId + ", 分数: " + score);
        }

        System.out.println("搜索结果: " + result);
        return GsonUtils.toJson(result);
    }
    @RequestMapping("/facesucceed")
    public String Faceloginsucceed(){
        System.out.println(1222222);
        return "succeed";
    }

}
