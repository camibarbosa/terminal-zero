package camila.barbosa.backend.entity;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "command_attempts")
public class CommandAttempt {
    @Id 
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY) 
    @JoinColumn(name = "player_id", nullable = false) 
    private Player player;

    @Column(name = "station_id", nullable = false) 
    private String stationId;

    @Column(nullable = false, columnDefinition = "text") 
    private String command;

    @Column(nullable = false) 
    private boolean accepted;

    @Column(name = "created_at", nullable = false) 
    private Instant createdAt;

    protected CommandAttempt() {}

    public CommandAttempt(Player player, String stationId, String command, boolean accepted) {
        this.id = UUID.randomUUID(); 
        this.player = player; 
        this.stationId = stationId;
        this.command = command; 
        this.accepted = accepted; 
        this.createdAt = Instant.now();
    }
}
