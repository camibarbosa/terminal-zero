package camila.barbosa.backend.entity;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity 
@Table(name = "players")
public class Player {

    @Id 
    private UUID id;

    @Column(nullable = false, length = 40)
    private String name;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
    
    protected Player() {
    }

    public Player(UUID id, String name, Instant createdAt) {
        this.id = id;
        this.name = name;
        this.createdAt = createdAt;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

}