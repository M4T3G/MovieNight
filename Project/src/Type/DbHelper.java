package Type;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DbHelper {

    private String url;
    private String user;
    private String password;
    public Connection connection;

    public DbHelper(String url, String user, String password) {
        this.url = url;
        this.user = user;
        this.password = password;
        try {
            // Bağlantıyı oluşturuyoruz
        	this.connection = DriverManager.getConnection(url, user, password);
            System.out.println("Veritabanına başarıyla bağlanıldı.");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Veritabanı bağlantısı başarısız.");
        }
    }
    
    public void addFilm(String isim, String tur) {
        String sql = "INSERT INTO film (isim, tur) VALUES (?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            // Set parameters for the query
            pstmt.setString(1, isim);
            pstmt.setString(2, tur);

            // Execute the update
            pstmt.executeUpdate();
            System.out.println("Film başarıyla eklendi: " + isim);
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Film ekleme başarısız.");
        }
    }
    
    public void addUser(String kullaniciAdi, String ilkIsim, String sonIsim, int yas, String sifre) throws SQLException {
        String insertQuery = "INSERT INTO kullanici (kullanici_adi, ilk_isim, son_isim, yas, sifre) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
            // Parametreleri ayarla
            preparedStatement.setString(1, kullaniciAdi);
            preparedStatement.setString(2, ilkIsim);
            preparedStatement.setString(3, sonIsim);
            preparedStatement.setInt(4, yas);
            preparedStatement.setString(5, sifre);

            // Sorguyu çalıştır
            int rowsAffected = preparedStatement.executeUpdate();
            System.out.println(rowsAffected + " row(s) inserted into kullanici.");
        } catch (SQLException e) {
            e.printStackTrace();
            throw e; // İstisnayı tekrar fırlat, böylece bu metodu çağıran yerde ele alınabilir
        }
    }

    public List<String> getUsernames() {
        List<String> usernames = new ArrayList<>();

        try {
            Statement statement = connection.createStatement();
            String sqlQuery = "SELECT kullanici_adi FROM kullanici";
            ResultSet resultSet = statement.executeQuery(sqlQuery);

            while (resultSet.next()) {
                String kullanici_adi = resultSet.getString("kullanici_adi");
                usernames.add(kullanici_adi);
            }

            resultSet.close();
            statement.close();
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return usernames;
    }
    
    public List<String> getPasswords() {
        List<String> passwords = new ArrayList<>();

        try {
            Statement statement = connection.createStatement();
            String sqlQuery = "SELECT sifre FROM kullanici";
            ResultSet resultSet = statement.executeQuery(sqlQuery);

            while (resultSet.next()) {
            	String password = resultSet.getString("sifre");
                passwords.add(password);
            }

            resultSet.close();
            statement.close();
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return passwords;
    } 
    
    public boolean authenticateUser(String kullaniciAdi, String sifre) throws SQLException {
        // Girişin doğru olup olmadığını kontrol etmek için boolean
        boolean isAuthenticated = false;

        // Kullanıcı adı ve şifreyi kontrol eden SQL sorgusu
        String selectQuery = "SELECT * FROM kullanici WHERE kullanici_adi = ? AND sifre = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(selectQuery)) {
            // Parametreleri sorguya ekle
            preparedStatement.setString(1, kullaniciAdi);
            preparedStatement.setString(2, sifre);

            // Sorguyu çalıştır ve sonucu al
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                // Eğer kayıt bulunduysa doğrulama başarılı
                if (resultSet.next()) {
                    isAuthenticated = true;
                    System.out.println("Giriş başarılı. Hoş geldiniz, " + kullaniciAdi + "!");
                } else {
                    // Eğer kayıt bulunamazsa hata ver
                    System.out.println("Kullanıcı adı veya şifre hatalı.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }

        return isAuthenticated;
    }

    
    public boolean changePassword(String kullaniciAdi, String sifre, String yeniSifre, String yeniSifreTekrar) throws SQLException {
        // Şifre değiştirme işleminin başarılı olup olmadığını döndürmek için boolean
        boolean isPasswordChanged = false;

        // Kullanıcı adı ve mevcut şifre kontrol sorgusu
        String selectQuery = "SELECT sifre FROM kullanici WHERE kullanici_adi = ?";
        // Şifre güncelleme sorgusu
        String updateQuery = "UPDATE kullanici SET sifre = ? WHERE kullanici_adi = ?";

        try (PreparedStatement selectStmt = connection.prepareStatement(selectQuery)) {
            // Kullanıcı adını sorguya ekle
            selectStmt.setString(1, kullaniciAdi);

            try (ResultSet resultSet = selectStmt.executeQuery()) {
                // Kullanıcı bulunursa
                if (resultSet.next()) {
                    String mevcutSifre = resultSet.getString("sifre");

                    // Mevcut şifre kontrolü
                    if (mevcutSifre.equals(sifre)) {
                        // Yeni şifrelerin eşleşip eşleşmediğini kontrol et
                        if (yeniSifre.equals(yeniSifreTekrar)) {
                            try (PreparedStatement updateStmt = connection.prepareStatement(updateQuery)) {
                                // Yeni şifreyi güncelle
                                updateStmt.setString(1, yeniSifre);
                                updateStmt.setString(2, kullaniciAdi);

                                int rowsAffected = updateStmt.executeUpdate();
                                if (rowsAffected > 0) {
                                    isPasswordChanged = true;
                                    System.out.println("Şifre başarıyla değiştirildi.");
                                }
                            }
                        } else {
                            System.out.println("Yeni şifreler birbiriyle uyuşmuyor.");
                        }
                    } else {
                        System.out.println("Mevcut şifre yanlış.");
                    }
                } 
                
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }

        return isPasswordChanged;
    }

    
    public List<Object> getUserInfoByCredentials(String kullaniciAdi, String sifre) {
        List<Object> userInfo = new ArrayList<>();

        String query = "SELECT kullanici_adi, ilk_isim, son_isim, yas, sifre " +
                       "FROM kullanici " +
                       "WHERE kullanici_adi = ? AND sifre = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            // Parametreleri ayarla
            preparedStatement.setString(1, kullaniciAdi);
            preparedStatement.setString(2, sifre);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                // Sonuçları diziye ekle
                userInfo.add(resultSet.getString("kullanici_adi")); // 0. indeks
                userInfo.add(resultSet.getString("ilk_isim"));      // 1. indeks
                userInfo.add(resultSet.getString("son_isim"));      // 2. indeks
                userInfo.add(resultSet.getInt("yas"));              // 3. indeks
                userInfo.add(resultSet.getString("sifre"));         // 4. indeks
            }

            resultSet.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return userInfo; // Eğer kullanıcı bulunamazsa boş bir liste döner
    }

    
    public void inviteEvent(int etkinlik_id, String davet_eden_id, String davet_edilen_id) throws SQLException {
        String insertQuery = "INSERT INTO davetistegi (etkinlik_id, davet_eden_id, davet_edilen_id) " +
                             "VALUES (?, ?, ?)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
            // Parametreleri ayarla
            preparedStatement.setInt(1, etkinlik_id);
            preparedStatement.setString(2, davet_eden_id);
            preparedStatement.setString(3, davet_edilen_id);

            // Sorguyu çalıştır
            int rowsAffected = preparedStatement.executeUpdate();
            System.out.println(rowsAffected + " row(s) inserted into davetistegi.");
        } catch (SQLException e) {
            e.printStackTrace();
            throw e; // İstisnayı tekrar fırlat, böylece bu metodu çağıran yerde ele alınabilir
        }
    }
    
    public void deleteInvite(int etkinlik_id, String davet_eden_id, String davet_edilen_id) throws SQLException {
        String deleteQuery = "DELETE FROM davetistegi WHERE etkinlik_id = ? AND davet_eden_id = ? AND davet_edilen_id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(deleteQuery)) {
            // Parametreleri ayarla
            preparedStatement.setInt(1, etkinlik_id);
            preparedStatement.setString(2, davet_eden_id);
            preparedStatement.setString(3, davet_edilen_id);

            // Sorguyu çalıştır
            int rowsAffected = preparedStatement.executeUpdate();
            System.out.println(rowsAffected + " row(s) deleted from davetistegi.");
        } catch (SQLException e) {
            e.printStackTrace();
            throw e; // İstisnayı tekrar fırlat, böylece bu metodu çağıran yerde ele alınabilir
        }
    }
   
    
    public void attendanceRequest(int etkinlik_id, String katilmak_isteyen_id) throws SQLException {
        String insertQuery = "INSERT INTO katilimistegi (etkinlik_id, katilmak_isteyen_id) " +
                             "VALUES (?, ?)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
            // Parametreleri ayarla
            preparedStatement.setInt(1, etkinlik_id);
            preparedStatement.setString(2, katilmak_isteyen_id);

            // Sorguyu çalıştır
            int rowsAffected = preparedStatement.executeUpdate();
            System.out.println(rowsAffected + " row(s) inserted into katilimistegi.");
        } catch (SQLException e) {
            e.printStackTrace();
            throw e; // İstisnayı tekrar fırlat, böylece bu metodu çağıran yerde ele alınabilir
        }
    }
    
    public void deleteAttendanceRequest(int etkinlik_id, String katilmak_isteyen_id) throws SQLException {
        String deleteQuery = "DELETE FROM katilimistegi WHERE etkinlik_id = ? AND katilmak_isteyen_id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(deleteQuery)) {
            // Parametreleri ayarla
            preparedStatement.setInt(1, etkinlik_id);
            preparedStatement.setString(2, katilmak_isteyen_id);

            // Sorguyu çalıştır
            int rowsAffected = preparedStatement.executeUpdate();
            System.out.println(rowsAffected + " row(s) deleted from katilimistegi.");
        } catch (SQLException e) {
            e.printStackTrace();
            throw e; // İstisnayı tekrar fırlat, böylece bu metodu çağıran yerde ele alınabilir
        }
    }

    
    public void removeUser(int etkinlik_id, String kullanici_adi) throws SQLException {
        String deleteQuery = "DELETE FROM davetliler " +
                             "WHERE etkinlik_id = ? AND davetli_id = ? ";

        try (PreparedStatement preparedStatement = connection.prepareStatement(deleteQuery)) {
            // Parametreleri ayarla
            preparedStatement.setInt(1, etkinlik_id);
            preparedStatement.setString(2, kullanici_adi);

            // Sorguyu çalıştır
            int rowsAffected = preparedStatement.executeUpdate();
            System.out.println(rowsAffected + " row(s) deleted from davetliler.");
        } catch (SQLException e) {
            e.printStackTrace();
            throw e; // İstisnayı tekrar fırlat, böylece bu metodu çağıran yerde ele alınabilir
        }
    }
    
    public void addParticipant(String kullaniciAdi, int etkinlikId) throws SQLException {
        String insertQuery = "INSERT INTO davetliler (etkinlik_id, davetli_id) VALUES (?, ?)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
            // Parametreleri ayarla
            preparedStatement.setInt(1, etkinlikId);
            preparedStatement.setString(2, kullaniciAdi);

            // Sorguyu çalıştır
            int rowsAffected = preparedStatement.executeUpdate();
            System.out.println(rowsAffected + " row(s) inserted into davetliler.");
        } catch (SQLException e) {
            e.printStackTrace();
            throw e; // İstisnayı tekrar fırlat, böylece bu metodu çağıran yerde ele alınabilir
        }
    }
    
    public void voteFilm(int etkinlik_id, int film_id, String oyVeren, int verdigiOy) throws SQLException {
        // Geçerli oy değeri kontrolü (1-10 arasında olmalı)
        if (verdigiOy < 1 || verdigiOy > 10) {
            throw new SQLException("Geçersiz oy değeri! Lütfen 1 ile 10 arasında bir değer girin.");
        }

        // SQL sorgusu
        String insertQuery = "INSERT INTO oy (etkinlik_id, film_id, oy_veren, verdigi_oy) VALUES (?, ?, ?, ?)";

        // Veritabanına bağlanarak oy verme işlemi
        try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {

            // Parametreleri ayarla
            preparedStatement.setInt(1, etkinlik_id);
            preparedStatement.setInt(2, film_id);
            preparedStatement.setString(3, oyVeren);
            preparedStatement.setInt(4, verdigiOy);

            // Sorguyu çalıştır
            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Oy başarıyla verildi.");
            } else {
                System.out.println("Oy verme işlemi başarısız.");
            }
        } catch (SQLException e) {
            // Hata mesajını yazdır ve istisnayı tekrar fırlat
            System.err.println("Hata: " + e.getMessage());
            throw e;
        }
    }

 // En yüksek 5 film ve ortalama puanını almak için fonksiyon
    public List<Map<String, Object>> getTop5Films() throws SQLException {
        List<Map<String, Object>> topFilms = new ArrayList<>();

        // SQL fonksiyon çağrısı
        String sqlQuery = "SELECT * FROM enyuksek5film()";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            // Sonuçları işleyin
            while (resultSet.next()) {
                Map<String, Object> filmData = new HashMap<>();
                filmData.put("film_adi", resultSet.getString("film_adi"));
                filmData.put("ortalama_puan", resultSet.getDouble("ortalama_puan"));
                filmData.put("tur", resultSet.getString("tur")); // Eğer tür sütunu varsa eklenir
                topFilms.add(filmData);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }

        return topFilms;
    }
    
    
    // Arama fonksiyonunu çağırmak için fonksiyon
    public List<Map<String, Object>> searchFilmByName(String arama) throws SQLException {
        List<Map<String, Object>> films = new ArrayList<>();

        // SQL fonksiyon çağrısı
        String sqlQuery = "{ ? = call arananfilm(?) }";

        try (CallableStatement callableStatement = connection.prepareCall(sqlQuery)) {

            // Çıkış parametreleri
            callableStatement.registerOutParameter(1, Types.OTHER); // RETURNS TABLE olduğu için 'OTHER' tipi kullanılır
            callableStatement.setString(2, arama); // arama parametresi

            // Fonksiyonu çalıştır
            callableStatement.execute();

            // Sonuçları almak için ResultSet
            ResultSet resultSet = (ResultSet) callableStatement.getObject(1);

            // Sonuçları işleyin
            while (resultSet.next()) {
                Map<String, Object> filmData = new HashMap<>();
                filmData.put("film_adi", resultSet.getString("film_adi"));
                filmData.put("ortalama_puan", resultSet.getDouble("ortalama_puan"));
                films.add(filmData);
            }

            resultSet.close();
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }

        return films;
    }
    
    
    // Katılımcı sayısını bulma fonksiyonu
    public int getParticipantNumber(int etkinlikId) {
        int katilankisisayisi = 0;
        
        try {
            // PostgreSQL fonksiyonunu çağırma
            CallableStatement callableStatement = connection.prepareCall("{ ? = call katilankisisayisibul(?) }");
            
            // Parametreleri ayarlama
            callableStatement.registerOutParameter(1, Types.INTEGER); // Çıktı parametresi
            callableStatement.setInt(2, etkinlikId); // Girdi parametresi (etkinlik_id)
            
            // Fonksiyonu çalıştırma
            callableStatement.execute();
            
            // Sonuçları almak
            katilankisisayisi = callableStatement.getInt(1);
            
            callableStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return katilankisisayisi;
    }

 // Kullanıcının favori filmini bulma fonksiyonu
    public String getFavoriteFilm(String kisininid) {
        String favoriFilm = null;

        try {
            // PostgreSQL fonksiyonunu çağırma
            CallableStatement callableStatement = connection.prepareCall("{ ? = call kisininfavorifilmi(?) }");

            // Parametreleri ayarlama
            callableStatement.registerOutParameter(1, Types.VARCHAR); // Çıktı parametresi
            callableStatement.setString(2, kisininid); // Girdi parametresi (kisininid)

            // Fonksiyonu çalıştırma
            callableStatement.execute();

            // Sonuçları almak
            favoriFilm = callableStatement.getString(1);

            callableStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return favoriFilm;
    }
    
    public List<String> getFilmsByType(String tur) {
        List<String> filmList = new ArrayList<>();

        try {
            // PostgreSQL fonksiyonunu çağırma
            CallableStatement callableStatement = connection.prepareCall("{ ? = call turfiltrele(?) }");

            // Parametreleri ayarlama
            callableStatement.registerOutParameter(1, Types.OTHER); // Çıktı parametresi
            callableStatement.setString(2, tur); // Girdi parametresi (film türü)

            // Fonksiyonu çalıştırma
            callableStatement.execute();

            // Sonuçları almak
            ResultSet resultSet = (ResultSet) callableStatement.getObject(1);

            while (resultSet.next()) {
                filmList.add(resultSet.getString("film_adi"));
            }

            resultSet.close();
            callableStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return filmList;
    }
    
    // PostgreSQL fonksiyonunu çağırarak film isimlerini ve türlerini almak
    public List<List<String>> getOrderedFilm() {
        List<String> filmNames = new ArrayList<>();
        List<String> filmTypes = new ArrayList<>();

        try {
            // PostgreSQL fonksiyonunu çağırma
            String query = "{ ? = call alfabetikfilmler() }";  // CallableStatement ile fonksiyonu çağırıyoruz.
            CallableStatement callableStatement = connection.prepareCall(query);

            // Çıktı parametresini ayarla
            callableStatement.registerOutParameter(1, Types.OTHER);  // Çıktı parametresi (tablo tipi)
            callableStatement.execute();

            // Sonuçları almak
            ResultSet resultSet = (ResultSet) callableStatement.getObject(1);

            while (resultSet.next()) {
                String filmAdi = resultSet.getString("film_adi");
                String filmTur = resultSet.getString("tur");

                // Sonuçları ilgili listelere ekliyoruz
                filmNames.add(filmAdi);
                filmTypes.add(filmTur);
            }

            resultSet.close();
            callableStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Sonuçları iki liste halinde döndürüyoruz
        List<List<String>> result = new ArrayList<>();
        result.add(filmNames);
        result.add(filmTypes);

        return result;
    }
    
    public ArrayList<String> getFilmNames() {
        ArrayList<String> filmNames = new ArrayList<>();
        String query = "SELECT film_adi FROM puansıralıfilmler()";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                filmNames.add(rs.getString("film_adi"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return filmNames;
    }
    
    public ArrayList<String> getFilmTypes() {
    	ArrayList<String> filmTypes = new ArrayList<>();
        String query = "SELECT tur FROM puansıralıfilmler()";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                filmTypes.add(rs.getString("tur"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return filmTypes;
    }
    
    public ArrayList<Double> getFilmRatings() {
    	ArrayList<Double> filmRatings = new ArrayList<>();
        String query = "SELECT puan FROM puansıralıfilmler()";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                filmRatings.add(rs.getDouble("puan"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return filmRatings;
    }
    
    public void createEvent(String etkinlikIsmi, Timestamp selectedDate, String adres, int maxKisi, String olusturanKisi, int favFilm) throws SQLException {
        String insertQuery = "INSERT INTO etkinlik (etkinlik_ismi, zaman, adres, max_kisi, olusturan_kisi, favfilm) " +
                             "VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
            // Parametreleri ayarla
            preparedStatement.setString(1, etkinlikIsmi);
            preparedStatement.setTimestamp(2, new java.sql.Timestamp(selectedDate.getTime()));
            preparedStatement.setString(3, adres);
            preparedStatement.setInt(4, maxKisi);
            preparedStatement.setString(5, olusturanKisi);
            preparedStatement.setInt(6, favFilm);

            // Sorguyu çalıştır
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Etkinlik başarıyla oluşturuldu.");
            } else {
                System.out.println("Etkinlik oluşturulamadı.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e; // Hatanın üst katmana iletilmesi
        }
    }
    
    public int getFilmIdByName(String filmName) throws SQLException {
        String query = "SELECT film_id FROM film WHERE isim = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, filmName);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("film_id");
                } else {
                    throw new SQLException("Film bulunamadı: " + filmName);
                }
            }
        }
    }
    
 // Etkinliği etkinlikId ile silen fonksiyon
    public void deleteEvent(int etkinlikId) throws SQLException {
        String sqlQuery = "DELETE FROM ETKINLIK WHERE etkinlik_id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery)) {
            // Parametreyi etkinlik_id'ye atıyoruz
            preparedStatement.setInt(1, etkinlikId);

            // Silme işlemini gerçekleştiriyoruz
            int rowsAffected = preparedStatement.executeUpdate();

            // Eğer satır silinmişse, işlem başarılı
            if (rowsAffected > 0) {
                System.out.println("Etkinlik başarıyla silindi.");
            } else {
                System.out.println("Silinecek etkinlik bulunamadı.");
            }
        } catch (SQLException e) {
            // Hata durumunda istisna fırlatıyoruz
            System.err.println("Etkinlik silinirken hata oluştu: " + e.getMessage());
            throw e;
        }
    }
    
    // Bağlantıyı kapatma
    public void closeConnection() {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
}