package Server;

import spacemarine.*;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Класс для хранения и обработки LinkedList
 */
public class CollectionUser {

	
	
    /**
     * Дата создания списка
     */
    private Date date = new Date();
    /**
     * Список, в котором хранятся элементы типа User
     */
    public LinkedList<User> list = new LinkedList<>();

    public Date getDate() {
        return date;
    }

    
}
