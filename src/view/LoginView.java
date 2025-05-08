package view;

import controller.AuthController;
import controller.DashboardController;
import util.AppContext;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginView extends JPanel {
    private AppContext appContext;
    private JFrame parentFrame;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton signUpButton;
    public LoginView(AppContext appContext, JFrame parentFrame) {
        this.appContext = appContext;
        this.parentFrame = parentFrame;

        setLayout(new BorderLayout());

        // Load background image
        ImageIcon backgroundImage = new ImageIcon("src/resources/backgrounds/bg.jpg");

        // Create a custom panel with background image
        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (backgroundImage.getImage() != null) {
                    g.drawImage(backgroundImage.getImage(), 0, 0, getWidth(), getHeight(), this);
                }
            }
        };

        backgroundPanel.setLayout(new GridBagLayout());

        // Create login form panel
        JPanel loginFormPanel = createLoginFormPanel();
        backgroundPanel.add(loginFormPanel);

        add(backgroundPanel, BorderLayout.CENTER);
    }

    private JPanel createLoginFormPanel() {
        JPanel loginFormPanel = new JPanel();
        loginFormPanel.setLayout(new BoxLayout(loginFormPanel, BoxLayout.Y_AXIS));
        loginFormPanel.setBackground(Color.WHITE);
        loginFormPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        loginFormPanel.setPreferredSize(new Dimension(350, 300));

        // Welcome title
        JLabel titleLabel = new JLabel("Welcome Back!");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Email field
        JLabel emailLabel = new JLabel("Email");
        emailLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        emailField = new JTextField(20);
        emailField.setMaximumSize(new Dimension(300, 30));
        emailField.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Password field
        JLabel passwordLabel = new JLabel("Password");
        passwordLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        passwordField = new JPasswordField(20);
        passwordField.setMaximumSize(new Dimension(300, 30));
        passwordField.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Login button
        loginButton = new JButton("Sign In");
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginButton.setBackground(Color.black);
        loginButton.setForeground(Color.white);
        loginButton.setFocusPainted(false);
        loginButton.setBorderPainted(false);
        loginButton.setPreferredSize(new Dimension(128, 50));
        loginButton.setMaximumSize(new Dimension(128, 50));


        // Add action listener to login button
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String email = emailField.getText();
                String password = new String(passwordField.getPassword());

                if (appContext.getAuthController().login(email, password)) {
                    JOptionPane.showMessageDialog(parentFrame, "Login successful!");

                    parentFrame.getContentPane().removeAll();
                    parentFrame.setContentPane(new DashboardView(appContext, parentFrame));
                    parentFrame.revalidate();
                    parentFrame.repaint();
                } else {
                    JOptionPane.showMessageDialog(parentFrame,
                            "Invalid email or password",
                            "Login Failed",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Sign up link
        JPanel signUpPanel = new JPanel();
        signUpPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        signUpPanel.setOpaque(false);

        JLabel signUpLabel = new JLabel("Don't have an Account?");
        signUpButton = new JButton("Sign Up");
        signUpButton.setBorderPainted(false);
        signUpButton.setContentAreaFilled(false);
        signUpButton.setForeground(Color.BLUE);
        signUpButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        signUpButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Navigate to registration view
                parentFrame.getContentPane().removeAll();
                parentFrame.setContentPane(new RegistrationView(appContext, parentFrame));
                parentFrame.revalidate();
                parentFrame.repaint();
            }
        });

        signUpPanel.add(signUpLabel);
        signUpPanel.add(signUpButton);

        // Add components to panel with spacing
        loginFormPanel.add(Box.createVerticalStrut(15));
        loginFormPanel.add(titleLabel);
        loginFormPanel.add(Box.createVerticalStrut(25));

        loginFormPanel.add(emailLabel);
        loginFormPanel.add(Box.createVerticalStrut(5));
        loginFormPanel.add(emailField);
        loginFormPanel.add(Box.createVerticalStrut(15));

        loginFormPanel.add(passwordLabel);
        loginFormPanel.add(Box.createVerticalStrut(5));
        loginFormPanel.add(passwordField);
        loginFormPanel.add(Box.createVerticalStrut(25));

        loginFormPanel.add(loginButton);
        loginFormPanel.add(Box.createVerticalStrut(20));

        loginFormPanel.add(signUpPanel);

        return loginFormPanel;
    }
}