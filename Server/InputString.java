package Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


/**
 * ����� - ���������� �������� � ������� �������
 */
public class InputString extends Thread {

    /**
     * ���
     */
    public void run() {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        while (true) {
            try {
                System.out.print("������� ������� ��� �������: ");
                String s = reader.readLine();
                if (s == null | s.trim().equals("")) continue;
                switch (s.trim().toLowerCase()) {
                    case "save":
                        System.out.println("������� �����������");
                      //  saveCommand.execute(null);
                        break;
                    case "exit":
                        System.out.println("������ � �����������");
                    //    saveCommand.execute(null);
                   //     exit.execute("NotNull");
                        System.out.println("�����");
                        break;
                    
                    default:
                        System.out.println("����������� �������.");
                }
            } catch (IOException e) {
                System.out.println("������ �����");
            } catch (Exception e) {
                continue;
            }
        }
    }
}
