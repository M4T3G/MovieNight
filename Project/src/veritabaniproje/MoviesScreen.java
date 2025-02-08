package veritabaniproje;

import javax.swing.*;
import javax.swing.border.*;

import Type.DbHelper;
import Type.Film;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Arrays;

public class MoviesScreen extends JFrame {
    private Color backgroundColor = new Color(255, 89, 56);
    private Color darkBackgroundColor = new Color(31, 37, 49);
    private Color textColor = Color.WHITE;
    private JPanel moviesPanel;
    private JTextField searchField;
    private JComboBox<String> genreComboBox;
    private ArrayList<String[]> allMovies;
    private JButton currentSortButton; // Aktif sıralama butonunu takip etmek için
    private JPanel currentIndicator;
    
    public MoviesScreen(DbHelper db_helper) {
        setTitle("Filmler");
        setSize(500, 600);
        setType(Window.Type.UTILITY);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(backgroundColor);
        
        // Üst panel (Arama ve filtreleme)
        JPanel topPanel = createTopPanel(db_helper);
        add(topPanel, BorderLayout.NORTH);
        
        // Filmler paneli
        moviesPanel = new JPanel();
        moviesPanel.setLayout(new BoxLayout(moviesPanel, BoxLayout.Y_AXIS));
        moviesPanel.setBackground(backgroundColor);
        
        JScrollPane scrollPane = new JScrollPane(moviesPanel);
        scrollPane.setBackground(backgroundColor);
        scrollPane.setBorder(null);
        add(scrollPane, BorderLayout.CENTER);
        
        // Film verilerini başlat ve puana göre sırala
        initializeMovies(db_helper);
        sortByRating(); // Başlangıçta puana göre sırala
        displayMovies(allMovies);
        
        setLocationRelativeTo(null);
    }
    
    private JPanel createTopPanel(DbHelper db_helper) {
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setBackground(backgroundColor);
        topPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Başlık ve Film Ekle butonu
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(backgroundColor);
        
        JLabel titleLabel = new JLabel("Filmler");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(textColor);
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        JButton addMovieButton = createStyledButton("Film Ekle");
        addMovieButton.addActionListener(e -> {
            AddMovieScreen addMovieScreen = new AddMovieScreen(this, db_helper);
            setEnabled(false);
            addMovieScreen.setLocationRelativeTo(null);
            addMovieScreen.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    setEnabled(true);
                }
            });
            addMovieScreen.setVisible(true);
        });
        headerPanel.add(addMovieButton, BorderLayout.EAST);
        
        topPanel.add(headerPanel);
        topPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        // Arama alanı
        JPanel searchPanel = new JPanel(new BorderLayout(10, 0));
        searchPanel.setBackground(backgroundColor);
        
        searchField = createStyledTextField();
        searchField.setPreferredSize(new Dimension(200, 35));
        searchField.setMaximumSize(new Dimension(400, 35));
        
        JButton searchButton = createStyledButton("Ara");
        searchButton.addActionListener(e -> performSearch());
        
        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.add(searchButton, BorderLayout.EAST);
        
        topPanel.add(searchPanel);
        topPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        
        // Tür filtresi
        JPanel filterPanel = new JPanel(new BorderLayout(10, 0));
        filterPanel.setBackground(backgroundColor);
        
        JLabel filterLabel = new JLabel("Tür:");
        filterLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        filterLabel.setForeground(textColor);
        
        String[] genres = {"Tümü", "Bilim Kurgu", "Dram", "Komedi", "Aksiyon", "Macera"};
        genreComboBox = new JComboBox<>(genres);
        styleComboBox(genreComboBox);
        genreComboBox.addActionListener(e -> performSearch());
        
        filterPanel.add(filterLabel, BorderLayout.WEST);
        filterPanel.add(genreComboBox, BorderLayout.CENTER);
        
        topPanel.add(filterPanel);
        topPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        
        // Sıralama butonları paneli
        JPanel sortPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        sortPanel.setBackground(backgroundColor);
        
        // Her buton için ayrı panel oluştur
        JPanel nameSortPanel = createSortButtonPanel("İsme Göre Sırala", e -> sortByName());
        JPanel genreSortPanel = createSortButtonPanel("Türe Göre Sırala", e -> sortByGenre());
        JPanel ratingSortPanel = createSortButtonPanel("Puana Göre Sırala", e -> sortByRating());
        
        // İlk açılışta puana göre sıralı olduğu için bu paneldeki göstergeyi göster
        JPanel indicator = (JPanel) ratingSortPanel.getComponent(1);
        currentIndicator = indicator;
        indicator.setVisible(true);
        currentSortButton = (JButton) ratingSortPanel.getComponent(0);
        updateSortButtonState(currentSortButton);
        
        sortPanel.add(nameSortPanel);
        sortPanel.add(genreSortPanel);
        sortPanel.add(ratingSortPanel);
        
        topPanel.add(sortPanel);
        
        return topPanel;
    }
    
    private void initializeMovies(DbHelper db_helper) {
    	
    	Film film=new Film();
    	ArrayList<String> isimler=film.getFilmNames(db_helper);
    	ArrayList<String> turler=film.getFilmTypes(db_helper);
    	ArrayList<Double> puanlar=film.getFilmRatings(db_helper);
    	allMovies = new ArrayList<>();

    	// Dinamik olarak allMovies dizisini doldur
    	for (int i = 0; i < isimler.size(); i++) {
    	    String[] movieData = new String[3];
    	    movieData[0] = isimler.get(i); // Film adı
    	    movieData[1] = turler.get(i); // Film türü
    	    movieData[2] = String.valueOf(puanlar.get(i)); // Ortalama puan (Double'dan String'e çeviriliyor)
    	    allMovies.add(movieData);
    	}
    }
    
    private void performSearch() {
        String searchText = searchField.getText().toLowerCase();
        String selectedGenre = (String) genreComboBox.getSelectedItem();
        
        ArrayList<String[]> filteredMovies = new ArrayList<>();
        
        for (String[] movie : allMovies) {
            boolean matchesSearch = movie[0].toLowerCase().contains(searchText);
            boolean matchesGenre = selectedGenre.equals("Tümü") || movie[1].equals(selectedGenre);
            
            if (matchesSearch && matchesGenre) {
                filteredMovies.add(movie);
            }
        }
        
        displayMovies(filteredMovies);
    }
    
    private void displayMovies(ArrayList<String[]> movies) {
        moviesPanel.removeAll();
        moviesPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        
        for (String[] movie : movies) {
            addMovieCard(movie[0], movie[1], movie[2]);
            moviesPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        }
        
        moviesPanel.revalidate();
        moviesPanel.repaint();
    }
    
    private void addMovieCard(String title, String genre, String rating) {
        JPanel card = new JPanel();
        card.setLayout(new BorderLayout());
        card.setMaximumSize(new Dimension(460, 70));
        card.setPreferredSize(new Dimension(460, 70));
        card.setBackground(darkBackgroundColor);
        card.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        
        JPanel movieInfo = new JPanel(new GridLayout(2, 1, 0, 5));
        movieInfo.setBackground(darkBackgroundColor);
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(textColor);
        
        JLabel genreLabel = new JLabel(genre);
        genreLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        genreLabel.setForeground(Color.GRAY);
        
        movieInfo.add(titleLabel);
        movieInfo.add(genreLabel);
        
        // Puan paneli
        JPanel ratingPanel = new JPanel(new BorderLayout(5, 0));
        ratingPanel.setBackground(darkBackgroundColor);
        
        JLabel ratingLabel = new JLabel("Ort. Puan " + rating);
        ratingLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        ratingLabel.setForeground(textColor);
        
        ratingPanel.add(ratingLabel, BorderLayout.CENTER);
        
        card.add(movieInfo, BorderLayout.CENTER);
        card.add(ratingPanel, BorderLayout.EAST);
        
        moviesPanel.add(card);
    }
    
    private JTextField createStyledTextField() {
        JTextField field = new JTextField();
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
        comboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        comboBox.setBackground(darkBackgroundColor);
        comboBox.setForeground(textColor);
        comboBox.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(255, 255, 255, 30), 1),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)));
    }
    
    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setForeground(textColor);
        button.setBackground(darkBackgroundColor);
        button.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(255, 255, 255, 30), 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)));
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
    
    public void addNewMovie(String title, String genre, String rating) {
        allMovies.add(new String[]{title, genre, rating});
        performSearch(); // Listeyi güncelle
    }
    
    private void sortByRating() {
        allMovies.sort((a, b) -> {
            double ratingA = Double.parseDouble(a[2]);
            double ratingB = Double.parseDouble(b[2]);
            return Double.compare(ratingB, ratingA); // Büyükten küçüğe sıralama
        });
        performSearch(); // Listeyi güncelle
    }
    
    private void sortByName() {
        allMovies.sort((a, b) -> {
            return a[0].compareToIgnoreCase(b[0]); // Büyük/küçük harf duyarsız sıralama
        });
        performSearch(); // Listeyi güncelle
    }
    
    private void sortByGenre() {
        allMovies.sort((a, b) -> {
            int genreCompare = a[1].compareToIgnoreCase(b[1]);
            if (genreCompare == 0) {
                // Aynı türdeki filmleri isme göre sırala
                return a[0].compareToIgnoreCase(b[0]);
            }
            return genreCompare;
        });
        performSearch(); // Listeyi güncelle
    }
    
    private void updateSortButtonState(JButton clickedButton) {
        // Önceki aktif butonun rengini sıfırla
        if (currentSortButton != null) {
            currentSortButton.setBackground(darkBackgroundColor);
        }
        
        // Yeni butonu aktif yap
        clickedButton.setBackground(new Color(41, 47, 59));
        currentSortButton = clickedButton;
    }
    
    private JPanel createSortButtonPanel(String buttonText, ActionListener action) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(backgroundColor);

        JButton button = createStyledButton(buttonText);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Nokta göstergesi paneli
        JPanel indicatorPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                if (isVisible()) {
                    g2d.setColor(textColor);
                    int diameter = 6;
                    int x = getWidth() / 2 - diameter / 2;
                    int y = 0;
                    g2d.fillOval(x, y, diameter, diameter);
                }
            }
        };
        indicatorPanel.setBackground(backgroundColor);
        indicatorPanel.setPreferredSize(new Dimension(button.getPreferredSize().width, 8));
        indicatorPanel.setMaximumSize(new Dimension(button.getPreferredSize().width, 8));
        indicatorPanel.setVisible(false); // Başlangıçta gizli
        
        button.addActionListener(e -> {
            action.actionPerformed(e);
            updateSortButtonState(button);
            updateIndicatorVisibility(indicatorPanel);
        });
        
        panel.add(button);
        panel.add(indicatorPanel);
        
        return panel;
    }
    
    private void updateIndicatorVisibility(JPanel newIndicator) {
        if (currentIndicator != null) {
            currentIndicator.setVisible(false);
        }
        newIndicator.setVisible(true);
        currentIndicator = newIndicator;
    }
} 