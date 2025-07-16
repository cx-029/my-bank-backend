package com.mybank.backend.controller;

import com.mybank.backend.entity.Notification;
import com.mybank.backend.entity.NotificationComment;
import com.mybank.backend.service.NotificationService;
import com.mybank.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.mybank.backend.entity.User;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private UserService userService; // 新增依赖，用于通过用户名查ID

    // 获取通知列表
    @GetMapping
    public List<Notification> listNotifications() {
        return notificationService.getAllNotifications();
    }

    // 获取通知点赞数
    @GetMapping("/{id}/likes")
    public int getLikeCount(@PathVariable Long id) {
        return notificationService.getLikeCount(id);
    }

    // 取消点赞
    @DeleteMapping("/{id}/likes")
    public boolean unlikeNotification(@PathVariable Long id, Principal principal) {
        User user = userService.findByUsername(principal.getName());
        if (user == null) return false;
        return notificationService.unlikeNotification(id, user.getId());
    }

    // 获取通知详情
    @GetMapping("/{id}")
    public Notification getNotification(@PathVariable Long id) {
        return notificationService.getNotificationDetail(id);
    }

    // 获取通知评论
    @GetMapping("/{id}/comments")
    public List<NotificationComment> getComments(@PathVariable Long id) {
        return notificationService.getComments(id);
    }

    // 新增评论，userId 由后端判定
    @PostMapping("/{id}/comments")
    public void addComment(@PathVariable Long id, @RequestBody Map<String, Object> body, Principal principal) {
        User user = userService.findByUsername(principal.getName());
        Long userId = user != null ? user.getId() : null;
        String comment = body.get("comment").toString();
        notificationService.addComment(id, userId, comment);
    }

    // 用户点赞，userId 由后端判定
    @PostMapping("/{id}/likes")
    public boolean likeNotification(@PathVariable Long id, Principal principal) {
        User user = userService.findByUsername(principal.getName());
        Long userId = user != null ? user.getId() : null;
        return notificationService.likeNotification(id, userId);
    }

    // 查询用户是否点赞，userId 由后端判定
    @GetMapping("/{id}/likes/me")
    public boolean hasUserLiked(@PathVariable Long id, Principal principal) {
        User user = userService.findByUsername(principal.getName());
        Long userId = user != null ? user.getId() : null;
        return notificationService.hasUserLiked(id, userId);
    }
}