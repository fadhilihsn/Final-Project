package eu.berdosi.app.heartbeat.api.response;

public class UserDAO {
    public String user_id;
    public String email;
    public String nama;
    public String phone;

    public UserDAO() {
    }

    public UserDAO(String user_id, String email, String nama, String phone) {
        this.user_id = user_id;
        this.email = email;
        this.nama = nama;
        this.phone = phone;
    }

}
