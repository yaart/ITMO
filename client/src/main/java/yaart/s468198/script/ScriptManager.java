package yaart.s468198.script;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import yaart.s468198.base.response.ServerResponse;
import yaart.s468198.exceptions.ScriptException;
import yaart.s468198.manager.ClientCommandManager;
import yaart.s468198.base.iomanager.IOManager;

import java.io.*;
import java.util.HashSet;

/**
 * ScriptManager - класс для запуска скриптов и отслеживания рекурсивных вызовов.
 */
public class ScriptManager {
    private static final Logger logger = LogManager.getRootLogger();
    private static final HashSet<String> recursionSet = new HashSet<>();

    private final String filepath;
    private final ClientCommandManager commandManager;
    private final IOManager ioManager;

    public ScriptManager(String filepath, ClientCommandManager commandManager, IOManager ioManager) {
        this.filepath = new File(filepath).getAbsolutePath();
        this.commandManager = commandManager;
        this.ioManager = ioManager;
    }

    /**
     * Метод для запуска скрипта.
     */
    public void runScript() throws ScriptException {
        if (recursionSet.contains(filepath)) {
            throw new ScriptException("Обнаружена рекурсия: " + filepath);
        }

        recursionSet.add(filepath);

        try (BufferedReader reader = new BufferedReader(new FileReader(filepath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                ioManager.writeLine("Выполняется команда: " + line);

                try {
                    ServerResponse response = commandManager.execute(line);
                    ioManager.writeLine(response);
                } catch (Exception e) {
                    ioManager.writeError("Ошибка выполнения: " + e.getMessage());
                    throw new ScriptException("Не удалось выполнить команду: " + e.getMessage(), e);
                }
            }
        } catch (FileNotFoundException e) {
            ioManager.writeError("Файл не найден: " + filepath);
            throw new ScriptException("Файл не найден", e);
        } catch (IOException e) {
            ioManager.writeError("Ошибка чтения файла: " + e.getMessage());
            throw new ScriptException("Ошибка чтения файла", e);
        } finally {
            recursionSet.remove(filepath);
        }
    }

    /**
     * Возвращает текущую глубину рекурсии.
     *
     * @return количество активных скриптов
     */
    public int getRecursionDepth() {
        return recursionSet.size();
    }
}