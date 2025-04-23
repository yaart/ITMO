package yaart.s468198.models;

import yaart.s468198.exceptions.InvalidArgumentsException;
import yaart.s468198.utility.Validatable;

import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * Класс, представляющий лабораторную работу.
 * Содержит информацию о работе, включая идентификатор, название, координаты, дату создания,
 * минимальный балл, количество настроенных работ, сложность и дисциплину.
 */
public class LabWork implements Validatable, Comparable<LabWork> {
    private final int id;
    private final String name;
    private final Coordinates coordinates;
    private final ZonedDateTime creationDate;
    private final Float minimalPoint;
    private final Integer tunedInWorks;
    private final Difficulty difficulty;
    private Discipline discipline;

    public LabWork(int id, String name, Coordinates coordinates, Float minimalPoint, Integer tunedInWorks,
                   Difficulty difficulty, Discipline discipline) {
        this(id, name, coordinates, ZonedDateTime.now(), minimalPoint, tunedInWorks, difficulty, discipline);
    }

    public LabWork(int id, String name, Coordinates coordinates, ZonedDateTime creationDate,
                   Float minimalPoint, Integer tunedInWorks, Difficulty difficulty, Discipline discipline) {

        this.id = id;
        this.name = name;
        this.coordinates = coordinates;
        this.creationDate = creationDate;
        this.minimalPoint = minimalPoint;
        this.tunedInWorks = tunedInWorks;
        this.difficulty = difficulty;
        this.discipline = discipline;

        if (!validate()) {
            throw new InvalidArgumentsException("Неверные аргументы для создания объекта класса LabWork");
        }
    }


    /**
     * Возвращает идентификатор лабораторной работы.
     *
     * @return идентификатор лабораторной работы
     */
    public int getId() {
        return id;
    }

    /**
     * Возвращает название лабораторной работы.
     *
     * @return название лабораторной работы
     */
    public String getName() {
        return name;
    }

    /**
     * Возвращает координаты лабораторной работы.
     *
     * @return координаты лабораторной работы
     */
    public Coordinates getCoordinates() {
        return coordinates;
    }

    /**
     * Возвращает дату создания лабораторной работы.
     *
     * @return дата создания лабораторной работы
     */
    public ZonedDateTime getCreationDate() {
        return creationDate;
    }

    /**
     * Возвращает минимальный балл лабораторной работы.
     *
     * @return минимальный балл лабораторной работы
     */
    public Float getMinimalPoint() {
        return minimalPoint;
    }

    /**
     * Возвращает количество настроенных работ.
     *
     * @return количество настроенных работ
     */
    public Integer getTunedInWorks() {
        return tunedInWorks;
    }

    /**
     * Возвращает сложность лабораторной работы.
     *
     * @return сложность лабораторной работы
     */
    public Difficulty getDifficulty() {
        return difficulty;
    }

    /**
     * Возвращает дисциплину лабораторной работы.
     *
     * @return дисциплина лабораторной работы
     */
    public Discipline getDiscipline() {
        return discipline;
    }

    public void setCreationDate(ZonedDateTime now) {
    }

    public boolean validate() {
        if (id <= 0) {
            return false;
        }
        if (name == null || name.isEmpty()) {
            return false;
        }
        if (coordinates == null) {
            return false;
        }
        if (creationDate == null) {
            return false;
        }
        if (minimalPoint == null || minimalPoint <= 0) {
            return false;
        }
        if (tunedInWorks == null) {
            return false;
        }
        if (difficulty == null) {
            return false;
        }
        return true;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        LabWork labWork = (LabWork) obj;
        return id == labWork.id &&
                Objects.equals(name, labWork.name) &&
                Objects.equals(coordinates, labWork.coordinates) &&
                Objects.equals(creationDate, labWork.creationDate) &&
                Objects.equals(minimalPoint, labWork.minimalPoint) &&
                Objects.equals(tunedInWorks, labWork.tunedInWorks) &&
                difficulty == labWork.difficulty &&
                Objects.equals(discipline, labWork.discipline);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, coordinates, creationDate, minimalPoint, tunedInWorks, difficulty, discipline);
    }

    @Override
    public String toString() {
        return "LabWork{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", coordinates=" + coordinates +
                ", creationDate=" + creationDate +
                ", minimalPoint=" + minimalPoint +
                ", tunedInWorks=" + tunedInWorks +
                ", difficulty=" + difficulty +
                ", discipline=" + discipline +
                '}';
    }

    @Override
    public int compareTo(LabWork other) {
        if (other == null) {
            throw new NullPointerException("Сравниваемый объект не может быть null");
        }
        return Integer.compare(this.id, other.id);
    }


}