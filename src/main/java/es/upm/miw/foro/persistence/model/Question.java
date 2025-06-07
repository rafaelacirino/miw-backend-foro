package es.upm.miw.foro.persistence.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "questions")
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "question_seq")
    @SequenceGenerator(name = "question_seq", sequenceName = "question_seq", allocationSize = 1)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false, referencedColumnName = "id")
    @JsonIgnoreProperties("questions")
    private User author;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "creation_date")
    private LocalDateTime creationDate;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties("questions")
    private List<Answer> answers = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "question_tag",
            joinColumns = @JoinColumn(name = "question_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    @JsonIgnoreProperties("questions")
    private Set<Tag> tags = new HashSet<>();

    @Column(name = "views", nullable = false)
    private Integer views = 0;

    @ElementCollection
    @CollectionTable(name = "question_viewed_by", joinColumns = @JoinColumn(name = "question_id"))
    @Column(name = "session_id")
    private Set<String> viewedBySessions = new HashSet<>();

    @ElementCollection
    @CollectionTable(name = "question_viewed_by_users", joinColumns = @JoinColumn(name = "question_id"))
    @Column(name = "user_id")
    private Set<Long> viewedByUsers = new HashSet<>();

    @PrePersist
    public void onCreate() {
        this.creationDate = LocalDateTime.now();
    }

    public void addAnswer(Answer answer) {
        answers.add(answer);
        answer.setQuestion(this);
    }

    public void removeAnswer(Answer answer) {
        answers.remove(answer);
        answer.setQuestion(null);
    }

    public boolean incrementViewsIfNew(String sessionId, Long userId) {
        boolean isNewView = false;

        if (userId != null) {
            isNewView = viewedByUsers.add(userId);
        } else if (sessionId != null) {
            isNewView = viewedBySessions.add(sessionId);
        }

        if (isNewView) {
            this.views++;
        }

        return isNewView;
    }
}
