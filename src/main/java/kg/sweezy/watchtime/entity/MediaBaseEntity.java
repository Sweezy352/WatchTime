package kg.sweezy.watchtime.entity;

import jakarta.persistence.*;

import java.time.LocalDate;

@MappedSuperclass
public abstract class MediaBaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "views")
    protected Long views;
    @Column(name = "likes")
    protected Long likes;
    @Column(name = "dislikes")
    protected Long dislikes;
    @Column(name = "amount_comments")
    protected Long amountComments;
    @Column(name = "date_created")
    protected LocalDate dateCreated;

    public Long getId() {
        return id;
    }

    public MediaBaseEntity setId(Long id) {
        this.id = id;
        return this;
    }

    public Long getViews() {
        return views;
    }

    public MediaBaseEntity setViews(Long views) {
        this.views = views;
        return this;
    }

    public Long getLikes() {
        return likes;
    }

    public MediaBaseEntity setLikes(Long likes) {
        this.likes = likes;
        return this;
    }

    public Long getDislikes() {
        return dislikes;
    }

    public MediaBaseEntity setDislikes(Long dislikes) {
        this.dislikes = dislikes;
        return this;
    }

    public Long getAmountComments() {
        return amountComments;
    }

    public MediaBaseEntity setAmountComments(Long amountComments) {
        this.amountComments = amountComments;
        return this;
    }

    public LocalDate getDateCreated() {
        return dateCreated;
    }

    public MediaBaseEntity setDateCreated(LocalDate dateCreated) {
        this.dateCreated = dateCreated;
        return this;
    }
}
