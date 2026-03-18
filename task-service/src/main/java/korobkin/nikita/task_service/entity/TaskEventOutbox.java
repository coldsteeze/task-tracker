package korobkin.nikita.task_service.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "task_events_outbox")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class TaskEventOutbox {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "event_type", nullable = false, updatable = false)
    private String eventType;

    @Column(name = "payload", nullable = false, updatable = false)
    private String payload;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
