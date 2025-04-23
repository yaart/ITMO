package yaart.s468198.models;

/**
 * Перечисление, представляющее уровни сложности.
 */
public enum Difficulty {
    EASY,
    NORMAL,
    IMPOSSIBLE,
    INSANE,
    TERRIBLE;

    /**
     * Возвращает строку, содержащую все названия уровней сложности, разделенные запятыми.
     *
     * @return строка с названиями уровней сложности, разделенными запятыми
     */
    public static String getAllNames() {
        StringBuilder nameList = new StringBuilder();
        for (var difficulty : values()) {
            nameList.append(difficulty.name()).append(", ");
        }
        if (!nameList.isEmpty()) {
            nameList.setLength(nameList.length() - 2);
        }
        return nameList.toString();
    }
}