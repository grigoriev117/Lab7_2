package spacemarine;

import Exceptions.FailedCheckException;

public interface Checker<T> {
    T checker(T t) throws FailedCheckException;
}