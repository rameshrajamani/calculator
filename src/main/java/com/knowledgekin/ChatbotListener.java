package com.knowledgekin;

import static spark.Spark.*;
import org.json.JSONObject;

public class ChatbotListener {
    public static void main(String[] args) {
        port(5678); // Run on port 5678

        post("/chatbot-listener", (req, res) -> {
            JSONObject json = new JSONObject(req.body());
            String botResponse = json.getString("response");

            // Update the Swing chatbot UI with the response
        //    SwingChatbot.appendBotResponse(botResponse);

            res.status(200);
            return "Response received";
        });

        System.out.println("Chatbot Listener is running on port 5678...");
    }
}