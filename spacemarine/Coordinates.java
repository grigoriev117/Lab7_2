package spacemarine;

import Exceptions.FailedCheckException;

/**
 * ����� - ���� ������ SpaceMarine
 */

public class Coordinates {
    private int x; //���� ����� ���� null
    private Double y; //�������� ���� ������ ���� ������ -765, ���� �� ����� ���� null

    public Coordinates(int x, Double y) {
        this.x = x;
        this.y = y;
    }

    /**
     * �������� ��� x Integer
     */
    public static Checker<Integer> xCheck = (Integer I) -> {
        if (I != null) return I;
        else throw new FailedCheckException();
    };

    /**
     * �������� ��� y Long
     */
    public static Checker<Double> yCheck = (Double D) -> {
        if (D != null) return D;
        else throw new FailedCheckException();
    };

    public int getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    @Override
    public String toString() {
        return "Coordinates{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}