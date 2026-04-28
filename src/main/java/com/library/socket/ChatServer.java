package com.library.socket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Multi-threaded TCP chat server.
 * Starts automatically when the Spring application context is ready.
 *
 * Connect with any TCP client (e.g., telnet localhost 9090).
 * First line sent = your username; subsequent lines = messages.
 */
@Component
@Slf4j
public class ChatServer {

    @Value("${library.socket.port:9090}")
    private int port;

    /** Thread-safe list of connected clients. */
    private final List<ClientHandler> clients = new CopyOnWriteArrayList<>();

    /** Fixed thread pool – one thread per client (max 20 concurrent users). */
    private final ExecutorService executor = Executors.newFixedThreadPool(20);

    private ServerSocket serverSocket;
    private Thread       acceptThread;

    /** Called once by Spring after the bean is fully initialized. */
    @PostConstruct
    public void start() {
        acceptThread = new Thread(this::acceptClients, "chat-accept-thread");
        acceptThread.setDaemon(true);   // dies when JVM exits
        acceptThread.start();
        log.info("💬 ChatServer listening on port {}", port);
    }

    /** Gracefully shuts down the server when Spring context closes. */
    @PreDestroy
    public void stop() {
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
            executor.shutdownNow();
            log.info("ChatServer stopped.");
        } catch (IOException e) {
            log.error("Error stopping ChatServer", e);
        }
    }

    /** Main accept loop – runs in a dedicated background thread. */
    private void acceptClients() {
        try {
            serverSocket = new ServerSocket(port);
            while (!serverSocket.isClosed()) {
                Socket clientSocket = serverSocket.accept();
                ClientHandler handler = new ClientHandler(clientSocket, this);
                clients.add(handler);
                executor.submit(handler);   // each client gets its own thread
            }
        } catch (IOException e) {
            if (!serverSocket.isClosed()) {
                log.error("ChatServer accept error", e);
            }
        }
    }

    /**
     * Broadcasts a message to all connected clients except the sender.
     *
     * @param message message text
     * @param sender  the ClientHandler that produced the message (excluded from send)
     */
    public void broadcast(String message, ClientHandler sender) {
        for (ClientHandler client : clients) {
            if (client != sender) {
                client.sendMessage(message);
            }
        }
    }

    /** Removes a disconnected client from the active list. */
    public void removeClient(ClientHandler handler) {
        clients.remove(handler);
    }

    /** Returns how many clients are currently connected. */
    public int getActiveClientCount() {
        return clients.size();
    }
}
