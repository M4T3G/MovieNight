package veritabaniproje;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.basic.BasicButtonUI;

import Type.DbHelper;
import Type.Etkinlik;
import Type.Film;
import Type.Kullanici;

import java.awt.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EventOwnerDetailScreen extends JFrame {
    private Color backgroundColor = Color.WHITE;
    private static Color accentColor = new Color(255, 89, 56);
    private Color darkBackgroundColor = new Color(31, 37, 49);
    private Color textColor = Color.BLACK;
    
    public EventOwnerDetailScreen(String movieName, String genre, String dateTime, String location, 
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
        
        // Film türü butonu
        JPanel genrePanel = createGenrePanel(genre);
        
        // Tarih, saat ve konum bilgileri
        JPanel infoPanel = createInfoPanel(dateTime, location);
        
        // Katılımcılar listesi
        JPanel participantsPanel = createParticipantsPanel( db_helper,kullanici,etkinlikId);
        
        // Kapasite göstergesi ve butonlar
        JPanel bottomPanel = createBottomPanel(kullanici,currentParticipants, maxParticipants,etkinlikId,db_helper);
        
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
    
    private JPanel createParticipantsPanel(DbHelper db_helper,Kullanici kullanici,int etkinlikId) {
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
        
        DefaultListModel<String> listModel = new DefaultListModel<>();
        for (String participant :participantsList) {
            listModel.addElement(participant);
        }
        
        JList<String> participantList = new JList<>(listModel);
        participantList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        JScrollPane scrollPane = new JScrollPane(participantList);
        scrollPane.setPreferredSize(new Dimension(350, 150));
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        panel.add(scrollPane);
        
        // Butonlar için panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(backgroundColor);
        
        // Katılımcı davet et butonu
        JButton inviteParticipantButton = new JButton("Katılımcı Davet Et");
        inviteParticipantButton.setFont(new Font("Dialog", Font.BOLD, 14));
        inviteParticipantButton.setForeground(Color.WHITE);
        inviteParticipantButton.setBackground(accentColor);
        inviteParticipantButton.setBorderPainted(false);
        inviteParticipantButton.setFocusPainted(false);
        inviteParticipantButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Butona tıklama olayı
        inviteParticipantButton.addActionListener(e -> {
            //Etkinlik etkinlik=new Etkinlik();
            showInviteDialog(db_helper, kullanici, etkinlikId);
            
        });
        
        // Katılımcı çıkarma butonu
        JButton removeParticipantButton = new JButton("Katılımcı Çıkar");
        removeParticipantButton.setFont(new Font("Dialog", Font.BOLD, 14));
        removeParticipantButton.setForeground(Color.WHITE);
        removeParticipantButton.setBackground(new Color(231, 76, 60)); // Kırmızı ton
        removeParticipantButton.setBorderPainted(false);
        removeParticipantButton.setFocusPainted(false);
        removeParticipantButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Butona tıklama olayı
        removeParticipantButton.addActionListener(e -> {
            String selectedParticipant = participantList.getSelectedValue();  // Tek bir katılımcı seçilir
            if (selectedParticipant == null) {
                JOptionPane.showMessageDialog(panel, "Lütfen çıkarılacak katılımcıyı seçin.");
            } else {
                Etkinlik etkinlik = new Etkinlik();
                try {
					etkinlik.removeUser(etkinlikId, selectedParticipant, db_helper);
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
            }
        });
        
        // Butonları panelin sağ üst köşesine yerleştir
        buttonPanel.add(inviteParticipantButton);
        buttonPanel.add(removeParticipantButton);
        
        panel.add(buttonPanel);
        
        return panel;
    }
    
    private JPanel createBottomPanel(Kullanici kullanici,String currentParticipants, String maxParticipants,int etkinlikId,DbHelper db_helper) {
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
        
        // Film oylama butonu
        JButton rateButton = new JButton("Film Oyla");
        rateButton.setFont(new Font("Dialog", Font.BOLD, 14));
        rateButton.setForeground(Color.WHITE);
        rateButton.setBackground(new Color(41, 128, 185)); // Mavi ton
        rateButton.setBorderPainted(false);
        rateButton.setFocusPainted(false);
        rateButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        rateButton.setPreferredSize(new Dimension(120, 40));
        
        // Film oylama butonu tıklama olayı
        rateButton.addActionListener(e -> {
        	try {
                // Ana pencere oluştur
                JFrame frame = new JFrame("Film Oylama Uygulaması");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setSize(600, 400);
                frame.setLayout(new BorderLayout());

                // Buton paneli
                JPanel buttonPanel = new JPanel();
                buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
                JButton showAllMoviesButton = new JButton("Tüm Filmleri Göster");
                JButton showHighRatedMoviesButton = new JButton("En Çok Oylananları Göster");

                buttonPanel.add(showAllMoviesButton);
                buttonPanel.add(showHighRatedMoviesButton);
                frame.add(buttonPanel, BorderLayout.NORTH);

                // Film listesi paneli
                JPanel movieListPanel = new JPanel();
                movieListPanel.setLayout(new BoxLayout(movieListPanel, BoxLayout.Y_AXIS));
                movieListPanel.setBackground(Color.WHITE);
                JScrollPane scrollPane = new JScrollPane(movieListPanel);
                frame.add(scrollPane, BorderLayout.CENTER);

                // Oylama işlevselliği
                JButton openDialogButton = new JButton("Film Oyla");
                openDialogButton.addActionListener(e2 -> {
    				try {
    					showRatingDialog(kullanici,db_helper,etkinlikId);
    				} catch (SQLException e1) {
    					// TODO Auto-generated catch block
    					e1.printStackTrace();
    				}
    			});

                frame.getContentPane().add(openDialogButton, BorderLayout.SOUTH);

                // Butonlara tıklama olayları
                showAllMoviesButton.addActionListener(e2 -> {
                    movieListPanel.removeAll();
                    showAllMovies(db_helper, movieListPanel);
                    movieListPanel.revalidate();
                    movieListPanel.repaint();
                });

                showHighRatedMoviesButton.addActionListener(e2 -> {
                    movieListPanel.removeAll();
                    showHighRatedMovies(db_helper, movieListPanel);
                    movieListPanel.revalidate();
                    movieListPanel.repaint();
                });

                frame.setVisible(true);

            } catch (Exception e1) {
                e1.printStackTrace();
            } 
        });
        
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
            	Etkinlik etkinlik=new Etkinlik();
            	etkinlik.deleteEvent(etkinlikId, db_helper);
                JOptionPane.showMessageDialog(this, "Etkinlik silinecek.");
                dispose();
            }
        });
        
        buttonsPanel.add(rateButton);
        buttonsPanel.add(deleteButton);
        
        panel.add(capacityPanel);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        panel.add(buttonsPanel);
        
        return panel;
    }
    
    private static void showRatingDialog(Kullanici kullanicikullanici,DbHelper db_helper,int etkinlikId) throws SQLException {
        // Oylama dialogunu göster
        JDialog dialog = new JDialog((Frame) null, "Film Oyla", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(300, 400);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("Film Seç ve Oyla");
        titleLabel.setFont(new Font("Dialog", Font.BOLD, 16));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(titleLabel);

        // Örnek filmler listesi
        String[] movies = new String[100];  // Yeterli büyüklükte bir dizi oluşturun
        int index = 0;

        String query = "SELECT isim FROM film";  // Film isimlerini sorgulayan SQL sorgusu

        try (PreparedStatement stmt = db_helper.connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            // Sonuçları döngüyle al ve diziye ekle
            while (rs.next()) {
                movies[index++] = rs.getString("isim");  // "isim" sütunundan veriyi al
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        JList<String> movieList = new JList<>(movies);
        movieList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        JPanel ratingPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        ratingPanel.setBackground(Color.WHITE);
        
        JLabel ratingLabel = new JLabel("Puan: ");
        SpinnerModel spinnerModel = new SpinnerNumberModel(5, 1, 10, 1);
        JSpinner ratingSpinner = new JSpinner(spinnerModel);
        
        ratingPanel.add(ratingLabel);
        ratingPanel.add(ratingSpinner);
        
        JButton submitButton = new JButton("Oyla");
        submitButton.setFont(new Font("Dialog", Font.BOLD, 14));
        submitButton.setForeground(Color.WHITE);
        submitButton.setBackground(accentColor);
        submitButton.setBorderPainted(false);
        submitButton.setFocusPainted(false);
        submitButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        submitButton.setUI(createRoundedButtonUI1(accentColor));
        
        submitButton.addActionListener(e -> {
            String selectedMovie = movieList.getSelectedValue();
            int rating = (Integer) ratingSpinner.getValue();
            if (selectedMovie != null) {
                int filmId = -1; // Varsayılan değer
                String query2 = "SELECT film_id FROM film WHERE isim = ?";

                try (PreparedStatement preparedStatement = db_helper.connection.prepareStatement(query2)) {
                    preparedStatement.setString(1, selectedMovie);
                    ResultSet resultSet = preparedStatement.executeQuery();

                    if (resultSet.next()) {
                        filmId = resultSet.getInt("film_id"); // film_id'yi alıyoruz
                    }

                    // Film oylama işlemi yapılacak
                    if (filmId != -1) {
                        Film film = new Film();
                       film.voteFilm(etkinlikId, filmId, kullanicikullanici.getKullanici_adi(), rating, db_helper);
                        
                        // TODO: Oylama işlemleri veritabanına kaydedilecek
                        JOptionPane.showMessageDialog(dialog,
                            "Film başarıyla oylandı!\nFilm: " + selectedMovie + "\nPuan: " + rating,
                            "Başarılı", JOptionPane.INFORMATION_MESSAGE);
                        dialog.dispose();
                    } else {
                        JOptionPane.showMessageDialog(dialog,
                            "Film bulunamadı!",
                            "Hata", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(dialog,
                        "Veritabanı hatası oluştu!",
                        "Hata", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(dialog,
                    "Lütfen bir film seçin!",
                    "Uyarı", JOptionPane.WARNING_MESSAGE);
            }
        });
        
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        mainPanel.add(new JScrollPane(movieList));
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        mainPanel.add(ratingPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        mainPanel.add(submitButton);
        

        dialog.add(mainPanel);
        dialog.setVisible(true);
    }
    
    private static BasicButtonUI createRoundedButtonUI1(Color buttonColor) {
        return new BasicButtonUI() {
            @Override
            public void paint(Graphics g, JComponent c) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                AbstractButton b = (AbstractButton) c;
                ButtonModel model = b.getModel();
                
                if (model.isPressed()) {
                    g2d.setColor(buttonColor.darker());
                } else {
                    g2d.setColor(buttonColor);
                }
                
                g2d.fillRoundRect(0, 0, c.getWidth(), c.getHeight(), 20, 20);
                super.paint(g, c);
                g2d.dispose();
            }
        };
    }
    
    private BasicButtonUI createRoundedButtonUI(Color buttonColor) {
        return new BasicButtonUI() {
            @Override
            public void paint(Graphics g, JComponent c) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                AbstractButton b = (AbstractButton) c;
                ButtonModel model = b.getModel();
                
                if (model.isPressed()) {
                    g2d.setColor(buttonColor.darker());
                } else {
                    g2d.setColor(buttonColor);
                }
                
                g2d.fillRoundRect(0, 0, c.getWidth(), c.getHeight(), 20, 20);
                super.paint(g, c);
                g2d.dispose();
            }
        };
    }
    
    private void showInviteDialog(DbHelper dbHelper,Kullanici kullanici ,int etkinlikId) {
        JDialog dialog = new JDialog(this, "Katılımcı Ekle", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(300, 400);
        
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(backgroundColor);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel titleLabel = new JLabel("Katılımcı Seç");
        titleLabel.setFont(new Font("Dialog", Font.BOLD, 16));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Veritabanından katılabilecek kişileri al
        ArrayList<String> availableUsers = new ArrayList<>();
        String query = """
            SELECT kullanici_adi 
            FROM kullanici 
            EXCEPT 
            SELECT davetli_id 
            FROM davetliler 
            WHERE etkinlik_id = ?
        """;
        
        try (PreparedStatement stmt = dbHelper.connection.prepareStatement(query)) {
            stmt.setInt(1, etkinlikId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                availableUsers.add(rs.getString("kullanici_adi"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Kullanıcı listesi alınırken hata oluştu: " + e.getMessage(),
                "Hata",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        JList<String> userList = new JList<>(availableUsers.toArray(new String[0]));
        userList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        JButton inviteButton = new JButton("Davet Et");
        inviteButton.setFont(new Font("Dialog", Font.BOLD, 14));
        inviteButton.setForeground(Color.WHITE);
        inviteButton.setBackground(accentColor);
        inviteButton.setBorderPainted(false);
        inviteButton.setFocusPainted(false);
        inviteButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        inviteButton.addActionListener(e -> {
            String selectedUser = userList.getSelectedValue();
            if (selectedUser != null) {
                try {
                    Etkinlik etkinlik = new Etkinlik();
                    etkinlik.inviteEvent(etkinlikId,kullanici.getKullanici_adi(), selectedUser, dbHelper);
                    JOptionPane.showMessageDialog(dialog,
                            "Kullanıcı başarıyla davet edildi!",
                            "Başarılı",
                            JOptionPane.INFORMATION_MESSAGE);
                        dialog.dispose();
                        //refreshParticipantsList(dbHelper, kullanici, etkinlikId);
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(dialog,
                        "Davet gönderilirken hata oluştu: " + ex.getMessage(),
                        "Hata",
                        JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(dialog,
                    "Lütfen bir kullanıcı seçin!",
                    "Uyarı",
                    JOptionPane.WARNING_MESSAGE);
            }
        });
        
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        mainPanel.add(new JScrollPane(userList));
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        mainPanel.add(inviteButton);
        
        dialog.add(mainPanel);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
    
    private void refreshParticipantsList(DbHelper db_helper,Kullanici kullanici,int etkinlikId) {
        // Mevcut paneli kaldır
        Container contentPane = getContentPane();
        Component[] components = ((JPanel)((JScrollPane)contentPane.getComponent(0)).getViewport().getView()).getComponents();
        for (Component component : components) {
            if (component instanceof JPanel && ((JPanel)component).getComponents().length > 0 
                && ((JPanel)component).getComponents()[0] instanceof JLabel 
                && ((JLabel)((JPanel)component).getComponents()[0]).getText().equals("Katılabilecek Kişiler")) {
                ((JPanel)((JScrollPane)contentPane.getComponent(0)).getViewport().getView()).remove(component);
                break;
            }
        }
        
        // Yeni panel ekle
        JPanel newParticipantsPanel = createParticipantsPanel(db_helper,kullanici,etkinlikId);
        ((JPanel)((JScrollPane)contentPane.getComponent(0)).getViewport().getView()).add(newParticipantsPanel, 3);
        
        // Ekranı yenile
        revalidate();
        repaint();
    }
    
    private static void showAllMovies(DbHelper db_helper, JPanel movieListPanel) {
        // Tüm filmleri gösteren bir liste oluştur
        String query = "SELECT isim FROM film";  // Film isimlerini sorgulayan SQL sorgusu

        try (PreparedStatement stmt = db_helper.connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String movieName = rs.getString("isim");
                JLabel movieLabel = new JLabel(movieName);
                movieLabel.setFont(new Font("Dialog", Font.PLAIN, 14));
                movieLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
                movieListPanel.add(movieLabel);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void showHighRatedMovies(DbHelper db_helper, JPanel movieListPanel) {
    	
    	
    	// Ortalama oy sayısından yüksek olan filmleri gösteren bir liste oluştur
    	String query = "SELECT * FROM ortalamaustufilmler()"; // PL/pgSQL fonksiyonunu çağırıyoruz

    	try (PreparedStatement stmt = db_helper.connection.prepareStatement(query)) {
    	    ResultSet rs = stmt.executeQuery();

    	    while (rs.next()) {
    	        String movieName = rs.getString("film_adi"); // Fonksiyondan dönen sütun adı "film_adi"
    	        JLabel movieLabel = new JLabel(movieName);
    	        movieLabel.setFont(new Font("Dialog", Font.PLAIN, 14));
    	        movieLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
    	        movieListPanel.add(movieLabel);
    	    }

    	} catch (SQLException e) {
    	    e.printStackTrace();
    	}
    }
} 