package veritabaniproje;

import javax.swing.*;
import javax.swing.border.*;
import Type.DbHelper;
import Type.Kullanici;

import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;

public class UserProfileScreen extends JFrame {
    private Color backgroundColor = new Color(255, 89, 56);
    private Color darkBackgroundColor = new Color(31, 37, 49);
    private Color textColor = Color.WHITE;

    public UserProfileScreen(Kullanici kullanici, DbHelper db_helper) {
        setTitle("Profil");
        setSize(350, 500);
        setType(Window.Type.UTILITY);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(backgroundColor);

        // Ana panel
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(backgroundColor);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        // Profil ikonu
        JLabel profileIcon = new JLabel("👤");
        profileIcon.setFont(new Font("Segoe UI", Font.PLAIN, 50));
        profileIcon.setForeground(textColor);
        profileIcon.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(profileIcon);

        // Kullanıcı adı ve username bilgilerini göster
        JPanel userInfoPanel = new JPanel();
        userInfoPanel.setLayout(new BoxLayout(userInfoPanel, BoxLayout.Y_AXIS));
        userInfoPanel.setBackground(backgroundColor);
        userInfoPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel usernameLabel = new JLabel(kullanici.getIlk_isim() + " " + kullanici.getSon_isim());
        usernameLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        usernameLabel.setForeground(textColor);
        usernameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel userIdLabel = new JLabel(kullanici.getKullanici_adi());
        userIdLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        userIdLabel.setForeground(new Color(200, 200, 200));
        userIdLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        userInfoPanel.add(usernameLabel);
        userInfoPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        userInfoPanel.add(userIdLabel);

        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        mainPanel.add(userInfoPanel);

        // Favori film bilgisini çek ve göster
        String favoriteMovie = kullanici.getFavoriteFilm(kullanici.getKullanici_adi(), db_helper);
        JPanel favoriteMoviePanel = createInfoPanel("Favori Film", favoriteMovie);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 30)));
        mainPanel.add(favoriteMoviePanel);

        // Şifre değiştir butonu
        JButton changePasswordButton = createStyledButton("Şifre Değiştir");
        changePasswordButton.addActionListener(e -> showChangePasswordDialog(kullanici,db_helper));
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        mainPanel.add(changePasswordButton);

        // Çıkış yap butonu
        JButton logoutButton = createStyledButton("Çıkış Yap");
        logoutButton.addActionListener(e -> logout());
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        mainPanel.add(logoutButton);

        add(mainPanel, BorderLayout.CENTER);
        setLocationRelativeTo(null);

        // Gölge efekti
        getRootPane().setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0, 0, 0, 50), 1),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));
    }

    private JPanel createInfoPanel(String title, String value) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(darkBackgroundColor);
        panel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(255, 255, 255, 30), 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)));
        panel.setMaximumSize(new Dimension(300, 80));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        titleLabel.setForeground(new Color(200, 200, 200));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        valueLabel.setForeground(textColor);
        valueLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(titleLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 5)));
        panel.add(valueLabel);

        return panel;
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(textColor);
        button.setBackground(darkBackgroundColor);
        button.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(255, 255, 255, 30), 1),
                BorderFactory.createEmptyBorder(10, 30, 10, 30)));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(200, 40));

        // Hover efekti
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(41, 47, 59));
            }

            public void mouseExited(MouseEvent e) {
                button.setBackground(darkBackgroundColor);
            }
        });

        return button;
    }

    private void showChangePasswordDialog(Kullanici kullanici, DbHelper db_helper) {
        JPanel panel = new JPanel(new GridLayout(3, 2, 5, 5));
        JPasswordField currentPass = new JPasswordField();
        JPasswordField newPass = new JPasswordField();
        JPasswordField confirmPass = new JPasswordField();

        panel.add(new JLabel("Mevcut Şifre:"));
        panel.add(currentPass);
        panel.add(new JLabel("Yeni Şifre:"));
        panel.add(newPass);
        panel.add(new JLabel("Yeni Şifre (Tekrar):"));
        panel.add(confirmPass);

        int result = JOptionPane.showConfirmDialog(this, panel, "Şifre Değiştir",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String mevcutSifre = new String(currentPass.getPassword());
            String yeniSifre = new String(newPass.getPassword());
            String yeniSifreTekrar = new String(confirmPass.getPassword());

            try {
                boolean isPasswordChanged = kullanici.changePassword(
                    kullanici.getKullanici_adi(), mevcutSifre, yeniSifre, yeniSifreTekrar,db_helper);

                if (isPasswordChanged) {
                    JOptionPane.showMessageDialog(this, "Şifre başarıyla değiştirildi!");
                } else {
                    JOptionPane.showMessageDialog(this, 
                            "Şifre değiştirme başarısız. Mevcut şifre yanlış olabilir veya yeni şifreler uyuşmuyor.",
                            "Hata", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this,
                        "Bir hata oluştu. Lütfen tekrar deneyin.",
                        "Hata", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }

    private void logout() {
        int response = JOptionPane.showConfirmDialog(this,
                "Çıkış yapmak istediğinize emin misiniz?",
                "Çıkış Yap",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (response == JOptionPane.YES_OPTION) {
            // Bu pencereyi kapat
            this.dispose();

            // AccountManager sayfasına dön
            SwingUtilities.invokeLater(() -> {
                try {
                    AccountManager accountManager = new AccountManager();
                    accountManager.setVisible(true);
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(null, 
                            "Hesap Yönetim ekranı yüklenirken bir hata oluştu.",
                            "Hata", JOptionPane.ERROR_MESSAGE);
                    e.printStackTrace();
                }
            });
        }
    }

}
