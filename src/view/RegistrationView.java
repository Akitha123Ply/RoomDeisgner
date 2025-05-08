package view;

import controller.AuthController;
import controller.DashboardController;
import util.AppContext;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class RegistrationView extends JPanel {
    private JFrame parentFrame;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JButton registerButton;
    private JButton signInButton;
    private AppContext appContext;

    public RegistrationView(AppContext appContext, JFrame parentFrame) {

        this.parentFrame = parentFrame;
        this.appContext = appContext;

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

        // Create registration form panel
        JPanel registrationFormPanel = createRegistrationFormPanel();
        backgroundPanel.add(registrationFormPanel);

        add(backgroundPanel, BorderLayout.CENTER);
    }

    private JPanel createRegistrationFormPanel() {
        JPanel registrationFormPanel = new JPanel();
        registrationFormPanel.setLayout(new BoxLayout(registrationFormPanel, BoxLayout.Y_AXIS));
        registrationFormPanel.setBackground(Color.WHITE);
        registrationFormPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        registrationFormPanel.setPreferredSize(new Dimension(350, 350));

        // Create Account title
        JLabel titleLabel = new JLabel("Create Account");
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

        // Confirm Password field
        JLabel confirmPasswordLabel = new JLabel("Password");
        confirmPasswordLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        confirmPasswordField = new JPasswordField(20);
        confirmPasswordField.setMaximumSize(new Dimension(300, 30));
        confirmPasswordField.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Register button
        registerButton = new JButton("Create");
        registerButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        registerButton.setBackground(Color.BLACK);
        registerButton.setForeground(Color.WHITE);
        registerButton.setFocusPainted(false);
        registerButton.setBorderPainted(false);
        registerButton.setPreferredSize(new Dimension(128, 50));
        registerButton.setMaximumSize(new Dimension(128, 50));

        // Add action listener to register button
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String email = emailField.getText();
                String password = new String(passwordField.getPassword());
                String confirmPassword = new String(confirmPasswordField.getPassword());

                // Check if passwords match
                if (!password.equals(confirmPassword)) {
                    JOptionPane.showMessageDialog(parentFrame,
                            "Passwords do not match",
                            "Registration Failed",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Validate email and password
                if (email.isEmpty() || password.isEmpty()) {
                    JOptionPane.showMessageDialog(parentFrame,
                            "Email and password cannot be empty",
                            "Registration Failed",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Register user
                if (appContext.getAuthController().register(email, password)) {
                    JOptionPane.showMessageDialog(parentFrame,
                            "Registration successful! Please log in.",
                            "Success",
                            JOptionPane.INFORMATION_MESSAGE);

                    // Navigate back to login
                    parentFrame.getContentPane().removeAll();
                    parentFrame.setContentPane(new LoginView(appContext,parentFrame));
                    parentFrame.revalidate();
                    parentFrame.repaint();
                } else {
                    JOptionPane.showMessageDialog(parentFrame,
                            "Email already exists",
                            "Registration Failed",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Sign in link
        JPanel signInPanel = new JPanel();
        signInPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        signInPanel.setOpaque(false);

        JLabel signInLabel = new JLabel("Already have an Account?");
        signInButton = new JButton("Sign In");
        signInButton.setBorderPainted(false);
        signInButton.setContentAreaFilled(false);
        signInButton.setForeground(Color.BLUE);
        signInButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        signInButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Navigate back to login view
                parentFrame.getContentPane().removeAll();
                parentFrame.setContentPane(new LoginView(appContext,parentFrame));
                parentFrame.revalidate();
                parentFrame.repaint();
            }
        });

        signInPanel.add(signInLabel);
        signInPanel.add(signInButton);

        // Add components to panel with spacing
        registrationFormPanel.add(Box.createVerticalStrut(15));
        registrationFormPanel.add(titleLabel);
        registrationFormPanel.add(Box.createVerticalStrut(25));

        registrationFormPanel.add(emailLabel);
        registrationFormPanel.add(Box.createVerticalStrut(5));
        registrationFormPanel.add(emailField);
        registrationFormPanel.add(Box.createVerticalStrut(15));

        registrationFormPanel.add(passwordLabel);
        registrationFormPanel.add(Box.createVerticalStrut(5));
        registrationFormPanel.add(passwordField);
        registrationFormPanel.add(Box.createVerticalStrut(15));

        registrationFormPanel.add(confirmPasswordLabel);
        registrationFormPanel.add(Box.createVerticalStrut(5));
        registrationFormPanel.add(confirmPasswordField);
        registrationFormPanel.add(Box.createVerticalStrut(25));

        registrationFormPanel.add(registerButton);
        registrationFormPanel.add(Box.createVerticalStrut(20));

        registrationFormPanel.add(signInPanel);

        return registrationFormPanel;
    }
}