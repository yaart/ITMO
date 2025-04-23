package yaart.s468198;

/**
 * Главный класс приложения
 */
public class Main {
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Использование: java -jar program.jar <файл_коллекции.csv>");
            System.out.println("Если файла не существует, он будет создан автоматически");
            return;
        }

        try {
            new Run(args[0]).run();
        } catch (Exception e) {
            System.err.println("Критическая ошибка: " + e.getMessage());
            e.printStackTrace();
        }
    }
}