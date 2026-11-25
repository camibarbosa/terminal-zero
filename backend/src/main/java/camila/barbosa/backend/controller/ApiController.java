package camila.barbosa.backend.controller;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import camila.barbosa.backend.dto.ApiDTOs.CommandResponse;
import camila.barbosa.backend.dto.ApiDTOs.PlayerResponse;
import camila.barbosa.backend.dto.ApiDTOs.ProgressResponse;
import camila.barbosa.backend.service.PlayerService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@RestController
@RequestMapping("/api")
public class ApiController {

    private final PlayerService service;

    public ApiController(PlayerService service) { 
        this.service = service; 
    }

    public record ValidatedCreatePlayer(@NotBlank @Size(min=2,max=40) String name) {
    }

    public record ValidatedCommand(@NotBlank @Size(max=160) String command) {
    }

    @PostMapping("/players")
    @ResponseStatus(HttpStatus.CREATED)
    public PlayerResponse create(@Valid @RequestBody ValidatedCreatePlayer body) {
        return service.create(body.name());
    }

    @GetMapping("/players/{playerId}/progress")
    public ProgressResponse progress(@PathVariable UUID playerId) { return service.getProgress(playerId); }

    @PostMapping("/players/{playerId}/stations/{stationId}/commands")
    public CommandResponse command(@PathVariable UUID playerId, @PathVariable String stationId,
        @Valid @RequestBody ValidatedCommand body) {
        return service.execute(playerId, stationId, body.command());
    }
}
