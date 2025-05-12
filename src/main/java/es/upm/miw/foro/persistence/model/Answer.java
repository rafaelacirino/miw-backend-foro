package es.upm.miw.foro.persistence.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "answers")
public class Answer {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "answer_seq")
    @SequenceGenerator(name = "answer_seq", sequenceName = "answer_seq", allocationSize = 1)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    @Size(max = 1000, message = "Content must be at most 1000 characters")
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false, referencedColumnName = "id")
    private Question question;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false, referencedColumnName = "id")
    private User author;

    @Column(name = "creation_date", nullable = false)
    private LocalDateTime creationDate;

    @PrePersist
    public void onCreate() {
        this.creationDate = LocalDateTime.now();
    }
}
