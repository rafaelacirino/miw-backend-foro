package es.upm.miw.foro.persistance.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "answers")
public class Answer {

    @Id
    @GeneratedValue(generator = "UUID")
    @UuidGenerator(style = UuidGenerator.Style.RANDOM)
    @Column(columnDefinition = "UUID", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
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

    public void setQuestion(Question question) {
        this.question = question;
        if (question != null) {
            List<Answer> answers = question.getAnswers();
            if (!answers.contains(this)) {
                answers.add(this);
            }
        }
    }
}
