package com.mybank.backend.controller;

import com.mybank.backend.entity.Notification;
import com.mybank.backend.entity.NotificationComment;
import com.mybank.backend.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    // 获取通知列表
    @GetMapping
    public List<Notification> listNotifications() {
        return notificationService.getAllNotifications();
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

    // 新增评论
    @PostMapping("/{id}/comments")
    public void addComment(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        Long userId = Long.valueOf(body.get("userId").toString());
        String comment = body.get("comment").toString();
        notificationService.addComment(id, userId, comment);
    }

    // 获取点赞数
    @GetMapping("/{id}/likes")
    public int getLikeCount(@PathVariable Long id) {
        return notificationService.getLikeCount(id);
    }

    // 用户点赞
    @PostMapping("/{id}/likes")
    public boolean likeNotification(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        Long userId = Long.valueOf(body.get("userId").toString());
        return notificationService.likeNotification(id, userId);
    }

    // 查询用户是否点赞
    @GetMapping("/{id}/likes/{userId}")
    public boolean hasUserLiked(@PathVariable Long id, @PathVariable Long userId) {
        return notificationService.hasUserLiked(id, userId);
    }
}