package Client;

import java.io.Serializable;

/**
 * ��������� Command
 */
public interface Command extends Serializable {
    /**
     * ���������
     *
     * @param par1 the par 1
     */
    abstract public void execute(String par1);

    /**
     * �������� �������� ���������� � �������
     *
     * @return ��������� ����������
     */
    abstract public String getInfo();
}