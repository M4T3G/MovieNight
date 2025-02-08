package veritabaniproje;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import java.util.List;

import Type.LoginClass;
import Type.DbHelper;
import Type.Kullanici;

public class AccountManager extends JFrame {
    private JPanel loginPanel, registerPanel;
    private CardLayout cardLayout;
    private DbHelper dbHelper;
    private LoginClass loginClass;
    
    public AccountManager() {
        setTitle("Account Manager");
        setSize(400, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        cardLayout = new CardLayout();
        setLayout(cardLayout);

        dbHelper = new DbHelper("jdbc:postgresql://localhost:5432/proje_DB","postgres","1234");
        loginClass = new LoginClass();

        createLoginPanel();
        createRegisterPanel();

        add(loginPanel, "login");
        add(registerPanel, "register");

        cardLayout.show(getContentPane(), "login");
    }

    private void createLoginPanel() {
        loginPanel = new JPanel();
        loginPanel.setLayout(null);
        loginPanel.setBackground(new Color(245, 245, 245));

        JLabel titleLabel = new JLabel("Giriş Yapma");
        titleLabel.setFont(new Font("Courier New", Font.BOLD, 24));
        titleLabel.setBounds(50, 50, 300, 30);

        JTextField usernameField = new JTextField("Kullanıcı Adı");
        usernameField.setBounds(50, 120, 300, 40);
        usernameField.setBorder(BorderFactory.createCompoundBorder(
            usernameField.getBorder(),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        usernameField.setForeground(Color.GRAY);

        JPasswordField passwordField = new JPasswordField("Şifre");
        passwordField.setBounds(50, 190, 300, 40);
        passwordField.setBorder(BorderFactory.createCompoundBorder(
            passwordField.getBorder(),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)));

        // Placeholder metni kaybolması için FocusListener
        usernameField.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (usernameField.getText().equals("Kullanıcı Adı")) {
                    usernameField.setText("");
                    usernameField.setForeground(Color.BLACK);
                }
            }
            public void focusLost(FocusEvent e) {
                if (usernameField.getText().isEmpty()) {
                    usernameField.setText("Kullanıcı Adı");
                    usernameField.setForeground(Color.GRAY);
                }
            }
        });

        passwordField.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (String.valueOf(passwordField.getPassword()).equals("Şifre")) {
                    passwordField.setText("");
                    passwordField.setForeground(Color.BLACK);
                }
            }
            public void focusLost(FocusEvent e) {
                if (String.valueOf(passwordField.getPassword()).isEmpty()) {
                    passwordField.setText("Şifre");
                    passwordField.setForeground(Color.GRAY);
                }
            }
        });

        JButton loginButton = new JButton("Giriş Yap");
        loginButton.setBounds(50, 260, 300, 40);
        loginButton.setBackground(new Color(0, 120, 215));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFont(new Font("Courier New", Font.BOLD, 14));

        JLabel loginIcon = new JLabel("🔑");
        loginIcon.setFont(new Font("Material Icons", Font.PLAIN, 18));
        loginIcon.setBounds(20, 260, 30, 40);
        loginPanel.add(loginIcon);

        JButton signUpButton = new JButton("Hesap Oluştur");
        signUpButton.setBounds(50, 320, 300, 40);
        signUpButton.setBackground(new Color(220, 220, 220));
        signUpButton.setFont(new Font("Courier New", Font.PLAIN, 14));

        JLabel signUpIcon = new JLabel("➕");
        signUpIcon.setFont(new Font("Material Icons", Font.PLAIN, 18));
        signUpIcon.setBounds(20, 320, 30, 40);
        loginPanel.add(signUpIcon);

        loginPanel.add(titleLabel);
        loginPanel.add(usernameField);
        loginPanel.add(passwordField);
        loginPanel.add(loginButton);
        loginPanel.add(signUpButton);

        signUpButton.addActionListener(e -> cardLayout.show(getContentPane(), "register"));

        loginButton.addActionListener(e -> {
            String username = usernameField.getText();
            String password = String.valueOf(passwordField.getPassword());
            
            try {
                if (loginClass.authenticateUser(username, password, dbHelper)) {
                	List<Object> userInfo = loginClass.userInformation(username, password, dbHelper);
                	Kullanici kullanici = new Kullanici(userInfo.get(0).toString(),userInfo.get(1).toString(),userInfo.get(2).toString(),(Integer)userInfo.get(3),userInfo.get(4).toString());
                    // Giriş başarılı - AnaEkran'ı açalım
                    AnaEkran anaEkran = new AnaEkran(kullanici,dbHelper);
                    anaEkran.setVisible(true);
                    dispose(); // Mevcut pencereyi kapatalım
                } else {
                    JOptionPane.showMessageDialog(this,
                        "Kullanıcı adı veya şifre hatalı!",
                        "Giriş Hatası",
                        JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this,
                    "Veritabanı hatası: " + ex.getMessage(),
                    "Hata",
                    JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private void createRegisterPanel() {
        registerPanel = new JPanel();
        registerPanel.setLayout(null);
        registerPanel.setBackground(new Color(245, 245, 245));

        JLabel titleLabel = new JLabel("Hesap Oluştur");
        titleLabel.setFont(new Font("Courier New", Font.BOLD, 24));
        titleLabel.setBounds(50, 50, 300, 30);

        JTextField usernameField = new JTextField("Kullanıcı Adı");
        usernameField.setBounds(50, 120, 300, 40);
        usernameField.setBorder(BorderFactory.createCompoundBorder(
            usernameField.getBorder(),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        usernameField.setForeground(Color.GRAY);

        JPasswordField passwordField = new JPasswordField("Şifre");
        passwordField.setBounds(50, 190, 300, 40);
        passwordField.setBorder(BorderFactory.createCompoundBorder(
            passwordField.getBorder(),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)));

        // İsim ve Soyisim TextField
        JTextField firstNameField = new JTextField("Ad");
        firstNameField.setBounds(50, 260, 140, 40);
        firstNameField.setBorder(BorderFactory.createCompoundBorder(
            firstNameField.getBorder(),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        firstNameField.setForeground(Color.GRAY);

        JTextField lastNameField = new JTextField("Soyad");
        lastNameField.setBounds(210, 260, 140, 40);
        lastNameField.setBorder(BorderFactory.createCompoundBorder(
            lastNameField.getBorder(),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        lastNameField.setForeground(Color.GRAY);

        // Yaş TextField
        JTextField ageField = new JTextField("Yaş");
        ageField.setBounds(50, 320, 140, 40);
        ageField.setBorder(BorderFactory.createCompoundBorder(
            ageField.getBorder(),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        ageField.setForeground(Color.GRAY);

        // Yaş için uyarı mesajı
        JLabel ageHintLabel = new JLabel("1 ile 110 arasında bir değer giriniz");
        ageHintLabel.setFont(new Font("Courier New", Font.PLAIN, 12));
        ageHintLabel.setBounds(50, 360, 300, 20);
        ageHintLabel.setForeground(Color.GRAY);

        // Placeholder metni kaybolması için FocusListener
        usernameField.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (usernameField.getText().equals("Kullanıcı Adı")) {
                    usernameField.setText("");
                    usernameField.setForeground(Color.BLACK);
                }
            }
            public void focusLost(FocusEvent e) {
                if (usernameField.getText().isEmpty()) {
                    usernameField.setText("Kullanıcı Adı");
                    usernameField.setForeground(Color.GRAY);
                }
            }
        });

        passwordField.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (String.valueOf(passwordField.getPassword()).equals("Şifre")) {
                    passwordField.setText("");
                    passwordField.setForeground(Color.BLACK);
                }
            }
            public void focusLost(FocusEvent e) {
                if (String.valueOf(passwordField.getPassword()).isEmpty()) {
                    passwordField.setText("Şifre");
                    passwordField.setForeground(Color.GRAY);
                }
            }
        });

        firstNameField.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (firstNameField.getText().equals("Ad")) {
                    firstNameField.setText("");
                    firstNameField.setForeground(Color.BLACK);
                }
            }
            public void focusLost(FocusEvent e) {
                if (firstNameField.getText().isEmpty()) {
                    firstNameField.setText("Ad");
                    firstNameField.setForeground(Color.GRAY);
                }
            }
        });

        lastNameField.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (lastNameField.getText().equals("Soyad")) {
                    lastNameField.setText("");
                    lastNameField.setForeground(Color.BLACK);
                }
            }
            public void focusLost(FocusEvent e) {
                if (lastNameField.getText().isEmpty()) {
                    lastNameField.setText("Soyad");
                    lastNameField.setForeground(Color.GRAY);
                }
            }
        });

        ageField.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (ageField.getText().equals("Yaş")) {
                    ageField.setText("");
                    ageField.setForeground(Color.BLACK);
                }
            }
            public void focusLost(FocusEvent e) {
                if (ageField.getText().isEmpty()) {
                    ageField.setText("Yaş");
                    ageField.setForeground(Color.GRAY);
                }
            }
        });

        JButton createAccountButton = new JButton("Oluştur");
        createAccountButton.setBounds(50, 420, 300, 40);
        createAccountButton.setBackground(new Color(0, 120, 215));
        createAccountButton.setForeground(Color.WHITE);
        createAccountButton.setFont(new Font("Courier New", Font.BOLD, 14));

        JButton backToLoginButton = new JButton("Giriş Yapmaya Dön");
        backToLoginButton.setBounds(50, 480, 300, 40);
        backToLoginButton.setBackground(new Color(220, 220, 220));
        backToLoginButton.setFont(new Font("Courier New", Font.PLAIN, 14));

        JLabel backToLoginIcon = new JLabel("🔙");
        backToLoginIcon.setFont(new Font("Material Icons", Font.PLAIN, 18));
        backToLoginIcon.setBounds(20, 480, 30, 40);
        registerPanel.add(backToLoginIcon);

        registerPanel.add(titleLabel);
        registerPanel.add(usernameField);
        registerPanel.add(passwordField);
        registerPanel.add(firstNameField);
        registerPanel.add(lastNameField);
        registerPanel.add(ageField);
        registerPanel.add(ageHintLabel);  // Yaş için uyarı mesajını ekledim
        registerPanel.add(createAccountButton);
        registerPanel.add(backToLoginButton);

        backToLoginButton.addActionListener(e -> cardLayout.show(getContentPane(), "login"));

        createAccountButton.addActionListener(e -> {
            String username = usernameField.getText();
            String password = String.valueOf(passwordField.getPassword());
            String firstName = firstNameField.getText();
            String lastName = lastNameField.getText();
            int age;

            try {
                // Yaş değeri sayıya çevrilirken hata olursa exception fırlatılır
                age = Integer.parseInt(ageField.getText());

                // Yaşın 1 ile 110 arasında olduğuna dair kontrol
                if (age < 1 || age > 110) {
                    JOptionPane.showMessageDialog(this,
                        "Yaş 1 ile 110 arasında olmalıdır!",
                        "Hata",
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }

                loginClass.addUser(username, firstName, lastName, age, password, dbHelper);

                JOptionPane.showMessageDialog(this,
                    "Hesap başarıyla oluşturuldu!",
                    "Başarılı",
                    JOptionPane.INFORMATION_MESSAGE);

                // Giriş paneline geri dön
                cardLayout.show(getContentPane(), "login");

            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this,
                    "Kayıt hatası: " + ex.getMessage(),
                    "Hata",
                    JOptionPane.ERROR_MESSAGE);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this,
                    "Yaş geçerli bir sayı olmalıdır!",
                    "Hata",
                    JOptionPane.ERROR_MESSAGE);
            }
        });
    }



    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            AccountManager accountManager = new AccountManager();
            accountManager.setVisible(true);
        });
    }
}
