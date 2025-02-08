package veritabaniproje;

import javax.swing.*;
import javax.swing.border.LineBorder;

import Type.DbHelper;
import Type.Film;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class AddMovieScreen extends JFrame {
    private Color backgroundColor = new Color(255, 89, 56);
    private Color darkBackgroundColor = new Color(31, 37, 49);
    private Color textColor = Color.WHITE;
    
    private JTextField movieNameField;
    private JComboBox<String> genreComboBox;
    private MoviesScreen parentScreen; // Ebeveyn ekran referansı


    public AddMovieScreen(MoviesScreen moviesScreen,DbHelper db_helper) {
        this.parentScreen = moviesScreen;
        setTitle("Film Ekle");
        setSize(400, 400);
        setType(Window.Type.UTILITY);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(backgroundColor);
        
        // Ana panel
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(backgroundColor);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
        
        // Başlık
        JLabel titleLabel = new JLabel("Yeni Film Ekle");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(textColor);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 25)));
        
        // Film adı
        addFormField(mainPanel, "FİLM ADI");
        movieNameField = createStyledTextField();
        mainPanel.add(movieNameField);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        
        // Film türü
        addFormField(mainPanel, "FİLM TÜRÜ");
        String[] genres = {"Bilim Kurgu", "Dram", "Komedi", "Aksiyon", "Macera"};
        genreComboBox = new JComboBox<>(genres);
        styleComboBox(genreComboBox);
        mainPanel.add(genreComboBox);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 25)));
        
        // Kaydet butonu
        JButton saveButton = createStyledButton("KAYDET");
        saveButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        saveButton.addActionListener(e -> saveMovie(db_helper));
        mainPanel.add(saveButton);
        
        add(mainPanel, BorderLayout.CENTER);
        setLocationRelativeTo(null);
        
        // Gölge efekti
        getRootPane().setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0, 0, 0, 50), 1),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)));
    }


    
    private void addFormField(JPanel panel, String labelText) {
        JLabel label = new JLabel(labelText);
        label.setForeground(textColor);
        label.setFont(new Font("Segoe UI", Font.BOLD, 12));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(label);
        panel.add(Box.createRigidArea(new Dimension(0, 5)));
    }
    
    private JTextField createStyledTextField() {
        JTextField field = new JTextField();
        field.setMaximumSize(new Dimension(350, 35));
        field.setPreferredSize(new Dimension(350, 35));
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBackground(darkBackgroundColor);
        field.setForeground(textColor);
        field.setCaretColor(textColor);
        field.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(255, 255, 255, 30), 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        return field;
    }
    
    private void styleComboBox(JComboBox<String> comboBox) {
        comboBox.setMaximumSize(new Dimension(350, 35));
        comboBox.setPreferredSize(new Dimension(350, 35));
        comboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        comboBox.setBackground(darkBackgroundColor);
        comboBox.setForeground(textColor);
        comboBox.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(255, 255, 255, 30), 1),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)));
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
    
    private void saveMovie(DbHelper db_helper) {
        String movieName = movieNameField.getText().trim();
        String genre = (String) genreComboBox.getSelectedItem();
        
        if (movieName.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Lütfen film adını giriniz!",
                "Hata",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Yeni filmi ekle (başlangıç puanı 0.0 olarak ayarlandı)
        parentScreen.addNewMovie(movieName, genre, "0.0");
        Film film=new Film();
        film.addFilm(movieName, genre, db_helper);
        
        JOptionPane.showMessageDialog(this,
            "Film başarıyla eklendi!",
            "Başarılı",
            JOptionPane.INFORMATION_MESSAGE);
            
        this.dispose();
    }
}