package camila.barbosa.backend.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import camila.barbosa.backend.entity.StationProgress;

public interface StationProgressRepository extends JpaRepository<StationProgress, UUID> {
    Optional<StationProgress> findByPlayerIdAndStationId(UUID playerId, String stationId);
    List<StationProgress> findAllByPlayerId(UUID playerId);
}

