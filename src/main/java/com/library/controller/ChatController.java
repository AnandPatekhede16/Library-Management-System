package com.library.controller;

import com.library.socket.ChatServer;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Serves the chat UI page. The actual communication uses raw TCP sockets.
 * The browser connects via WebSocket proxy or users connect via telnet/nc.
 */
@Controller
@RequiredArgsConstructor
public class ChatController {

    private final ChatServer chatServer;

    @Value("${library.socket.port:9090}")
    private int socketPort;

    @GetMapping("/chat")
    public String chatPage(Model model) {
        model.addAttribute("socketPort", socketPort);
        model.addAttribute("activeClients", chatServer.getActiveClientCount());
        return "chat/chat";
    }
}
