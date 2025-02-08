package Type;

public class DavetIstegi {
    // Private özellikler
    private int etkinlik_id;
    private String davet_eden_id;
    private String davet_edilen_id;

    // Constructor
    public DavetIstegi(int etkinlik_id, String davet_eden_id, String davet_edilen_id) {
        this.etkinlik_id = etkinlik_id;
        this.davet_eden_id = davet_eden_id;
        this.davet_edilen_id = davet_edilen_id;
    }

    // Getter ve Setter yöntemleri

    public int getEtkinlik_id() {
        return etkinlik_id;
    }

    public void setEtkinlik_id(int etkinlik_id) {
        this.etkinlik_id = etkinlik_id;
    }

    public String getDavet_eden_id() {
        return davet_eden_id;
    }

    public void setDavet_eden_id(String davet_eden_id) {
        this.davet_eden_id = davet_eden_id;
    }

    public String getDavet_edilen_id() {
        return davet_edilen_id;
    }

    public void setDavet_edilen_id(String davet_edilen_id) {
        this.davet_edilen_id = davet_edilen_id;
    }

    // toString metodu (isteğe bağlı)
    @Override
    public String toString() {
        return "DavetIstegi{" +
                "etkinlik_id=" + etkinlik_id +
                ", davet_eden_id='" + davet_eden_id + '\'' +
                ", davet_edilen_id='" + davet_edilen_id + '\'' +
                '}';
    }
}
