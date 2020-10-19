package Server;

import Exceptions.FailedCheckException;
import spacemarine.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.Scanner;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

/**
 * Класс, оперирующий с файлами
 */

public class SaveManagement {
    private static File file;

    public static void setFile(File file) {
        SaveManagement.file = file;
    }

    /**
     * Сохранение файла в CSV формат
     */
    public static void saveToFile(Collection c) {
        if (file == null) {
        	//private static final DateFormat sdf = new SimpleDateFormat("_yyyy_MM_dd__HH_mm_ss");
        	//private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("_yyyy_MM_dd__HH_mm_ss");
        	// Date date = new Date();
        	//  file = new File("file"+sdf.format(date)+"file.csv");
        	file = new File("file_file.csv");}
        try (FileWriter fileWriter = new FileWriter(file)) {
            for (SpaceMarine r : c.list) {
                fileWriter.write(r.toCSVfile() + "\n");
            }
        } catch (IOException e) {
        	Writer.writeln("Ошибка доступа к файлу");
        }
    }

    /**
     * Возвращает коллекцию из сохраненного файла
     * @throws IOException 
     */
    
    public static Collection listFromSave() throws IOException {
        Collection collection = new Collection(0);
      try {  BufferedReader reader1 = new BufferedReader(new FileReader(file));
      
        // считываем построчно
        String line = null;
        Scanner scanner = null;
        int index = 0;
        int cx = 1;
        Double cy = 1.0;
        String n1 = null;
        String n2 = null;
 try {
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
                	sm.setCreator(data);
                else
                	Writer.writeln("/");
                index++;
            
            }
            index = 0;
            collection.list.add(sm);
        } } catch (ArrayIndexOutOfBoundsException | DateTimeParseException | NumberFormatException e) {
        	Writer.writeln("\u001B[31m" + "Ошибка чтения файла, строка: " + "\u001B[0m");}
        } catch (FileNotFoundException e) {
        	Writer.writeln("\u001B[31m" + "Ошибка доступа к файлу" + "\u001B[0m");}
         
       
       // reader.close();
        
      return collection;
        
        }
    }
        
        
 
