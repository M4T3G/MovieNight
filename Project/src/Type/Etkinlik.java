package Type;

import java.sql.Date;
import java.sql.SQLException;
import java.sql.Timestamp;

public class Etkinlik {
	private int etkinlik_id;
	private String etkinlik_ismi;
	private Timestamp zaman;
	private int olusturan_kisi;
	private String adres;
	private int max_kisi;
	private int favfilm;
	public Etkinlik() {
		
	}
	public Etkinlik( String etkinlik_ismi, Timestamp zaman, int olusturan_kisi, String adres,
			int max_kisi, int favfilm,DbHelper db_helper) throws SQLException {
		super();
		this.etkinlik_ismi = etkinlik_ismi;
		this.zaman = zaman;
		this.olusturan_kisi = olusturan_kisi;
		this.adres = adres;
		this.max_kisi = max_kisi;
		this.favfilm = favfilm;
	}
	public int getEtkinlik_id() {
		return etkinlik_id;
	}
	public String getEtkinlik_ismi() {
		return etkinlik_ismi;
	}
	public void setEtkinlik_ismi(String etkinlik_ismi) {
		this.etkinlik_ismi = etkinlik_ismi;
	}
	public Timestamp getZaman() {
		return zaman;
	}
	public void setZaman(Timestamp zaman) {
		this.zaman = zaman;
	}
	public int getOlusturan_kisi() {
		return olusturan_kisi;
	}
	public void setOlusturan_kisi(int olusturan_kisi) {
		this.olusturan_kisi = olusturan_kisi;
	}
	public String getAdres() {
		return adres;
	}
	public void setAdres(String adres) {
		this.adres = adres;
	}
	public int getMax_kisi() {
		return max_kisi;
	}
	public void setMax_kisi(int max_kisi) {
		this.max_kisi = max_kisi;
	}
	public int getFavfilm() {
		return favfilm;
	}
	public void setFavfilm(int favfilm) {
		this.favfilm = favfilm;
	}
	
	public void createEvent(String etkinlikIsmi, java.sql.Timestamp selectedDate, String adres, int maxKisi, String olusturanKisi, int favFilm,DbHelper db_helper) throws SQLException{
		db_helper.createEvent(etkinlikIsmi, selectedDate, adres, maxKisi, olusturanKisi, favFilm);
	}
	
	public void inviteEvent(int etkinlik_id, String davet_eden_id, String davet_edilen_id,DbHelper db_helper) throws SQLException {
		db_helper.inviteEvent(etkinlik_id, davet_eden_id, davet_edilen_id);
	}
	
	public void deleteInvent(int etkinlik_id, String davet_eden_id, String davet_edilen_id,DbHelper db_helper) throws SQLException{
		db_helper.deleteInvite(etkinlik_id, davet_eden_id, davet_edilen_id);
	}
	 public void attendanceRequest(int etkinlik_id, String katilmak_isteyen_id,DbHelper db_helper) throws SQLException {
		 db_helper.attendanceRequest(etkinlik_id, katilmak_isteyen_id);
	 }
	 public void deleteAttendanceRequest(int etkinlik_id, String katilmak_isteyen_id,DbHelper db_helper) throws SQLException{
		 db_helper.deleteAttendanceRequest(etkinlik_id, katilmak_isteyen_id);
	 }
	 public void removeUser(int etkinlik_id, String kullanici_adi,DbHelper db_helper) throws SQLException{
		 db_helper.removeUser(etkinlik_id, kullanici_adi);
	 }
	 public void addParticipant(String kullaniciAdi, int etkinlikId,DbHelper db_helper) throws SQLException{
		 db_helper.addParticipant(kullaniciAdi, etkinlikId);
	 }
	 public Integer getParticipantNumber(int etkinlikId,DbHelper db_helper) {
		 Integer katilankisisayisi = db_helper.getParticipantNumber(etkinlikId);
		 return katilankisisayisi;
	 }
	 public void deleteEvent(int etkinlikId,DbHelper db_helper) {
		 try {
			db_helper.deleteEvent(etkinlikId);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	 }
	 
}
