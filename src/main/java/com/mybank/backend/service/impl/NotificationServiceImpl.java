package com.mybank.backend.service.impl;

import com.mybank.backend.entity.Notification;
import com.mybank.backend.entity.NotificationComment;
import com.mybank.backend.entity.NotificationLike;
import com.mybank.backend.repository.NotificationCommentRepository;
import com.mybank.backend.repository.NotificationLikeRepository;
import com.mybank.backend.repository.NotificationRepository;
import com.mybank.backend.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;
    @Autowired
    private NotificationCommentRepository commentRepository;
    @Autowired
    private NotificationLikeRepository likeRepository;

    @Override
    public List<Notification> getAllNotifications() {
        return notificationRepository.findAll();
    }

    @Override
    public Notification getNotificationDetail(Long id) {
        return notificationRepository.findById(id).orElse(null);
    }

    @Override
    public List<NotificationComment> getComments(Long notificationId) {
        return commentRepository.findByNotificationId(notificationId);
    }

    @Override
    public void addComment(Long notificationId, Long userId, String comment) {
        NotificationComment nc = new NotificationComment();
        nc.setNotificationId(notificationId);
        nc.setUserId(userId);
        nc.setComment(comment);
        nc.setCreatedAt(new Date());
        commentRepository.save(nc);
    }

    @Override
    public boolean unlikeNotification(Long notificationId, Long userId) {
        NotificationLike like = likeRepository.findByNotificationIdAndUserId(notificationId, userId);
        if (like != null) {
            likeRepository.delete(like);
            return true;
        }
        return false;
    }

    @Override
    public int getLikeCount(Long notificationId) {
        return likeRepository.countByNotificationId(notificationId);
    }

    @Override
    public boolean likeNotification(Long notificationId, Long userId) {
        if (likeRepository.existsByNotificationIdAndUserId(notificationId, userId)) {
            return false; // 已点赞
        }
        NotificationLike nl = new NotificationLike();
        nl.setNotificationId(notificationId);
        nl.setUserId(userId);
        nl.setCreatedAt(new Date());
        likeRepository.save(nl);
        return true;
    }

    @Override
    public boolean hasUserLiked(Long notificationId, Long userId) {
        return likeRepository.existsByNotificationIdAndUserId(notificationId, userId);
    }
}