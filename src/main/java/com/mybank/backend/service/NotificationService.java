package com.mybank.backend.service;

import com.mybank.backend.entity.Notification;
import com.mybank.backend.entity.NotificationComment;

import java.util.List;

public interface NotificationService {
    List<Notification> getAllNotifications();
    Notification getNotificationDetail(Long id);
    List<NotificationComment> getComments(Long notificationId);
    void addComment(Long notificationId, Long userId, String comment);
    int getLikeCount(Long notificationId);
    boolean likeNotification(Long notificationId, Long userId);
    boolean hasUserLiked(Long notificationId, Long userId);
}