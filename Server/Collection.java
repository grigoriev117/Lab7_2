package Server;

import spacemarine.*;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Класс для хранения и обработки LinkedList
 */
public class Collection {

	
	Collection(long i)
    {
        ids = i;
    }
	
	public Map<User, List<SpaceMarine>> map1 = new ConcurrentHashMap<>();
	Map<User, List<SpaceMarine>> map = Collections.synchronizedMap(map1); 

    public Long ids;
	
    /**
     * Дата создания списка
     */
    private Date date = new Date();
    /**
     * Список, в котором хранятся элементы типа SpaceMarine
     */
    public LinkedList<SpaceMarine> list = new LinkedList<>();

    
    /**
     * Метод, возвращающий список, удобный для сохранения в формат CSV
     */
    public static Collection start(PostgreSQL sql) {
        Collection c = new Collection(sql.getIds());
        c.map = sql.getMapOfUsers();
        return c;
    }

    /**
     * Метод, осуществляющий поиск элемента по id
     */
    public SpaceMarine searchById(Long id) {
        for (User user: map.keySet()) {
            for (SpaceMarine r : map.get(user)) {
                if (r.getId().equals(id))
                    return r;
            }
        }
        return null;
    }
    
    public Long searchFirstId() {
        for (User user: map.keySet()) {
            for (SpaceMarine r : map.get(user)) {
                    return r.getId();
            }
        }
        return null;
    }
    
    public boolean isUserInMap(User user) {
        if (user.login.equals("login")) return false;
        return map.containsKey(user);
    }

    public boolean isLoginUsed(String login) {
        if (login.equals("login")) return true;
        for (User user : map.keySet()) {
            if (user.login.equals(login))
                return true;
        }
        return false;
    }
    
    public void getAll(PostgreSQL sqlRun) {
        sqlRun.setSpaceMarine(map);
    }

    public Date getDate() {
        return date;
    }
    
    /**
     * Метод, возвращающий уникальный id
     */
    public Long getNextId() {
        return ids++;
    }

    /**
     * Метод, возвращающий уникальный id
     */
    public Long getRandId() {
        Long id;
        do {
            id = (long) (1 + Math.random() * (Long.MAX_VALUE - 1));
        } while (this.searchById(id) != null);
        return id;
    }
}
