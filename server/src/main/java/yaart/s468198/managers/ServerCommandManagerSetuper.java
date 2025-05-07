package yaart.s468198.managers;

import yaart.s468198.commands.*;

/**
 * ServerCommandManagerSetuper - класс для регистрации всех команд в серверном CommandManager.
 */
public class ServerCommandManagerSetuper {
    /**
     * Метод для загрузки стандартного набора команд в менеджер команд сервера.
     *
     * @param collectionManager менеджер коллекции
     * @param commandManager    менеджер команд сервера
     */

    public static void setupCommandManager(CollectionManager collectionManager, ServerCommandManager commandManager) {


        commandManager.addCommand(new Add(collectionManager));
        commandManager.addCommand(new Clear(collectionManager));
        commandManager.addCommand(new Show(collectionManager));
        commandManager.addCommand(new Info(collectionManager));
        commandManager.addCommand(new RemoveById(collectionManager));
        commandManager.addCommand(new UpdateId(collectionManager));
        commandManager.addCommand(new Head(collectionManager));
        commandManager.addCommand(new Help(commandManager));
        commandManager.addCommand(new FilterBySize(collectionManager));
        commandManager.addCommand(new RemoveLower(collectionManager));
        commandManager.addCommand(new RemoveFirst(collectionManager));
        commandManager.addCommand(new CountLessThanDiscipline(collectionManager));
        commandManager.addCommand(new PrintFieldAscendingDiscipline(collectionManager));
        commandManager.addCommand(new PrintUniqueTunedInWorks(collectionManager));
    }
}