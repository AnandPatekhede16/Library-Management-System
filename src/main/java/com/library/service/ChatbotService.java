package com.library.service;

import com.library.entity.Book;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * A simulated AI librarian that answers user queries.
 */
@Service
@RequiredArgsConstructor
public class ChatbotService {

    private final BookService bookService;

    public String getResponse(String message) {
        String lowerMsg = message.toLowerCase();

        // Greeting
        if (lowerMsg.matches(".*\\b(hi|hello|hey|greetings)\\b.*")) {
            return "Hello! I am your AI Library Assistant. How can I help you today? I can help you find books, check our policies, or answer general library questions!";
        }
        
        // Help / Capabilities
        if (lowerMsg.contains("help") || lowerMsg.contains("what can you do")) {
            return "I can help you search for books, tell you about our borrow limits, hours of operation, or recommend popular categories. Try asking: 'Do you have books on Java?'";
        }

        // Borrow Limits
        if (lowerMsg.contains("limit") || lowerMsg.contains("how many books")) {
            return "You can borrow up to 5 books at a time. Each book has a 14-day borrowing period. Make sure to return them on time to avoid penalties!";
        }
        
        // Hours
        if (lowerMsg.contains("hours") || lowerMsg.contains("open")) {
            return "Our digital library is open 24/7! You can browse and borrow books at any time.";
        }

        // Search for a book
        if (lowerMsg.contains("do you have") || lowerMsg.contains("search") || lowerMsg.contains("book on")) {
            String keyword = extractKeyword(lowerMsg);
            if (keyword != null && !keyword.isBlank()) {
                List<Book> results = bookService.search(keyword, null);
                if (!results.isEmpty()) {
                    StringBuilder sb = new StringBuilder("Yes! I found " + results.size() + " book(s) related to '" + keyword + "'. Here are a few:<ul>");
                    results.stream().limit(3).forEach(b -> sb.append("<li><b>").append(b.getTitle()).append("</b> by ").append(b.getAuthor()).append("</li>"));
                    sb.append("</ul>Go to the <a href='/books'>Books</a> page to borrow them!");
                    return sb.toString();
                } else {
                    return "I'm sorry, I couldn't find any books matching '" + keyword + "' right now. But we add new books all the time!";
                }
            }
            return "I can certainly search for books! Just tell me what you're looking for, like 'Do you have books on History?'";
        }

        // Default fallback
        return "That's an interesting question! I'm still learning, so I might not have the answer. You can try searching our catalog directly or ask me about our borrow policies.";
    }

    private String extractKeyword(String msg) {
        // Very basic extraction logic
        String[] prefixes = {"books on ", "book on ", "about ", "for ", "have "};
        for (String prefix : prefixes) {
            int idx = msg.indexOf(prefix);
            if (idx != -1) {
                return msg.substring(idx + prefix.length()).replaceAll("[^a-zA-Z0-9 ]", "").trim();
            }
        }
        return msg.replace("do you have", "").replace("search", "").trim();
    }
}
