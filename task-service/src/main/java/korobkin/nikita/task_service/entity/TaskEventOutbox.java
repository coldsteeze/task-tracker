package korobkin.nikita.task_service.entity;

import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.*;
import korobkin.nikita.task_events.TaskEvent;
import korobkin.nikita.task_service.entity.enums.TaskEventStatus;
import korobkin.nikita.task_service.entity.enums.TaskEventType;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;

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

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false, updatable = false)
    private TaskEventType eventType;

    @Type(JsonBinaryType.class)
    @Column(name = "payload", nullable = false, updatable = false, columnDefinition = "jsonb")
    private TaskEvent payload;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private TaskEventStatus status;

    @Column(name = "retry_count", nullable = false)
    private Integer retryCount;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
