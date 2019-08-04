package Model;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDate;


@Entity
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String description;

    @Enumerated(EnumType.STRING)
    private Category category;
    private LocalDate deadline;

    private boolean isReady; //czy jest zrobione
    private LocalDate startDate;
    private LocalDate finishDate;


    public Task() {
    }

    public Task(String description, Category category, boolean isReady, LocalDate deadline) {
        this.description = description;
        this.category = category;
        this.deadline = deadline;
        this.isReady = isReady;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public LocalDate getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDate deadline) {
        this.deadline = deadline;
    }

    public boolean isReady() {
        return isReady;
    }

    public void setReady(boolean ready) {
        isReady = ready;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getFinishDate() {
        return finishDate;
    }

    public void setFinishDate(LocalDate finishDate) {
        this.finishDate = finishDate;
    }

    @Override
    public String toString() {
        return "id=" + id +
                ", description='" + description + '\'' +
                ", category=" + category +
                ", deadline=" + deadline +
                ", isReady=" + isReady +
                ", startDate=" + startDate +
                ", finishDate=" + finishDate;
    }
}
