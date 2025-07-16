package com.mybank.backend.repository;

import com.mybank.backend.entity.CommentLike;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {
    int countByCommentId(Long commentId);
    boolean existsByCommentIdAndUserId(Long commentId, Long userId);
    CommentLike findByCommentIdAndUserId(Long commentId, Long userId);
}