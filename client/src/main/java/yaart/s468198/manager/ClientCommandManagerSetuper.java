package yaart.s468198.manager;

import yaart.s468198.base.iomanager.IOManager;
import yaart.s468198.network.NetworkClient;
import yaart.s468198.commands.*;

import java.util.stream.Collectors;

/**
 * ClientCommandManagerSetuper - класс для регистрации стандартного набора команд в ClientCommandManager.
 */
public class ClientCommandManagerSetuper {
    /**
     * Метод для загрузки стандартного набора команд в ClientCommandManager.
     *
     * @param networkClient сетевой клиент
     * @param ioManager     менеджер ввода-вывода
     * @param commandManager менеджер команд
     */
    public static void setupCommandManager(NetworkClient networkClient, IOManager ioManager, ClientCommandManager commandManager) {
        commandManager.clearCommands();

        commandManager.addCommand(new Clear(ioManager, networkClient));
        commandManager.addCommand(new Help(ioManager, networkClient));
        commandManager.addCommand(new Info(ioManager, networkClient));

        commandManager.addCommand(new Add(ioManager, networkClient));
        commandManager.addCommand(new CountLessThanDiscipline(ioManager, networkClient));
        commandManager.addCommand(new FilterBySize(ioManager, networkClient));
        commandManager.addCommand(new Head(ioManager, networkClient));
        commandManager.addCommand(new PrintFieldAscendingDiscipline(ioManager, networkClient));
        commandManager.addCommand(new PrintUniqueTunedInWorks(ioManager, networkClient));
        commandManager.addCommand(new RemoveById(ioManager, networkClient));
        commandManager.addCommand(new RemoveFirst(ioManager, networkClient));
        commandManager.addCommand(new RemoveLower(ioManager, networkClient));
        commandManager.addCommand(new Show(ioManager, networkClient));
        commandManager.addCommand(new UpdateId(ioManager, networkClient));
        commandManager.addCommand(new ExecuteScript(ioManager, networkClient, commandManager));
        commandManager.addCommand(new Exit(ioManager, networkClient));

        System.out.println("Зарегистрированы команды: " +
                commandManager.getCommands().stream()
                        .map(UserCommand::getName)
                        .collect(Collectors.joining(", "))
        );
    }

}