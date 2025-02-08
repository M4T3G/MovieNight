package Type;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class LoginClass {
	
	public String usernameControl(String username, DbHelper db_Helper) {
        List<String> list = db_Helper.getUsernames();

        for (String item : list) {
            if (item == username) {
                return username; // Eşleşme bulundu, 1 döndür
            }
        }

        return null; // Eşleşme bulunamadı, 0 döndür
    }
	
	public int passwordControl(String username, DbHelper db_Helper) {
        List<String> list = db_Helper.getUsernames();

        for (String item : list) {
            if (item == username) {
                return 1; // Eşleşme bulundu, 1 döndür
            }
        }

        return 0; // Eşleşme bulunamadı, 0 döndür
    }
	
	public List<Object> userInformation(String kullaniciAdi, String sifre,DbHelper db_helper){
		List<Object> userInfo = db_helper.getUserInfoByCredentials(kullaniciAdi, sifre);
		return userInfo;
	}
	
	
	public boolean authenticateUser(String kullaniciAdi, String sifre, DbHelper db_helper) throws SQLException {
        boolean isAuthenticated = db_helper.authenticateUser(kullaniciAdi, sifre);
        return isAuthenticated;
    }
	
	public void addUser(String kullaniciAdi, String ilkIsim, String sonIsim, int yas, String sifre, DbHelper db_helper) throws SQLException {
         db_helper.addUser(kullaniciAdi, ilkIsim, sonIsim, yas, sifre);
    }
	
	
}
