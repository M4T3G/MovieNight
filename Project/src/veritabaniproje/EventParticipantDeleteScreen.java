package veritabaniproje;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.*;

public class EventParticipantDeleteScreen extends JFrame {
    private Color backgroundColor = Color.WHITE;
    private Color accentColor = new Color(255, 89, 56);
    private Color darkBackgroundColor = new Color(31, 37, 49);
    private Color textColor = Color.BLACK;
    
    public EventParticipantDeleteScreen(String movieName, String genre, String dateTime, String location, 
                           String currentParticipants, String maxParticipants) {
        setTitle("Etkinlik Detayları");
        setSize(400, 600);
        setType(Window.Type.UTILITY);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(backgroundColor);
        
        // Ana panel
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(backgroundColor);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Film başlığı ve puanı
        JPanel headerPanel = createHeaderPanel(movieName, "4.5");
        
        // Film türü butonu
        JPanel genrePanel = createGenrePanel(genre);
        
        // Tarih, saat ve konum bilgileri
        JPanel infoPanel = createInfoPanel(dateTime, location);
        
        // Katılımcılar listesi
        JPanel participantsPanel = createParticipantsPanel();
        
        // Kapasite göstergesi ve butonlar
        JPanel bottomPanel = createBottomPanel(currentParticipants, maxParticipants);
        
        // Panelleri ana panele ekle
        mainPanel.add(headerPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        mainPanel.add(genrePanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        mainPanel.add(infoPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        mainPanel.add(participantsPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        mainPanel.add(bottomPanel);
        
        // Scroll panel
        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        
        add(scrollPane);
        setLocationRelativeTo(null);
    }
    
    private JPanel createHeaderPanel(String movieName, String rating) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(accentColor);
        panel.setBorder(BorderFactory.createEmptyBorder(30, 20, 30, 20));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));
        
        JLabel nameLabel = new JLabel(movieName);
        nameLabel.setFont(new Font("Dialog", Font.BOLD, 24));
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel ratingLabel = new JLabel("⭐ " + rating);
        ratingLabel.setFont(new Font("Dialog", Font.BOLD, 20));
        ratingLabel.setForeground(Color.WHITE);
        ratingLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        panel.add(nameLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(ratingLabel);
        
        return panel;
    }
    
    private JPanel createGenrePanel(String genre) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBackground(backgroundColor);
        
        JLabel genreLabel = new JLabel(genre);
        genreLabel.setFont(new Font("Dialog", Font.BOLD, 14));
        genreLabel.setForeground(Color.WHITE);
        genreLabel.setBackground(new Color(41, 128, 185));
        genreLabel.setOpaque(true);
        genreLabel.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        
        panel.add(genreLabel);
        return panel;
    }
    
    private JPanel createInfoPanel(String dateTime, String location) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(backgroundColor);
        
        // Tarih ve saat
        JPanel datePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        datePanel.setBackground(backgroundColor);
        JLabel dateIcon = new JLabel("📅");
        JLabel dateLabel = new JLabel(dateTime);
        dateLabel.setFont(new Font("Dialog", Font.PLAIN, 14));
        datePanel.add(dateIcon);
        datePanel.add(dateLabel);
        
        // Konum
        JPanel locationPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        locationPanel.setBackground(backgroundColor);
        JLabel locationIcon = new JLabel("📍");
        JLabel locationLabel = new JLabel(location);
        locationLabel.setFont(new Font("Dialog", Font.PLAIN, 14));
        locationPanel.add(locationIcon);
        locationPanel.add(locationLabel);
        
        panel.add(datePanel);
        panel.add(locationPanel);
        
        return panel;
    }
    
    private JPanel createParticipantsPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(backgroundColor);
        
        JLabel titleLabel = new JLabel("Katılımcılar");
        titleLabel.setFont(new Font("Dialog", Font.BOLD, 16));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(titleLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        
        // Test için örnek katılımcılar
        String[] participants = {
            "Ahmet Yılmaz", "Mehmet Demir", "Ayşe Kaya", "Fatma Şahin",
            "Ali Öztürk", "Zeynep Çelik", "Mustafa Aydın", "Elif Yıldız"
        };
        
        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBackground(backgroundColor);
        
        for (String participant : participants) {
            JPanel participantPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            participantPanel.setBackground(backgroundColor);
            participantPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
            
            JLabel nameLabel = new JLabel(participant);
            nameLabel.setFont(new Font("Dialog", Font.PLAIN, 14));
            
            participantPanel.add(nameLabel);
            listPanel.add(participantPanel);
        }
        
        JScrollPane scrollPane = new JScrollPane(listPanel);
        scrollPane.setPreferredSize(new Dimension(350, 150));
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        panel.add(scrollPane);
        
        // Katılımcı ekleme butonu
        JButton addParticipantButton = new JButton("+ Katılımcı Ekle");
        addParticipantButton.setFont(new Font("Dialog", Font.BOLD, 14));
        addParticipantButton.setForeground(Color.WHITE);
        addParticipantButton.setBackground(accentColor);
        addParticipantButton.setBorderPainted(false);
        addParticipantButton.setFocusPainted(false);
        addParticipantButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        addParticipantButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Butona tıklama olayı
        addParticipantButton.addActionListener(e -> {
            // TODO: Katılımcı ekleme işlemleri
            JOptionPane.showMessageDialog(panel, "Katılımcı ekleme işlemi burada yapılacak.");
        });
        
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(addParticipantButton);
        
        return panel;
    }
    
    private JPanel createBottomPanel(String currentParticipants, String maxParticipants) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(backgroundColor);
        
        // Kapasite göstergesi
        JPanel capacityPanel = new JPanel(new BorderLayout());
        capacityPanel.setBackground(backgroundColor);
        capacityPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        
        JLabel capacityLabel = new JLabel("Katılımcı Sayısı");
        capacityLabel.setFont(new Font("Dialog", Font.BOLD, 14));
        
        JProgressBar progressBar = new JProgressBar(0, Integer.parseInt(maxParticipants));
        progressBar.setValue(Integer.parseInt(currentParticipants));
        progressBar.setStringPainted(true);
        progressBar.setString(currentParticipants + "/" + maxParticipants);
        progressBar.setForeground(accentColor);
        
        capacityPanel.add(capacityLabel, BorderLayout.NORTH);
        capacityPanel.add(progressBar, BorderLayout.CENTER);
        
        // Butonlar için panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 0));
        buttonsPanel.setBackground(backgroundColor);
        
        // Etkinliği silme butonu
        JButton deleteButton = new JButton("Etkinliği Sil");
        deleteButton.setFont(new Font("Dialog", Font.BOLD, 14));
        deleteButton.setForeground(Color.WHITE);
        deleteButton.setBackground(new Color(231, 76, 60)); // Kırmızı ton
        deleteButton.setBorderPainted(false);
        deleteButton.setFocusPainted(false);
        deleteButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        deleteButton.setPreferredSize(new Dimension(120, 40));
        
        // Etkinliği silme butonu tıklama olayı
        deleteButton.addActionListener(e -> {
            int result = JOptionPane.showConfirmDialog(
                this,
                "Etkinliği silmek istediğinize emin misiniz?",
                "Etkinliği Sil",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
            );
            
            if (result == JOptionPane.YES_OPTION) {
                // TODO: Etkinliği silme işlemleri
                JOptionPane.showMessageDialog(this, "Etkinlik silinecek.");
                dispose();
            }
        });
        
        buttonsPanel.add(deleteButton);
        
        panel.add(capacityPanel);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        panel.add(buttonsPanel);
        
        return panel;
    }
} 