package camila.barbosa.backend.dto;

import java.util.List;
import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public final class ApiDTOs {
    
    private ApiDTOs() {}

    public record CreatePlayerRequest(String name) {}

    public record PlayerResponse(UUID id, String name) {}

    public record StepResponse(
        String goal,
        String explanation
    ) {}

    public record StationResponse(
        String id,
        String station,
        String title,
        String subtitle,
        String sticker,
        String intro,
        String concept,
        int totalSteps,
        int currentStep,
        boolean completed,
        List<StepResponse> steps
    ) {}

    public record ProgressResponse(
        UUID playerId,
        String name,
        List<StationResponse> stations,
        long completedStations,
        boolean operationsUnlocked
    ) {}

    public record CommandRequest(String command) {}

    public record CommandResponse(
        boolean accepted,
        String message,
        List<String> output,
        int currentStep,
        boolean completed,
        String nextGoal
    ) {}

    public record ErrorResponse(
        @NotBlank(message = "Digite um nome para entrar no Terminal Zero.")
        @Size(min = 2, max = 40, message = "O nome precisa ter entre 2 e 40 caracteres.")
        String message,
        List<String> details
    ) {}
}
