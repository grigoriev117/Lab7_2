package Server;

import Exceptions.EndOfFileException;
import Exceptions.IncorrectFileNameException;
import spacemarine.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * ����� ��� ���������� ������ � �����
 */
public class Reader extends AbstractReader {
    public Reader(String file) throws IncorrectFileNameException, FileNotFoundException {
        File f = new File(file);
        if (!f.exists())
            throw new IncorrectFileNameException("������! ���� �� ������!");
        scan = new Scanner(new File(file));
    }

    /**
     * ���������� ������
     */
    @Override
    public String read(Writer w) {
        if (scan.hasNextLine()) {
            String line = scan.nextLine();
            w.addToList(false, line + "\n");
            return line;
        }
        w.addToList(false, "����� �����." + "\n");
        return null;
    }
    public String read() {
        if (scan.hasNextLine()) {
            return scan.nextLine();
        }
        return null;
    }
}