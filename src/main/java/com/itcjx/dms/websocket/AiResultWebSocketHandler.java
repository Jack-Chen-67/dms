package com.itcjx.dms.websocket;

import com.itcjx.dms.service.WebSocketService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Map;

@Slf4j
@Component
public class AiResultWebSocketHandler extends TextWebSocketHandler {

    @Autowired
    private WebSocketService webSocketService;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        // 从URL参数中获取用户ID
        Long userId = (Long) session.getAttributes().get("userId");

        if (userId != null) {
            webSocketService.addSession(userId, session);
            session.sendMessage(new TextMessage("{\"type\":\"connected\",\"message\":\"WebSocket连接已建立\"}"));
            log.info("用户 {} 的WebSocket连接已建立", userId);
        } else {
            session.close();
            log.warn("WebSocket连接缺少用户ID参数，连接已关闭");
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        // 处理客户端发送的消息（如果需要）
        String payload = message.getPayload();
        log.info("收到客户端消息: {}", payload);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        Map<String, Object> attributes = session.getAttributes();
        Long userId = (Long) attributes.get("userId");

        if (userId != null) {
            webSocketService.removeSession(userId);
            log.info("用户 {} 的WebSocket连接已关闭，状态: {}", userId, status);
        }
    }
}
