document.addEventListener('DOMContentLoaded', () => {
    const toggleBtn = document.getElementById('chatbot-toggle');
    const windowEl = document.getElementById('chatbot-window');
    const closeBtn = document.getElementById('chatbot-close');
    const inputEl = document.getElementById('chatbot-input');
    const sendBtn = document.getElementById('chatbot-send');
    const messagesEl = document.getElementById('chatbot-messages');

    // Toggle Chat Window
    toggleBtn.addEventListener('click', () => {
        windowEl.classList.toggle('active');
        if (windowEl.classList.contains('active')) {
            inputEl.focus();
            if (messagesEl.children.length === 0) {
                appendMessage("bot", "Hello! I am your AI Library Assistant. Ask me anything about our books or policies!");
            }
        }
    });

    closeBtn.addEventListener('click', () => {
        windowEl.classList.remove('active');
    });

    // Send Message
    const sendMessage = async () => {
        const text = inputEl.value.trim();
        if (!text) return;

        appendMessage("user", text);
        inputEl.value = '';

        // Show typing indicator
        const typingId = "typing-" + Date.now();
        messagesEl.insertAdjacentHTML('beforeend', `
            <div class="bot-msg" id="${typingId}">
                <div class="typing-indicator"><span></span><span></span><span></span></div>
            </div>
        `);
        scrollToBottom();

        try {
            const response = await fetch('/api/chatbot/ask', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ message: text })
            });
            const data = await response.json();
            
            // Remove typing indicator and append real message
            document.getElementById(typingId).remove();
            appendMessage("bot", data.reply);
        } catch (err) {
            document.getElementById(typingId).remove();
            appendMessage("bot", "Sorry, I am having trouble connecting to my neural network right now.");
        }
    };

    sendBtn.addEventListener('click', sendMessage);
    inputEl.addEventListener('keypress', (e) => {
        if (e.key === 'Enter') sendMessage();
    });

    function appendMessage(sender, htmlContent) {
        const div = document.createElement('div');
        div.className = sender === 'user' ? 'user-msg' : 'bot-msg';
        div.innerHTML = htmlContent;
        messagesEl.appendChild(div);
        scrollToBottom();
    }

    function scrollToBottom() {
        messagesEl.scrollTop = messagesEl.scrollHeight;
    }
});
