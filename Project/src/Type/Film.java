package Type;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Film {
	private int film_id;
	private String isim;
	private String tur;
	
	public Film() {
	}
	
	public Film( String isim, String tur,DbHelper db_helper) {
		super();
		this.isim = isim;
		this.tur = tur;
		db_helper.addFilm(isim, tur);
	}
	public int getFilm_id() {
		return film_id;
	}
	public void setFilm_id(int film_id) {
		this.film_id = film_id;
	}
	public String getIsim() {
		return isim;
	}
	public void setIsim(String isim) {
		this.isim = isim;
	}
	public String getTur() {
		return tur;
	}
	public void setTur(String tur) {
		this.tur = tur;
	}
	
	public void addFilm(String isim, String tur, DbHelper db_helper) {
	db_helper.addFilm(isim, tur);
	}
	
	 public void voteFilm(int etkinlik_id, int film_id, String oyVeren, int verdigiOy,DbHelper db_helper) throws SQLException{
		 db_helper.voteFilm(etkinlik_id, film_id, oyVeren, verdigiOy);
	 }
	 
	 public List<Map<String, Object>> getTop5Films(DbHelper db_helper) throws SQLException{
		 List<Map<String, Object>> topFilms = db_helper.getTop5Films();
		 return topFilms;
	    }
	 public List<Map<String, Object>> searchFilmByName(String arama,DbHelper db_helper) throws SQLException{
		 List<Map<String, Object>> films = db_helper.searchFilmByName(arama);
		 return films;
	 }
	 public List<String> getFilmsByType(String tur,DbHelper db_helper){
		 List<String> films = db_helper.getFilmsByType(tur);
		 return films;
	 }
	 public List<List<String>> getOrderedFilm(DbHelper db_helper){
		 List<List<String>> films = db_helper.getOrderedFilm();
		 return films;
	 }
	 public ArrayList<String> getFilmNames(DbHelper db_helper){
		 ArrayList<String> filmNames = db_helper.getFilmNames();
		 return filmNames;
	 }
	 public ArrayList<String> getFilmTypes(DbHelper db_helper){
		 ArrayList<String> filmTypes = db_helper.getFilmTypes();
		 return filmTypes;
	 }
	 public ArrayList<Double> getFilmRatings(DbHelper db_helper){
		 ArrayList<Double> filmRatings = db_helper.getFilmRatings();
		 return filmRatings;
	 }
	 public int getFilmIdByName(String filmName,DbHelper db_helper) throws SQLException {
		 int filmId=db_helper.getFilmIdByName(filmName);
		 return filmId;
	 }
}
