package com.itcjx.dms.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class WebSocketService {
    // 存储用户ID和WebSocket会话的映射
    private static final ConcurrentHashMap<Long, WebSocketSession> userSessions = new ConcurrentHashMap<>();

    /**
     * 添加用户会话
     */
    public void addSession(Long userId, WebSocketSession session) {
        userSessions.put(userId, session);
        log.info("用户 {} 连接到WebSocket", userId);
    }

    /**
     * 移除用户会话
     */
    public void removeSession(Long userId) {
        userSessions.remove(userId);
        log.info("用户 {} 断开WebSocket连接", userId);
    }

    /**
     * 向特定用户发送消息
     */
    public void sendMessageToUser(Long userId, String message) {
        WebSocketSession session = userSessions.get(userId);
        if (session != null && session.isOpen()) {
            try {
                session.sendMessage(new TextMessage(message));
                log.info("向用户 {} 发送消息: {}", userId, message);
            } catch (IOException e) {
                log.error("向用户 {} 发送消息失败: {}", userId, e.getMessage());
            }
        } else {
            log.warn("用户 {} 的会话不存在或已关闭", userId);
        }
    }
}
