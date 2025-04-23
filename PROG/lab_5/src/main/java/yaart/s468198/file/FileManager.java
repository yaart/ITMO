package yaart.s468198.file;

import java.io.*;
import java.util.List;

/**
 * Менеджер для работы с файлами: чтение и запись данных
 */
public class FileManager {
    private final String path; // Путь к файлу

    /**
     * Конструктор создает файл, если он не существует
     * @param path путь к файлу
     * @throws IOException если произошла ошибка при создании файла
     */
    public FileManager(String path) throws IOException {
        this.path = path;
        File file = new File(path);
        if (!file.exists()) {
            file.createNewFile(); // Создаем новый файл, если не существует
        }
    }

    /**
     * Чтение всего содержимого файла
     * @return содержимое файла в виде строки
     * @throws IOException если произошла ошибка чтения
     */
    public String readAll() throws IOException {
        // Используем BufferedInputStream для эффективного чтения
        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(path));
             ByteArrayOutputStream bos = new ByteArrayOutputStream()) {

            byte[] buffer = new byte[1024]; // Буфер для чтения
            int length;
            // Читаем файл блоками по 1024 байта
            while ((length = bis.read(buffer)) != -1) {
                bos.write(buffer, 0, length);
            }
            return bos.toString("UTF-8"); // Преобразуем в строку с UTF-8 кодировкой
        }
    }

    /**
     * Запись строки в файл (перезаписывает содержимое)
     * @param text текст для записи
     * @throws IOException если произошла ошибка записи
     */
    public void writeAllToFile(String text) throws IOException {
        // Используем PrintWriter для удобной записи
        try (PrintWriter writer = new PrintWriter(new FileWriter(path))) {
            writer.print(text); // Записываем текст
        }
    }

    /**
     * Запись списка строк в файл (каждая строка с новой линии)
     * @param lines список строк для записи
     * @throws IOException если произошла ошибка записи
     */
    public void writeAllToFile(List<String> lines) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(path))) {
            for (String line : lines) {
                writer.println(line); // Записываем каждую строку с новой линии
            }
        }
    }
}