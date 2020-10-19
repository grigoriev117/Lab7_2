package Client;
import spacemarine.*;
import command.*;
import Exceptions.FailedCheckException;
import java.io.FileNotFoundException;
import java.time.LocalDate;
import Exceptions.EndOfFileException;
import Exceptions.IncorrectFileNameException;

import java.util.Date;
import java.util.LinkedList;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;



import java.util.Collections;

public class CommandConvertClient {

	/**
     * ќбработка команд, вводимых с консоли
	 * @throws IOException 
     */
    public static CommandSimple switcher(User user, String s1, String s2) throws EndOfFileException, IOException {
        switch (s1) {
            case ("help"):
                return new CommandSimple(user, CommandsList.HELP, null);
            case ("info"):
                return new CommandSimple(user, CommandsList.INFO, null);
            case ("show"):
                return new CommandSimple(user, CommandsList.SHOW, null);
            case ("remove_first"):
                return new CommandSimple(user, CommandsList.REMOVE_FIRST, null);
            case ("add"):
                return add(user, s2);
            case ("update"):
                return update(user, s2);
            case ("remove_by_id"):
                return removeById(user, s2);
            case ("clear"):
                return new CommandSimple(user, CommandsList.CLEAR, null);
            case ("execute_script"):
                return new CommandScript(user, s2);
            case ("exit"):
                return new CommandSimple(user, CommandsList.EXIT, null);
            case ("filter_less_than_loyal"):
                return filter_less_than_loyal(user, s2);
            case ("remove_all_by_weapon_type"):
                return remove_all_by_weapon_type(user, s2);
            case ("remove_head"):
                return new CommandSimple(user, CommandsList.REMOVE_HEAD, null);
            case ("head"):
                return new CommandSimple(user, CommandsList.HEAD, null);
            case ("group_counting_by_chapter"):
                return new CommandSimple(user, CommandsList.GROUP_COUNTING_BY_CHAPTER, null);
            case("login"):
                return login(user);
            case("register"):
                return register(user);
            default:
                Writer.writeln("“акой команды нет");
        }
        return null;
    }
    
    public static CommandSimple remove_all_by_weapon_type(User user, String s2) throws EndOfFileException{
		
		return new CommandWArg(user, s2);
	}

	public static CommandSimple filter_less_than_loyal(User user, String s2) throws EndOfFileException {
		
		return new CommandBoolArg(user, s2);
	}

	
    
    /**
     * —читывает скрипт
     */
    
    
    /**
     * ѕерезаписывает элемент списка с указанным id
     */
    public static CommandSimple update(User user, String s) throws EndOfFileException {
    	SpaceMarine sm = new SpaceMarine();
        Long id;
        try {
            id = SpaceMarine.idCheck.checker(Long.parseLong(s));
        } catch (NumberFormatException | FailedCheckException e) {
            id = ConsoleClient.handlerL("¬ведите Long id: ", SpaceMarine.idCheck);
        }
        removeById(user, id.toString());
        sm.setId(id);
        s = ConsoleClient.handlerS("¬ведите String name, диной больше 0: ", SpaceMarine.nameCheck);
        sm.setName(s.replace(",", "").replace(" ",""));
        Writer.writeln("¬вoд полей Coordinates:");
        int cx = ConsoleClient.handlerI("¬ведите int x: ", Coordinates.xCheck);
        Double cy = ConsoleClient.handlerD("¬ведите Double y: ", Coordinates.yCheck);
        sm.setCoordinates(new Coordinates(cx, cy));

        LocalDate creationTime = LocalDate.now();
        sm.setCreationDate(creationTime);
        
        Double health1 = ConsoleClient.handlerD("¬ведите Double health, больше 0:", SpaceMarine.healthCheck);
        sm.setHealth(health1);

        boolean loyal1 = ConsoleClient.handlerB("¬ведите boolean loyal", boolCheck);
        sm.setLoyal(loyal1);
        
        String achievements = ConsoleClient.handlerS("¬ведите String achievements", SpaceMarine.nameCheck);
        sm.setAchievements(achievements.replace(",", "").replace(" ",""));

        Writer.writeln("¬вoд полей Chapter");
        String name1 = ConsoleClient.handlerS("¬ведите String name: ", Chapter.cCheck);
        String parentLegion1 = ConsoleClient.handlerS("¬ведите String parentLegion: ", Chapter.cCheck);
        sm.setChapter(new Chapter(name1.replace(",", ""), parentLegion1.replace(",", "")));
        
        String weaponType1 = ConsoleClient.handlerS("¬ведите Weapon weaponType {\r\n" + 
        		"    HEAVY_BOLTGUN,\r\n" + 
        		"    BOLT_RIFLE,\r\n" + 
        		"    PLASMA_GUN,\r\n" + 
        		"    COMBI_PLASMA_GUN,\r\n" + 
        		"    INFERNO_PISTOL;\r\n" + 
        		"} ", SpaceMarine.nameCheck);
        
        boolean fl = true;
        if (weaponType1.replace(",", "").replace(" ","").equals("null")) {sm.setWeaponType(null); 
        } else {
        while (fl) {
        weaponType1 = weaponType1.replace(",", "").replace(" ","");
        if (weaponType1.equals("HEAVY_BOLTGUN") || weaponType1.equals("BOLT_RIFLE") || weaponType1.equals("PLASMA_GUN") || weaponType1.equals("COMBI_PLASMA_GUN") || weaponType1.equals("INFERNO_PISTOL"))
        {
        	sm.setWeaponType(SpaceMarine.Weapon.valueOf(weaponType1));
        	fl = false;
        }
        else {
        	weaponType1 = ConsoleClient.handlerS("¬ведите Weapon weaponType {\r\n" + 
            		"    HEAVY_BOLTGUN,\r\n" + 
            		"    BOLT_RIFLE,\r\n" + 
            		"    PLASMA_GUN,\r\n" + 
            		"    COMBI_PLASMA_GUN,\r\n" + 
            		"    INFERNO_PISTOL;\r\n" + 
            		"} ", SpaceMarine.nameCheck);
        } }}
        s = sm.toString();
        return new CommandA(user, s, id); 
        //return new CommandLonArg(id);
    }
    
    /**
     * ”дал€ет элемент по его id
     */
    public static CommandSimple removeById(User user, String s) throws EndOfFileException {
        Long id;
        try {
            id = SpaceMarine.idCheck.checker(Long.parseLong(s));
        } catch (NumberFormatException | FailedCheckException e) {
            id = ConsoleClient.handlerL("¬ведите Long id: ", SpaceMarine.idCheck);
        }
          
        return new CommandLonArg(user, id);
    }
    
    /**
     * ƒобавл€ет элемент в список
     */
    public static CommandSimple add(User user, String s) throws EndOfFileException, IOException {
    	SpaceMarine sm = new SpaceMarine();
    	String smtr;
        //hmm
    	Long id1 = (long) (1 + Math.random() * (Long.MAX_VALUE - 1));
        sm.setId(id1);
        try {
            SpaceMarine.nameCheck.checker(s);
            Writer.writeln("ѕоле name: " + s);
        } catch (FailedCheckException e) {
            s = ConsoleClient.handlerS("¬ведите String name, диной больше 0: ", SpaceMarine.nameCheck);
        }
        sm.setName(s.replace(",", "").replace(" ",""));
        Writer.writeln("¬вoд полей Coordinates: ");
        int cx = ConsoleClient.handlerI("¬ведите int x: ", Coordinates.xCheck);
        Double cy = ConsoleClient.handlerD("¬ведите Double y: ", Coordinates.yCheck);
        sm.setCoordinates(new Coordinates(cx, cy));

        LocalDate creationTime = LocalDate.now();
        sm.setCreationDate(creationTime);
        
        Double health1 = ConsoleClient.handlerD("¬ведите Double health, больше 0: ", SpaceMarine.healthCheck);
        sm.setHealth(health1);

        boolean loyal1 = ConsoleClient.handlerB("¬ведите boolean loyal: ", boolCheck);
        sm.setLoyal(loyal1);
        
        String achievements = ConsoleClient.handlerS("¬ведите String achievements: ", SpaceMarine.nameCheck);
        sm.setAchievements(achievements.replace(",", "").replace(" ",""));

        Writer.writeln("¬вoд полей Chapter: ");
        String name1 = ConsoleClient.handlerS("¬ведите String name: ", Chapter.cCheck);
        String parentLegion1 = ConsoleClient.handlerS("¬ведите String parentLegion: ", Chapter.cCheck);
        sm.setChapter(new Chapter(name1.replace(",", ""), parentLegion1.replace(",", "")));
        
        String weaponType1 = ConsoleClient.handlerS("¬ведите Weapon weaponType из предложенных{\r\n" + 
        		"    HEAVY_BOLTGUN,\r\n" + 
        		"    BOLT_RIFLE,\r\n" + 
        		"    PLASMA_GUN,\r\n" + 
        		"    COMBI_PLASMA_GUN,\r\n" + 
        		"    INFERNO_PISTOL;\r\n" + 
        		"}      ", SpaceMarine.nameCheck);
        
        boolean fl = true;
        if (weaponType1.replace(",", "").replace(" ","").equals("null")) {sm.setWeaponType(null); 
        } else {
        while (fl) {
        weaponType1 = weaponType1.replace(",", "").replace(" ","");
        if (weaponType1.equals("HEAVY_BOLTGUN") || weaponType1.equals("BOLT_RIFLE") || weaponType1.equals("PLASMA_GUN") || weaponType1.equals("COMBI_PLASMA_GUN") || weaponType1.equals("INFERNO_PISTOL"))
        {
        	sm.setWeaponType(SpaceMarine.Weapon.valueOf(weaponType1));
        	fl = false;
        }
        else {
        	weaponType1 = ConsoleClient.handlerS("¬ведите Weapon weaponType {\r\n" + 
            		"    HEAVY_BOLTGUN,\r\n" + 
            		"    BOLT_RIFLE,\r\n" + 
            		"    PLASMA_GUN,\r\n" + 
            		"    COMBI_PLASMA_GUN,\r\n" + 
            		"    INFERNO_PISTOL;\r\n" + 
            		"}   ", SpaceMarine.nameCheck);
        } }}
        smtr = sm.toString();
       
        return new CommandArgs(user, smtr);
    }
    
    public static Checker<Boolean> boolCheck = (Boolean B) -> {
        if (B != null) return B;
        else throw new FailedCheckException();
    };
    
    
    private static CommandSimple register(User user) throws EndOfFileException {
        String login = ConsoleClient.handlerS("¬ведите логин: ", Utils.loginCheck);
        String password = ConsoleClient.handlerS("¬ведите пароль: ", Utils.passwordCheck);
        user.changeUser(login, password);
        return new CommandSimple(user, CommandsList.REGISTER, null);
    }

    private static CommandSimple login(User user) throws EndOfFileException {
        String login = ConsoleClient.handlerS("¬ведите логин: ", Utils.loginCheck);
        String password = ConsoleClient.handlerS("¬ведите пароль: ", Utils.passwordCheck);
        user.changeUser(login, password);
        return new CommandSimple(user, CommandsList.LOGIN, null);
    }

	
}
