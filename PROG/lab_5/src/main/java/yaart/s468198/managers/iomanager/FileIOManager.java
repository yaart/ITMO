package yaart.s468198.managers.iomanager;

import yaart.s468198.file.FileReaderIterator;

/**
 * Реализация IOManager для чтения из файла
 */
public class FileIOManager implements IOManager {
    private final FileReaderIterator fileReaderIterator; // Итератор для чтения файла

    /**
     * Конструктор
     * @param iterator итератор файла
     */
    public FileIOManager(FileReaderIterator iterator) {
        this.fileReaderIterator = iterator;
    }

    @Override
    public void write(Object obj) {
        System.out.print(obj); // Вывод в стандартный поток
    }

    @Override
    public void writeLine(Object obj) {
        System.out.println(obj); // Вывод с новой строкой
    }

    @Override
    public void writeError(Object obj) {
        System.err.println(obj); // Вывод ошибок
    }

    @Override
    public String readLine() {
        return fileReaderIterator.next(); // Чтение строки из файла
    }

    @Override
    public boolean hasNext() {
        return fileReaderIterator.hasNext(); // Проверка конца файла
    }
}