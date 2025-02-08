package veritabaniproje;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.basic.BasicButtonUI;

import Type.DbHelper;
import Type.Kullanici;

import java.awt.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class EventDetailScreen extends JFrame {
    private Color backgroundColor = Color.WHITE;
    private Color accentColor = new Color(255, 89, 56);
    private Color darkBackgroundColor = new Color(31, 37, 49);
    private Color textColor = Color.BLACK;
    
    public EventDetailScreen(String movieName, String genre, String dateTime, String location, 
                           String currentParticipants, String maxParticipants,DbHelper db_helper,Kullanici kullanici,int etkinlikId) {
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
        
        // Film türü butonua
        JPanel genrePanel = createGenrePanel(genre);
        
        // Tarih, saat ve konum bilgileri
        JPanel infoPanel = createInfoPanel(dateTime, location);
        
        // Katılımcılar listesi
        JPanel participantsPanel = createParticipantsPanel(db_helper,etkinlikId);
        
        // Kapasite göstergesi ve katılım butonu
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
    
    private JPanel createParticipantsPanel(DbHelper db_helper, int etkinlikId) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(backgroundColor);
        
        JLabel titleLabel = new JLabel("Katılımcılar");
        titleLabel.setFont(new Font("Dialog", Font.BOLD, 16));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(titleLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        
     // Test için örnek katılımcılar
        ArrayList<String> participantsList = new ArrayList<>();
        
     // Veritabanı sorgusunu yapıyoruz
        String query = "SELECT davetli_id FROM davetliler WHERE etkinlik_id = ?";
        try (PreparedStatement preparedStatement = db_helper.connection.prepareStatement(query)) {
            // Parametreyi ayarlıyoruz
            preparedStatement.setInt(1, etkinlikId);

            // Sorguyu çalıştırıyoruz
            ResultSet resultSet = preparedStatement.executeQuery();

            // Sonuçları listeye ekliyoruz
            while (resultSet.next()) {
                String davetliId = resultSet.getString("davetli_id");
                participantsList.add(davetliId);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBackground(backgroundColor);
        
        for (String participant : participantsList) {
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
        
        JLabel capacityLabel = new JLabel("Kalan Kontenjan");
        capacityLabel.setFont(new Font("Dialog", Font.BOLD, 14));
        
        JProgressBar progressBar = new JProgressBar(0, Integer.parseInt(maxParticipants));
        progressBar.setValue(Integer.parseInt(currentParticipants));
        progressBar.setStringPainted(true);
        progressBar.setString(currentParticipants + "/" + maxParticipants);
        progressBar.setForeground(accentColor);
        
        capacityPanel.add(capacityLabel, BorderLayout.NORTH);
        capacityPanel.add(progressBar, BorderLayout.CENTER);
        
        // Katılım butonu
        JButton joinButton = new JButton("Katılım Talebi Gönder");
        joinButton.setFont(new Font("Dialog", Font.BOLD, 14));
        joinButton.setForeground(Color.WHITE);
        joinButton.setBackground(accentColor);
        joinButton.setBorderPainted(false);
        joinButton.setFocusPainted(false);
        joinButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        joinButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        
        // Oval görünüm için özel UI
        joinButton.setUI(new BasicButtonUI() {
            @Override
            public void paint(Graphics g, JComponent c) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                AbstractButton b = (AbstractButton) c;
                ButtonModel model = b.getModel();
                
                if (model.isPressed()) {
                    g2d.setColor(accentColor.darker());
                } else {
                    g2d.setColor(b.getBackground());
                }
                
                g2d.fillRoundRect(0, 0, c.getWidth(), c.getHeight(), 20, 20);
                super.paint(g, c);
                g2d.dispose();
            }
        });
        
        panel.add(capacityPanel);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        panel.add(joinButton);
        
        return panel;
    }
} 