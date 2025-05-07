package yaart.s468198.base.iomanager;

import yaart.s468198.base.models.Coordinates;
import yaart.s468198.base.models.Difficulty;
import yaart.s468198.base.models.Discipline;
import yaart.s468198.base.models.LabWork;
import yaart.s468198.base.response.ServerResponse;

import java.time.ZonedDateTime;
import java.util.Scanner;

/**
 * Реализация IOManager для работы с консолью.
 */
public class StandartIOManager implements IOManager {
    private final Scanner scanner = new Scanner(System.in);

    @Override
    public void write(Object obj) {
        System.out.print(obj);
    }

    @Override
    public void writeLine(Object obj) {
        System.out.println(obj);
    }

    @Override
    public void writeError(Object obj) {
        System.err.println(obj);
    }

    @Override
    public String readLine() {
        return scanner.nextLine();
    }

    @Override
    public boolean hasNext() {
        return scanner.hasNext();
    }

    /**
     * Выводит ServerResponse пользователю
     */
    @Override
    public void printServerResponse(ServerResponse response) {
        switch (response.getType()) {
            case SUCCESS -> writeLine("[SERVER] " + response.getMessage());
            case ERROR -> writeError("[ERROR] " + response.getMessage());
            case CORRUPTED -> writeError("[CORRUPTED] " + response.getMessage());
            default -> writeLine(response.getMessage());
        }
    }

    /**
     * Выводит сообщение об ошибке
     */
    @Override
    public void printError(String s) {
        System.err.println(s);
    }
    public LabWork readLabWorkFromConsole() {
        write("Введите название лабораторной работы: ");
        String name = readLine().trim();

        write("X (long): ");
        long x = Long.parseLong(readLine().trim());

        write("Y (int): ");
        int y = Integer.parseInt(readLine().trim());

        ZonedDateTime creationDate = ZonedDateTime.now();

        write("Минимальный балл (float): ");
        float minimalPoint = Float.parseFloat(readLine().trim());

        write("TunedInWorks (int): ");
        int tunedInWorks = Integer.parseInt(readLine().trim());

        write("Сложность (EASY, NORMAL, HARD, TERRIBLE): ");
        Difficulty difficulty = Difficulty.valueOf(readLine().trim().toUpperCase());

        write("Имя дисциплины: ");
        String disciplineName = readLine().trim();

        Coordinates coordinates = new Coordinates(x, y);
        Discipline discipline = new Discipline(disciplineName, 1L, 1L, 1, 1);

        return new LabWork(0, name, coordinates, creationDate, minimalPoint, tunedInWorks, difficulty, discipline);
    }
}