package Server;
import Exceptions.FailedCheckException;
import java.io.FileNotFoundException;
import java.time.LocalDate;
import Exceptions.EndOfFileException;
import Exceptions.IncorrectFileNameException;
import command.CommandA;
import command.CommandArgs;
import command.CommandLonArg;
import command.CommandSimple;
import command.CommandsList;
import spacemarine.*;

import java.util.Date;
import java.util.LinkedList;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

import Client.ConsoleClient;

import java.util.Collections;
/**
 * ����� ��� ��������� ������, �������� � �������
 */

public class Command {
	

    /**
     * ��������� ������
     * @throws IOException 
     * @throws NumberFormatException 
     * @throws FailedCheckException 
     */
    public static boolean switcher(Writer w, AbstractReader reader, Collection c, String s1, String s2, PostgreSQL sql, User user) throws EndOfFileException, NumberFormatException, IOException, FailedCheckException {
        switch (s1) {
            case ("help"):
                help(w);
                break;
            case ("info"):
                info(w, c);
                break;
            case ("show"):
                show(w, c, user);
                break;
            case ("add"):
                add(w, reader, c, s2, sql, user);
                break;
            case ("update"):
                update(w, reader, c, s2, sql, user);
                break;
            case ("remove_by_id"):
                removeById(w, reader, c, s2, sql, user);
                break;
            case ("clear"):
                clear(w, c, sql, user);
                break;
            case ("execute_script"):
                return executeScript(w, c, s2, sql, user);
            case ("exit"):
                return false;
            case ("remove_first"):
            	removeFirst(w, c, sql, user);
                break;
            case ("head"):
            	head(w, c);
                break;
            case ("remove_head"):
            	remove_head(w, c, sql, user);
                break;
            case ("remove_all_by_weapon_type"):
            	remove_all_by_weapon_type(w, reader, c, s2, sql, user);
                break;
            case ("group_counting_by_chapter"):
            	group_counting_by_chapter(w, c);
                break;
            case ("filter_less_than_loyal"):
            	filter_less_than_loyal(w, reader, c, s2);
                break;
            default:
            	w.addToList(true, "����� ������� �� ����������!\n ������� 'help', ����� ���������� ������ ��������� ������.");
        }
        return true;
    }
        
 interface Comparable<T> extends java.lang.Comparable<T> {

    }  


public static Checker<Boolean> boolCheck = (Boolean B) -> {
    if (B != null) return B;
    else throw new FailedCheckException();
};
    /**
     * help : ������� ������� �� ��������� ��������
     */
    public static void help(Writer w) {
    	w.addToList(true,
        				"help : ������� ������� �� ��������� ��������\n"+
    					"info : ������� � ����������� ����� ������ ���������� � ��������� (���, ���� �������������, ���������� ��������� � �.�.)\n"+
    					"show : ������� � ����������� ����� ������ ��� �������� ��������� � ��������� �������������\n"+
    					"add {element} : �������� ����� ������� � ���������\n"+
    					"update id {element} : �������� �������� �������� ���������, id �������� ����� ���������\n"+
    					"remove_by_id id : ������� ������� �� ��������� �� ��� id\n"+
    					"clear : �������� ���������\n"+
    					"save : ��������� ��������� � ����\n"+
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
    }
    
 // head : ������� ������ ������� ���������
    public static void head(Writer w, Collection c) throws EndOfFileException {
    
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
    
    }
    
    public static List<SpaceMarine> properUser(Writer w, User user, Collection collection) {
        if (collection.isUserInMap(user))
            return collection.map.get(user);
        else {
            w.addToList(true, "�� ������ ��������� �������.");
            w.addToList(true, "����������, ��������� ������������ ��������� ������ � ������.");
        }
        return null;
    }

    
    //remove_first : ������� ������ ������� �� ���������
    public static void removeFirst(Writer w, Collection c, PostgreSQL sql, User user) throws EndOfFileException {
    	List<SpaceMarine> list = properUser(w, user, c);
        
        
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

    	       
    	        
    	    }
    	    else {
    	    list.remove(smm);
    	   // Writer.writeln("1");
    	    CommandSimple com1 = new CommandLonArg(user, id);
    	    sql.add(com1);
    	   // Writer.writeln("1");
    	    }
    	    }
    	    else {
    	    	w.addToList(true, "\u001B[31m" + "�� �� ������ ������� ������ �������, ������ ��� �� ��������� ��� ����������" + "\u001B[0m");
    	        
    	   // w.addToList(true, "\u001B[31m" + "� ��������� ��� ���������" + "\u001B[0m");
    	    	}
    	    
    	    w.addToList(true, "...");
    	    Collections.sort(c.list);
        }
    //remove_head : ������� ������ ������� ��������� � ������� ���
            public static void remove_head(Writer w, Collection c, PostgreSQL sql, User user) throws EndOfFileException {
            	List<SpaceMarine> list = properUser(w, user, c);
                
                
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

                        
                        
                    }
                    else {
                    w.addToList(true, smm.toString());
                    list.remove(smm);
                   // Writer.writeln("1");
                    CommandSimple com1 = new CommandLonArg(user, id);
                    sql.add(com1);
                   // Writer.writeln("1");
                    }
                    }
                    else {
                    	w.addToList(true, "\u001B[31m" + "�� �� ������ ������� ������ �������, ������ ��� �� ��������� ��� ����������" + "\u001B[0m");
                        
                   // w.addToList(true, "\u001B[31m" + "� ��������� ��� ���������" + "\u001B[0m");
                    	}
                    
                    w.addToList(true, "...");
                    Collections.sort(c.list);
                }
            //group_counting_by_chapter
            
                        public static void group_counting_by_chapter(Writer w, Collection c) {
                        	ArrayList<List<SpaceMarine>> listAll = new ArrayList<>(c.map.values());
                            
                            List<SpaceMarine> flat = 
                            		    listAll.stream()
                            		        .flatMap(List::stream)
                            		        .collect(Collectors.toList());
                            if (flat.isEmpty()) {
                            w.addToList(true, "� ��������� ��� ���������");
                            
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
                            }}        
                /**
                 * remove_all_by_weapon_type
                 * @param w 
                 */
                public static void remove_all_by_weapon_type(Writer w, AbstractReader reader, Collection c, String s, PostgreSQL sql, User user) {
                    CommandSimple com1 = null;
                    
                    if (s.equals("HEAVY_BOLTGUN") || s.equals("BOLT_RIFLE") || s.equals("PLASMA_GUN") || s.equals("COMBI_PLASMA_GUN") || s.equals("INFERNO_PISTOL"))
                    {
                    List<SpaceMarine> list = properUser(w, user, c);
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
                    	 		
                    	 		com1 = new CommandLonArg(user, sm.getId());
                    	 		list.remove(sm);
                    	 	    sql.add(com1);
                    	 		
                    	                   }
                     		Collections.sort(c.list); 
                    w.addToList(true, "\u001B[32m" + "..." + "\u001B[32m");
                   
                    }
                    else {
                    w.addToList(true, "\u001B[32m" + "������ ���� ������ ���� ���!" + "\u001B[32m");
                    
                    }
                    }

            
                /**
                 * filter_less_than_loyal
                 * @param w 
                 */
                public static void filter_less_than_loyal(Writer w, AbstractReader reader, Collection c, String s) throws EndOfFileException {
                	ArrayList<List<SpaceMarine>> listAll = new ArrayList<>(c.map.values());
                    
                    List<SpaceMarine> flat = 
                    		    listAll.stream()
                    		        .flatMap(List::stream)
                    		        .collect(Collectors.toList());
                    
                    if (s == "0" || s == "f" || s == "false"|| s == "False" || s == "F") {w.addToList(true, "����� ��������� ���");}
                    else {
                    for (SpaceMarine sm : flat) {
                    if (sm.getLoyal() == false)
                    w.addToList(true, sm.toString());;
                    }
                    }
                    Collections.sort(c.list);
                    
                }
                
                
             public static void upload(Writer w, CommandReader reader, Collection c, String s) throws EndOfFileException, NumberFormatException, IOException {
    	BufferedReader reader1 = new BufferedReader(new FileReader(s));
        // ��������� ���������
        String line = null;
        Scanner scanner = null;
        int index = 0;
        int cx = 1;
        Double cy = 1.0;
        String n1 = null;
        String n2 = null;
 
        while ((line = reader1.readLine()) != null) {
        	SpaceMarine sm = new SpaceMarine();
            scanner = new Scanner(line);
          
            
            scanner.useDelimiter(",");
            while (scanner.hasNext()) {
                String data = scanner.next();
                if (index == 0)
                    sm.setId(Long.parseLong(data));
                else if (index == 1)
                    sm.setName(data);
                else if (index == 2)
                	cx = Integer.parseInt(data.replace("Coordinates{x=", ""));
                else if (index == 3) {
                	cy = Double.parseDouble(data.replace("y=", "").replace("}", ""));
                    sm.setCoordinates(new Coordinates(cx, cy)); }
                else if (index == 4) {
                	String aa = data;
                	LocalDate creationTime = LocalDate.now();
                    sm.setCreationDate(creationTime);
                }
                else if (index == 5)
                	sm.setHealth(Double.parseDouble(data));
                else if (index == 6)
                	sm.setLoyal(Boolean.parseBoolean(data));
                else if (index == 7)
                	sm.setAchievements(data);
                else if (index == 8) {
                	String weaponType1 = data;
                	if (weaponType1.equals("HEAVY_BOLTGUN") || weaponType1.equals("BOLT_RIFLE") || weaponType1.equals("PLASMA_GUN") || weaponType1.equals("COMBI_PLASMA_GUN") || weaponType1.equals("INFERNO_PISTOL"))
                    {
                    	sm.setWeaponType(SpaceMarine.Weapon.valueOf(weaponType1));
                    }
                }
                else if (index == 9)
                	n1 = data.replace("Chapter{name=", "");
                else if (index == 10) {
                	n2 = data.replace("parentLegion=", "").replace("}", "");
                	sm.setChapter(new Chapter(n1, n2));
                }
                else if (index == 11)
                	sm.setCreator(data.replace("creator=", "").replace("}", ""));
                else
                	w.addToList(true, "/");
                index++;
            }
            index = 0;
            c.list.add(sm);
        }
         
        //��������� ��� �����
        reader.close();
    }      
                
    /**
     * ������� � ����������� ����� ������ ���������� � ���������
     * @param w 
     */
    public static void info(Writer w, Collection collection) {
    	w.addToList(true,"���: " + collection.list.getClass().getName());
    	w.addToList(true,"����������� ���������: " + collection.list.size());
    	w.addToList(true,"���� �������������: " + collection.getDate());
    }
    
    /**
     * ������� � ����������� ����� ������ ��� �������� ��������� � ��������� �������������
     * @param w 
     */
    public static void show(Writer w, Collection c, User user) {
    	List<SpaceMarine> list = properUser(w, user, c);
        if (list != null) {
            for (User user1 : c.map.keySet()) {
                w.addToList(true, "��� ����������� �������� �������������: " + user1.login);
                if (c.map.get(user1).isEmpty())
                    w.addToList(true, "� ��������� ��� ���������");
                else
                    c.map.get(user1).forEach(r -> w.addToList(true, r.toString()));
            }
        }
    }
    
    
    
    /**
     * �������� �������� �������� ���������, id �������� ����� ���������
     * @param w 
     * @throws FailedCheckException 
     */
    public static void update(Writer w, AbstractReader reader, Collection c, String s, PostgreSQL sql, User user) throws EndOfFileException, FailedCheckException {

    	Long id = Utils.SpaceMarineIdCheck.checker(Long.parseLong(s));
        List<SpaceMarine> list = properUser(w, user, c);
        if (list != null) {
            SpaceMarine sm = null;
            for (SpaceMarine sm1 : list) {
                if (sm1.getId().equals(id)) {
                    sm = sm1;
                }
            }
            if (sm == null) {
                w.addToList(true, "������ � �������� ��� ��������� �������� ���");

                w.addToList(false, "end");
                return;
            }
            String name = Utils.SpaceMarineNameCheck.checker(reader.read(w));
            SpaceMarine sm2 = deser1(w, reader, id, name);
            list.set(list.indexOf(sm), sm2);
            w.addToList(true, "������� � id: " + id + " ������� �������");
            sql.add(new CommandA(user, sm2.toString(), id));
        }
        
    }
    
    public static SpaceMarine deser1(Writer w, AbstractReader reader, Long id, String s) throws FailedCheckException, NumberFormatException, EndOfFileException {
    	
    	
        SpaceMarine sm = new SpaceMarine();
        sm.setId(id);

        sm.setName(Utils.SpaceMarineNameCheck.checker(s));
        w.addToList(true, "��o� ����� Coordinates:");
        w.addToList(false, "      ������� int x");
        int cx = Utils.coordinatesXCheck.checker(Integer.parseInt(reader.read(w)));
        w.addToList(false, "     ������� Double y");
        Double cy = Utils.coordinatesYCheck.checker(Double.parseDouble(reader.read(w)));
        sm.setCoordinates(new Coordinates(cx, cy));

        LocalDate creationTime = LocalDate.now();
        sm.setCreationDate(creationTime);
        
        w.addToList(false, "������� Double health, ������ 0:");
        Double health1 = Utils.coordinatesYCheck.checker(Double.parseDouble(reader.read(w)));
        sm.setHealth(health1);

        w.addToList(false, "������� boolean loyal");
        boolean loyal1 = Utils.boolCheck.checker(Boolean.parseBoolean(reader.read(w)));
        sm.setLoyal(loyal1);
        
        w.addToList(false, "������� String achievements");
        String achievements = reader.read(w);
        sm.setAchievements(achievements.replace(",", "").replace(" ",""));

        w.addToList(true, "��o� ����� Chapter");
        w.addToList(false, "������� String name: ");
        String name1 = reader.read(w);
        w.addToList(false, "������� String parentLegion: ");
        String parentLegion1 = reader.read(w);
        sm.setChapter(new Chapter(name1.replace(",", ""), parentLegion1.replace(",", "")));
        
        w.addToList(false, "������� Weapon weaponType {\r\n" + 
        		"    HEAVY_BOLTGUN,\r\n" + 
        		"    BOLT_RIFLE,\r\n" + 
        		"    PLASMA_GUN,\r\n" + 
        		"    COMBI_PLASMA_GUN,\r\n" + 
        		"    INFERNO_PISTOL;\r\n" + 
        		"} ");
        
        
        String weaponType1 = reader.read(w).replace(",", "").replace(" ","");
        if (weaponType1.equals("HEAVY_BOLTGUN") || weaponType1.equals("BOLT_RIFLE") || weaponType1.equals("PLASMA_GUN") || weaponType1.equals("COMBI_PLASMA_GUN") || weaponType1.equals("INFERNO_PISTOL"))
        {
        	sm.setWeaponType(SpaceMarine.Weapon.valueOf(weaponType1));
        	
        }
        else {
        	w.addToList(true, "������ ������ ��� � �������!");
        	sm.setWeaponType(null);
        	
        }
        
        return sm;
        
       
        }
    
    /**
     * ������� ������� �� ��������� �� ��� id
     * @throws FailedCheckException 
     * @throws NumberFormatException 
     */
    public static void removeById(Writer w, AbstractReader reader, Collection c, String s, PostgreSQL sql, User user) throws EndOfFileException, NumberFormatException, FailedCheckException {
        
        List<SpaceMarine> list = properUser(w, user, c);
        
        if (list != null) {
        	Long id = SpaceMarine.idCheck.checker(Long.parseLong(s));
            SpaceMarine smm = c.searchById(id);
            if (smm == null) {
                w.addToList(true, "������ � �������� ��� ��������� �������� ���");

              
                
            }
            list.remove(smm);
            w.addToList(true, "������� � id: " + id + " ������� ������");
            
            sql.add(new CommandLonArg(user, id));
        }

    }
    
    /**
     * ��������� ��������� � ����
     */
    public static void save(Collection c) {
        SaveManagement.saveToFile(c);
    }

    /**
     * ������� ��� �������� �� ���������
     * @param w 
     */
    public static void clear(Writer w, Collection c, PostgreSQL sql, User user) {
    	List<SpaceMarine> list = properUser(w, user, c);
        if (list != null) {
            list.clear();
            w.addToList(true, "��������� ��� ��������� �������");
            CommandSimple com = new CommandSimple(user, CommandsList.CLEAR, null);
            sql.add(com);
        }
    }
    
     /**
     * ��������� � ��������� ������ �� ���������� �����.
     * � ������� ���������� ������� � ����� �� ����, � ������� �� ������ ������������ � ������������� ������
     * @param w 
     * @throws IOException 
     * @throws NumberFormatException 
     * @throws FailedCheckException 
     */
    public static boolean executeScript(Writer w, Collection c, String s, PostgreSQL sql, User user) throws NumberFormatException, IOException, FailedCheckException {
        boolean programIsWorking = true;
        //Reader reader;
        try (Reader reader = new Reader(s)) {
            if (RecursionHandler.isContains(s)) {
                RecursionHandler.addToFiles(s);
                String[] com;
                w.addToList(false,"\u001B[36m" + "������ ������� � ����� " + s + ": " + "\u001B[36m");
                String line = reader.read(w);
                while (line != null && programIsWorking) {
                    com = CommandReader.splitter(line);
                    programIsWorking = Command.switcher(w, reader, c, com[0], com[1], sql, user);
                    w.addToList(false, "\u001B[33m" + "������ ������� � ����� " + s + ": " + "\u001B[0m");
                    line = reader.read(w);
                }
                RecursionHandler.removeLast();
            } else
            	w.addToList(true,"\u001B[31m" + "���-�� ����� �������� ��������? �� ����� �����!" + "\u001B[31m");

        } catch (IncorrectFileNameException e) {
        	w.addToList(true, "\u001B[31m" + "�������� ��� �����" + "\u001B[0m");
        } catch (EndOfFileException e) {
        	w.addToList(true, "\u001B[31m" + "����������� ����� ����� " + s + "\u001B[0m");
            RecursionHandler.removeLast();
        } catch (FileNotFoundException e) {
        	w.addToList(true, "\u001B[31m" + "���� �� ������" + "\u001B[0m");
        }
        return programIsWorking;
    }
    
    /**
     * �������� ������� � ���������
     * @param w 
     * @throws FailedCheckException 
     */
    public static void add(Writer w, AbstractReader reader, Collection c, String s, PostgreSQL sql, User user) throws EndOfFileException, FailedCheckException {
    	List<SpaceMarine> list = properUser(w, user, c);
        if (list != null) {
            Long id = c.getNextId();
            SpaceMarine sm1 = deser1(w, reader, id, s);
            list.add(sm1);
            w.addToList(true, "������� ������� ��������");
            sql.add(new CommandArgs(user, sm1.toString()));
        }
    }
    
    
    public static SpaceMarine toAdd(Writer w) throws EndOfFileException, FailedCheckException {

    	SpaceMarine sm = new SpaceMarine();
    	Long idd = (long) (1 + Math.random() * (Long.MAX_VALUE - 1));
    	sm.setId(idd);
    	//s = ConsoleClient.handlerS("������� String name, ����� ������ 0: ", SpaceMarine.nameCheck);
    	sm.setName("Ann");
        //int cx = ConsoleClient.handlerI("��o� ����� Coordinates: /n ������� int x: ", Coordinates.xCheck);
        //Double cy = ConsoleClient.handlerD("������� Double y: ", Coordinates.yCheck);
        sm.setCoordinates(new Coordinates(43, 45.4));

        LocalDate creationTime = LocalDate.now();
        sm.setCreationDate(creationTime);
        
       // Double health1 = ConsoleClient.handlerD("������� Double health, ������ 0:", SpaceMarine.healthCheck);
        sm.setHealth(43.4);

       // boolean loyal1 = ConsoleClient.handlerB("������� boolean loyal", boolCheck);
        sm.setLoyal(true);
        
       // String achievements = ConsoleClient.handlerS("������� String achievements", SpaceMarine.nameCheck);
        sm.setAchievements("super");

        //String name1 = ConsoleClient.handlerS("��o� ����� Chapter /n ������� String name: ", Chapter.cCheck);
        //String parentLegion1 = ConsoleClient.handlerS("������� String parentLegion: ", Chapter.cCheck);
        sm.setChapter(new Chapter("fd", "fdfd"));
       
        sm.setWeaponType(SpaceMarine.Weapon.valueOf("BOLT_RIFLE"));
        sm.setCreator("s284769");
       
    	/*
        SpaceMarine sm = new SpaceMarine();
        sm.setId(id);
        sm.setName(SpaceMarine.nameCheck.checker(s));

        w.addToList(true,"��o� ����� Coordinates:");
        w.addToList(false, "������� int x: ");
        int cx = Coordinates.xCheck.checker(Integer.parseInt(reader.read(w)));
        w.addToList(false, "������� Double y: ");
        Double cy = Coordinates.yCheck.checker(Double.parseDouble(reader.read(w)));
        sm.setCoordinates(new Coordinates(cx, cy));

        LocalDate creationTime = LocalDate.now();
        sm.setCreationDate(creationTime);
        
        w.addToList(false, "������� Double health, ������ 0:");
        Double health1 = SpaceMarine.healthCheck.checker(Double.parseDouble(reader.read(w)));
        //Double health1 = reader.handlerD("������� Double health, ������ 0:", SpaceMarine.healthCheck);
        sm.setHealth(health1);

        w.addToList(false, "������� boolean loyal");
        boolean loyal1 = boolCheck.checker(Boolean.parseBoolean(reader.read(w)));
        //boolean loyal1 = reader.handlerB("������� boolean loyal", boolCheck);
        sm.setLoyal(loyal1);
        
        String achievements = SpaceMarine.nameCheck.checker(reader.read(w));
        //String achievements = reader.handlerS("������� String achievements", SpaceMarine.nameCheck);
        sm.setAchievements(achievements);

        w.addToList(true,"��o� ����� Chapter");
        w.addToList(false,"������� String name: ");
        String name1 = Chapter.cCheck.checker(reader.read(w));
        //String name1 = reader.handlerS("������� String name: ", Chapter.cCheck);
        w.addToList(false,"������� String parentLegion: ");
        String parentLegion1 = Chapter.cCheck.checker(reader.read(w));
        //String parentLegion1 = reader.handlerS("������� String parentLegion: ", Chapter.cCheck);
        sm.setChapter(new Chapter(name1, parentLegion1));
        
        w.addToList(false,"������� Weapon weaponType {\r\n" + 
        		"    HEAVY_BOLTGUN,\r\n" + 
        		"    BOLT_RIFLE,\r\n" + 
        		"    PLASMA_GUN,\r\n" + 
        		"    COMBI_PLASMA_GUN,\r\n" + 
        		"    INFERNO_PISTOL;\r\n" + 
        		"} ");
        String weaponType1 = SpaceMarine.nameCheck.checker(reader.read(w));
        
        if (weaponType1.equals("HEAVY_BOLTGUN") || weaponType1.equals("BOLT_RIFLE") || weaponType1.equals("PLASMA_GUN") || weaponType1.equals("COMBI_PLASMA_GUN") || weaponType1.equals("INFERNO_PISTOL"))
        {
        	sm.setWeaponType(SpaceMarine.Weapon.valueOf(weaponType1));
        }
        else {
        	w.addToList(true,"������� �� ������������");
        	w.addToList(true,"    HEAVY_BOLTGUN,\r\n" + 
    		"    BOLT_RIFLE,\r\n" + 
    		"    PLASMA_GUN,\r\n" + 
    		"    COMBI_PLASMA_GUN,\r\n" + 
    		"    INFERNO_PISTOL;\r\n"
    		);
        } */
	w.addToList(true, "������� � id: " + idd + " tuuut");
        return sm;
    }
}