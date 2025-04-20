package com.knowledgekin;

import org.apache.hc.client5.http.fluent.Request;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.client5.http.fluent.Content;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SwingChatbot extends JFrame {
    private static JTextArea chatArea;
    private JTextField inputField;
    private JButton sendButton;

    // Replace with your n8n webhook URL
    // private static final String N8N_WEBHOOK_URL = "http://localhost:5678/webhook-test/9cb6f5a1-a4db-45f1-ab99-db19a9f6e130";

     private static final String N8N_WEBHOOK_URL = "https://apt-formerly-lemming.ngrok-free.app/webhook/9cb6f5a1-a4db-45f1-ab99-db19a9f6e130";

    public SwingChatbot() {
        setTitle("Swing Chatbot with n8n");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        chatArea = new JTextArea();
        chatArea.setEditable(false);
        add(new JScrollPane(chatArea), BorderLayout.CENTER);

        JPanel inputPanel = new JPanel(new BorderLayout());
        inputField = new JTextField();
        sendButton = new JButton("Send");

        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);
        add(inputPanel, BorderLayout.SOUTH);

        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String userMessage = inputField.getText();
                if (!userMessage.isEmpty()) {
                    appendMessage("You: " + userMessage);
                    inputField.setText("");

                    // Send message to n8n webhook and get response
                    new Thread(() -> sendMessageToN8n(userMessage)).start();
                }
            }
        });
    }

    private static void appendMessage(String message) {
        SwingUtilities.invokeLater(() -> chatArea.append(message + "\n"));
    }

    private void sendMessageToN8n(String message) {
        try {
            JSONObject json = new JSONObject();
            json.put("message", message);

            Content response = Request.post(N8N_WEBHOOK_URL)
                    .bodyString(json.toString(), ContentType.APPLICATION_JSON)
                    .execute()
                    .returnContent();

            JSONObject jsonResponse = new JSONObject(response.asString());

            String botReply = jsonResponse.optString("output", "No response from bot");

            appendMessage("Bot: " + botReply);

        } catch (Exception e) {
            e.printStackTrace();
            appendMessage("Bot: Error communicating with n8n.");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SwingChatbot().setVisible(true));
    }
}
