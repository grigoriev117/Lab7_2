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
                Writer.writeln("����� ������� ���. ������� help, ����� ���������� ������ ��������� ������");
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
	* ���������� ���������� �� ���� ��������� ��������
	*/

    public static Writer help() {
        Writer w = new Writer();
        w.addToList(true,
        		"help : ������� ������� �� ��������� ��������\n"+
        				"register : ������������������\n" +
        				"login : ����� � ������� ������\n" +
        				"info : ������� � ����������� ����� ������ ���������� � ��������� (���, ���� �������������, ���������� ��������� � �.�.)\n"+
        				"show : ������� � ����������� ����� ������ ��� �������� ��������� � ��������� �������������\n"+
        				"add {element} : �������� ����� ������� � ���������\n"+
        				"update id {element} : �������� �������� �������� ���������, id �������� ����� ���������\n"+
        				"remove_by_id id : ������� ������� �� ��������� �� ��� id\n"+
        				"clear : �������� ���������\n"+
        				"execute_script file_name : ������� � ��������� ������ �� ���������� �����.\n"+
        				"� ������� ���������� ������� � ����� �� ����, � ������� �� ������ ������������ � ������������� ������.\n"+
        				"exit : ��������� ��������� (��� ���������� � ����)\n"+
        				"remove_first : ������� ������ ������� �� ���������\n"+
        				"head : ������� ������ ������� ���������\n"+
        				"remove_head : ������� ������ ������� ��������� � ������� ���\n"+
        				"remove_all_by_weapon_type weaponType : ������� �� ��������� ��� ��������, �������� ���� weaponType �������� ������������ ���������\n"+
        				"group_counting_by_chapter : ������������� �������� ��������� �� �������� ���� chapter, ������� ���������� ��������� � ������ ������\n"+
        				"filter_less_than_loyal loyal : ������� ��������, �������� ���� loyal ������� ������ ���������\n"
        				);

        w.addToList(false,"end");
        return w;
    }
    
    public static Writer info(Collection collection) {
        Writer w = new Writer();
        w.addToList(true, "��� ���������: " + collection.map.getClass().getName());
        w.addToList(true, "����������� ������������������ �������������: " + collection.map.size());
        w.addToList(true, "�������� �������: " + collection.getDate());
        w.addToList(false,"end");
        return w;
    }
    
    private static Writer login(Collection c, CommandSimple com) {
        Writer w = new Writer();
        if (c.isUserInMap(com.getUser()))
            w.addToList(true, "\u001B[32m" + "�� ���������������� ��� �������: " + com.getUser().login + "\u001B[0m");
        else {
            w.addToList(true, "�� ������ �����.");
            w.addToList(true, "��������� ����� � ������.");
        }

        w.addToList(false,"end");
        return w;
    }

    
    
    /**
    * ��������� � ��������� ������ �� ���������� �����.
    * � ������� ���������� ������� � ����� �� ����, � ������� �� ������ ������������ � ������������� ������
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
                w.addToList(false, "\u001B[33m" + "������ ������� � ����� " + s + ": " + "\u001B[0m");
                String line = reader.read(w);
                while (line != null && programIsWorking) {
                com = AbstractReader.splitter(line);
                programIsWorking = Command.switcher(w, reader, c, com[0], com[1], sqlRun, command.getUser());
                w.addToList(false, "\u001B[33m" + "������ ������� � ����� " + s + ": " + "\u001B[0m");
                line = reader.read(w);
                }
                RecursionHandler.removeLast();
                } else
                w.addToList(true, "\u001B[31m" + "������� ����������" + "\u001B[0m");

                } catch (Exceptions.IncorrectFileNameException e) {
                w.addToList(true, "\u001B[31m" + "�������� ��� �����" + "\u001B[0m");
                } catch (Exceptions.EndOfFileException e) {
                w.addToList(true, "\u001B[31m" + "����������� ����� ����� " + s + "\u001B[0m");
                RecursionHandler.removeLast();
                } catch (FileNotFoundException e) {
                w.addToList(true, "\u001B[31m" + "���� �� ������" + "\u001B[0m");
                } catch (FailedCheckException | NumberFormatException e) {
                w.addToList(true, "\u001B[31m" + "���� �������� ������������ ������" + "\u001B[0m");
                RecursionHandler.removeLast();
                }

        w.addToList(false,"end");
        return w;
    }
    
    public static List<SpaceMarine> properUser(Writer w, User user, Collection collection) {
        if (collection.isUserInMap(user))
            return collection.map.get(user);
        else {
            w.addToList(true, "�� ������ ��������� �������.");
            w.addToList(true, "��������������� ��� ��������� ������������ ������ � ������.");
        }
        return null;
    }
    
  
    
    /**
    * �������������� ������� ������ � ��������� id
    */
    public static Writer update(Collection c, CommandSimple com, PostgreSQL sqlRun) {
    Writer w = new Writer();
    List<SpaceMarine> list = properUser(w, com.getUser(), c);
    SpaceMarine sm1 = deser( ((String) com.returnObj()).replace("{", "").replace("}", "").replace(" ", ""));
    
    
    if (list != null) {
        Long id = sm1.getId();
        SpaceMarine sm2 = null;
        for (SpaceMarine s : list) {
            if (s.getId().equals(id)) {
                sm2 = s;
            }
        }
        if (sm2 == null) {
            w.addToList(true, "� ��������� ��� ��������� ������ �������� ���");

            w.addToList(false, "end");
            return w;
        }
        list.set(list.indexOf(sm2), (SpaceMarine) deser( ((String) com.returnObj())
        		.replace("{", "").replace("}", "").replace(" ", "")));
        
        w.addToList(true, "������� � id: " + id + " ������� �������");
        sqlRun.add(com);
    }

    w.addToList(false,"end");
    return w;
}
    
    
   

    /**
    * ������� ��� �������� �� ���������
    */
    public static Writer clear(Collection c, CommandSimple com, PostgreSQL sqlRun) {
    Writer w = new Writer();
    
    List<SpaceMarine> list = properUser(w, com.getUser(), c);
    if (list != null) {
        list.clear();
        w.addToList(true, "��������� ��� ��������� �������");
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
            w.addToList(true, "� ��������� ��� ��������� ������ �������� ���");

            w.addToList(false, "end");
            return w;
        }
        else {
        if (smm.getCreator().equals(com.getUser().login)) {
        list.remove(smm);
        w.addToList(true, "������� � id: " + id + " ������� ������. ");
        sqlRun.add(com);}
        else {
        	w.addToList(true, "������� �� ����� ���� �����! �� �� ��������� ��� ����������.");
            
        }
        }
    }
    else {
    	w.addToList(true, "� ��������� ��� ��������� ������ �������� ���.");
    }

    w.addToList(false,"end");
    return w;
    }
    
    public static SpaceMarine routeWithId(SpaceMarine sm, Long id) {
        sm.setId(id);
        return sm;
    }
    
    private static Writer register(Collection c, CommandSimple com, PostgreSQL sqlRun) {
        Writer w = new Writer();
        if (!c.isLoginUsed(com.getUser().login)) {
            c.map.put(com.getUser(), new CopyOnWriteArrayList<>());
            w.addToList(true, "\u001B[32m" + "������������ ������� ���������������." + "\u001B[0m");
            sqlRun.add(com);
        }
        else {
            w.addToList(true, "������������ � ����� ������� ��� ����������.");
            w.addToList(true, "���������� ������ �����.");
        }

        w.addToList(false,"end");
        return w;
    }



    /**
    * ������� ��� �������� ������
    */
    
    public static Writer show(Collection c, CommandSimple com) {
        Writer w = new Writer();
        List<SpaceMarine> list = properUser(w, com.getUser(), c);
        if (list != null) {
        	for (User user : c.map.keySet()) {
                w.addToList(true, "\u001B[35m" + "��� ����������� �������� �������������: " + "\u001B[32m" + user.login + "\u001B[0m");
                if (c.map.get(user).isEmpty())
                    w.addToList(true, "� ��������� ��� ���������");
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
    w.addToList(true, "������� �������� � ���������");
    w.addToList(false,"end");
    return sm;
    }

    /**
    * ��������� ������� � ������
    */
    public static Writer add(Collection c, CommandSimple com, PostgreSQL sqlRun) {
    Writer w = new Writer();
    List<SpaceMarine> list = properUser(w, com.getUser(), c);
    
    String s = (String) com.returnObj();
    s.replace("{", "").replace("}", "").replace(" ", "");
    SpaceMarine smm = deser(s);
    
    if (list != null) {
    	Long id = c.getNextId();
    	while (c.searchById(id) != null) {
        id = c.getNextId();
    	}
        list.add(routeWithId((SpaceMarine) smm, id));
        w.addToList(true, "������� ������� ��������");
        com.setID(id);
        sqlRun.add(com);
        
    }
    w.addToList(false,"end");
    return w;
    }

    public static SpaceMarine deser(String s) {
    // ��������� ���������
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
    
    
  //remove_first : ������� ������ ������� �� ���������
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
        w.addToList(true, "������ � ��c������ ��� ��������� �������� ���");

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
    	w.addToList(true, "\u001B[31m" + "�� �� ������ ������� ������ �������, ������ ��� �� ��������� ��� ����������" + "\u001B[0m");
        
   // w.addToList(true, "\u001B[31m" + "� ��������� ��� ���������" + "\u001B[0m");
    	}
    
    w.addToList(true, "...");
    Collections.sort(c.list);
    w.addToList(false,"end");
    return w;
    }
    // head : ������� ������ ������� ���������
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
    w.addToList(true, "\u001B[31m" + "� ��������� ��� ��������� ��� ������� ��������" + "\u001B[0m");
    }
    w.addToList(false,"end");
    return w;
    }
    //remove_head : ������� ������ ������� ��������� � ������� ���
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
            w.addToList(true, "������ � ��c������ ��� ��������� �������� ���");

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
        	w.addToList(true, "\u001B[31m" + "�� �� ������ ������� ������ �������, ������ ��� �� ��������� ��� ����������" + "\u001B[0m");
            
       // w.addToList(true, "\u001B[31m" + "� ��������� ��� ���������" + "\u001B[0m");
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
    w.addToList(true, "� ��������� ��� ���������");
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
    w.addToList(true, "������:");
    w.addToList(true, lan.get(j));
    int k = 0;
    for (SpaceMarine sm : flat) {
    if (sm.getChapter().getParentLegion().equals(lan.get(j))) {
    w.addToList(true, sm.toString());
    k = k+1;
    }
    }
    w.addToList(true, "����� ���������: " + k);
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
    w.addToList(true, "\u001B[32m" + "�������� � " + s + "������� ������" + "\u001B[0m");
    w.addToList(false,"end");
    }
    else {
    w.addToList(true, "\u001B[32m" + "������ ���� ������ ���� ���!" + "\u001B[0m");
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
    if (s == "0" || s == "f" || s == "false"|| s == "False" || s == "F") {w.addToList(true, "����� ��������� ���");}
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