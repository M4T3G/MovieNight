package Type;

import java.sql.SQLException;

public class Kullanici {
	private String kullanici_adi;
	private String ilk_isim;
	private String son_isim;
	private int yas;
	private String sifre;
	public Kullanici(String kullanici_adi, String ilk_isim, String son_isim, int yas, String sifre) throws SQLException {
		super();
		this.kullanici_adi = kullanici_adi;
		this.ilk_isim = ilk_isim;
		this.son_isim = son_isim;
		this.yas = yas;
		this.sifre = sifre;
	}
	public String getKullanici_adi() {
		return kullanici_adi;
	}
	public void setKullanici_adi(String kullanici_adi) {
		this.kullanici_adi = kullanici_adi;
	}
	public String getIlk_isim() {
		return ilk_isim;
	}
	public void setIlk_isim(String ilk_isim) {
		this.ilk_isim = ilk_isim;
	}
	public String getSon_isim() {
		return son_isim;
	}
	public void setSon_isim(String son_isim) {
		this.son_isim = son_isim;
	}
	public int getYas() {
		return yas;
	}
	public void setYas(int yas) {
		this.yas = yas;
	}
	public String getSifre() {
		return sifre;
	}
	public void setSifre(String sifre) {
		this.sifre = sifre;
	}
	
	public String getFavoriteFilm(String kisininid, DbHelper db_helper) {
		String favoriFilm = db_helper.getFavoriteFilm(kisininid);
		return favoriFilm;
	}
	
	public boolean changePassword(String kullaniciAdi, String sifre, String yeniSifre, String yeniSifreTekrar, DbHelper db_Helper) throws SQLException {
		 boolean isChanged = db_Helper.changePassword(kullaniciAdi, sifre, yeniSifre, yeniSifreTekrar);
		 return isChanged;
	}
}
