package yaart.s468198.managers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import yaart.s468198.base.exceptions.DeserializationException;
import yaart.s468198.base.exceptions.ElementNotFoundException;
import yaart.s468198.base.exceptions.IdAlreadyExistsException;
import yaart.s468198.base.models.Discipline;
import yaart.s468198.base.models.LabWork;
import yaart.s468198.base.parser.CsvCollectionManager;

import java.io.*;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * CollectionManager - класс для управления коллекцией объектов LabWork.
 */
public class CollectionManager {
    private final Logger logger = LogManager.getRootLogger();
    private final PriorityQueue<LabWork> collection;
    private int lastId;
    private final ZonedDateTime creationDate;

    /**
     * Конструктор класса.
     *
     * @param filePath путь к файлу, где хранится коллекция
     */
    public CollectionManager(String filePath) {
        this.collection = new PriorityQueue<>();
        this.lastId = 0;
        this.creationDate = ZonedDateTime.now();

        try {
            loadCollection(filePath);
        } catch (IOException | DeserializationException e) {
            logger.error("Ошибка при загрузке коллекции: {}", e.getMessage());
        }
    }

    /**
     * Метод для получения даты создания коллекции.
     *
     * @return Дата создания коллекции
     */
    public ZonedDateTime getCreationDate() {
        return creationDate;
    }

    /**
     * Метод для получения типа коллекции.
     *
     * @return Тип коллекции
     */
    public String getCollectionType() {
        return collection.getClass().getSimpleName();
    }

    /**
     * Метод для получения неизменяемой копии коллекции.
     *
     * @return Неизменяемая коллекция
     */
    public Collection<LabWork> getCollection() {
        return Collections.unmodifiableCollection(collection);
    }

    /**
     * Генерирует новый уникальный ID.
     *
     * @return Новый ID
     */
    public int generateId() {
        if (collection.isEmpty()) {
            return 1;
        }

        int maxId = collection.stream()
                .mapToInt(LabWork::getId)
                .max()
                .orElse(0);

        return maxId + 1;
    }

    /**
     * Добавляет новый элемент в коллекцию.
     *
     * @param labWork Добавляемый элемент
     * @throws IdAlreadyExistsException Если элемент с таким ID уже существует
     * @throws IllegalArgumentException Если данные объекта невалидны
     */
    public void addLabWork(LabWork labWork) throws IdAlreadyExistsException {
        if (!labWork.validate()) {
            throw new IllegalArgumentException("Некорректные данные LabWork");
        }

        if (labWork.getCreationDate() == null) {
            labWork = new LabWork(
                    labWork.getId(),
                    labWork.getName(),
                    labWork.getCoordinates(),
                    ZonedDateTime.now(),
                    labWork.getMinimalPoint(),
                    labWork.getTunedInWorks(),
                    labWork.getDifficulty(),
                    labWork.getDiscipline()
            );
        }

        collection.add(labWork);
        lastId = Math.max(lastId, labWork.getId());
        logger.info("Добавлен новый элемент: {}", labWork);
    }

    /**
     * Проверяет наличие элемента по ID.
     *
     * @param id идентификатор
     * @return true если такой ID есть
     */
    public boolean containsId(int id) {
        return collection.stream().anyMatch(lw -> lw.getId() == id);
    }

    /**
     * Удаляет элемент из коллекции по ID.
     *
     * @param id ID удаляемого элемента
     * @throws ElementNotFoundException Если элемент не найден
     */
    public void removeLabWorkById(int id) throws ElementNotFoundException {
        LabWork labWork = getLabWorkById(id);
        if (labWork != null) {
            boolean removed = collection.removeIf(lw -> lw.getId() == id);
            if (removed) {
                logger.info("Удалён элемент: {}", labWork);
            } else {
                throw new ElementNotFoundException("Элемент с ID " + id + " не найден");
            }
        } else {
            throw new ElementNotFoundException("Элемент с ID " + id + " не найден");
        }
    }

    /**
     * Обновляет элемент в коллекции.
     *
     * @param newLabWork Новый элемент
     * @return
     * @throws ElementNotFoundException Если элемент с таким ID не найден
     */
    public boolean updateLabWork(LabWork newLabWork) throws ElementNotFoundException {
        if (!newLabWork.validate()) {
            throw new IllegalArgumentException("Некорректные данные LabWork");
        }

        LabWork finalNewLabWork = newLabWork;
        if (!collection.removeIf(lw -> lw.getId() == finalNewLabWork.getId())) {
            throw new ElementNotFoundException("Элемент с ID " + newLabWork.getId() + " не найден");
        }

        if (newLabWork.getCreationDate() == null) {
            newLabWork = new LabWork(
                    newLabWork.getId(),
                    newLabWork.getName(),
                    newLabWork.getCoordinates(),
                    ZonedDateTime.now(),
                    newLabWork.getMinimalPoint(),
                    newLabWork.getTunedInWorks(),
                    newLabWork.getDifficulty(),
                    newLabWork.getDiscipline()
            );
        }

        collection.add(newLabWork);
        logger.info("Обновлён элемент: {}", newLabWork);
        return false;
    }

    /**
     * Очищает всю коллекцию.
     */
    public void clear() {
        collection.clear();
        logger.info("Коллекция была очищена");
    }

    /**
     * Сохраняет коллекцию в файл.
     *
     * @throws IOException При ошибке сохранения
     */
    public void saveCollection() throws IOException {
        Set<Integer> usedIds = new HashSet<>();
        List<LabWork> validWorks = new ArrayList<>();

        for (LabWork labWork : collection) {
            if (!labWork.validate()) {
                logger.warn("Пропущен невалидный элемент при сохранении: {}", labWork);
                continue;
            }

            if (usedIds.contains(labWork.getId())) {
                logger.warn("Дубликат ID пропущен при сохранении: {}", labWork.getId());
                continue;
            }

            usedIds.add(labWork.getId());
            validWorks.add(labWork);
        }
        CsvCollectionManager csvManager = new CsvCollectionManager("./data/collection.csv");
        csvManager.saveCollection(validWorks);
        logger.info("Коллекция сохранена ({} элементов)", validWorks.size());
    }

    /**
     * Загружает коллекцию из файла.
     *
     * @param filePath путь к файлу
     * @throws IOException При ошибке чтения
     * @throws DeserializationException При ошибках десериализации
     */
    public void loadCollection(String filePath) throws IOException, DeserializationException {
        CsvCollectionManager csvManager = new CsvCollectionManager(filePath);
        List<LabWork> loaded = new ArrayList<>(csvManager.loadCollection());

        Set<Integer> usedIds = new HashSet<>();
        List<LabWork> validWorks = new ArrayList<>();

        for (LabWork labWork : loaded) {
            if (!labWork.validate()) {
                logger.warn("Пропущен невалидный элемент: {}", labWork);
                continue;
            }

            if (usedIds.contains(labWork.getId())) {
                logger.warn("Обнаружен дубликат ID: {}", labWork.getId());
                continue;
            }

            usedIds.add(labWork.getId());
            validWorks.add(labWork);
        }

        collection.clear();
        collection.addAll(validWorks);

        if (!validWorks.isEmpty()) {
            lastId = validWorks.stream()
                    .mapToInt(LabWork::getId)
                    .max()
                    .orElse(1);
        }

        logger.info("Загружено {} элементов", validWorks.size());
    }

    /**
     * Получить элемент по ID.
     *
     * @param id идентификатор элемента
     * @return элемент или null, если его нет
     */
    public LabWork getLabWorkById(int id) {
        return collection.stream()
                .filter(lw -> lw.getId() == id)
                .findFirst()
                .orElse(null);
    }

    /**
     * Получить первый элемент (без удаления).
     *
     * @return Первый элемент или null, если коллекция пуста
     */
    public LabWork getHead() {
        return collection.peek();
    }

    /**
     * Удалить и вернуть первый элемент.
     *
     * @return Удаленный элемент или null, если коллекция пуста
     */
    public LabWork removeFirst() {
        return collection.poll();
    }

    /**
     * Удаляет все элементы с ID меньше заданного.
     *
     * @param id граничный ID
     */
    public void removeLower(int id) {
        Iterator<LabWork> iterator = collection.iterator();
        int count = 0;

        while (iterator.hasNext()) {
            LabWork labWork = iterator.next();
            if (labWork.getId() < id) {
                iterator.remove();
                count++;
            }
        }

        logger.info("Удалено {} элементов с ID < {}", count, id);
    }




    /**
     * Возвращает количество элементов в коллекции.
     *
     * @return Размер коллекции
     */
    public int size() {
        return collection.size();
    }

    /**
     * Проверяет, пуста ли коллекция.
     *
     * @return true, если коллекция пуста
     */
    public boolean isEmpty() {
        return collection.isEmpty();
    }

    /**
     * Сортирует коллекцию заново.
     */
    public void sort() {
        List<LabWork> sortedList = new ArrayList<>(collection);
        sortedList.sort(Comparator.naturalOrder());
        collection.clear();
        collection.addAll(sortedList);
        logger.info("Коллекция отсортирована");
    }

    /**
     * Возвращает строковое представление коллекции.
     *
     * @return описание коллекции
     */
    @Override
    public String toString() {
        return "CollectionManager{" +
                "collection=" + collection +
                ", size=" + size() +
                ", created=" + creationDate +
                '}';
    }
}