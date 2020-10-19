package spacemarine;

import Exceptions.FailedCheckException;

/**
 * Класс - поле класса SpaceMarine
 */

public class Coordinates {
    private int x; //Поле может быть null
    private Double y; //Значение поля должно быть больше -765, Поле не может быть null

    public Coordinates(int x, Double y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Проверка для x Integer
     */
    public static Checker<Integer> xCheck = (Integer I) -> {
        if (I != null) return I;
        else throw new FailedCheckException();
    };

    /**
     * Проверка для y Long
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