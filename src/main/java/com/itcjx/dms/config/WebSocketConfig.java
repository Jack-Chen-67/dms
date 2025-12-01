package com.itcjx.dms.config;

import com.itcjx.dms.websocket.AiResultWebSocketHandler;
import com.itcjx.dms.websocket.WebSocketHandshakeInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    @Autowired
    private AiResultWebSocketHandler aiResultWebSocketHandler;

    @Autowired
    private WebSocketHandshakeInterceptor webSocketHandshakeInterceptor;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(aiResultWebSocketHandler, "/ws/ai-results")
                .setAllowedOrigins("*")
                .addInterceptors(webSocketHandshakeInterceptor);
    }
}
