package com.knowledgekin;

import javax.swing.*;

import org.apache.hc.client5.http.fluent.Content;
import org.apache.hc.client5.http.fluent.Request;
import org.apache.hc.core5.http.ContentType;
import org.json.JSONObject;

import java.awt.*;
import java.awt.event.*;
import java.util.Arrays;

public class Calculator extends JFrame {
    private final JTextField display;

    private static final String N8N_WEBHOOK_URL = "https://apt-formerly-lemming.ngrok-free.app/webhook/9cb6f5a1-a4db-45f1-ab99-db19a9f6e130";

    public Calculator() {
        setTitle("Scientific Calculator");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // Retro-style display
        display = new JTextField();
        display.setFont(new Font("Courier New", Font.BOLD, 28));
        display.setEditable(false);
        display.setBackground(Color.BLACK);
        display.setForeground(Color.GREEN);
        display.setHorizontalAlignment(SwingConstants.RIGHT);
        display.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(display, BorderLayout.NORTH);

        // All buttons including scientific operations
        JPanel keyPanel = new JPanel(new GridLayout(8, 9, 1, 7  )); // 7x9 grid
        String[] keys = {
            "1", "2", "3", "4", "5", "6", "7",  // Row 1
            "8", "9", "*", "/", "√", "x²", "π",  // Row 2
            "(", ")", "mod", "log", "ln", "e", "ANS",  // Row 3
            "0", ".", "=", "+", "-", "sin", "cos",  // Row 4
            "tan", "asin", "acos", "atan", "abs", "exp", "1/x",  // Row 5
            "x^y", "d/dx", "∫", "DEG", "RAD", "A", "B",  // Row 6
            "C", "D", "E", "F", "G", "H", "I",  // Row 7
            "J", "K", "L", "M", "N", "O", "P",  // Row 8
            "Q", "R", "S", "T", "U", "V", "W",  // Row 9
            "X", "Y", "Z", "CLR", "BKSP","SPACE", "MEAN", "MEDIAN", "x³" // Added x³
        };

        for (String key : keys) {
            JButton button = new JButton(key);
            button.setFont(new Font("Arial", Font.PLAIN, 18));
            button.addActionListener(e -> handleInput(key));
            keyPanel.add(button);
        }

        add(keyPanel, BorderLayout.CENTER);

        // Key listener for physical keyboard input
        display.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char keyChar = e.getKeyChar();
                if (Character.isLetterOrDigit(keyChar) || "+-*/=().,".indexOf(keyChar) >= 0) {
                    display.setText(display.getText() + keyChar);
                } else if (keyChar == '\b') { // Backspace
                    deleteLastChar();
                }
            }
        });

        // Focus on display to catch keystrokes
        SwingUtilities.invokeLater(() -> display.requestFocusInWindow());

        setVisible(true);
    }

    private void handleInput(String key) {
        switch (key) {
            case "SPACE":
                // Add a space to the display
                display.setText(display.getText() + " ");
                break;
            case "=" : 
                evaluateExpression();
                break;
            case "CLR" : 
                display.setText("");
                break;
            case "BKSP" : 
                deleteLastChar();
                break;
            case "MEAN" : 
                calculateMean();
                break;
            case "MEDIAN" : 
                calculateMedian();
                break;
            case "sin" : 
                calculateTrig(Math::sin);
                break;
            case "cos" : 
                calculateTrig(Math::cos);
                break;
            case "tan" : 
                calculateTrig(Math::tan);
                break;
            case "asin" : 
                calculateTrigInverse(Math::asin);
                break;
            case "acos" : 
                calculateTrigInverse(Math::acos);
                break;
            case "atan" : 
                calculateTrigInverse(Math::atan);
                break;
            case "log" : 
                calculateLog();
                break;
            case "√" : 
                calculateSqrt();
                break;
            case "1/x" : 
                calculateInverse();
                break;
            case "exp" : 
                calculateExp();
                break;
            case "ln" : 
                calculateLn();
                break;
            case "abs" : 
                calculateAbs();
                break;
            case "x²" : 
                calculateSquare();
                break;
            case "x³" : // Added x³ operation
                calculateCube();
                break;
            case "x^y" : 
                calculatePower();
                break;
            case "pi" : 
                display.setText(String.valueOf(Math.PI));
                break;
            case "e" : 
                display.setText(String.valueOf(Math.E));
                break;
            case "mod" : 
                handleModulo();
                break;
            case "d/dx" : 
                handleDerivative();
                break;
            case "∫" : 
                handleIntegral();
                break;
            default : 
                display.setText(display.getText() + key);
        }
    }

    private void evaluateExpression() {
        try {
            String input = display.getText();
            if (isTextOrSentence(input)) {
                // If the input is text or a sentence, send it to n8n
                sendMessageToN8n(input);
            } else {
                // Otherwise, evaluate it as a mathematical expression
                javax.script.ScriptEngine engine = new javax.script.ScriptEngineManager().getEngineByName("JavaScript");
                Object result = engine.eval(input);
                display.setText(result.toString());
            }
        } catch (Exception e) {
            display.setText("Error");
        }
    }

    private void deleteLastChar() {
        String text = display.getText();
        if (!text.isEmpty()) {
            display.setText(text.substring(0, text.length() - 1));
        }
    }

    private void calculateCube() {
        try {
            double input = Double.parseDouble(display.getText());
            double result = Math.pow(input, 3);
            display.setText(String.valueOf(result));
        } catch (Exception e) {
            display.setText("Cube Error");
        }
    }

    private void calculateMean() {
        try {
            String[] parts = display.getText().split(",");
            double sum = 0;
            for (String p : parts) {
                sum += Double.parseDouble(p.trim());
            }
            double mean = sum / parts.length;
            display.setText(String.valueOf(mean));
        } catch (Exception e) {
            display.setText("Mean Error");
        }
    }

    private void calculatePower() {
        try {
            String[] parts = display.getText().split(",");
            double base = Double.parseDouble(parts[0].trim());
            double exponent = Double.parseDouble(parts[1].trim());
            double result = Math.pow(base, exponent);
            display.setText(String.valueOf(result));
        } catch (Exception e) {
            display.setText("Power Error");
        }
    }

    private void calculateMedian() {
        try {
            String[] parts = display.getText().split(",");
            double[] nums = new double[parts.length];
            for (int i = 0; i < parts.length; i++) {
                nums[i] = Double.parseDouble(parts[i].trim());
            }
            Arrays.sort(nums);
            double median;
            int n = nums.length;
            if (n % 2 == 0) {
                median = (nums[n / 2 - 1] + nums[n / 2]) / 2.0;
            } else {
                median = nums[n / 2];
            }
            display.setText(String.valueOf(median));
        } catch (Exception e) {
            display.setText("Median Error");
        }
    }

    private void calculateTrig(java.util.function.DoubleUnaryOperator trigFunction) {
        try {
            double input = Double.parseDouble(display.getText());
            double result = trigFunction.applyAsDouble(Math.toRadians(input)); // Convert to radians
            display.setText(String.valueOf(result));
        } catch (Exception e) {
            display.setText("Trig Error");
        }
    }

    private void calculateTrigInverse(java.util.function.DoubleUnaryOperator trigFunction) {
        try {
            double input = Double.parseDouble(display.getText());
            double result = Math.toDegrees(trigFunction.applyAsDouble(input)); // Convert result to degrees
            display.setText(String.valueOf(result));
        } catch (Exception e) {
            display.setText("Inverse Trig Error");
        }
    }

    private void calculateLog() {
        try {
            double input = Double.parseDouble(display.getText());
            double result = Math.log10(input);
            display.setText(String.valueOf(result));
        } catch (Exception e) {
            display.setText("Log Error");
        }
    }

    private void calculateSqrt() {
        try {
            double input = Double.parseDouble(display.getText());
            double result = Math.sqrt(input);
            display.setText(String.valueOf(result));
        } catch (Exception e) {
            display.setText("Sqrt Error");
        }
    }

    private void calculateSquare() {
        try {
            double input = Double.parseDouble(display.getText());
            double result = Math.pow(input, 2);
            display.setText(String.valueOf(result));
        } catch (Exception e) {
            display.setText("Square Error");
        }
    }

    private void calculateInverse() {
        try {
            double input = Double.parseDouble(display.getText());
            double result = 1 / input;
            display.setText(String.valueOf(result));
        } catch (Exception e) {
            display.setText("Inverse Error");
        }
    }

    private void calculateExp() {
        try {
            double input = Double.parseDouble(display.getText());
            double result = Math.exp(input);
            display.setText(String.valueOf(result));
        } catch (Exception e) {
            display.setText("Exp Error");
        }
    }

    private void calculateLn() {
        try {
            double input = Double.parseDouble(display.getText());
            double result = Math.log(input);
            display.setText(String.valueOf(result));
        } catch (Exception e) {
            display.setText("Ln Error");
        }
    }

    private void calculateAbs() {
        try {
            double input = Double.parseDouble(display.getText());
            double result = Math.abs(input);
            display.setText(String.valueOf(result));
        } catch (Exception e) {
            display.setText("Abs Error");
        }
    }

    private void handleModulo() {
        try {
            String[] parts = display.getText().split("%");
            if (parts.length == 2) {
                double a = Double.parseDouble(parts[0].trim());
                double b = Double.parseDouble(parts[1].trim());
                double result = a % b;
                display.setText(String.valueOf(result));
            } else {
                display.setText("Invalid Modulo Format");
            }
        } catch (Exception e) {
            display.setText("Error");
        }
    }
    

    private void handleDerivative() {
        try {
            String input = display.getText().trim().replace(" ", "");
    
            if (input.matches("[-+]?\\d*x\\^\\d+")) {
                // Match and extract a and n from ax^n
                String[] parts = input.split("x\\^");
                double a = parts[0].isEmpty() || parts[0].equals("+") ? 1 : 
                           parts[0].equals("-") ? -1 : Double.parseDouble(parts[0]);
                int n = Integer.parseInt(parts[1]);
    
                double newCoeff = a * n;
                int newPower = n - 1;
    
                String result;
                if (newPower == 0) {
                    result = String.valueOf(newCoeff);
                } else if (newPower == 1) {
                    result = newCoeff + "x";
                } else {
                    result = newCoeff + "x^" + newPower;
                }
    
                display.setText(result);
            } else {
                display.setText("Format: ax^n");
            }
        } catch (Exception e) {
            display.setText("Error");
        }
    }
    

    private void handleIntegral() {
        try {
            String input = display.getText().trim().replace(" ", "");
    
            if (input.matches("[-+]?\\d*x\\^\\d+")) {
                String[] parts = input.split("x\\^");
                double a = parts[0].isEmpty() || parts[0].equals("+") ? 1 : 
                           parts[0].equals("-") ? -1 : Double.parseDouble(parts[0]);
                int n = Integer.parseInt(parts[1]);
    
                int newPower = n + 1;
                double newCoeff = a / newPower;
    
                String result;
                if (newCoeff == 1.0) {
                    result = "x^" + newPower + " + C";
                } else {
                    result = newCoeff + "x^" + newPower + " + C";
                }
    
                display.setText(result);
            } else {
                display.setText("Format: ax^n");
            }
        } catch (Exception e) {
            display.setText("Error");
        }
    }
    

    private void appendMessage(String message) {
        SwingUtilities.invokeLater(() -> display.setText(message + "\n"));
    }

    // Helper method to check if the input is text or a sentence
    private boolean isTextOrSentence(String input) {
    // Check if the input contains letters or spaces (indicating text or a sentence)
    return input.matches(".*[a-zA-Z ].*");
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
        SwingUtilities.invokeLater(Calculator::new);
    }
}