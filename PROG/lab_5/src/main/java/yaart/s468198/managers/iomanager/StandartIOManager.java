package yaart.s468198.managers.iomanager;

import java.util.Scanner;

/**
 * Реализация IOManager для стандартного ввода/вывода (консоль)
 */
public class StandartIOManager implements IOManager {
    private final Scanner scanner; // Сканнер для чтения ввода

    /**
     * Конструктор по умолчанию (чтение из System.in)
     */
    public StandartIOManager() {
        this.scanner = new Scanner(System.in);
    }

    /**
     * Конструктор с пользовательским сканнером
     * @param scanner сканнер для чтения ввода
     */
    public StandartIOManager(Scanner scanner) {
        this.scanner = scanner;
    }

    @Override
    public void write(Object obj) {
        System.out.print(obj); // Вывод без перевода строки
    }

    @Override
    public void writeLine(Object obj) {
        System.out.println(obj); // Вывод с переводом строки
    }

    @Override
    public void writeError(Object obj) {
        System.err.println(obj); // Вывод в stderr
    }

    @Override
    public String readLine() {
        return scanner.nextLine(); // Чтение строки
    }

    @Override
    public boolean hasNext() {
        return scanner.hasNext(); // Проверка наличия данных
    }
}