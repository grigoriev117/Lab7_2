package Server;

import spacemarine.*;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * ����� ��� �������� � ��������� LinkedList
 */
public class CollectionUser {

	
	
    /**
     * ���� �������� ������
     */
    private Date date = new Date();
    /**
     * ������, � ������� �������� �������� ���� User
     */
    public LinkedList<User> list = new LinkedList<>();

    public Date getDate() {
        return date;
    }

    
}