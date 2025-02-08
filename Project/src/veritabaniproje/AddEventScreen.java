package veritabaniproje;

import javax.swing.*;

import Type.DbHelper;
import Type.Etkinlik;
import Type.Film;
import Type.Kullanici;

import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class AddEventScreen extends JFrame {
    private JTextField eventNameField;
    private JComboBox<String> movieComboBox;
    private JTextField addressField;
    private JSpinner dateSpinner;
    private JTextField maxParticipantsField; // Maksimum katılımcı sayısı için alan

    public AddEventScreen(Kullanici kullanici,DbHelper db_helper) {
        setTitle("Etkinlik Ekle");
        setSize(400, 500);
        setType(Window.Type.UTILITY);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(255, 89, 56));
        
        // Ana panel
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(new Color(255, 89, 56));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Başlık
        JLabel titleLabel = new JLabel("Yeni Etkinlik");
        titleLabel.setFont(new Font("Dialog", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 30)));
        
        // Etkinlik Adı
        addFormField(mainPanel, "Etkinlik Adı:");
        eventNameField = new JTextField();
        styleTextField(eventNameField);
        mainPanel.add(eventNameField);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        
       
        Film film = new Film();
        ArrayList<String> movies = film.getFilmNames(db_helper); // Film isimlerini alıyoruz

        // JComboBox için model oluşturuyoruz
        DefaultComboBoxModel<String> comboBoxModel = new DefaultComboBoxModel<>();

        // ArrayList'teki her bir film ismini JComboBox modeline ekliyoruz
        for (String movie : movies) {
            comboBoxModel.addElement(movie);
        }

        // JComboBox'ı model ile oluşturuyoruz
        movieComboBox = new JComboBox<>(comboBoxModel);
        styleComboBox(movieComboBox);

        // Panel üzerine ekliyoruz
        addFormField(mainPanel, "Film Seçin:");
        mainPanel.add(movieComboBox);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        
        // Adres
        addFormField(mainPanel, "Adres:");
        addressField = new JTextField();
        styleTextField(addressField);
        mainPanel.add(addressField);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        
        // Maksimum Katılımcı Sayısı
        addFormField(mainPanel, "Maksimum Katılımcı Sayısı:");
        maxParticipantsField = new JTextField();
        styleTextField(maxParticipantsField);
        mainPanel.add(maxParticipantsField);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        
        // Tarih
        addFormField(mainPanel, "Tarih:");
        Calendar calendar = Calendar.getInstance();
        Date initDate = calendar.getTime();
        calendar.add(Calendar.YEAR, -100);
        Date earliestDate = calendar.getTime();
        calendar.add(Calendar.YEAR, 200);
        Date latestDate = calendar.getTime();
        SpinnerDateModel dateModel = new SpinnerDateModel(initDate, earliestDate, latestDate, Calendar.DAY_OF_MONTH);
        dateSpinner = new JSpinner(dateModel);
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dateSpinner, "dd.MM.yyyy");
        dateSpinner.setEditor(dateEditor);
        styleSpinner(dateSpinner);
        mainPanel.add(dateSpinner);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 30)));
        
        // Kaydet Butonu
        JButton saveButton = new JButton("Kaydet");
        styleButton(saveButton);
        saveButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        saveButton.addActionListener(e -> {
            // Form verilerini al
            String eventName = eventNameField.getText();
            String selectedMovie = (String) movieComboBox.getSelectedItem();
            String address = addressField.getText();
            String maxParticipantsStr = maxParticipantsField.getText(); // Maksimum katılımcı sayısını al
            Date selectedDate = (Date) dateSpinner.getValue();
            java.sql.Timestamp timestamp = new java.sql.Timestamp(selectedDate.getTime());
            
            // Basit doğrulama
            if (eventName.trim().isEmpty() || address.trim().isEmpty() || maxParticipantsStr.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "Lütfen tüm alanları doldurun!", 
                    "Hata", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Maksimum katılımcı sayısını kontrol et
            int maxParticipants;
            try {
                maxParticipants = Integer.parseInt(maxParticipantsStr);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, 
                    "Maksimum katılımcı sayısı geçersiz! Lütfen bir sayı girin.", 
                    "Hata", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Burada veritabanına kaydetme işlemleri yapılacak
            
            Film film1=new Film();
            int filmId = 0;
			try {
				filmId = film1.getFilmIdByName(selectedMovie, db_helper);
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
            Etkinlik etkinlik=new Etkinlik();
            
				try {
					etkinlik.createEvent(eventName, timestamp, address, maxParticipants, kullanici.getKullanici_adi(),filmId , db_helper);
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			
            
            JOptionPane.showMessageDialog(this, "Etkinlik kaydedildi!");
            this.dispose();
        });
        mainPanel.add(saveButton);
        
        add(mainPanel, BorderLayout.CENTER);
        setLocationRelativeTo(null);
    }
    
    private void addFormField(JPanel panel, String labelText) {
        JLabel label = new JLabel(labelText);
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Dialog", Font.BOLD, 14));
        panel.add(label);
        panel.add(Box.createRigidArea(new Dimension(0, 5)));
    }
    
    private void styleTextField(JTextField field) {
        field.setMaximumSize(new Dimension(380, 30));
        field.setPreferredSize(new Dimension(380, 30));
        field.setFont(new Font("Dialog", Font.PLAIN, 14));
    }
    
    private void styleComboBox(JComboBox<String> comboBox) {
        comboBox.setMaximumSize(new Dimension(380, 30));
        comboBox.setPreferredSize(new Dimension(380, 30));
        comboBox.setFont(new Font("Dialog", Font.PLAIN, 14));
    }
    
    private void styleSpinner(JSpinner spinner) {
        spinner.setMaximumSize(new Dimension(380, 30));
        spinner.setPreferredSize(new Dimension(380, 30));
        spinner.setFont(new Font("Dialog", Font.PLAIN, 14));
    }
    
    private void styleButton(JButton button) {
        button.setFont(new Font("Dialog", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(31, 37, 49));
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setMaximumSize(new Dimension(200, 40));
        button.setPreferredSize(new Dimension(200, 40));
    }
}