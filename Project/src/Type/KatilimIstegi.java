package Type;

public class KatilimIstegi {
    // Private özellikler
    private int etkinlik_id;
    private String katilmak_isteyen_id;

    // Constructor
    public KatilimIstegi(int etkinlik_id, String katilmak_isteyen_id) {
        this.etkinlik_id = etkinlik_id;
        this.katilmak_isteyen_id = katilmak_isteyen_id;
    }

    // Getter ve Setter yöntemleri

    public int getEtkinlik_id() {
        return etkinlik_id;
    }

    public void setEtkinlik_id(int etkinlik_id) {
        this.etkinlik_id = etkinlik_id;
    }

    public String getKatilmak_isteyen_id() {
        return katilmak_isteyen_id;
    }

    public void setKatilmak_isteyen_id(String katilmak_isteyen_id) {
        this.katilmak_isteyen_id = katilmak_isteyen_id;
    }

    // toString metodu (isteğe bağlı)
    @Override
    public String toString() {
        return "KatilimIstegi{" +
                "etkinlik_id=" + etkinlik_id +
                ", katilmak_isteyen_id='" + katilmak_isteyen_id + '\'' +
                '}';
    }
}
