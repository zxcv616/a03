/**
 * GUI for login with username and password fields.
 *
 * @author Anthony Soto
 * @version 1.0
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginGUI extends JFrame {

    private JTextField userField;
    private JPasswordField passField;
    private JButton loginButton;
    private Login login;

    public LoginGUI() {
        login = new Login();
        setTitle("Login");
        setSize(400, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(Color.WHITE);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.CENTER;

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Username:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        userField = new JTextField();
        userField.setPreferredSize(new Dimension(200, 15));
        panel.add(new JScrollPane(userField), gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Password:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        passField = new JPasswordField();
        passField.setPreferredSize(new Dimension(200, 15));
        panel.add(new JScrollPane(passField), gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        loginButton = new JButton("Login");
        loginButton.setPreferredSize(new Dimension(80, 15));
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                login.setUser(userField.getText());
                login.setPass(new String(passField.getPassword()));
                // For demo, assume password is "password"
                boolean valid = login.checkValidity();
                if (valid) {
                    JOptionPane.showMessageDialog(LoginGUI.this, "Login successful!");
                    new Blackboard();
                    LoginGUI.this.dispose();
                } else {
                    JOptionPane.showMessageDialog(LoginGUI.this, "Invalid credentials!");
                }
            }
        });
        panel.add(loginButton, gbc);

        add(panel);
        setVisible(true);
    }

    public static void main(String[] args) {
        new LoginGUI();
    }
}