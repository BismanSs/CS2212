import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * This class represents the Login UI for the application, it inherits from JFrame
 * @author Paul Scoropan, Gouri Sikha, Bizman Sawhney, Owen Tjhie
 */
public class LoginUI extends JFrame {

    private String title = "Login"; // window title
    private JTextField usernameText; // text field for username
    private JPasswordField passwordText; // password field for password (password fields obfuscate the text)
    private JButton submitButton; // the submit button

    private JLabel validateText; // label to display whether credentials were valid

    private boolean isLogin = false; // flag for whether the user was successfully logged in

    /**
     * The LoginUI constructor, initializes the window and instance variables
     */
    public LoginUI() {
        setTitle(title); // set the title of the window, called from superclass
        setPreferredSize(new Dimension(450, 280)); // set the preferred window size, called from superclass
        setDefaultCloseOperation(EXIT_ON_CLOSE); // terminate program on window close, called from superclass

        JLabel loginLabel = new JLabel("Login"); // labels for text to add to window
        JLabel usernameLabel = new JLabel("Username: ");
        JLabel passwordLabel = new JLabel("Password: ");

        validateText = new JLabel(""); // initialize the instance variables
        usernameText = new JTextField("");
        passwordText = new JPasswordField("");
        submitButton = new JButton("Login");


        setLayout(null); // set layout of components to null
        loginLabel.setLayout(null);
        usernameLabel.setLayout(null);
        usernameText.setLayout(null);
        passwordLabel.setLayout(null);
        passwordText.setLayout(null);
        submitButton.setLayout(null);
        validateText.setLayout(null);

        loginLabel.setFont(new Font("Monaco", Font.BOLD, 36)); // set fonts of different components appropriately
        usernameLabel.setFont(new Font("Monaco", Font.PLAIN, 24));
        usernameText.setFont(new Font("Monaco", Font.PLAIN, 24));
        passwordLabel.setFont(new Font("Monaco", Font.PLAIN, 24));
        passwordText.setFont(new Font("Monaco", Font.PLAIN, 24));
        submitButton.setFont(new Font("Monaco", Font.PLAIN, 24));
//        validateText.setFont(new Font("Monaco", Font.BOLD, 24));

        loginLabel.setBounds(150, 10, 200, 50); // set locations and sizes of different components appropriately
        usernameLabel.setBounds(15, 60, 200, 40);
        usernameText.setBounds(215, 60, 200, 40);
        passwordLabel.setBounds(15, 110, 200, 40);
        passwordText.setBounds(215, 110, 200, 40);
        submitButton.setBounds(125, 160, 200, 40);
        validateText.setBounds(125,200, 200, 40);

        add(loginLabel); // add the components to the object using add() from the superclass
        add(usernameLabel);
        add(passwordLabel);
        add(usernameText);
        add(passwordText);
        add(submitButton);
        add(validateText);
        pack(); // pack the window
        setLocationRelativeTo(null); // set window location to center of the screen
//        validateText.setVisible(false);

        // add action listeners
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) { // called on submit button pressed
                login(); // call the login method
            }
        });

        setVisible(true); // display the login window
        getRootPane().setDefaultButton(submitButton); // when enter is pressed the submit button will be pressed
    }

    /**
     * This method handles the login procedure for a user
     */
    private void login() {
        validateText.setVisible(true); // show validation text
        if (validate(usernameText.getText(), passwordText.getPassword())) { // check if a valid username and password combo
            validateText.setForeground(Color.green); // set text color to green, indicating success
            validateText.setText("Login Successful"); // set text as login successful
            isLogin = true; // set login flag to true
        } else {
            validateText.setForeground(Color.red); // set the text to red, indicating unsuccessful login
            validateText.setText("Invalid Credentials"); // set text to invalid credentials
        }
    }

    /**
     * This method gets the value of the isLogin flag
     * @return the isLogin boolean flag
     */
    public boolean isLogin() {
        return isLogin;
    }

    /**
     * This method handles the validation of user credentials
     * @param username the username entered by the user
     * @param password the password entered by the user
     * @return true if valid credentials and false if not
     */
    private boolean validate(String username, char[] password) {
        BufferedReader reader; // declare a buffered reader
        try {
            reader = new BufferedReader(new FileReader("userDB.txt")); // try to read user database (local text file)
            String line = reader.readLine(); // read the first line
            while (line != null) { // continue until no more next line
                if (line.split(",")[0].equals(username) && line.split(",")[1].equals(new String(password))) { // check if any lines equal the given credentials, after formatting
                    return true;
                }
                line = reader.readLine(); // read next line
            }
        } catch (IOException e) { // catch IOException from trying to read the file
            validateText.setForeground(Color.red); // set the text to red, indicating an error
            validateText.setText("Can not read user database file"); // set the validate text to alert that the user database file couldn't be read
        }
        return false;
    }

}
