package camila.barbosa.backend.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import camila.barbosa.backend.entity.Player;

public interface PlayerRepository extends JpaRepository<Player, UUID> {
    
}