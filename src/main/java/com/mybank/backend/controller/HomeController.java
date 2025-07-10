package com.mybank.backend.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
@RequestMapping("/api")
public class HomeController {

    @GetMapping("/home")
    public Map<String, Object> getHomePage() {
        Map<String, Object> res = new HashMap<>();

        // 获取当前登录用户信息
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        String avatar = "https://api.dicebear.com/7.x/identicon/svg?seed=" + username;

        // 当前日期和时间
        LocalDate nowDate = LocalDate.now();
        LocalTime nowTime = LocalTime.now();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

        // 主页主信息
        res.put("username", username);
        res.put("avatar", avatar);
        res.put("welcome", "欢迎回来，" + username + "！");
        res.put("notice", "智慧银行系统为您服务，更多功能请通过左侧菜单访问。");
        res.put("aiWelcome", "AI助手：您好，有什么可以帮您？");
        res.put("nowDate", nowDate.format(dateFormatter));
        res.put("nowTime", nowTime.format(timeFormatter));

        // 示例：通知中心数据
        List<Map<String, String>> notices = new ArrayList<>();
        notices.add(Map.of("title", "系统升级公告", "content", "本周六0:00-6:00系统升级，请提前安排业务。"));
        notices.add(Map.of("title", "防诈骗提醒", "content", "请勿向陌生人透露验证码和账户信息。"));
        notices.add(Map.of("title", "新功能上线", "content", "AI助手现已支持常见业务咨询，欢迎体验！"));
        res.put("notices", notices);

        // 菜单结构（仅前端可用，方便未来扩展）
        List<Map<String, String>> menu = new ArrayList<>();
        menu.add(Map.of("key", "profile", "name", "个人中心", "icon", "UserFilled"));
        menu.add(Map.of("key", "account", "name", "账户管理", "icon", "CreditCard"));
        menu.add(Map.of("key", "loss", "name", "挂失管理", "icon", "WarningFilled"));
        menu.add(Map.of("key", "notice", "name", "通知中心", "icon", "BellFilled"));
        menu.add(Map.of("key", "deposit", "name", "存取管理", "icon", "WalletFilled"));
        menu.add(Map.of("key", "ai", "name", "AI助手", "icon", "Cpu"));

        res.put("menu", menu);

        // 常见问题快捷问法（可前端渲染）
        List<String> quickQuestions = Arrays.asList(
                "如何查询账户余额？",
                "如何修改手机号？",
                "如何挂失银行卡？",
                "如何查看存取明细？",
                "如何收到我的通知？",
                "AI助手能做什么？"
        );
        res.put("quickQuestions", quickQuestions);

        return res;
    }
}