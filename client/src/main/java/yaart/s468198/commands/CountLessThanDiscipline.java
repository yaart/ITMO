package yaart.s468198.commands;

import yaart.s468198.base.exceptions.CommandArgumentException;
import yaart.s468198.base.fieldReader.DisciplineFieldReader;
import yaart.s468198.base.fieldReader.LabWorkFieldReader;
import yaart.s468198.base.models.Discipline;
import yaart.s468198.base.models.LabWork;
import yaart.s468198.base.response.ServerResponse;
import yaart.s468198.base.iomanager.IOManager;
import yaart.s468198.base.response.ServerResponseType;
import yaart.s468198.network.NetworkClient;

import java.io.Serializable;
import java.util.List;

/**
 * CountLessThanDiscipline - клиентская команда для подсчёта элементов,
 * у которых дисциплина меньше заданной.
 */
public class CountLessThanDiscipline extends UserCommand {
    public CountLessThanDiscipline(IOManager ioManager, NetworkClient networkClient) {
        super("count_less_than_discipline", "count_less_than_discipline {discipline} : вывести количество элементов, значение поля discipline которых меньше заданного", ioManager, networkClient);
    }

    @Override
    public ServerResponse execute(List<Serializable> args) throws CommandArgumentException {

        try {
            Discipline discipline = new DisciplineFieldReader(ioManager).executeDiscipline(); // ID = 0 → генерируется на сервере

            return sendAndReceive(getName(), List.of(discipline));
        } catch (Exception e) {
            return new ServerResponse(ServerResponseType.ERROR, "Ошибка при выполнении команды: " + e.getMessage());
        }
    }
}