package yaart.s468198.managers;

import yaart.s468198.commands.*;
import yaart.s468198.parser.CsvCollectionManager;
import yaart.s468198.managers.iomanager.IOManager;
import yaart.s468198.script.ScriptManager;

/**
 * CommandManagerSetuper - класс для загрузки всех необходимых команд в CommandManager.
 */

public class CommandManagerSetuper {
    public static void setupCommandManager(
            IOManager ioManager,
            CollectionManager collectionManager,
            CommandManager commandManager,
            CsvCollectionManager csvManager) {

        // Базовые команды
        commandManager.addCommand(new Help(commandManager, ioManager));
        commandManager.addCommand(new Exit(ioManager));

        // Команды работы с коллекцией
        commandManager.addCommand(new Add(collectionManager, ioManager));
        commandManager.addCommand(new Clear(collectionManager, ioManager));
        commandManager.addCommand(new Show(collectionManager, ioManager));
        commandManager.addCommand(new Info(collectionManager, ioManager));
        commandManager.addCommand(new RemoveById(collectionManager, ioManager));
        commandManager.addCommand(new UpdateId(collectionManager, ioManager));
        commandManager.addCommand(new Head(collectionManager, ioManager));

        // Команды для файлов
        commandManager.addCommand(new Save(collectionManager, csvManager, ioManager));
        commandManager.addCommand(new ExecuteScript(commandManager, ioManager));

        // Специальные команды
        commandManager.addCommand(new RemoveLower(collectionManager, ioManager));
        commandManager.addCommand(new RemoveFirst(collectionManager, ioManager));
        commandManager.addCommand(new PrintFieldAscendingDiscipline(collectionManager, ioManager));
        commandManager.addCommand(new CountLessThanDiscipline(collectionManager, ioManager));
        commandManager.addCommand(new PrintUniqueTunedInWorks(collectionManager, ioManager));
    }
}

