package spacemarine;

import Exceptions.FailedCheckException;

/**
 * Класс - поле класса SpaceMarine
 */

public class Chapter {
    private String name; //Поле не может быть null, Строка не может быть пустой
    private String parentLegion;


    public Chapter(String name, String parentLegion) {
        this.name = name;
        this.parentLegion = parentLegion;
    }

 
    /**
     * Проверка String
     */
    public static Checker<String> cCheck = (String S) -> {
        if (S != null) return S;
        else throw new FailedCheckException();
    };

    public String getName() {
        return name;
    }

    public String getParentLegion() {
        return parentLegion;
    }

    @Override
    public String toString() {
        return "Chapter{" +
                "name=" + name +
                ", parentLegion=" + parentLegion +
                '}';
    }
}
