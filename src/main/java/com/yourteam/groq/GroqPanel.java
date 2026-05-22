package com.yourteam.groq;

import com.yourteam.Blackboard;
import com.yourteam.Project;
import com.yourteam.Sprints;
import com.yourteam.Stories;
import org.json.JSONObject;

import javax.swing.*;
import javax.swing.text.*;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Chat-style AI assistant panel powered by Groq.
 * Maintains a full conversation history and automatically injects the
 * current Blackboard state as context so the AI can see all loaded
 * projects, stories, and sprints without the user having to describe them.
 * The Groq API key is requested on first use and cached for the session.
 *
 * @author Matthew Wiecking
 * @version 1.0
 */
public class GroqPanel extends JPanel {

    private GroqClient groqClient;
    private final List<JSONObject> conversationHistory = new ArrayList<>();
    private String focusedItemContext = "";

    private final JTextPane chatDisplay;
    private final JScrollPane chatScrollPane;
    private final StyledDocument doc;
    private final JTextField inputField;
    private final JButton sendBtn;
    private final JLabel statusLabel;

    private Style userLabelStyle;
    private Style userTextStyle;
    private Style aiLabelStyle;
    private Style aiTextStyle;

    /**
     * Constructs the chat panel with a scrollable conversation display and input row.
     */
    public GroqPanel() {
        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createTitledBorder("AI Assistant (Groq)"));

        chatDisplay = new JTextPane();
        chatDisplay.setEditable(false);
        chatDisplay.setBackground(new Color(250, 250, 250));
        chatDisplay.setCaret(new DefaultCaret() {
            @Override public void paint(Graphics g) {}
        });
        chatDisplay.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        doc = chatDisplay.getStyledDocument();
        initStyles();
        chatScrollPane = new JScrollPane(chatDisplay);

        inputField = new JTextField();
        inputField.setToolTipText("Ask Groq anything about your project...");
        inputField.addActionListener(e -> handleSend());

        sendBtn = new JButton("Send");
        sendBtn.addActionListener(e -> handleSend());

        JPanel inputRow = new JPanel(new BorderLayout(5, 0));
        inputRow.add(inputField, BorderLayout.CENTER);
        inputRow.add(sendBtn, BorderLayout.EAST);

        statusLabel = new JLabel(" ");
        statusLabel.setForeground(Color.GRAY);
        statusLabel.setFont(statusLabel.getFont().deriveFont(Font.ITALIC, 11f));

        JButton apiKeyBtn = new JButton("Set API Key");
        apiKeyBtn.addActionListener(e -> promptForApiKey());
        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 2, 2));
        topBar.add(apiKeyBtn);

        JPanel bottomPanel = new JPanel(new BorderLayout(0, 3));
        bottomPanel.add(statusLabel, BorderLayout.NORTH);
        bottomPanel.add(inputRow, BorderLayout.SOUTH);

        add(topBar, BorderLayout.NORTH);
        add(chatScrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    /**
     * Pre-loads a specific story as focused context so the AI immediately knows which item
     * is being worked on. Also shows a note in the chat.
     *
     * @param story the story to focus on
     */
    public void setStory(Stories story) {
        focusedItemContext = "Title: " + story.getSubjectLine()
            + "\nDescription: " + story.getDescription()
            + "\nValue: " + story.getValue()
            + "\nAssigned To: " + story.getAssignedUser();
        appendSystemNote("Context loaded: \"" + story.getSubjectLine() + "\" — ask me anything about it.");
    }

    /**
     * Sets arbitrary focused context text (used by live form listeners during creation).
     *
     * @param text the context text to inject
     */
    public void setStoryText(String text) {
        focusedItemContext = text;
    }

    private void initStyles() {
        userLabelStyle = chatDisplay.addStyle("userLabel", null);
        StyleConstants.setBold(userLabelStyle, true);
        StyleConstants.setForeground(userLabelStyle, new Color(30, 100, 200));

        userTextStyle = chatDisplay.addStyle("userText", null);
        StyleConstants.setForeground(userTextStyle, new Color(40, 40, 40));

        aiLabelStyle = chatDisplay.addStyle("aiLabel", null);
        StyleConstants.setBold(aiLabelStyle, true);
        StyleConstants.setForeground(aiLabelStyle, new Color(20, 140, 70));

        aiTextStyle = chatDisplay.addStyle("aiText", null);
        StyleConstants.setForeground(aiTextStyle, Color.BLACK);

        Style noteStyle = chatDisplay.addStyle("note", null);
        StyleConstants.setItalic(noteStyle, true);
        StyleConstants.setForeground(noteStyle, Color.GRAY);
    }

    private void scrollToBottom() {
        SwingUtilities.invokeLater(() ->
            chatScrollPane.getVerticalScrollBar().setValue(
                chatScrollPane.getVerticalScrollBar().getMaximum()));
    }

    private void appendSystemNote(String note) {
        try {
            doc.insertString(doc.getLength(), note + "\n\n", doc.getStyle("note"));
            scrollToBottom();
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    private void appendMessage(String sender, String message, Style labelStyle, Style textStyle) {
        try {
            doc.insertString(doc.getLength(), sender + ": ", labelStyle);
            doc.insertString(doc.getLength(), message + "\n\n", textStyle);
            scrollToBottom();
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    private void promptForApiKey() {
        String key = JOptionPane.showInputDialog(
            this, "Enter your Groq API key:", "Groq API Key", JOptionPane.PLAIN_MESSAGE);
        if (key == null || key.trim().isEmpty()) return;
        groqClient = new GroqClient(key.trim());
        appendSystemNote("API key updated.");
    }

    private String buildSystemContext() {
        StringBuilder sb = new StringBuilder();
        sb.append("You are an AI assistant embedded in a project management desktop application. ")
          .append("Help the user with software engineering tasks such as reviewing user stories, ")
          .append("generating tasks, improving acceptance criteria, and general project advice.\n\n");

        List<Project> projects = Blackboard.getInstance().getProjects();
        if (!projects.isEmpty()) {
            sb.append("Current application state — projects loaded in the app:\n");
            for (Project p : projects) {
                sb.append("Project: ").append(p.getName())
                  .append(" (ID: ").append(p.getId()).append(")\n");
                for (Stories s : p.getStory()) {
                    sb.append("  Story: ").append(s.getSubjectLine())
                      .append(" | Value: ").append(s.getValue())
                      .append(" | Assigned: ").append(s.getAssignedUser()).append("\n");
                }
                for (Sprints sp : p.getSprint()) {
                    sb.append("  Sprint: ").append(sp.getName())
                      .append(" | ").append(sp.getDescription()).append("\n");
                }
            }
            sb.append("\n");
        }

        if (!focusedItemContext.isEmpty()) {
            sb.append("Currently focused item:\n").append(focusedItemContext).append("\n");
        }

        return sb.toString();
    }

    private void handleSend() {
        if (groqClient == null) {
            promptForApiKey();
            if (groqClient == null) return;
        }

        String userText = inputField.getText().trim();
        if (userText.isEmpty()) return;
        inputField.setText("");

        appendMessage("You", userText, userLabelStyle, userTextStyle);

        JSONObject userMsg = new JSONObject();
        userMsg.put("role", "user");
        userMsg.put("content", userText);
        conversationHistory.add(userMsg);

        inputField.setEnabled(false);
        sendBtn.setEnabled(false);
        statusLabel.setText("Groq is thinking...");

        String systemContext = buildSystemContext();

        SwingWorker<String, Void> worker = new SwingWorker<>() {
            @Override
            protected String doInBackground() throws Exception {
                return groqClient.chat(systemContext, conversationHistory);
            }

            @Override
            protected void done() {
                inputField.setEnabled(true);
                sendBtn.setEnabled(true);
                statusLabel.setText(" ");
                try {
                    String response = get();
                    appendMessage("Groq", response, aiLabelStyle, aiTextStyle);

                    JSONObject aiMsg = new JSONObject();
                    aiMsg.put("role", "assistant");
                    aiMsg.put("content", response);
                    conversationHistory.add(aiMsg);
                } catch (Exception ex) {
                    appendMessage("Error", ex.getMessage(), aiLabelStyle, aiTextStyle);
                }
            }
        };
        worker.execute();
    }
}
