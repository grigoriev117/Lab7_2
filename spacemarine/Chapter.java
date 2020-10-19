package spacemarine;

import Exceptions.FailedCheckException;

/**
 * ����� - ���� ������ SpaceMarine
 */

public class Chapter {
    private String name; //���� �� ����� ���� null, ������ �� ����� ���� ������
    private String parentLegion;


    public Chapter(String name, String parentLegion) {
        this.name = name;
        this.parentLegion = parentLegion;
    }

 
    /**
     * �������� String
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
