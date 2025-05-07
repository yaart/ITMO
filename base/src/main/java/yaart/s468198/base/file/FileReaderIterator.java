package yaart.s468198.base.file;

import java.io.*;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Итератор для построчного чтения файла через BufferedInputStream
 */
public class FileReaderIterator implements AutoCloseable, Iterator<String> {
    private final BufferedInputStream bis; // Поток для чтения
    private final StringBuilder lineBuffer = new StringBuilder(); // Буфер для текущей строки
    private boolean endOfFile = false; // Флаг конца файла

    /**
     * Конструктор открывает файл для чтения
     * @param filePath путь к файлу
     * @throws IOException если не удалось открыть файл
     */
    public FileReaderIterator(String filePath) throws IOException {
        this.bis = new BufferedInputStream(new FileInputStream(filePath));
        checkEOF(); // Проверяем, не пустой ли файл
    }

    /**
     * Проверяет, есть ли еще строки для чтения
     * @return true если есть следующая строка
     */
    @Override
    public boolean hasNext() {
        return !endOfFile;
    }

    /**
     * Возвращает следующую строку из файла
     * @return следующая строка
     * @throws NoSuchElementException если достигнут конец файла
     */
    @Override
    public String next() {
        if (endOfFile) throw new NoSuchElementException("Достигнут конец файла");

        try {
            lineBuffer.setLength(0); // Очищаем буфер
            int currentByte;

            // Читаем байты до символа новой строки или конца файла
            while ((currentByte = bis.read()) != -1) {
                if (currentByte == '\n') break; // Конец строки
                if (currentByte != '\r') { // Игнорируем carriage return
                    lineBuffer.append((char) currentByte);
                }
            }

            checkEOF(); // Проверяем, не достигли ли конца файла
            return lineBuffer.toString(); // Возвращаем собранную строку
        } catch (IOException e) {
            throw new RuntimeException("Ошибка чтения файла", e);
        }
    }

    /**
     * Проверяет, достигнут ли конец файла
     */
    private void checkEOF() throws IOException {
        bis.mark(1); // Ставим метку
        endOfFile = (bis.read() == -1); // Проверяем следующий байт
        bis.reset(); // Возвращаемся к метке
    }

    /**
     * Закрывает поток чтения
     */
    @Override
    public void close() {
        try {
            bis.close();
        } catch (IOException ignored) {} // Игнорируем ошибку закрытия
    }
}