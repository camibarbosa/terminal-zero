package camila.barbosa.backend.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import camila.barbosa.backend.entity.CommandAttempt;

public interface CommandAttemptRepository extends JpaRepository<CommandAttempt, UUID> {
    
}
