package Type;

public class Oy {
	
    private int oy_id;
    private int etkinlik_id;
    private int film_id;
    private String oy_veren;
    private int verdigi_oy;

    public Oy() {
    	
    }

    public Oy(int oy_id, int etkinlik_id, int film_id, String oy_veren, int verdigi_oy) {
        this.oy_id = oy_id;
        this.etkinlik_id = etkinlik_id;
        this.film_id = film_id;
        this.oy_veren = oy_veren;
        setVerdigi_oy(verdigi_oy);
    }


    public int getOy_id() {
        return oy_id;
    }

    public void setOy_id(int oy_id) {
        this.oy_id = oy_id;
    }

    public int getEtkinlik_id() {
        return etkinlik_id;
    }

    public void setEtkinlik_id(int etkinlik_id) {
        this.etkinlik_id = etkinlik_id;
    }

    public int getFilm_id() {
        return film_id;
    }

    public void setFilm_id(int film_id) {
        this.film_id = film_id;
    }

    public String getOy_veren() {
        return oy_veren;
    }

    public void setOy_veren(String oy_veren) {
        this.oy_veren = oy_veren;
    }

    public int getVerdigi_oy() {
        return verdigi_oy;
    }

    public void setVerdigi_oy(int verdigi_oy) {
        // Check kuralını uygular
        if (verdigi_oy < 1 || verdigi_oy > 10) {
            throw new IllegalArgumentException("Verdigi oy 1 ile 10 arasında olmalıdır.");
        }
        this.verdigi_oy = verdigi_oy;
    }
    
    

    // toString metodu (isteğe bağlı)
    @Override
    public String toString() {
        return "Oy{" +
                "oy_id=" + oy_id +
                ", etkinlik_id=" + etkinlik_id +
                ", film_id=" + film_id +
                ", oy_veren='" + oy_veren + '\'' +
                ", verdigi_oy=" + verdigi_oy +
                '}';
    }
}
