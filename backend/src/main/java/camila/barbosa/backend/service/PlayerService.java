package camila.barbosa.backend.service;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;

import org.springframework.stereotype.Service;

import camila.barbosa.backend.dto.ApiDTOs.CommandResponse;
import camila.barbosa.backend.dto.ApiDTOs.PlayerResponse;
import camila.barbosa.backend.dto.ApiDTOs.ProgressResponse;
import camila.barbosa.backend.dto.ApiDTOs.StationResponse;
import camila.barbosa.backend.dto.ApiDTOs.StepResponse;
import camila.barbosa.backend.entity.CommandAttempt;
import camila.barbosa.backend.entity.Player;
import camila.barbosa.backend.entity.StationProgress;
import camila.barbosa.backend.game.GameCatalog;
import camila.barbosa.backend.repository.CommandAttemptRepository;
import camila.barbosa.backend.repository.PlayerRepository;
import camila.barbosa.backend.repository.StationProgressRepository;
import jakarta.transaction.Transactional;

@Service 
public class PlayerService {
private final PlayerRepository players;

    private final StationProgressRepository progress;

    private final CommandAttemptRepository attempts;

    private final GameCatalog catalog;

    public PlayerService(PlayerRepository players, StationProgressRepository progress,
        CommandAttemptRepository attempts, GameCatalog catalog) {
        this.players = players; 
        this.progress = progress; 
        this.attempts = attempts; 
        this.catalog = catalog;
    }

    @Transactional
    public PlayerResponse create(String rawName) {

        if (rawName == null) {
            throw new IllegalArgumentException(
                "Informe seu nome para jogar."
            );
        }

        String name = rawName.trim().replaceAll("\\s+", " ");

        if (name.isBlank()) {
            throw new IllegalArgumentException(
                "O nome é obrigatório."
            );
        }

        if (name.length() < 2 || name.length() > 40) {
            throw new IllegalArgumentException(
                "O nome deve ter entre 2 e 40 caracteres."
            );
        }

        if (!name.matches("[\\p{L}][\\p{L} .'-]*")) {
            throw new IllegalArgumentException(
                "Use apenas letras, espaços, ponto, hífen ou apóstrofo no nome."
            );
        }

        Player player = players.save(new Player(UUID.randomUUID(), name, Instant.now()));

        return new PlayerResponse(
            player.getId(),
            player.getName()
        );
    }

    @Transactional
    public ProgressResponse getProgress(UUID playerId) {
        Player player = players.findById(playerId)
        .orElseThrow(() -> new NoSuchElementException("Jogador não encontrado."));

        Map<String,StationProgress> saved;
        saved = new HashMap<>();

        progress.findAllByPlayerId(playerId).forEach(p -> saved.put(p.getStationId(), p));

        List<StationResponse> stations = catalog
            .stations()
            .stream()
            .map(s -> {
            StationProgress p = saved.get(s.id());

            int current = p == null ? 0 : p.getCurrentStep();

            boolean done = p != null && p.isCompleted();
                List<StepResponse> steps = s.steps()
                    .stream()
                    .map(st -> new StepResponse(
                        st.goal(),
                        st.explanation()
                            ))
                        .toList();

            return new StationResponse(s.id(),
                s.station(),
                s.title(),
                s.subtitle(),
                s.sticker(),
                s.intro(),
                s.concept(),
                s.steps().size(),current,done,steps);
        }).toList();

        long completed = stations
            .stream()
            .filter(StationResponse::completed)
            .count();
        return new ProgressResponse(playerId, player.getName(), stations, completed, completed == catalog.stations().size());
    }


    @Transactional
    public CommandResponse execute(UUID playerId, String stationId, String rawCommand) {
        Player player = players.findById(playerId).orElseThrow(() -> 
        new NoSuchElementException("Jogador não encontrado."));

        GameCatalog.Station station = catalog.find(stationId).orElseThrow(() -> new NoSuchElementException("Estação não encontrada."));

        if (rawCommand == null || rawCommand.isBlank()) throw new IllegalArgumentException("Digite um comando.");

        String command = rawCommand
            .trim()
            .replaceAll("\\s+", " ");

        if (command.length() > 160) throw new IllegalArgumentException("O comando é muito longo.");

        StationProgress p = progress.findByPlayerIdAndStationId(playerId, stationId)
            .orElseGet(() -> progress.save(new StationProgress(player, stationId)));

        if (p.isCompleted()) return new CommandResponse(false,
            "Esta estação já foi restaurada.",List.of(),
            p.getCurrentStep(),true,"");

        if (p.getCurrentStep() >= station.steps().size()) {
            throw new IllegalStateException(
                "Progresso da estação inconsistente."
            );
        }

        GameCatalog.Step step = station
            .steps()
            .get(p.getCurrentStep());

        boolean accepted = step
            .command()
            .equals(command) || step.alternatives()
            .contains(command);

        attempts.save(new CommandAttempt(player, stationId, command, accepted));

        if (!accepted) {
            String message = command.startsWith("chmod 777") || command.startsWith("rm -rf")
                ? "Comando bloqueado pelas regras de segurança do simulador."
                : "Esse comando não corresponde a esta etapa. Confira a dica e tente novamente.";
            return new CommandResponse(false,message,List
                .of(),
                p.getCurrentStep(),
                false,
                step.goal());
        }

        p.advance(station.steps().size());

        boolean completed = p.isCompleted();

        String next = completed ? "" : station
            .steps()
            .get(p.getCurrentStep())
            .goal();

        return new CommandResponse(true,step
            .success(),
            step.output(),
            p.getCurrentStep(),
            completed,next);
    }
}
