package entities;

import java.time.LocalDate;
import java.util.UUID;

public record ProductRecord(UUID id, String name, Category category, double rating, LocalDate created_at,
                            LocalDate updated_at) {
}
