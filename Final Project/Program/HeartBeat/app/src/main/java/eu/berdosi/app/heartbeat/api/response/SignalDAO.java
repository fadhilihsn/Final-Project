package eu.berdosi.app.heartbeat.api.response;

public class SignalDAO {
    public String id;
    public String pulse;
    public String cycles;
    public String duration;
    public String signals;
    public String user_id;
    public String timestamp;

    public SignalDAO() {
    }

    public SignalDAO(String id, String pulse, String cycles, String duration, String signals, String user_id, String timestamp) {
        this.id = id;
        this.pulse = pulse;
        this.cycles = cycles;
        this.duration = duration;
        this.signals = signals;
        this.user_id = user_id;
        this.timestamp = timestamp;
    }
}
