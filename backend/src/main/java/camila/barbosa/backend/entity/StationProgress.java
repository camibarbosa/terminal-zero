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
import jakarta.persistence.UniqueConstraint;

@Entity 
@Table(name = "station_progress", uniqueConstraints = @UniqueConstraint (columnNames = {"player_id", "station_id"}))
public class StationProgress {
    @Id 
    private UUID id;

    @ManyToOne (fetch = FetchType.LAZY) 
    @JoinColumn(name = "station_id", nullable = false)
    private Player player;

    @Column(name = "station_id", nullable = false, length = 40)
    private String stationId;

    @Column(name = "current_step", nullable = false)
    private int currentStep;

    @Column (name = "completed", nullable = false)
    private boolean completed;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected StationProgress() {
    }

    public StationProgress(Player player, String stationId) {
        this.id = UUID.randomUUID();
        this.player = player;
        this.stationId = stationId;
        this.currentStep = 0;
        this.completed = false;
        this.updatedAt = Instant.now();
    }
    
    public String getStationId() {
        return stationId;
    }

    public int getCurrentStep() {
        return currentStep;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void advance(int totalSteps) {
        if (currentStep < totalSteps) currentStep++;
        if (currentStep >= totalSteps) completed = true;

        updatedAt = Instant.now();
    }
}