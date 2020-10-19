package Server;


import Exceptions.FailedCheckException;
import command.CommandA;
import command.CommandArgs;
import command.CommandLonArg;
import command.CommandSimple;
import spacemarine.Chapter;
import spacemarine.Coordinates;
import spacemarine.SpaceMarine;
import spacemarine.User;
import spacemarine.Utils;
import spacemarine.Writer;

import java.sql.*;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import static spacemarine.User.userFromHashPassword;

public class PostgreSQL extends Thread {
    AtomicBoolean killFlag;
    final String DB_URL;
    final String USER;
    final String PASS;
    ConcurrentLinkedQueue<CommandSimple> commands = new ConcurrentLinkedQueue<>();

    public PostgreSQL(String url, String user, String pass, AtomicBoolean killFlag) {
        DB_URL = url;
        USER = user;
        PASS = pass;
        this.killFlag = killFlag;
    }
    public void add(CommandSimple com) {
        commands.add(com);
    }
    public void run(){
        try {
            Connection connection = DriverManager.getConnection(DB_URL, USER, PASS);

            while (!killFlag.get()) {
                if (!commands.isEmpty()) {
                    CommandSimple com = commands.poll();
                    switch (com.getCurrent()) {
                        case ADD:
                            addSQL((CommandArgs) com, connection);
                            break;
                        case UPDATE:
                            updateSQL((CommandA) com, connection);
                            break;
                        case REMOVE_BY_ID:
                            removeByIdSQL((CommandLonArg) com, connection);
                            break;
                        case CLEAR:
                            clearSQL(com, connection);
                            break;
                        case EXECUTE_SCRIPT:
                            //executeScriptSQL(com, connection);
                            break;
                        case REGISTER:
                            registerSQL(com, connection);
                            break;
                        default:
                            Writer.writeln("Неизвестная комманда в потоке SQL");
                    }
                }
                Thread.sleep(500);
            }
        } catch (Exception e) {
            Writer.writeln("Не удалось установить соединение с Базой Данных.");
            e.printStackTrace();
        }
        killFlag.set(true);
    }

    public void setSpaceMarine(Map<User, List<SpaceMarine>> map) {
        try (Connection connection = DriverManager.getConnection(DB_URL, USER, PASS)){
            for (User user : map.keySet()) {
                try (Statement statement = connection.createStatement()) {
                    ResultSet rs = statement.executeQuery("SELECT * FROM spacemarine WHERE creator = '" + user.login + "';");
                //	ResultSet rs = statement.executeQuery("SELECT * FROM spacemarine;");
                    while (rs.next()) {
                        try {
                            map.get(user).add(parseData(rs));
                        } catch (DateTimeParseException | FailedCheckException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (SQLException e) {
                    System.out.println("Соединение прервано. (Код ошибки: 1)");
                    e.printStackTrace();
                    killFlag.set(true);
                }
            }
        } catch (SQLException e) {
            System.out.println("Соединение прервано. (Код ошибки: 2)");
            e.printStackTrace();
            killFlag.set(true);
        }
    }

    private SpaceMarine parseData(ResultSet resSM) throws DateTimeParseException, SQLException, FailedCheckException {
     
        SpaceMarine nextSM = new SpaceMarine();
        nextSM.setId(resSM.getLong("id"));
        nextSM.setName(resSM.getString("name"));
        nextSM.setCoordinates(new Coordinates(resSM.getInt("x"), resSM.getDouble("y")));
        nextSM.setCreationDate(resSM.getDate("crdate").toLocalDate());
        nextSM.setHealth(resSM.getDouble("health"));
        nextSM.setLoyal(resSM.getBoolean("loyal"));
        nextSM.setChapter(new Chapter(resSM.getString("chaptername"), resSM.getString("parentlegion")));
        nextSM.setAchievements(resSM.getString("achievements"));
        String weaponType1 = resSM.getString("weappontype");
    	if (weaponType1.equals("HEAVY_BOLTGUN") || weaponType1.equals("BOLT_RIFLE") || weaponType1.equals("PLASMA_GUN") || weaponType1.equals("COMBI_PLASMA_GUN") || weaponType1.equals("INFERNO_PISTOL"))
        {
        	nextSM.setWeaponType(SpaceMarine.Weapon.valueOf(weaponType1));
        }
        
        nextSM.setCreator(resSM.getString("creator"));
        return nextSM;
    }

    public Map<User, List<SpaceMarine>> getMapOfUsers() {
        Map<User, List<SpaceMarine>> map = new ConcurrentHashMap<>();
        try (Connection connection = DriverManager.getConnection(DB_URL, USER, PASS)){
            try (Statement statement = connection.createStatement()) {
                ResultSet rs = statement.executeQuery("SELECT * FROM users");
                while (rs.next()) {
                    String login = rs.getString("login");
                    String hash = rs.getString("password");
                    map.put(userFromHashPassword(login,hash), new CopyOnWriteArrayList<>());
                }
            } catch (SQLException e) {
                System.out.println("Соединение прервано. (Код ошибки: 3)");
                e.printStackTrace();
                killFlag.set(true);
            }
        } catch (SQLException e) {
            System.out.println("Соединение прервано. (Код ошибки: 4)");
            e.printStackTrace();
            killFlag.set(true);
        }
        return map;
    }

    public Long getIds() {
        try (Connection connection = DriverManager.getConnection(DB_URL, USER, PASS)){
            try (Statement statement = connection.createStatement()) {
                ResultSet rs = statement.executeQuery("SELECT nextval('ids');");
                if (rs.next()) {
                    Long id = (Long) rs.getLong(1);
                    statement.execute("SELECT setval('ids', " + id + ", false);");
                    return id;
                }
            } catch (SQLException e) {
                System.out.println("Соединение прервано. (Код ошибки: 5)");
                killFlag.set(true);
            }
        } catch (SQLException e) {
            System.out.println("Соединение прервано. (Код ошибки: 6)");
            killFlag.set(true);
        }
        return (long) 0;
    }
    
    private void registerSQL(CommandSimple com, Connection connection) {
        try (PreparedStatement statement = connection.prepareStatement("INSERT INTO users (login, password) VALUES (? , ?);")) {
            statement.setString(1, com.getUser().login);
            statement.setString(2, com.getUser().hashPassword);
            statement.execute();
        } catch (SQLException e) {
            System.out.println("Соединение прервано. (Код ошибки: 7)");
            killFlag.set(true);
        }
    }

    private void clearSQL(CommandSimple com, Connection connection) {
        try (PreparedStatement statement = connection.prepareStatement("DELETE FROM spacemarine WHERE creator = ?;")) {
            statement.setString(1, com.getUser().login);
            statement.execute();
        } catch (SQLException e) {
            System.out.println("Соединение прервано. (Код ошибки: 8)");
            killFlag.set(true);
        }
    }

    private void removeByIdSQL(CommandLonArg com, Connection connection) {
        try (PreparedStatement statement = connection.prepareStatement("DELETE FROM spacemarine WHERE id = ? and creator = ?;")) {
            statement.setLong(1, com.returnObj());
            statement.setString(2, com.getUser().login);
            statement.execute();
        } catch (SQLException e) {
            System.out.println("Соединение прервано. (Код ошибки: 9)");
            killFlag.set(true);
        }
    }

    private void updateSQL(CommandA com, Connection connection) {
    	
    	/*               	
        Table "spacemarine"
    Column    |  Type   | Modifiers
 --------------+---------+-----------
 id           | bigint  | not null
 name         | text    | not null
 x            | integer | not null
 y            | real    | not null
 crdate       | date    | not null
 health       | real    |
 loyal        | boolean |
 achievements | text    | not null
 weappontype  | text    |
 chaptername  | text    | not null
 parentlegion | text    |
 creator      | text    |
 
 INSERT INTO spacemarine (id, name, x,y,crdate,health,loyal,achievements,weappontype,chaptername,parentlegion,creator ) 
    	VALUES (126334, 'Am', 3, 4.6, '12-10-2020', 12.6, true, 'sdsd', 'INFERNO_PISTOL', 'fdfdfd', 'dffd', 's284769');
    	

 */	 
    	
        try (PreparedStatement statement = connection.prepareStatement("UPDATE spacemarine SET "
        		+ "(name, x, y, crdate, "
        		+ "health, loyal, achievements, weappontype, chaptername, "
        		+ "parentlegion) = ((?),(?),(?),(?),(?),(?),(?),(?),(?),(?)) "
        		+ "WHERE id = ? AND creator = ?;")) {
            //statement.setString(1, com.getUser().login);
        	SpaceMarine sm1 = deser(((String) com.returnObj()).replace("{", "").replace("}", "").replace(" ", ""));
            statement.setString(1, sm1.getName());
            statement.setInt(2, sm1.getCoordinstes().getX());
            statement.setDouble(3, sm1.getCoordinstes().getY());
            statement.setDate(4, java.sql.Date.valueOf(sm1.getCreationDate()));
            statement.setDouble(5, sm1.getHealth());
            statement.setBoolean(6, sm1.getLoyal());
            statement.setString(7, sm1.getAchievements());
            statement.setString(8, sm1.getWeaponType().toString());
            statement.setString(9, sm1.getChapter().getName());
            statement.setString(10, sm1.getChapter().getParentLegion());
            
            statement.setLong(11, sm1.getId());
            statement.setString(12, com.getUser().login);
          
            
            statement.execute();
        } catch (SQLException e) {
            System.out.println("Соединение прервано. (Код ошибки: 10)");
            killFlag.set(true);
        }
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
    
    
    
    private void addSQL(CommandArgs com, Connection connection) {
    	 try (PreparedStatement statement = connection.prepareStatement("INSERT INTO spacemarine "
    	 		+ "(id, name, x, y, crdate, "
    	 		+ "health, loyal, achievements, weappontype, chaptername, parentlegion, creator)"
    	 		+ "VALUES ((?), (?),(?),(?),(?),(?),(?),(?),(?),(?),(?),(?));")) {
             
    	
            //statement.setString(1, com.getUser().login);
        	SpaceMarine sm1 = deser(((String) com.returnObj()).replace("{", "").replace("}", "").replace(" ", ""));
        	statement.setLong(1, sm1.getId());
            statement.setString(2, sm1.getName());
            statement.setInt(3, sm1.getCoordinstes().getX());
            statement.setDouble(4, sm1.getCoordinstes().getY());
            Date date = Date.valueOf(sm1.getCreationDate());
            statement.setDate(5, date);
            statement.setDouble(6, sm1.getHealth());
            statement.setBoolean(7, sm1.getLoyal());
            statement.setString(8, sm1.getAchievements());
            statement.setString(9, sm1.getWeaponType().toString());
            statement.setString(10, sm1.getChapter().getName());
            statement.setString(11, sm1.getChapter().getParentLegion());
            
            //statement.setLong(11, sm1.getId());
            statement.setString(12, com.getUser().login);
    	
            statement.execute();
        } catch (SQLException e) {
            System.out.println("Соединение прервано. (Код ошибки: 11)");
            e.printStackTrace();
            killFlag.set(true);
        }
    }
}
