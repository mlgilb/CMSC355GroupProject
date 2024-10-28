import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class MentalHealthApp {
    // main frame and panels
    private JFrame frame;
    private JPanel loginPanel, registerPanel, moodPanel, historyPanel;
    private JTextField usernameField, regUsernameField, moodField;
    private JPasswordField passwordField, regPasswordField;
    private JTextArea moodDescriptionField, moodHistoryArea;
    private JLabel messageLabel, moodMessageLabel;
    private Map<String, String> userDatabase;
    private String loggedInUser;
    private static final String DATA_FILE = "user_data.txt";

    public MentalHealthApp() {
        userDatabase = new HashMap<>();
        loadUserData(); // load user info from file
        initUI(); // set up the main UI
    }

    private void initUI() {
        // setting up the main app window
        frame = new JFrame("Mental Health App");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);

        showLoginPanel(); // show login first

        frame.setVisible(true); // make it show up
    }

    private void showLoginPanel() {
        // creating login screen
        loginPanel = new JPanel();
        loginPanel.setLayout(new GridLayout(4, 2));

        // username input
        loginPanel.add(new JLabel("Username:"));
        usernameField = new JTextField();
        loginPanel.add(usernameField);

        // password input
        loginPanel.add(new JLabel("Password:"));
        passwordField = new JPasswordField();
        loginPanel.add(passwordField);

        // login button
        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(new LoginAction());
        loginPanel.add(loginButton);

        // register button
        JButton registerButton = new JButton("Register");
        registerButton.addActionListener(e -> showRegisterPanel());
        loginPanel.add(registerButton);

        messageLabel = new JLabel(); // label for error/success messages
        loginPanel.add(messageLabel);

        frame.setContentPane(loginPanel); // set login panel as main view
        frame.revalidate();
    }

    private void showRegisterPanel() {
        // registration panel setup
        registerPanel = new JPanel();
        registerPanel.setLayout(new GridLayout(4, 2));

        // username for registration
        registerPanel.add(new JLabel("New Username:"));
        regUsernameField = new JTextField();
        registerPanel.add(regUsernameField);

        // password for registration
        registerPanel.add(new JLabel("New Password:"));
        regPasswordField = new JPasswordField();
        registerPanel.add(regPasswordField);

        // create account button
        JButton createAccountButton = new JButton("Create Account");
        createAccountButton.addActionListener(new RegisterAction());
        registerPanel.add(createAccountButton);

        // back button to return to login
        JButton backButton = new JButton("Back to Login");
        backButton.addActionListener(e -> showLoginPanel());
        registerPanel.add(backButton);

        frame.setContentPane(registerPanel); // switch to register view
        frame.revalidate();
    }

    private void showMoodPanel() {
        // mood input panel setup
        moodPanel = new JPanel();
        moodPanel.setLayout(new GridLayout(5, 2));

        // mood rating input
        moodPanel.add(new JLabel("How are you feeling today? (1-10):"));
        moodField = new JTextField();
        moodPanel.add(moodField);

        // mood description input
        moodPanel.add(new JLabel("Why do you feel this way?"));
        moodDescriptionField = new JTextArea(3, 20);
        moodPanel.add(new JScrollPane(moodDescriptionField));

        // submit mood button
        JButton submitMoodButton = new JButton("Submit Mood");
        submitMoodButton.addActionListener(new SubmitMoodAction());
        moodPanel.add(submitMoodButton);

        // view mood history button
        JButton viewHistoryButton = new JButton("View Mood History");
        viewHistoryButton.addActionListener(e -> showHistoryPanel());
        moodPanel.add(viewHistoryButton);

        // make logout button
        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> {
            loggedInUser = null;
            showLoginPanel();
        });
        moodPanel.add(logoutButton);

        moodMessageLabel = new JLabel(); // for feedback messages
        moodPanel.add(moodMessageLabel);

        frame.setContentPane(moodPanel); // show mood panel
        frame.revalidate();
    }

    private void showHistoryPanel() {
        // history view panel setup
        historyPanel = new JPanel();
        historyPanel.setLayout(new BorderLayout());

        moodHistoryArea = new JTextArea();
        moodHistoryArea.setEditable(false);
        loadMoodHistory(); // load and display history

        historyPanel.add(new JScrollPane(moodHistoryArea), BorderLayout.CENTER);

        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> showMoodPanel()); // go back to mood panel
        historyPanel.add(backButton, BorderLayout.SOUTH);

        frame.setContentPane(historyPanel); // show history panel
        frame.revalidate();
    }

    private void loadUserData() {
        // load user credentials from file
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

    private void saveUserData() {
        // save user data to file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(DATA_FILE))) {
            for (Map.Entry<String, String> entry : userDatabase.entrySet()) {
                writer.write(entry.getKey() + ":" + entry.getValue());
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Failed to save user data.");
        }
    }

    private void loadMoodHistory() {
        // load mood history for the logged-in user
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

    private void saveMoodData(int moodRating, String description) {
        // save or update today's mood data
        String today = java.time.LocalDate.now().toString();
        String filename = loggedInUser + "_mood.txt";

        try {
            // check if mood for today already exists
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

            // ask user if they want to overwrite today's mood
            if (moodExistsForToday) {
                int confirm = JOptionPane.showConfirmDialog(frame, "You've already submitted a mood today. Submitting a new mood will overwrite the previous one. Continue?", "Warning", JOptionPane.YES_NO_OPTION);
                if (confirm != JOptionPane.YES_OPTION) {
                    return;
                }
            }

            // save updated mood data
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
                writer.write(history.toString());
                writer.write("Date: " + today + ", Mood: " + moodRating + ", Reason: " + description + "\n");
            }
        } catch (IOException e) {
            System.out.println("Failed to save mood data.");
        }
    }

    // Action listener classes
    private class LoginAction implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            // login logic
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
	// creates new txt file when new user is registered
	private void createUserMoodFile(String username) {
    String moodFileName = username + "_mood.txt";
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(moodFileName))) {
			writer.newLine();
		} catch (IOException e) {
			System.out.println("Error creating mood file for " + username + ": " + e.getMessage());
		}
	}

	

    private class RegisterAction implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            // registration logic
            String newUsername = regUsernameField.getText();
            String newPassword = new String(regPasswordField.getPassword());

            if (newUsername.length() < 5 || newPassword.length() < 5 ) {
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

    private class SubmitMoodAction implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            // mood submission logic
            String moodInput = moodField.getText();
            String moodDescription = moodDescriptionField.getText();
            try {
                int moodRating = Integer.parseInt(moodInput);
                if (moodRating < 1 || moodRating > 10) {
                    moodMessageLabel.setText("Please enter a number between 1 and 10.");
                } else {
                    saveMoodData(moodRating, moodDescription);
                    JOptionPane.showMessageDialog(frame, "Mood submitted!");
                    
                }
            } catch (NumberFormatException ex) {
                moodMessageLabel.setText("Please enter a valid number.");
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MentalHealthApp::new);
    }
}
