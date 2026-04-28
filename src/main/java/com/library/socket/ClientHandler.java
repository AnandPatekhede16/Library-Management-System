package com.library.socket;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.Socket;

/**
 * Handles a single client connection in its own thread.
 * Reads messages from the client and broadcasts them to all connected clients.
 */
@Slf4j
public class ClientHandler implements Runnable {

    private final Socket                socket;
    private final ChatServer            server;
    private       PrintWriter           out;
    private       String                username;

    public ClientHandler(Socket socket, ChatServer server) {
        this.socket = socket;
        this.server = server;
    }

    @Override
    public void run() {
        try (
            BufferedReader in = new BufferedReader(
                new InputStreamReader(socket.getInputStream()));
        ) {
            out = new PrintWriter(socket.getOutputStream(), true);

            // First message the client sends is treated as the username
            username = in.readLine();
            log.info("Chat: '{}' connected from {}", username, socket.getInetAddress());
            server.broadcast("[" + username + " joined the chat]", this);

            String message;
            while ((message = in.readLine()) != null) {
                log.debug("Chat message from {}: {}", username, message);
                server.broadcast(username + ": " + message, this);
            }
        } catch (IOException e) {
            log.warn("Chat client '{}' disconnected: {}", username, e.getMessage());
        } finally {
            server.removeClient(this);
            server.broadcast("[" + username + " left the chat]", this);
            try { socket.close(); } catch (IOException ignored) {}
        }
    }

    /** Send a message to THIS client. */
    public void sendMessage(String message) {
        if (out != null) {
            out.println(message);
        }
    }

    public String getUsername() { return username; }
}
