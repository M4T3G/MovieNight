package veritabaniproje;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.basic.BasicToggleButtonUI;

import Type.DbHelper;
import Type.Etkinlik;
import Type.Kullanici;

import java.awt.*;
import java.awt.event.*;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import veritabaniproje.EventParticipantDetailScreen;

public class EventsScreen extends JFrame {
    private Color backgroundColor = Color.WHITE;
    private Color darkBackgroundColor = new Color(31, 37, 49);
    private Color textColor = Color.WHITE;
    private JPanel eventsPanel;
    private ArrayList<String[]> allEvents;
    private JToggleButton currentFilter;
    private Etkinlik etkinlik1 = new Etkinlik(); 
    
    public EventsScreen(Kullanici kullanici, DbHelper db_helper) {
        setTitle("Etkinlikler");
        setSize(500, 600);
        setType(Window.Type.UTILITY);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(backgroundColor);
        
        // Ana panel
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBackground(backgroundColor);
        
        // Filtre butonları paneli
        JPanel filterPanel = createFilterPanel(kullanici,db_helper);
        mainPanel.add(filterPanel, BorderLayout.NORTH);
        
        // Etkinlikler paneli
        eventsPanel = new JPanel();
        eventsPanel.setLayout(new BoxLayout(eventsPanel, BoxLayout.Y_AXIS));
        eventsPanel.setBackground(backgroundColor);
        
        JScrollPane scrollPane = new JScrollPane(eventsPanel);
        scrollPane.setBackground(backgroundColor);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        
        add(mainPanel);
        
        // Test verileri
        try {
			initializeEvents(kullanici, db_helper);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        displayEvents("Katılabileceklerim",kullanici,db_helper); // Başlangıçta Katılabileceklerim görünsün
        
        setLocationRelativeTo(null);
    }
    
    private JPanel createFilterPanel(Kullanici kullanici,DbHelper db_helper) {
        JPanel filterPanel = new JPanel();
        filterPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 15));
        filterPanel.setBackground(backgroundColor);
        
        String[] filters = {"Katılabileceklerim", "Beklediklerim", "Katıldıklarım"};
        ButtonGroup filterGroup = new ButtonGroup();
        
        for (String filter : filters) {
            JToggleButton filterButton = createFilterButton(filter,kullanici,db_helper);
            filterGroup.add(filterButton);
            filterPanel.add(filterButton);
            
            if (filter.equals("Katılabileceklerim")) {
                filterButton.setSelected(true);
                currentFilter = filterButton;
                updateFilterButtonStyle(filterButton);
            }
        }
        
        return filterPanel;
    }
    
    private JToggleButton createFilterButton(String text,Kullanici kullanici,DbHelper db_helper) {
        JToggleButton button = new JToggleButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setForeground(textColor);
        button.setBackground(darkBackgroundColor);
        
        // Oval şekil için
        int arc = 20;
        button.setBorder(new EmptyBorder(8, 20, 8, 20));
        button.setUI(new BasicToggleButtonUI() {
            @Override
            protected void paintButtonPressed(Graphics g, AbstractButton b) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(41, 47, 59));
                g2d.fillRoundRect(0, 0, b.getWidth(), b.getHeight(), arc, arc);
                g2d.dispose();
            }
            
            protected void paintBackground(Graphics g, AbstractButton b, Rectangle r) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (b.isSelected()) {
                    g2d.setColor(new Color(41, 47, 59));
                } else {
                    g2d.setColor(darkBackgroundColor);
                }
                g2d.fillRoundRect(0, 0, b.getWidth(), b.getHeight(), arc, arc);
                g2d.dispose();
            }
        });
        
        button.addActionListener(e -> {
            if (currentFilter != button) {
                updateFilterButtonStyle(button);
                currentFilter = button;
                displayEvents(text,kullanici,db_helper);
            }
        });
        
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setFocusPainted(false);
        
        return button;
    }
    
    private void updateFilterButtonStyle(JToggleButton button) {
        button.setBackground(new Color(41, 47, 59));
    }
    
    public void initializeEvents(Kullanici kullanici, DbHelper db_helper) throws SQLException {
    	if (allEvents != null) {
            allEvents.clear(); // Mevcut listeyi temizle
        }
        allEvents = new ArrayList<>();
        String kullaniciId = kullanici.getKullanici_adi();

        // Sorgular
        String katilabileceklerimQuery = """
            SELECT 
                e.etkinlik_ismi,
                e.zaman,
                e.adres,
                e.max_kisi,
                f.isim AS favfilm,
                f.tur
            FROM 
                etkinlik e
            JOIN 
                film f ON e.favfilm = f.film_id
            WHERE 
                e.etkinlik_id IN (
                    SELECT etkinlik_id 
                    FROM etkinlik
                    EXCEPT
                    (
                        SELECT etkinlik_id 
                        FROM katilimistegi 
                        WHERE katilmak_isteyen_id = ?
                        UNION 
                        SELECT etkinlik_id 
                        FROM davetliler 
                        WHERE davetli_id = ?
                    )
                );
        """;

        String beklediklerimQuery = """
            SELECT 
                e.etkinlik_ismi,
                e.zaman,
                e.adres,
                e.max_kisi,
                f.isim AS favfilm,
                f.tur
            FROM 
                etkinlik e
            JOIN 
                film f ON e.favfilm = f.film_id
            JOIN 
                katilimistegi k ON k.etkinlik_id = e.etkinlik_id
            WHERE 
                k.katilmak_isteyen_id = ?;
        """;

        String katildiklarimQuery = """
            SELECT 
                e.etkinlik_ismi,
                e.zaman,
                e.adres,
                e.max_kisi,
                f.isim AS favfilm,
                f.tur
            FROM 
                etkinlik e
            JOIN 
                film f ON e.favfilm = f.film_id
            JOIN 
                davetliler d ON d.etkinlik_id = e.etkinlik_id
            WHERE 
                d.davetli_id = ?;
        """;

		// Katılabileceklerim
        try (PreparedStatement stmt = db_helper.connection.prepareStatement(katilabileceklerimQuery)) {
        	
            stmt.setString(1, kullaniciId);
            stmt.setString(2, kullaniciId);

            ResultSet resultSet = stmt.executeQuery();
            while (resultSet.next()) {
                String etkinlikIsmi = resultSet.getString("etkinlik_ismi");
                Date zaman = resultSet.getDate("zaman");
                String adres = resultSet.getString("adres");
                String maxKisi = String.valueOf(resultSet.getInt("max_kisi"));
                String favFilm = resultSet.getString("favfilm");
                String durum = "Katılabileceklerim";
                String tur =resultSet.getString("tur");

                 // Etkinlik ID'sini bulmak için sorgu
                Integer etkinlikId = findEventId(adres, zaman,db_helper);

                // Mevcut katılımcı sayısını bulmak için metot çağrısı
                String mevcutKatilan = etkinlik1.getParticipantNumber(etkinlikId, db_helper).toString();
                

                // Listeye ekle
                String etkinlikZamani = zaman.toString();
                allEvents.add(new String[]{favFilm, etkinlikZamani, adres, durum, mevcutKatilan, maxKisi,etkinlikId.toString(),tur});
            }
        }

        // Beklediklerim
        try (PreparedStatement stmt = db_helper.connection.prepareStatement(beklediklerimQuery)) {
        	
            stmt.setString(1, kullaniciId);

            ResultSet resultSet = stmt.executeQuery();
            while (resultSet.next()) {
                String etkinlikIsmi = resultSet.getString("etkinlik_ismi");
                Date zaman = resultSet.getDate("zaman");
                String adres = resultSet.getString("adres");
                String maxKisi = String.valueOf(resultSet.getInt("max_kisi"));
                String favFilm = resultSet.getString("favfilm");
                String durum = "Beklediklerim";
                String tur =resultSet.getString("tur");

                // Etkinlik ID'sini bulmak için sorgu
                Integer etkinlikId = findEventId(adres, zaman,db_helper);

                // Mevcut katılımcı sayısını bulmak için metot çağrısı
                Integer mevcutKatilan = etkinlik1.getParticipantNumber(etkinlikId, db_helper);

                // Listeye ekle
                String etkinlikZamani = zaman.toString();
                allEvents.add(new String[]{favFilm, etkinlikZamani, adres, durum, mevcutKatilan.toString(), maxKisi,etkinlikId.toString(),tur});
            }
        }

        // Katıldıklarım
        try (PreparedStatement stmt = db_helper.connection.prepareStatement(katildiklarimQuery)) {
        	
            stmt.setString(1, kullaniciId);

            ResultSet resultSet = stmt.executeQuery();
            while (resultSet.next()) {
                String etkinlikIsmi = resultSet.getString("etkinlik_ismi");
                Date zaman = resultSet.getDate("zaman");
                String adres = resultSet.getString("adres");
                String maxKisi = String.valueOf(resultSet.getInt("max_kisi"));
                String favFilm = resultSet.getString("favfilm");
                String durum = "Katıldıklarım";
                String tur =resultSet.getString("tur");

                 // Etkinlik ID'sini bulmak için sorgu
                Integer etkinlikId = findEventId(adres, zaman,db_helper);

                // Mevcut katılımcı sayısını bulmak için metot çağrısı
                Integer mevcutKatilan = etkinlik1.getParticipantNumber(etkinlikId, db_helper);
                // Listeye ekle
                String etkinlikZamani = zaman.toString();
                allEvents.add(new String[]{favFilm, etkinlikZamani, adres, durum, mevcutKatilan.toString(), maxKisi,etkinlikId.toString(),tur});
            }
        }
    }


    // Etkinlik ID'sini bulmak için bir yardımcı metot
    private int findEventId(String adres, Date zaman,DbHelper db_helper) throws SQLException {
    	String query = "SELECT etkinlik_id FROM etkinlik WHERE adres = ? AND zaman = ?";
        try (PreparedStatement stmt = db_helper.connection.prepareStatement(query)) {
            stmt.setString(1, adres);
            stmt.setDate(2, zaman);

            ResultSet resultSet = stmt.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("etkinlik_id");
            }
        }
        throw new SQLException("Etkinlik ID bulunamadı! Adres: " + adres + ", Zaman: " + zaman);
    }
    
    private void displayEvents(String filter,Kullanici kullanici,DbHelper dbhelper) {
        eventsPanel.removeAll();
        eventsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        
        for (String[] event : allEvents) {
            if (event[3].equals(filter)) {
                addEventCard(event[0], event[1], event[2], event[4], event[5],kullanici,dbhelper,event[6],event[7]);
                eventsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
            }
        }
        
        eventsPanel.revalidate();
        eventsPanel.repaint();
    }
    
    private class RoundedPanel extends JPanel {
        private int cornerRadius = 15;
        
        public RoundedPanel(LayoutManager layout) {
            super(layout);
            setOpaque(false);
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            g2d.setColor(getBackground());
            g2d.fillRoundRect(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius);
            g2d.dispose();
        }
    }
    
    private void addEventCard(String movieName, String dateTime, String location, String currentParticipants, String maxParticipants, Kullanici kullanici,DbHelper dbhelper,String etkinlikIdString,String tur) {
    	
    	 SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

         Date date = null;
         
         int etkinlikId = Integer.parseInt(etkinlikIdString);
           
		
			
		
    	RoundedPanel card = new RoundedPanel(new BorderLayout());
        card.setMaximumSize(new Dimension(360, 80));
        card.setPreferredSize(new Dimension(360, 80));
        card.setBackground(getParticipantRatioColor(currentParticipants, maxParticipants));

        // İç padding
        card.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        // Sol taraftaki film ikonu
        JLabel iconLabel = new JLabel("🎬");
        iconLabel.setFont(new Font("Segoe UI", Font.PLAIN, 24));
        iconLabel.setForeground(textColor);
        iconLabel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));

        // Orta kısım - film adı ve detaylar
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(darkBackgroundColor);
        infoPanel.setOpaque(false);

        JLabel nameLabel = new JLabel(movieName);
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        nameLabel.setForeground(textColor);

        JLabel dateLabel = new JLabel(dateTime);
        dateLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        dateLabel.setForeground(new Color(200, 200, 200));

        JLabel locationLabel = new JLabel(location);
        locationLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        locationLabel.setForeground(new Color(200, 200, 200));

        infoPanel.add(nameLabel);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        infoPanel.add(dateLabel);
        infoPanel.add(locationLabel);

        // Katılımcı bilgisi ekleyelim
        JLabel participantsLabel = new JLabel(currentParticipants + "/" + maxParticipants);
        participantsLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        participantsLabel.setForeground(textColor);

        // Info panele katılımcı bilgisini ekleyelim
        infoPanel.add(Box.createRigidArea(new Dimension(0, 3)));
        infoPanel.add(participantsLabel);

        // Sağ ok ikonu
        JLabel arrowLabel = new JLabel("→");
        arrowLabel.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        arrowLabel.setForeground(textColor);
        arrowLabel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));

        card.add(iconLabel, BorderLayout.WEST);
        card.add(infoPanel, BorderLayout.CENTER);
        card.add(arrowLabel, BorderLayout.EAST);

        // Hover efektini güncelleyelim
        Color baseColor = card.getBackground();
        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                card.setBackground(baseColor.darker());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                card.setBackground(baseColor);
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                String currentFilter = getCurrentFilter();

                if (currentFilter.equals("Katılabileceklerim")) {
                    EventParticipantDetailScreen detailScreen = new EventParticipantDetailScreen(
                            movieName, tur,
                            dateTime, location,
                            currentParticipants, maxParticipants,dbhelper,kullanici,etkinlikId
                    );
                    setEnabled(false);
                    detailScreen.addWindowListener(new WindowAdapter() {
                        @Override
                        public void windowClosed(WindowEvent e) {
                            setEnabled(true);
                            refreshScreen(kullanici, dbhelper);
                        }
                    });
                    detailScreen.setLocationRelativeTo(null);
                    detailScreen.setVisible(true);
                } else if (currentFilter.equals("Beklediklerim")) {
                    EventWaitingDetailScreen waitingDetailScreen = new EventWaitingDetailScreen(
                            movieName, tur,
                            dateTime, location,
                            currentParticipants, maxParticipants,dbhelper,kullanici,etkinlikId
                    );
                    setEnabled(false);
                    waitingDetailScreen.addWindowListener(new WindowAdapter() {
                        @Override
                        public void windowClosed(WindowEvent e) {
                            setEnabled(true);
                            refreshScreen(kullanici, dbhelper);
                        }
                    });
                    waitingDetailScreen.setLocationRelativeTo(null);
                    waitingDetailScreen.setVisible(true);
                } else {
                    // Kullanıcıya göre sorgu kontrolü
                    String query = "SELECT EXISTS (SELECT * FROM owners_view WHERE davetli_id = ? AND etkinlik_id = ?)";
                    try (PreparedStatement preparedStatement = dbhelper.connection.prepareStatement(query)) {
                        // Parametreyi ayarlıyoruz
                        preparedStatement.setString(1, kullanici.getKullanici_adi());
                        preparedStatement.setInt(2, etkinlikId);

                        // Sorguyu çalıştırıyoruz ve sonucu alıyoruz
                        ResultSet resultSet = preparedStatement.executeQuery();

                        if (resultSet.next()) {
                        	
                        	 boolean exists = resultSet.getBoolean(1);
                             System.out.println("Result: " + exists);
                             
                             if (exists) {
                                 // Eğer kullanıcı mevcutsa, katılımcı ekranını göster
                                 EventOwnerDetailScreen detailScreen = new EventOwnerDetailScreen(
                                         movieName, tur,
                                         dateTime, location,
                                         currentParticipants, maxParticipants,dbhelper,kullanici,etkinlikId
                                 );
                                 setEnabled(false);
                                 detailScreen.addWindowListener(new WindowAdapter() {
                                     @Override
                                     public void windowClosed(WindowEvent e) {
                                         setEnabled(true);
                                     }
                                 });
                                 detailScreen.setLocationRelativeTo(null);
                                 detailScreen.setVisible(true);
                             } else {
                                 // Eğer kullanıcı yoksa
                                 EventParticipantDetailScreen detailScreen = new EventParticipantDetailScreen(
                                         movieName, tur,
                                         dateTime, location,
                                         currentParticipants, maxParticipants,dbhelper,kullanici,etkinlikId
                                 );
                                 setEnabled(false);
                                 detailScreen.addWindowListener(new WindowAdapter() {
                                     @Override
                                     public void windowClosed(WindowEvent e) {
                                         setEnabled(true);
                                         refreshScreen(kullanici, dbhelper);
                                     }
                                 });
                                 detailScreen.setLocationRelativeTo(null);
                                 detailScreen.setVisible(true);
                             }
                         } else {
                             // Eğer hiç satır gelmezse
                             System.out.println("No data returned");
                         }
                     } catch (SQLException e1) {
                         e1.printStackTrace();
                     }
                }
            }
        });

        card.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Kartlar arasında boşluk bırak
        JPanel wrapperPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        wrapperPanel.setBackground(backgroundColor);
        wrapperPanel.add(card);
    
        eventsPanel.add(wrapperPanel);
    }

    
    private Color getParticipantRatioColor(String current, String max) {
        int currentParticipants = Integer.parseInt(current);
        int maxParticipants = Integer.parseInt(max);
        double ratio = (double) currentParticipants / maxParticipants;
        
        // Renk tonlarını ayarlayalım (daha soft tonlar)
        Color greenColor = new Color(46, 125, 50);    // Soft yeşil
        Color yellowColor = new Color(251, 192, 45);  // Soft sarı
        Color redColor = new Color(198, 40, 40);      // Soft kırmızı
        
        if (ratio < 0.5) {
            return greenColor;        // %50'den az doluluk - yeşil
        } else if (ratio < 0.8) {
            return yellowColor;       // %50-%80 arası doluluk - sarı
        } else {
            return redColor;         // %80'den fazla doluluk - kırmızı
        }
    }
    
    private String getCurrentFilter() {
        if (currentFilter != null) {
            return currentFilter.getText();
        }
        return "Katıldıklarım";
    }
    
    private void openEventDetailScreen(String movieName, String genre, String dateTime, String location, 
                                     String currentParticipants, String maxParticipants,DbHelper db_helper,Kullanici kullanici,int etkinlikId) {
        // EventParticipantDetailScreen açılıyor
        EventParticipantDetailScreen detailScreen = new EventParticipantDetailScreen(movieName, genre, dateTime, location, 
                                                                      currentParticipants, maxParticipants, db_helper, kullanici,etkinlikId);
        detailScreen.setVisible(true);
    }
    
    private void refreshScreen(Kullanici kullanici, DbHelper dbHelper) {

        try {

            // Verileri yeniden yükle

            initializeEvents(kullanici, dbHelper);

            // Mevcut filtreyi al ve ekranı güncelle

            String currentFilterText = getCurrentFilter();

            displayEvents(currentFilterText, kullanici, dbHelper);

        } catch (SQLException e) {

            e.printStackTrace();

            JOptionPane.showMessageDialog(this,

                "Veriler yenilenirken hata oluştu: " + e.getMessage(),

                "Hata",

                JOptionPane.ERROR_MESSAGE);

        }

    }
} 