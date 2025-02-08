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

public class EventParticipantDetailScreen extends JFrame {
    private Color backgroundColor = Color.WHITE;
    private static Color accentColor = new Color(255, 89, 56);
    private static Color darkBackgroundColor = new Color(31, 37, 49);
    private static Color textColor = Color.BLACK;

    
    public EventParticipantDetailScreen(String movieName, String genre, String dateTime, String location, 
                           String currentParticipants, String maxParticipants, 
                           DbHelper dbHelper,Kullanici kullanici ,int etkinlikId) {
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
        JPanel participantsPanel = createParticipantsPanel(dbHelper,etkinlikId,kullanici);
        
        // Kapasite göstergesi ve butonlar
        JPanel bottomPanel = createBottomPanel(currentParticipants, maxParticipants,etkinlikId,dbHelper,kullanici);
        
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
    
    private JPanel createParticipantsPanel(DbHelper dbHelper,int etkinlikId,Kullanici kullanici) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(backgroundColor);
        
        JLabel titleLabel = new JLabel("Katılabilecek Kişiler");
        titleLabel.setFont(new Font("Dialog", Font.BOLD, 16));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(titleLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        
        // Veritabanından katılımcıları al
        ArrayList<String> participants = new ArrayList<>();
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
                participants.add(rs.getString("kullanici_adi"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Katılımcı listesi alınırken hata oluştu: " + e.getMessage(),
                "Hata",
                JOptionPane.ERROR_MESSAGE);
        }
        
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
        
        addParticipantButton.addActionListener(e -> {
            showInviteDialog(dbHelper, kullanici, etkinlikId);
        });
        
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(addParticipantButton);
        
        return panel;
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
                        refreshParticipantsList(dbHelper, kullanici, etkinlikId);
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
        JPanel newParticipantsPanel = createParticipantsPanel(db_helper,etkinlikId,kullanici);
        ((JPanel)((JScrollPane)contentPane.getComponent(0)).getViewport().getView()).add(newParticipantsPanel, 3);
        
        // Ekranı yenile
        revalidate();
        repaint();
    }
    
    private JPanel createBottomPanel(String currentParticipants, String maxParticipants,int etkinlikId,DbHelper db_helper,Kullanici kullanici) {
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
        
        // Etkinlikten ayrıl butonu
        JButton leaveEventButton = new JButton("Etkinlikten Ayrıl");
        leaveEventButton.setFont(new Font("Dialog", Font.BOLD, 14));
        leaveEventButton.setForeground(Color.WHITE);
        leaveEventButton.setBackground(Color.RED); // Kırmızı
        leaveEventButton.setBorderPainted(false);
        leaveEventButton.setFocusPainted(false);
        leaveEventButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        leaveEventButton.setPreferredSize(new Dimension(160, 40));
        
        // Etkinlikten ayrıl butonu tıklama olayı
        leaveEventButton.addActionListener(e -> {
            // TODO: Etkinlikten ayrılma işlemleri
        	Etkinlik etkinlik=new Etkinlik();
        	try {
				etkinlik.removeUser(etkinlikId, kullanici.getKullanici_adi(), db_helper);
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
            JOptionPane.showMessageDialog(this, "Etkinlikten ayrılma işlemi burada yapılacak.");
        });
        
        // Film oylama butonu
        JButton rateMovieButton = new JButton("Film Oyla");
        rateMovieButton.setFont(new Font("Dialog", Font.BOLD, 14));
        rateMovieButton.setForeground(Color.WHITE);
        rateMovieButton.setBackground(Color.BLUE); // Mavi
        rateMovieButton.setBorderPainted(false);
        rateMovieButton.setFocusPainted(false);
        rateMovieButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        rateMovieButton.setPreferredSize(new Dimension(160, 40));
        
        // Film oylama butonu tıklama olayı
        rateMovieButton.addActionListener(e -> {
            // Film oylama işlemleri
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
        
        // Butonları yan yana yerleştirmek için panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(backgroundColor);
        buttonPanel.add(leaveEventButton);
        buttonPanel.add(rateMovieButton);
        
        panel.add(capacityPanel);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        panel.add(buttonPanel);
        
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
        submitButton.setUI(createRoundedButtonUI(accentColor));
        
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
    
    private static BasicButtonUI createRoundedButtonUI(Color buttonColor) {
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