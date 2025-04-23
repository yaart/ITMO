package yaart.s468198.managers;

import yaart.s468198.exceptions.ElementNotFoundException;
import yaart.s468198.exceptions.IdAlreadyExistsException;
import yaart.s468198.models.Discipline;
import yaart.s468198.models.LabWork;
import yaart.s468198.utility.Sortable;

import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Менеджер коллекции LabWork, реализующий операции управления коллекцией
 * и сортировку через интерфейс Sortable.
 */
public class CollectionManager implements Sortable {
    private final PriorityQueue<LabWork> collection;
    private int lastId;
    private final ZonedDateTime collectionCreationDate;

    /**
     * Конструктор по умолчанию, инициализирует пустую коллекцию
     */
    public CollectionManager() {
        this.collection = new PriorityQueue<>();
        this.lastId = 0;
        this.collectionCreationDate = ZonedDateTime.now();
    }

    /**
     * Получить дату создания коллекции
     * @return дата создания коллекции
     */
    public ZonedDateTime getCollectionCreationDate() {
        return collectionCreationDate;
    }

    /**
     * Получить неизменяемую копию коллекции
     * @return неизменяемая копия коллекции
     */
    public Collection<LabWork> getCollection() {
        return Collections.unmodifiableCollection(collection);
    }

    /**
     * Найти LabWork по ID
     * @param id идентификатор искомого объекта
     * @return найденный объект или null если не найден
     */
    public LabWork getLabWorkById(int id) {
        return collection.stream()
                .filter(lw -> lw.getId() == id)
                .findFirst()
                .orElse(null);
    }

    /**
     * Генерация уникального ID.
     * Находит наименьший свободный ID в диапазоне [1, Integer.MAX_VALUE].
     *
     * @return уникальный ID
     * @throws RuntimeException если невозможно найти свободный ID
     */
    public int generateId() {
        // Собираем все занятые ID в отсортированный список
        List<Integer> usedIds = collection.stream()
                .map(LabWork::getId)
                .sorted()
                .collect(Collectors.toList());

        // Ищем первый свободный ID
        int candidate = 1;
        for (int usedId : usedIds) {
            if (candidate < usedId) {
                return candidate; // Найден свободный ID
            }
            candidate = usedId + 1;
        }

        // Проверяем, что candidate не превышает максимальное значение int
        if (candidate > 0 && candidate <= Integer.MAX_VALUE) {
            return candidate;
        }

        throw new RuntimeException("Невозможно сгенерировать уникальный ID. Коллекция переполнена.");
    }

    /**
     * Добавить новый элемент в коллекцию
     * @param lw добавляемый объект LabWork
     * @throws IdAlreadyExistsException если элемент с таким ID уже существует
     * @throws IllegalArgumentException если данные объекта невалидны
     */
    public void addLabWork(LabWork lw) throws IdAlreadyExistsException {
        if (!lw.validate()) {
            throw new IllegalArgumentException("Некорректные данные LabWork");
        }

        if (containsId(lw.getId())) {
            throw new IdAlreadyExistsException(lw.getId());
        }

        if (lw.getCreationDate() == null) {
            lw.setCreationDate(ZonedDateTime.now());
        }

        collection.add(lw);
        lastId = Math.max(lastId, lw.getId());
    }

    /**
     * Удалить элемент по ID
     * @param id идентификатор удаляемого объекта
     * @throws ElementNotFoundException если элемент не найден
     */
    public void removeLabWorkById(int id) throws ElementNotFoundException {
        if (!collection.removeIf(lw -> lw.getId() == id)) {
            throw new ElementNotFoundException("LabWork с ID " + id + " не найден");
        }
    }

    /**
     * Обновить элемент коллекции
     * @param newLw новый вариант объекта
     * @throws ElementNotFoundException если элемент не найден
     * @throws IllegalArgumentException если данные объекта невалидны
     */
    public void updateLabWork(LabWork newLw) throws ElementNotFoundException {
        if (!newLw.validate()) {
            throw new IllegalArgumentException("Некорректные данные LabWork");
        }

        if (!collection.removeIf(lw -> lw.getId() == newLw.getId())) {
            throw new ElementNotFoundException("LabWork не найден");
        }
        collection.add(newLw);
    }

    /**
     * Очистить коллекцию
     */
    public void clear() {
        collection.clear();
        lastId = 0;
    }

    /**
     * Проверить существование элемента с указанным ID
     * @param id проверяемый идентификатор
     * @return true если элемент существует, false если нет
     */
    public boolean containsId(int id) {
        return getLabWorkById(id) != null;
    }

    /**
     * Получить первый элемент (без удаления)
     * @return первый элемент коллекции или null если коллекция пуста
     */
    public LabWork getHead() {
        return collection.peek();
    }

    /**
     * Удалить и вернуть первый элемент
     * @return удаленный элемент или null если коллекция пуста
     */
    public LabWork removeFirst() {
        return collection.poll();
    }

    /**
     * Удалить элементы меньше заданного
     * @param element элемент для сравнения
     */
    public void removeLower(LabWork element) {
        collection.removeIf(lw -> lw.compareTo(element) < 0);
    }

    /**
     * Получить количество элементов
     * @return размер коллекции
     */
    public int size() {
        return collection.size();
    }

    /**
     * Проверить, пуста ли коллекция
     * @return true если коллекция пуста, false если нет
     */
    public boolean isEmpty() {
        return collection.isEmpty();
    }

    /**
     * Отсортировать коллекцию (реализация интерфейса Sortable)
     */
    @Override
    public void sort() {
        // PriorityQueue автоматически поддерживает сортировку,
        // поэтому просто перестраиваем очередь
        PriorityQueue<LabWork> sorted = new PriorityQueue<>(collection);
        collection.clear();
        collection.addAll(sorted);
    }

    /**
     * Получить количество элементов с меньшей дисциплиной
     * @param discipline дисциплина для сравнения
     * @return количество элементов
     */
    public long countLessThanDiscipline(Discipline discipline) {
        return collection.stream()
                .filter(lw -> lw.getDiscipline() != null)
                .filter(lw -> lw.getDiscipline().compareTo(discipline) < 0)
                .count();
    }

    /**
     * Получить уникальные значения tunedInWorks
     * @return множество уникальных значений
     */
    public Set<Integer> getUniqueTunedInWorks() {
        return collection.stream()
                .map(LabWork::getTunedInWorks)
                .collect(Collectors.toSet());
    }

    /**
     * Получить значения discipline в порядке возрастания
     * @return отсортированный список дисциплин
     */
    public List<Discipline> getDisciplinesAscending() {
        return collection.stream()
                .filter(lw -> lw.getDiscipline() != null)
                .map(LabWork::getDiscipline)
                .sorted()
                .collect(Collectors.toList());
    }

    /**
     * Получить строковое представление коллекции
     * @return строковое представление
     */
    @Override
    public String toString() {
        return "CollectionManager{" +
                "collection=" + collection +
                ", size=" + size() +
                ", created=" + collectionCreationDate +
                '}';
    }
}