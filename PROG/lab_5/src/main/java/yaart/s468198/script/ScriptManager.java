package yaart.s468198.script;

import yaart.s468198.exceptions.ScriptException;
import yaart.s468198.file.FileReaderIterator;
import yaart.s468198.managers.CommandManager;
import yaart.s468198.managers.iomanager.FileIOManager;

import java.io.File;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ScriptManager - класс для выполнения скриптов.
 *
 * Этот класс предоставляет функционал для выполнения команд из файлов скриптов,
 * а также обработки рекурсивных вызовов скриптов.
 */
public class ScriptManager {
    /**
     * Множество путей к выполняемым скриптам.
     * Используется для предотвращения рекурсивного выполнения одного и того же скрипта.
     */
    private static final Set<String> executingScripts = Collections.newSetFromMap(new ConcurrentHashMap<>());

    /**
     * Менеджер команд, используемый для выполнения команд из скрипта.
     */
    private final CommandManager commandManager;

    /**
     * Конструктор класса.
     *
     * @param commandManager менеджер команд для выполнения команд из скрипта
     */
    public ScriptManager(CommandManager commandManager) {
        this.commandManager = commandManager;
    }

    /**
     * Метод для выполнения скрипта.
     *
     * @param filePath путь к файлу скрипта
     * @throws ScriptException если произошла ошибка выполнения скрипта:
     *                         - обнаружена рекурсия в скриптах
     *                         - ошибка чтения файла
     *                         - ошибка выполнения команды
     */
    public void executeScript(String filePath) throws ScriptException {
        File scriptFile = new File(filePath).getAbsoluteFile();

        // Проверка на рекурсию
        if (!executingScripts.add(scriptFile.getAbsolutePath())) {
            throw new ScriptException("Обнаружена рекурсия в скриптах");
        }

        try (FileReaderIterator iterator = new FileReaderIterator(filePath)) {
            FileIOManager fileIoManager = new FileIOManager(iterator);

            // Выполнение команд из скрипта
            while (iterator.hasNext()) {
                String commandLine = iterator.next().trim();
                if (!commandLine.isEmpty()) {
                    commandManager.executeCommand(commandLine, fileIoManager);
                }
            }
        } catch (Exception e) {
            // Обработка ошибок выполнения скрипта
            throw new ScriptException("Ошибка выполнения скрипта '" + filePath + "'", e);
        } finally {
            // Удаление скрипта из множества выполняемых скриптов
            executingScripts.remove(scriptFile.getAbsolutePath());
        }
    }
}