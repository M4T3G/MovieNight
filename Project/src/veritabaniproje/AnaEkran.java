package veritabaniproje;

import javax.swing.*;
import javax.swing.plaf.basic.BasicButtonUI;
import Type.DbHelper;
import Type.Film;
import Type.KatilimIstegi;
import Type.Kullanici;
import java.awt.*;
import java.awt.event.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

public class AnaEkran extends JFrame {
    private JLabel nameLabel;
    private JLabel popularMoviesLabel;
    private JPanel moviesPanel;

    public AnaEkran(Kullanici kullanici, DbHelper db_helper) {
        // Frame ayarları
        setTitle("Film Uygulaması");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 600);
        setLayout(new BorderLayout());
        getContentPane().setBackground(Color.WHITE);

        // Üst panel
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.WHITE);

        // Bildirim ikonu
        JButton notificationButton = new JButton("🔔");
        notificationButton.setFont(new Font("Dialog", Font.PLAIN, 20));
        notificationButton.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        notificationButton.setBackground(Color.WHITE);
        notificationButton.setBorderPainted(false);
        notificationButton.setFocusPainted(false);
        notificationButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        notificationButton.addActionListener(e -> {
            JPopupMenu notifications = new JPopupMenu();
            notifications.setBackground(new Color(31, 37, 49));

            // Örnek veritabanı sorguları
            List<KatilimIstegi> KatilimIstegis = getKatilimIstegisForUser(kullanici.getKullanici_adi(), db_helper); // Kullanıcının katılım isteklerini al
            List<KatilimIstegi> invitationRequests = getInvitationRequestsForUser(kullanici.getKullanici_adi(), db_helper); // Kullanıcının davetlerini al

            // Katılım isteklerini göster
            for (KatilimIstegi request : KatilimIstegis) {
                JPanel notificationPanel = new JPanel();
                notificationPanel.setLayout(new BorderLayout());
                notificationPanel.setBackground(new Color(31, 37, 49));

                String notificationText = request.getKatilmak_isteyen_id() + " etkinliğe katılmak istiyor.";
                JLabel notificationLabel = new JLabel(notificationText);
                notificationLabel.setForeground(Color.WHITE);
                notificationPanel.add(notificationLabel, BorderLayout.CENTER);

                JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
                buttonPanel.setBackground(new Color(31, 37, 49));

                JButton acceptButton = new JButton("Kabul Et");
                JButton rejectButton = new JButton("Reddet");

                acceptButton.setBackground(new Color(0, 128, 0));
                acceptButton.setForeground(Color.WHITE);
                rejectButton.setBackground(new Color(128, 0, 0));
                rejectButton.setForeground(Color.WHITE);

                acceptButton.addActionListener(event -> {
                    // Katılım isteği kabul edildi
                    acceptKatilimIstegi(request.getEtkinlik_id(), kullanici.getKullanici_adi(), db_helper); // Veritabanında katılım isteğini kabul et
                    JOptionPane.showMessageDialog(null, notificationText + " kabul edildi.");
                    notifications.setVisible(false);
                });

                rejectButton.addActionListener(event -> {
                    // Katılım isteği reddedildi
                    rejectKatilimIstegi(request.getEtkinlik_id(), kullanici.getKullanici_adi(), db_helper); // Veritabanında katılım isteğini reddet
                    JOptionPane.showMessageDialog(null, notificationText + " reddedildi.");
                    notifications.setVisible(false);
                });

                buttonPanel.add(acceptButton);
                buttonPanel.add(rejectButton);

                notificationPanel.add(buttonPanel, BorderLayout.EAST);
                notifications.add(notificationPanel);
            }

            // Davet isteklerini göster
            for (KatilimIstegi invitation : invitationRequests) {
                JPanel notificationPanel = new JPanel();
                notificationPanel.setLayout(new BorderLayout());
                notificationPanel.setBackground(new Color(31, 37, 49));

                String notificationText = invitation.getKatilmak_isteyen_id() + " sizi davet etti.";
                JLabel notificationLabel = new JLabel(notificationText);
                notificationLabel.setForeground(Color.WHITE);
                notificationPanel.add(notificationLabel, BorderLayout.CENTER);

                JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
                buttonPanel.setBackground(new Color(31, 37, 49));

                JButton acceptButton = new JButton("Kabul Et");
                JButton rejectButton = new JButton("Reddet");

                acceptButton.setBackground(new Color(0, 128, 0));
                acceptButton.setForeground(Color.WHITE);
                rejectButton.setBackground(new Color(128, 0, 0));
                rejectButton.setForeground(Color.WHITE);

                acceptButton.addActionListener(event -> {
                    // Davet kabul edildi
                    acceptDavetIstegi(invitation.getEtkinlik_id(), kullanici.getKullanici_adi(), db_helper); // Daveti kabul et
                    JOptionPane.showMessageDialog(null, notificationText + " kabul edildi.");
                    notifications.setVisible(false);
                });

                rejectButton.addActionListener(event -> {
                    // Davet reddedildi
                    rejectDavetIstegi(invitation.getEtkinlik_id(), kullanici.getKullanici_adi(), db_helper); // Daveti reddet
                    JOptionPane.showMessageDialog(null, notificationText + " reddedildi.");
                    notifications.setVisible(false);
                });

                buttonPanel.add(acceptButton);
                buttonPanel.add(rejectButton);

                notificationPanel.add(buttonPanel, BorderLayout.EAST);
                notifications.add(notificationPanel);
            }

            // Bildirim menüsünü göster
            notifications.show(notificationButton, 0, notificationButton.getHeight());
        }); // Kapanış parantezi burada

        topPanel.add(notificationButton, BorderLayout.EAST);

        // Kullanıcı adı
        JPanel namePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(255, 89, 56)); // Turuncu arka plan rengi
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30); // Oval şekil
                g2d.dispose();
            }
        };
        namePanel.setLayout(new BorderLayout());
        namePanel.setOpaque(false);
        namePanel.setPreferredSize(new Dimension(200, 50));
        namePanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));

        nameLabel = new JLabel(kullanici.getIlk_isim() + " " + kullanici.getSon_isim());
        nameLabel.setFont(new Font("Courier New", Font.BOLD, 18));
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setHorizontalAlignment(SwingConstants.CENTER);
        nameLabel.setVerticalAlignment(SwingConstants.CENTER);
        nameLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Mouse listener ekle
        nameLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Favori film bilgisi dinamik olarak alınıyor
                UserProfileScreen profileScreen = new UserProfileScreen(
                    kullanici,db_helper
                );
                setEnabled(false);
                profileScreen.setLocationRelativeTo(null);
                profileScreen.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosed(WindowEvent e) {
                        setEnabled(true);
                    }
                });
                profileScreen.setVisible(true);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                nameLabel.setForeground(new Color(220, 220, 220));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                nameLabel.setForeground(Color.WHITE);
            }
        });

        namePanel.add(nameLabel, BorderLayout.CENTER);
        topPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 0, 0));
        topPanel.add(namePanel, BorderLayout.WEST);

        add(topPanel, BorderLayout.NORTH);

        // Popüler filmler başlığı
        popularMoviesLabel = new JLabel("En Popüler 5 Film");
        popularMoviesLabel.setFont(new Font("Courier New", Font.BOLD, 18));
        popularMoviesLabel.setForeground(Color.BLACK);
        popularMoviesLabel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        
        // Filmler paneli
        moviesPanel = new JPanel();
        moviesPanel.setLayout(new BoxLayout(moviesPanel, BoxLayout.Y_AXIS));
        moviesPanel.setBackground(Color.WHITE);
        
        Film film=new Film();
        
        try {
            List<Map<String, Object>> topFilms = film.getTop5Films(db_helper);

            // Gelen verileri movies dizisine aktar
            String[][] movies = new String[topFilms.size()][3];
            for (int i = 0; i < topFilms.size(); i++) {
                Map<String, Object> filmData = topFilms.get(i);
                movies[i][0] = (String) filmData.get("film_adi"); // Film adı
                movies[i][1] = (String) filmData.get("tur");      // Tür
                movies[i][2] = String.valueOf(filmData.get("ortalama_puan")); // Ortalama puan
            }

            // Movies dizisini ekrana yazdır
            for (int i = 0; i < movies.length; i++) {
                addMovieCard(movies[i][0], movies[i][1], movies[i][2], i + 1);
            }

        } catch (SQLException e1) {
            e1.printStackTrace();
            JOptionPane.showMessageDialog(null, 
                "Film bilgileri alınırken bir hata oluştu.", 
                "Hata", JOptionPane.ERROR_MESSAGE);
        }
        
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(Color.WHITE);
        contentPanel.add(popularMoviesLabel, BorderLayout.NORTH);
        contentPanel.add(moviesPanel, BorderLayout.CENTER);
        
        add(contentPanel, BorderLayout.CENTER);
        
        // Alt menü
        JPanel bottomMenu = createBottomMenu(kullanici, db_helper);
        add(bottomMenu, BorderLayout.SOUTH);
    }
    
    private void addMovieCard(String title, String genre, String rating, int rank) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(31, 37, 49));
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20); // 20 piksel yarıçaplı oval köşeler
                g2d.dispose();
            }
        };
        card.setLayout(new BorderLayout());
        card.setMaximumSize(new Dimension(380, 70));
        card.setPreferredSize(new Dimension(380, 70));
        card.setBackground(new Color(31, 37, 49));
        card.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        card.setOpaque(false); // Arka planın şeffaf olması için gerekli
        
        JLabel rankLabel = new JLabel(String.valueOf(rank));
        rankLabel.setFont(new Font("Dialog", Font.BOLD, 24));
        rankLabel.setForeground(Color.WHITE);
        rankLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 20));
        
        JPanel movieInfo = new JPanel(new GridLayout(2, 1, 0, 5));
        movieInfo.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        movieInfo.setBackground(new Color(31, 37, 49));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Dialog", Font.BOLD, 16));
        titleLabel.setForeground(Color.WHITE);
        
        JLabel genreLabel = new JLabel(genre);
        genreLabel.setFont(new Font("Dialog", Font.PLAIN, 14));
        genreLabel.setForeground(Color.GRAY);
        
        movieInfo.add(titleLabel);
        movieInfo.add(genreLabel);
        
        JLabel ratingLabel = new JLabel("Ort. Puan " + rating);
        ratingLabel.setFont(new Font("Dialog", Font.PLAIN, 14));
        ratingLabel.setForeground(Color.WHITE);
        
        card.add(rankLabel, BorderLayout.WEST);
        card.add(movieInfo, BorderLayout.CENTER);
        card.add(ratingLabel, BorderLayout.EAST);
        
        moviesPanel.add(card);
        moviesPanel.add(Box.createRigidArea(new Dimension(0, 10)));
    }
    
    private JPanel createBottomMenu(Kullanici kullanici, DbHelper db_helper) {
        JPanel bottomMenu = new JPanel(new GridLayout(1, 3));
        bottomMenu.setBackground(new Color(255, 89, 56));
        
        JButton moviesButton = new JButton("🎬");
        moviesButton.addActionListener(e -> {
            MoviesScreen moviesScreen = new MoviesScreen(db_helper);
            setEnabled(false);
            moviesScreen.setLocationRelativeTo(null);
            moviesScreen.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    setEnabled(true);
                }
            });
            moviesScreen.setVisible(true);
        });
        JButton addButton = new JButton("➕");
        addButton.addActionListener(e -> {
            AddEventScreen addEventScreen = new AddEventScreen(kullanici,db_helper);
            setEnabled(false);
            addEventScreen.setLocationRelativeTo(null);
            addEventScreen.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    setEnabled(true);
                }
            });
            addEventScreen.setVisible(true);
        });
        JButton calendarButton = new JButton("📅");
        calendarButton.addActionListener(e -> {
            EventsScreen eventsScreen = new EventsScreen(kullanici,db_helper);
            setEnabled(false);
            eventsScreen.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    setEnabled(true);
                }
            });
            eventsScreen.setVisible(true);
        });
        
        JButton[] buttons = {moviesButton, addButton, calendarButton};
        
        for (JButton button : buttons) {
            button.setFont(new Font("Dialog", Font.PLAIN, 20));
            button.setForeground(Color.WHITE);
            button.setBackground(new Color(255, 89, 56));
            button.setBorderPainted(false);
            button.setFocusPainted(false);
            
            // Oval görünüm için özel UI
            button.setUI(new BasicButtonUI() {
                public void paint(Graphics g, JComponent c) {
                    Graphics2D g2d = (Graphics2D) g.create();
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    
                    AbstractButton b = (AbstractButton) c;
                    ButtonModel model = b.getModel();
                    
                    if (model.isPressed()) {
                        g2d.setColor(new Color(235, 69, 36)); // Basıldığında biraz daha koyu
                    } else {
                        g2d.setColor(b.getBackground());
                    }
                    
                    g2d.fillRoundRect(0, 0, c.getWidth(), c.getHeight(), 20, 20);
                    super.paint(g, c);
                    g2d.dispose();
                }
            });
            
            bottomMenu.add(button);
        }
        
        return bottomMenu;
    }
    public void acceptKatilimIstegi(int eventId, String userId,DbHelper dbhelper) {
        String deleteQuery = "DELETE FROM KatilimIstegi WHERE etkinlik_id = ? AND katilmak_isteyen_id = ?";
        try (PreparedStatement stmt = dbhelper.connection.prepareStatement(deleteQuery)) {
            stmt.setInt(1, eventId);
            stmt.setString(2, userId);
            stmt.executeUpdate();
            
            // Davetli tablosuna ekleme işlemi burada yapılabilir
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void rejectKatilimIstegi(int eventId, String userId,DbHelper dbhelper) {
        String deleteQuery = "DELETE FROM KatilimIstegi WHERE etkinlik_id = ? AND katilmak_isteyen_id = ?";
        try (PreparedStatement stmt = dbhelper.connection.prepareStatement(deleteQuery)) {
            stmt.setInt(1, eventId);
            stmt.setString(2, userId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void acceptDavetIstegi(int eventId, String userId,DbHelper dbhelper) {
        String deleteQuery = "DELETE FROM DavetIstegi WHERE etkinlik_id = ? AND davet_edilen_id = ?";
        try (PreparedStatement stmt = dbhelper.connection.prepareStatement(deleteQuery)) {
            stmt.setInt(1, eventId);
            stmt.setString(2, userId);
            stmt.executeUpdate();
            
            // Davetli tablosuna ekleme işlemi burada yapılabilir
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public void rejectDavetIstegi(int eventId, String userId,DbHelper dbhelper) {
        String deleteQuery = "DELETE FROM DavetIstegi WHERE etkinlik_id = ? AND davet_edilen_id = ?";
        try (PreparedStatement stmt = dbhelper.connection.prepareStatement(deleteQuery)) {
            stmt.setInt(1, eventId);
            stmt.setString(2, userId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public List<KatilimIstegi> getKatilimIstegisForUser(String userId,DbHelper dbhelper) {
        List<KatilimIstegi> requests = new ArrayList<>();
        String query = "SELECT katilmak_isteyen_id, etkinlik_id FROM KatilimIstegi WHERE katilmak_isteyen_id = ?";
        
        try (PreparedStatement stmt = dbhelper.connection.prepareStatement(query)) {
            stmt.setString(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                 
                int eventId = rs.getInt("etkinlik_id");
                requests.add(new KatilimIstegi(eventId, userId)); // false, katılım isteği olduğunu belirtir
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return requests;
    }
    
    public List<KatilimIstegi> getInvitationRequestsForUser(String userId,DbHelper dbhelper) {
        List<KatilimIstegi> requests = new ArrayList<>();
        String query = "SELECT davet_eden_id, etkinlik_id FROM DavetIstegi WHERE davet_edilen_id = ?";
        
        try (PreparedStatement stmt = dbhelper.connection.prepareStatement(query)) {
            stmt.setString(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                 // Davet edenin kullanıcı adını almak için bir metot
                int eventId = rs.getInt("etkinlik_id");
                requests.add(new KatilimIstegi(eventId, userId)); // true, davet isteği olduğunu belirtir
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return requests;
    }
    
    
    
    
    
}

