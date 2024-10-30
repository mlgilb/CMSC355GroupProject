import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * MentalHealthApp is a Java Swing application that allows users to register, log in,
 * submit their mood ratings, and view their mood history.
 */
public class MentalHealthApp {
    // Main frame and panels
    private JFrame frame;
    private JPanel loginPanel, registerPanel, moodPanel, historyPanel;
    private JTextField usernameField, regUsernameField, moodField;
    private JPasswordField passwordField, regPasswordField;
    private JTextArea moodDescriptionField, moodHistoryArea;
    private JLabel messageLabel, moodMessageLabel;
    private Map<String, String> userDatabase;
    private String loggedInUser;
    private static final String DATA_FILE = "user_data.txt";

    /**
     * Constructor for MentalHealthApp.
     * Initializes the user database and sets up the main UI.
     */
    public MentalHealthApp() {
        userDatabase = new HashMap<>();
        loadUserData(); // Load user info from file
        initUI(); // Set up the main UI
    }

    /**
     * Initializes the main UI of the application.
     * Sets up the main frame and displays the login panel.
     */
    private void initUI() {
        frame = new JFrame("Mental Health App");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);

        showLoginPanel(); // Show login first

        frame.setVisible(true); // Make it show up
    }

    /**
     * Displays the login panel where users can enter their username and password.
     */
    private void showLoginPanel() {
        loginPanel = new JPanel(new GridLayout(4, 2));

        loginPanel.add(new JLabel("Username:"));
        usernameField = new JTextField();
        loginPanel.add(usernameField);

        loginPanel.add(new JLabel("Password:"));
        passwordField = new JPasswordField();
        loginPanel.add(passwordField);

        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(new LoginAction());
        loginPanel.add(loginButton);

        JButton registerButton = new JButton("Register");
        registerButton.addActionListener(e -> showRegisterPanel());
        loginPanel.add(registerButton);

        messageLabel = new JLabel(); // Label for error/success messages
        loginPanel.add(messageLabel);

        frame.setContentPane(loginPanel); // Set login panel as main view
        frame.revalidate();
    }

    /**
     * Displays the registration panel where new users can create an account.
     */
    private void showRegisterPanel() {
        registerPanel = new JPanel(new GridLayout(4, 2));

        registerPanel.add(new JLabel("New Username:"));
        regUsernameField = new JTextField();
        registerPanel.add(regUsernameField);

        registerPanel.add(new JLabel("New Password:"));
        regPasswordField = new JPasswordField();
        registerPanel.add(regPasswordField);

        JButton createAccountButton = new JButton("Create Account");
        createAccountButton.addActionListener(new RegisterAction());
        registerPanel.add(createAccountButton);

        JButton backButton = new JButton("Back to Login");
        backButton.addActionListener(e -> showLoginPanel());
        registerPanel.add(backButton);

        frame.setContentPane(registerPanel); // Switch to register view
        frame.revalidate();
    }

    /**
     * Displays the mood panel where users can submit their mood ratings and descriptions.
     */
    private void showMoodPanel() {
        moodPanel = new JPanel(new GridLayout(5, 2));

        moodPanel.add(new JLabel("How are you feeling today? (1-10):"));
        moodField = new JTextField();
        moodPanel.add(moodField);

        moodPanel.add(new JLabel("Why do you feel this way?"));
        moodDescriptionField = new JTextArea(3, 20);
        moodPanel.add(new JScrollPane(moodDescriptionField));

        JButton submitMoodButton = new JButton("Submit Mood");
        submitMoodButton.addActionListener(new SubmitMoodAction());
        moodPanel.add(submitMoodButton);

        JButton viewHistoryButton = new JButton("View Mood History");
        viewHistoryButton.addActionListener(e -> showHistoryPanel());
        moodPanel.add(viewHistoryButton);

        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> {
            loggedInUser = null;
            showLoginPanel();
        });
        moodPanel.add(logoutButton);

        moodMessageLabel = new JLabel(); // For feedback messages
        moodPanel.add(moodMessageLabel);

        frame.setContentPane(moodPanel); // Show mood panel
        frame.revalidate();
    }

    /**
     * Displays the history panel where users can view their mood history.
     */
    private void showHistoryPanel() {
        historyPanel = new JPanel(new BorderLayout());

        moodHistoryArea = new JTextArea();
        moodHistoryArea.setEditable(false);
        loadMoodHistory(); // Load and display history

        historyPanel.add(new JScrollPane(moodHistoryArea), BorderLayout.CENTER);

        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> showMoodPanel()); // Go back to mood panel
        historyPanel.add(backButton, BorderLayout.SOUTH);

        frame.setContentPane(historyPanel); // Show history panel
        frame.revalidate();
    }

    /**
     * Loads user credentials from a file into the user database.
     */
    private void loadUserData() {
        try (BufferedReader reader = new BufferedReader(new FileReader(DATA_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts.length == 2) {
                    userDatabase.put(parts[0], parts[1]);
                }
            }
        } catch (IOException e) {
            System.out.println("User data file not found. Starting fresh.");
        }
    }

    /**
     * Saves user credentials from the user database to a file.
     */
    private void saveUserData() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(DATA_FILE))) {
            for (Map.Entry<String, String> entry : userDatabase.entrySet()) {
                writer.write(entry.getKey() + ":" + entry.getValue());
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Failed to save user data.");
        }
    }

    /**
     * Loads the mood history for the logged-in user from a file.
     */
    private void loadMoodHistory() {
        moodHistoryArea.setText("");
        String filename = loggedInUser + "_mood.txt";
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                moodHistoryArea.append(line + "\n");
            }
        } catch (IOException e) {
            moodHistoryArea.setText("No mood history found.");
        }
    }

    /**
     * Saves or updates the mood data for the logged-in user.
     * 
     * @param moodRating The mood rating (1-10).
     * @param description The description of the mood.
     */
    private void saveMoodData(int moodRating, String description) {
        String today = java.time.LocalDate.now().toString();
        String filename = loggedInUser + "_mood.txt";

        try {
            StringBuilder history = new StringBuilder();
            boolean moodExistsForToday = false;
            try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.startsWith("Date: " + today)) {
                        moodExistsForToday = true;
                    } else {
                        history.append(line).append("\n");
                    }
                }
            }

            if (moodExistsForToday) {
                int confirm = JOptionPane.showConfirmDialog(frame, "You've already submitted a mood today. Submitting a new mood will overwrite the previous one. Continue?", "Warning", JOptionPane.YES_NO_OPTION);
                if (confirm != JOptionPane.YES_OPTION) {
                    JOptionPane.showMessageDialog(frame, "Mood will not be submitted!");
                    return;
                }
            }

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
                writer.write(history.toString());
                writer.write("Date: " + today + ", Mood: " + moodRating + ", Reason: " + description + "\n");
            }
        } catch (IOException e) {
            System.out.println("Failed to save mood data.");
        }
    }

    /**
     * Action listener for the login button.
     * Handles the login logic.
     */
    private class LoginAction implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());

            if (username.isEmpty() || password.isEmpty()) {
                messageLabel.setText("Fields cannot be blank.");
            } else if (userDatabase.containsKey(username) && userDatabase.get(username).equals(password)) {
                loggedInUser = username;
                JOptionPane.showMessageDialog(frame, "Login successful!");
                showMoodPanel();
            } else {
                messageLabel.setText("Invalid username or password.");
            }
        }
    }

    /**
     * Creates a new mood file for a newly registered user.
     * 
     * @param username The username of the new user.
     */
    private void createUserMoodFile(String username) {
        String moodFileName = username + "_mood.txt";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(moodFileName))) {
            writer.newLine();
        } catch (IOException e) {
            System.out.println("Error creating mood file for " + username + ": " + e.getMessage());
        }
    }

    /**
     * Action listener for the create account button.
     * Handles the registration logic.
     */
    private class RegisterAction implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String newUsername = regUsernameField.getText();
            String newPassword = new String(regPasswordField.getPassword());

            if (newUsername.length() < 5 || newPassword.length() < 5) {
                JOptionPane.showMessageDialog(frame, "Username and password must be at least 5 characters.");
            } else if (userDatabase.containsKey(newUsername)) {
                JOptionPane.showMessageDialog(frame, "Username already taken.");
            } else {
                userDatabase.put(newUsername, newPassword);
                saveUserData();
                JOptionPane.showMessageDialog(frame, "Account created successfully!");
                showLoginPanel();
                createUserMoodFile(newUsername);
            }
        }
    }

    /**
     * Action listener for the submit mood button.
     * Handles the mood submission logic.
     */
    private class SubmitMoodAction implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String moodInput = moodField.getText();
            String moodDescription = moodDescriptionField.getText();
            try {
                int moodRating = Integer.parseInt(moodInput);
                if (moodRating < 1 || moodRating > 10) {
                    moodMessageLabel.setText("Please enter a number between 1 and 10.");
                } else {
                    saveMoodData(moodRating, moodDescription);
                    JOptionPane.showMessageDialog(frame, "Success!");
                }
            } catch (NumberFormatException ex) {
                moodMessageLabel.setText("Please enter a valid number.");
            }
        }
    }

    /**
     * Main method to launch the application.
     * 
     * @param args Command line arguments.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(MentalHealthApp::new);
    }
}