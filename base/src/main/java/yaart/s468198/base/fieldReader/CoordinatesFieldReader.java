package yaart.s468198.base.fieldReader;


import yaart.s468198.base.exceptions.InvalidArgumentsException;
import yaart.s468198.base.iomanager.IOManager;
import yaart.s468198.base.models.Coordinates;

/**
 * CoordinatesFieldReader - класс для создания объекта Coordinates через пользовательский ввод.
 */
public class CoordinatesFieldReader {
    private final IOManager ioManager;

    /**
     * Конструктор класса
     * @param ioManager класс для работы с вводом-выводом
     */
    public CoordinatesFieldReader(IOManager ioManager) {
        this.ioManager = ioManager;
    }

    /**
     * Метод для получения объекта Label с использованием пользовательских данных
     * @throws InterruptedException если пользователь прервал ввод
     */
    public Coordinates executeCoordinates() throws InterruptedException {
        ioManager.writeLine("Ввод значений полей Coordinates");
        while (true) {

            LongFieldReader xReader = new LongFieldReader(this.ioManager, "Введите x: ");
            IntFieldReader yReader = new IntFieldReader(this.ioManager, "Введите y: ");

            var x = xReader.executeLong();
            var y = yReader.executeInt();

            try {
                Coordinates coordinates = new Coordinates(x, y);
                return coordinates;
            }
            catch(InvalidArgumentsException e) {
                ioManager.writeError(e.getMessage());
            }
        }
    }
}