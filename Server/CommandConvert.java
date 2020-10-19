package Server;
import spacemarine.*;
import command.*;
import dnl.utils.text.table.TextTable;
import Exceptions.FailedCheckException;
import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import Exceptions.EndOfFileException;
import Exceptions.IncorrectFileNameException;

import java.util.Date;
import java.util.LinkedList;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;
import java.util.Collections;

public class CommandConvert {
	
	public static Object[][] dataT;
	
	public static Writer switcher(CommandSimple com, Collection c, PostgreSQL sqlRun) throws IOException, EndOfFileException {
        switch (com.getCurrent()) {
            case HELP:
                return help();
            case INFO:
                return info(c);
            case SHOW:
                return show(c, com);
            case ADD:
                return add(c, com, sqlRun);
            case UPDATE:
                return update(c, com, sqlRun);
            case REMOVE_BY_ID:
                return removeById(c, com, sqlRun);
            case CLEAR:
                return clear(c, com, sqlRun);
            case EXECUTE_SCRIPT:
                return executeScript(c, com, sqlRun);
            case REMOVE_FIRST:
                return removeFirst(c, com, sqlRun);
            case HEAD:
                return head(c);
            case REMOVE_HEAD:
                return remove_head(c, com, sqlRun);
            case REMOVE_ALL_BY_WEAPON_TYPE:
                return remove_all_by_weapon_type(c, com, sqlRun);
            case GROUP_COUNTING_BY_CHAPTER:
                return group_counting_by_chapter(c);
            case FILTER_LESS_THAN_LOYAL:
                return filter_less_than_loyal(c, com);
            case LOGIN:
                return login(c, com);
            case REGISTER:
            	return register(c, com, sqlRun);
            default:
                Writer.writeln("Такой команды нет. Введите help, чтобы посмотреть список доступных команд");
        }
        return new Writer();
    }
	
	public static TextTable table(Object[][] data) {

		
		String[] columnNames = {"id", "name", "x", "y", "creationDate", "health", "loyal", "achievements", 
				"weaponType", "Chapter name", "Chapter legion", "creator"};
		
		
		TextTable tt = new TextTable(columnNames, data);
		return tt;
		
	}

	public static void addData(SpaceMarine r){
		String[] s = {
				Long.toString(r.getId()), 
				r.getName(), 
				Integer.toString(r.getCoordinstes().getX()),
				Double.toString(r.getCoordinstes().getY()),
				r.getCreationDate().toString(),
				Double.toString(r.getHealth()),
				Boolean.toString(r.getLoyal()),
				r.getAchievements(),
				r.getWeaponType().toString(),
				r.getChapter().getName(),
				r.getChapter().getParentLegion(),
				r.getCreator()
		};
		dataT[0][dataT[0].length] = s;
		
		
	}

	/**
	* Показывает информацию по всем возможным командам
	*/

    public static Writer help() {
        Writer w = new Writer();
        w.addToList(true,
        		"help : вывести справку по доступным командам\n"+
        				"register : зарегистрироваться\n" +
        				"login : войти в учетную запись\n" +
        				"info : вывести в стандартный поток вывода информацию о коллекции (тип, дата инициализации, количество элементов и т.д.)\n"+
        				"show : вывести в стандартный поток вывода все элементы коллекции в строковом представлении\n"+
        				"add {element} : добавить новый элемент в коллекцию\n"+
        				"update id {element} : обновить значение элемента коллекции, id которого равен заданному\n"+
        				"remove_by_id id : удалить элемент из коллекции по его id\n"+
        				"clear : очистить коллекцию\n"+
        				"execute_script file_name : считать и исполнить скрипт из указанного файла.\n"+
        				"В скрипте содержатся команды в таком же виде, в котором их вводит пользователь в интерактивном режиме.\n"+
        				"exit : завершить программу (без сохранения в файл)\n"+
        				"remove_first : удалить первый элемент из коллекции\n"+
        				"head : вывести первый элемент коллекции\n"+
        				"remove_head : вывести первый элемент коллекции и удалить его\n"+
        				"remove_all_by_weapon_type weaponType : удалить из коллекции все элементы, значение поля weaponType которого эквивалентно заданному\n"+
        				"group_counting_by_chapter : сгруппировать элементы коллекции по значению поля chapter, вывести количество элементов в каждой группе\n"+
        				"filter_less_than_loyal loyal : вывести элементы, значение поля loyal которых меньше заданного\n"
        				);

        w.addToList(false,"end");
        return w;
    }
    
    public static Writer info(Collection collection) {
        Writer w = new Writer();
        w.addToList(true, "Тип коллекции: " + collection.map.getClass().getName());
        w.addToList(true, "Колличество зарегестрированных пользователей: " + collection.map.size());
        w.addToList(true, "Коллеция создана: " + collection.getDate());
        w.addToList(false,"end");
        return w;
    }
    
    private static Writer login(Collection c, CommandSimple com) {
        Writer w = new Writer();
        if (c.isUserInMap(com.getUser()))
            w.addToList(true, "\u001B[32m" + "Вы авторизировались под логином: " + com.getUser().login + "\u001B[0m");
        else {
            w.addToList(true, "Не удаётся войти.");
            w.addToList(true, "Проверьте логин и пароль.");
        }

        w.addToList(false,"end");
        return w;
    }

    
    
    /**
    * Считывает и исполняет скрипт из указанного файла.
    * В скрипте содержатся команды в таком же виде, в котором их вводит пользователь в интерактивном режиме
    * @throws IOException
    */
    public static Writer executeScript(Collection c, CommandSimple command, PostgreSQL sqlRun) throws IOException {
        Writer w = new Writer();
        String s = (String) command.returnObj();
        boolean programIsWorking = true;
        try (Reader reader = new Reader(s)) {
            if (RecursionHandler.isContains(s)) {
                RecursionHandler.addToFiles(s);
                String[] com;
                w.addToList(false, "\u001B[33m" + "Чтение команды в файле " + s + ": " + "\u001B[0m");
                String line = reader.read(w);
                while (line != null && programIsWorking) {
                com = AbstractReader.splitter(line);
                programIsWorking = Command.switcher(w, reader, c, com[0], com[1], sqlRun, command.getUser());
                w.addToList(false, "\u001B[33m" + "Чтение команды в файле " + s + ": " + "\u001B[0m");
                line = reader.read(w);
                }
                RecursionHandler.removeLast();
                } else
                w.addToList(true, "\u001B[31m" + "Найдено повторение" + "\u001B[0m");

                } catch (Exceptions.IncorrectFileNameException e) {
                w.addToList(true, "\u001B[31m" + "Неверное имя файла" + "\u001B[0m");
                } catch (Exceptions.EndOfFileException e) {
                w.addToList(true, "\u001B[31m" + "Неожиданный конец файла " + s + "\u001B[0m");
                RecursionHandler.removeLast();
                } catch (FileNotFoundException e) {
                w.addToList(true, "\u001B[31m" + "Файл не найден" + "\u001B[0m");
                } catch (FailedCheckException | NumberFormatException e) {
                w.addToList(true, "\u001B[31m" + "Файл содержит неправильные данные" + "\u001B[0m");
                RecursionHandler.removeLast();
                }

        w.addToList(false,"end");
        return w;
    }
    
    public static List<SpaceMarine> properUser(Writer w, User user, Collection collection) {
        if (collection.isUserInMap(user))
            return collection.map.get(user);
        else {
            w.addToList(true, "Не удаётся выполнить команду.");
            w.addToList(true, "Авторизируйтесь или проверьте правильность логина и пароля.");
        }
        return null;
    }
    
  
    
    /**
    * Перезаписывает элемент списка с указанным id
    */
    public static Writer update(Collection c, CommandSimple com, PostgreSQL sqlRun) {
    Writer w = new Writer();
    List<SpaceMarine> list = properUser(w, com.getUser(), c);
    SpaceMarine sm1 = (SpaceMarine) com.returnObj();
    
    
    if (list != null) {
        Long id = sm1.getId();
        SpaceMarine sm2 = null;
        for (SpaceMarine s : list) {
            if (s.getId().equals(id)) {
                sm2 = s;
            }
        }
        if (sm2 == null) {
            w.addToList(true, "В доступной Вам коллекции такого элемента нет");

            w.addToList(false, "end");
            return w;
        }
        list.set(list.indexOf(sm2), (SpaceMarine) deser( ((String) com.returnObj())
        		.replace("{", "").replace("}", "").replace(" ", "")));
        
        w.addToList(true, "Элемент с id: " + id + " успешно изменен");
        sqlRun.add(com);
    }

    w.addToList(false,"end");
    return w;
}
    
    
   

    /**
    * Удаляет все элементы из коллекции
    */
    public static Writer clear(Collection c, CommandSimple com, PostgreSQL sqlRun) {
    Writer w = new Writer();
    
    List<SpaceMarine> list = properUser(w, com.getUser(), c);
    if (list != null) {
        list.clear();
        w.addToList(true, "Доступная Вам коллекция очищена");
        sqlRun.add(com);
    }
    w.addToList(false,"end");
    return w;
    }

    public static Writer removeById(Collection c, CommandSimple com, PostgreSQL sqlRun) {
    Writer w = new Writer();
    
    List<SpaceMarine> list = properUser(w, com.getUser(), c);
    
    if (list != null) {
        Long id = (Long) com.returnObj();
        SpaceMarine smm = c.searchById(id);
        if (smm == null) {
            w.addToList(true, "В доступной Вам коллекции такого элемента нет");

            w.addToList(false, "end");
            return w;
        }
        else {
        if (smm.getCreator().equals(com.getUser().login)) {
        list.remove(smm);
        w.addToList(true, "Элемент с id: " + id + " успешно удален. ");
        sqlRun.add(com);}
        else {
        	w.addToList(true, "Элемент не может быть удалён! Вы не являетесь его создателем.");
            
        }
        }
    }
    else {
    	w.addToList(true, "В доступной Вам коллекции такого элемента нет.");
    }

    w.addToList(false,"end");
    return w;
    }
    
    public static SpaceMarine SMWithId(SpaceMarine sm, Long id) {
        sm.setId(id);
        return sm;
    }
    
    private static Writer register(Collection c, CommandSimple com, PostgreSQL sqlRun) {
        Writer w = new Writer();
        if (!c.isLoginUsed(com.getUser().login)) {
            c.map.put(com.getUser(), new CopyOnWriteArrayList<>());
            w.addToList(true, "\u001B[32m" + "Пользователь успешно зарегестрирован." + "\u001B[0m");
            sqlRun.add(com);
        }
        else {
            w.addToList(true, "Пользователь с таким логином уже существует.");
            w.addToList(true, "Попробуйте другой логин.");
        }

        w.addToList(false,"end");
        return w;
    }



    /**
    * Выводит все элементы списка
    */
    
    public static Writer show(Collection c, CommandSimple com) {
        Writer w = new Writer();
        List<SpaceMarine> list = properUser(w, com.getUser(), c);
        if (list != null) {
        	for (User user : c.map.keySet()) {
                w.addToList(true, "\u001B[35m" + "Все добавленные элементы поользователя: " + "\u001B[32m" + user.login + "\u001B[0m");
                if (c.map.get(user).isEmpty())
                    w.addToList(true, "В коллекции нет элементов");
                else
                    c.map.get(user).forEach(r -> w.addToList(true, r.toString()));
            }
        }

        w.addToList(false,"end");
        return w;
    } 
   

    public static SpaceMarine SM_ID(SpaceMarine sm, Long id) {
    Writer w = new Writer();
    sm.setId(id);
    w.addToList(true, "Элемент добавлен в коллекцию");
    w.addToList(false,"end");
    return sm;
    }

    /**
    * Добавляет элемент в список
    */
    public static Writer add(Collection c, CommandSimple com, PostgreSQL sqlRun) {
    Writer w = new Writer();
    List<SpaceMarine> list = properUser(w, com.getUser(), c);
    
    SpaceMarine smm = (SpaceMarine) com.returnObj();
    
    if (list != null) {
    	Long id = c.getNextId();
    	while (c.searchById(id) != null) {
        id = c.getNextId();
    	}
        list.add(SMId((SpaceMarine) smm, id));
        w.addToList(true, "Элемент успешно добавлен");
        com.setID(id);
        sqlRun.add(com);
        
    }
    w.addToList(false,"end");
    return w;
    }

    public static SpaceMarine deser(String s) {
    // считываем построчно
    String line = null;
    Scanner scanner = null;
    int index = 0;
    int cx = 1;
    Long id = null;
    Double cy = 1.0;
    String n1 = null;
    String n2 = null;
    SpaceMarine sm = new SpaceMarine();
    try {
        
            scanner = new Scanner(s.replace("{", "").replace("}", "").replace(" ", "").replace("'", ""));
            scanner.useDelimiter(",");
            while (scanner.hasNext()) {
                String data = scanner.next();
                if (index == 0) {
                	id = Long.parseLong(data.replace("SpaceMarineid=", ""));
                    sm.setId(id);
                }
                else if (index == 1)
                    sm.setName(data.replace("name=", ""));
                else if (index == 2)
                	cx = Integer.parseInt(data.replace("coordinates=Coordinatesx=", ""));
                else if (index == 3) {
                	cy = Double.parseDouble(data.replace("y=", ""));
                    sm.setCoordinates(new Coordinates(cx, cy)); }
                else if (index == 4) {
                	String aa = data;
                	LocalDate creationTime = LocalDate.now();
                    sm.setCreationDate(creationTime);
                }
                else if (index == 5)
                	sm.setHealth(Double.parseDouble(data.replace("health=", "")));
                else if (index == 6)
                	sm.setLoyal(Boolean.parseBoolean(data.replace("loyal=", "")));
                else if (index == 7)
                	sm.setAchievements(data.replace("achievements=", ""));
                else if (index == 8) {
                	String weaponType1 = data.replace("weaponType=", "");
                	if (weaponType1.equals("HEAVY_BOLTGUN") || weaponType1.equals("BOLT_RIFLE") || weaponType1.equals("PLASMA_GUN") || weaponType1.equals("COMBI_PLASMA_GUN") || weaponType1.equals("INFERNO_PISTOL"))
                    {
                    	sm.setWeaponType(SpaceMarine.Weapon.valueOf(weaponType1));
                    }
                }
                else if (index == 9)
                	n1 = data.replace("chapter=Chaptername=", "");
                else if (index == 10) {
                	n2 = data.replace("parentLegion=", "").replace("}", "");
                	sm.setChapter(new Chapter(n1, n2));
                }
                else if (index == 11)
                	sm.setCreator(data.replace("creator=", "").replace("}", ""));
                else
                	Writer.writeln("/");
                index++;
            
            }
            index = 0;
        
    } finally {}
 return sm;
    }
    
    
  //remove_first : удалить первый элемент из коллекции
    public static Writer removeFirst(Collection c, CommandSimple com, PostgreSQL sqlRun) throws EndOfFileException {
    Writer w = new Writer();
    List<SpaceMarine> list = properUser(w, com.getUser(), c);
    
    
ArrayList<List<SpaceMarine>> listAll = new ArrayList<>(c.map.values());
    
    List<SpaceMarine> flat = 
    		    listAll.stream()
    		        .flatMap(List::stream)
    		        .collect(Collectors.toList());
    Collections.sort(flat);

    if (c.list != null) {
    
    		
    //Writer.writeln("1");
    Long id = flat.get(0).getId();
    //Writer.writeln("1");
    SpaceMarine smm = c.searchById(id);
    if (smm == null) {
        w.addToList(true, "Такого в доcтупной вам коллекции элемента нет");

        w.addToList(false, "end");
        return w;
    }
    else {
    list.remove(smm);
   // Writer.writeln("1");
    CommandSimple com1 = new CommandLonArg(com.getUser(), id);
    sqlRun.add(com1);
   // Writer.writeln("1");
    }
    }
    else {
    	w.addToList(true, "\u001B[31m" + "Вы не можете удалить первый элемент, потому что не являетесь его создателем" + "\u001B[0m");
        
   // w.addToList(true, "\u001B[31m" + "В коллекции нет элементов" + "\u001B[0m");
    	}
    
    w.addToList(true, "...");
    Collections.sort(c.list);
    w.addToList(false,"end");
    return w;
    }
    // head : вывести первый элемент коллекции
    public static Writer head(Collection c) throws EndOfFileException {
    	
    Writer w = new Writer();

    ArrayList<List<SpaceMarine>> listAll = new ArrayList<>(c.map.values());
    
    List<SpaceMarine> flat = 
    		    listAll.stream()
    		        .flatMap(List::stream)
    		        .collect(Collectors.toList());
    Collections.sort(flat);
    int size = flat.size();
    int i = 0;
    if (i < size) {
    w.addToList(true, flat.get(i).toString());
    }
    else {
    w.addToList(true, "\u001B[31m" + "В доступной Вам коллекции нет нужного элемента" + "\u001B[0m");
    }
    w.addToList(false,"end");
    return w;
    }
    //remove_head : вывести первый элемент коллекции и удалить его
    public static Writer remove_head(Collection c, CommandSimple com, PostgreSQL sqlRun) throws EndOfFileException {
    
    	Writer w = new Writer();
        List<SpaceMarine> list = properUser(w, com.getUser(), c);
        
        
    ArrayList<List<SpaceMarine>> listAll = new ArrayList<>(c.map.values());
        
        List<SpaceMarine> flat = 
        		    listAll.stream()
        		        .flatMap(List::stream)
        		        .collect(Collectors.toList());
        Collections.sort(flat);

        if (c.list != null) {
        
        		
        //Writer.writeln("1");
        Long id = flat.get(0).getId();
        //Writer.writeln("1");
        SpaceMarine smm = c.searchById(id);
        if (smm == null) {
            w.addToList(true, "Такого в доcтупной вам коллекции элемента нет");

            w.addToList(false, "end");
            return w;
        }
        else {
        w.addToList(true, smm.toString());
        list.remove(smm);
       // Writer.writeln("1");
        CommandSimple com1 = new CommandLonArg(com.getUser(), id);
        sqlRun.add(com1);
       // Writer.writeln("1");
        }
        }
        else {
        	w.addToList(true, "\u001B[31m" + "Вы не можете удалить первый элемент, потому что не являетесь его создателем" + "\u001B[0m");
            
       // w.addToList(true, "\u001B[31m" + "В коллекции нет элементов" + "\u001B[0m");
        	}
        
        w.addToList(true, "...");
        Collections.sort(c.list);
        w.addToList(false,"end");
        return w;
    
    }
    //group_counting_by_chapter

    public static Writer group_counting_by_chapter(Collection c) {
    Writer w = new Writer();
    ArrayList<List<SpaceMarine>> listAll = new ArrayList<>(c.map.values());
    
    List<SpaceMarine> flat = 
    		    listAll.stream()
    		        .flatMap(List::stream)
    		        .collect(Collectors.toList());
    if (flat.isEmpty()) {
    w.addToList(true, "В коллекции нет элементов");
    w.addToList(false,"end");
    return w;
    }
    else {
    LinkedList lan = new LinkedList<>();
    //boolean contains(Object element);
    for (SpaceMarine sm : flat) {
    if (!lan.contains(sm.getChapter().getParentLegion())) {
    lan.add(sm.getChapter().getParentLegion());
    }
    }
    for (int j = 0; j < lan.size(); j++) {
    w.addToList(true, "Легион:");
    w.addToList(true, lan.get(j));
    int k = 0;
    for (SpaceMarine sm : flat) {
    if (sm.getChapter().getParentLegion().equals(lan.get(j))) {
    w.addToList(true, sm.toString());
    k = k+1;
    }
    }
    w.addToList(true, "Всего элементов: " + k);
    }
    w.addToList(false,"end");
    return w;

    }}
    /**
    * remove_all_by_weapon_type
    * @param w
    */
    public static Writer remove_all_by_weapon_type(Collection c, CommandSimple com, PostgreSQL sqlRun) {
    Writer w = new Writer();
    CommandSimple com1 = null;
    String s = (String) com.returnObj();
    if (s.equals("HEAVY_BOLTGUN") || s.equals("BOLT_RIFLE") || s.equals("PLASMA_GUN") || s.equals("COMBI_PLASMA_GUN") || s.equals("INFERNO_PISTOL"))
    {
    List<SpaceMarine> list = properUser(w, com.getUser(), c);
    int i = 0;
    List<SpaceMarine> listDelete = new ArrayList<SpaceMarine>();

    for (SpaceMarine sm : list) {
    	     if (sm.getWeaponType().toString().equals(s)) {
    	    	 listDelete.add(sm);
    	    	 }
    	 			else {
    	 				i = i+1;
    	 			}}
    	 		for (SpaceMarine sm : listDelete)
    	 		{
    	 	//	Long id = list.get((int) num.get(j)).getId();
    	 		
    	 		com1 = new CommandLonArg(com.getUser(), sm.getId());
    	 		list.remove(sm);
    	 	    sqlRun.add(com1);
    	 		
    	                   }
     		Collections.sort(c.list); 
    w.addToList(true, "\u001B[32m" + "Элементы с " + s + "успешно далены" + "\u001B[0m");
    w.addToList(false,"end");
    }
    else {
    w.addToList(true, "\u001B[32m" + "Такого типа оружия пока нет!" + "\u001B[0m");
    w.addToList(false,"end");
    return w;
    }
    return w;
    }


    /**
    * filter_less_than_loyal
    * @param w
    */
    public static Writer filter_less_than_loyal(Collection c, CommandSimple com) throws EndOfFileException {
    Writer w = new Writer();
ArrayList<List<SpaceMarine>> listAll = new ArrayList<>(c.map.values());
    
    List<SpaceMarine> flat = 
    		    listAll.stream()
    		        .flatMap(List::stream)
    		        .collect(Collectors.toList());
    String s = (String) com.returnObj();
    if (s == "0" || s == "f" || s == "false"|| s == "False" || s == "F") {w.addToList(true, "Таких элементов нет");}
    else {
    for (SpaceMarine sm : flat) {
    if (sm.getLoyal() == false)
    w.addToList(true, sm.toString());;
    }
    }
    Collections.sort(c.list);
    w.addToList(false,"end");
    return w;
    }
    }
