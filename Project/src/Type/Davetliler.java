package Type;

public class Davetliler {
    // Private özellikler
    private int etkinlik_id;
    private String davetli_id;

    // Constructor
    public Davetliler(int etkinlik_id, String davetli_id) {
        this.etkinlik_id = etkinlik_id;
        this.davetli_id = davetli_id;
    }

    // Getter ve Setter yöntemleri

    public int getEtkinlik_id() {
        return etkinlik_id;
    }

    public void setEtkinlik_id(int etkinlik_id) {
        this.etkinlik_id = etkinlik_id;
    }

    public String getDavetli_id() {
        return davetli_id;
    }

    public void setDavetli_id(String davetli_id) {
        this.davetli_id = davetli_id;
    }

    // toString metodu (isteğe bağlı)
    @Override
    public String toString() {
        return "Davetliler{" +
                "etkinlik_id=" + etkinlik_id +
                ", davetli_id='" + davetli_id + '\'' +
                '}';
    }
}

