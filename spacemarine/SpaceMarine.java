package spacemarine;

import java.io.Serializable;
import java.sql.Date;
import java.time.LocalDate;
import Exceptions.FailedCheckException;

public class SpaceMarine implements Comparable<Object>, Serializable{
    /**
	 * 
	 */
    private static final long serialVersionUID = 137;
    private Long id; //Поле не может быть null, Значение поля должно быть больше 0, Значение этого поля должно быть уникальным, Значение этого поля должно генерироваться автоматически
    private String name; //Поле не может быть null, Строка не может быть пустой
    private Coordinates coordinates; //Поле не может быть null
    private java.time.LocalDate creationDate; //Поле не может быть null, Значение этого поля должно генерироваться автоматически
    private Double health; //Поле может быть null, Значение поля должно быть больше 0
    private boolean loyal;
    private String achievements; //Поле не может быть null
    private Weapon weaponType; //Поле может быть null
    private Chapter chapter; //Поле не может быть null
    private String creator;

   
@Override
public String toString() {
    return "SpaceMarine{" +
            "id=" + id +
            ", name='" + name + '\'' +
            ", coordinates=" + coordinates +
            ", creationDate=" + creationDate +
            ", health=" + health +
            ", loyal=" + loyal +
            ", achievements=" + achievements +
            ", weaponType=" + weaponType +
            ", chapter=" + chapter +
            ", creator=" + creator +
            '}';
}

public String toString1() {
    return id + ","+name + ","+coordinates +","+ creationDate + ","+health + ","+loyal +","+ achievements +","+ weaponType +","+ chapter+","+ creator;
}



public enum Weapon {
    HEAVY_BOLTGUN,
    BOLT_RIFLE,
    PLASMA_GUN,
    COMBI_PLASMA_GUN,
    INFERNO_PISTOL;
}

public Long getId() {
    return id;
}

public void setId(Long id) {
    this.id = id;
}

public void setCoordinates(Coordinates coordinates) {
    this.coordinates = coordinates;
}

public String getName() {
    return name;
}

public String getCreator() {
    return creator;
}

public void setCreator(String creator) {
    this.creator = creator;
}

public String getAchievements() {
    return achievements;
}
public void setAchievements(String achievements) {
    this.achievements = achievements;
}

public void setName(String name) {
    this.name = name;
}

public Double getHealth() {
    return health;
}

public void setHealth(Double health) {
    this.health = health;
}


public void setCreationDate(LocalDate creationDate) {
    this.creationDate = creationDate;
}

public LocalDate getCreationDate() {
    return creationDate;
}

public boolean getLoyal() {
    return loyal;
}

public void setLoyal(Boolean loyal) {
    this.loyal = loyal;
}

public Weapon getWeaponType() {
    return weaponType;
}

public void setWeaponType(Weapon weaponType) {
    this.weaponType = weaponType;
}

public Chapter getChapter() {
    return chapter;
}
public Coordinates getCoordinstes() {
    return coordinates;
}

public void setChapter(Chapter chapter) {
    this.chapter = chapter;
}

/**
 * Конвертирование элемента списка в удобный для сохранения формат
 */
public String toCSVfile() {
    String CSV = toString1();
    return CSV;
}


public static Checker<Weapon> WeaponCheck = (Weapon W) -> {
    if (W == null) return null;
    else return W;
};


/**
 * Проверка Long
 */
public static Checker<Long> idCheck = (Long L) -> {
     if (L != null ) return L;
     else throw new FailedCheckException();
};
/**
 * Проверка Integer
 */
public static Checker<Integer> idCheck1 = (Integer I) -> {
    if (I != null && I > 0) return I;
    else throw new FailedCheckException();
};

/**
 * Проверка String
 */
public static Checker<String> nameCheck = (String S) -> {
    if (S != null && S.length() != 0) return S;
    else throw new FailedCheckException();
};

/**
 * Проверка Double
 */
public static Checker<Double> healthCheck = (Double D) -> {
    if (D != null) return D;
    else throw new FailedCheckException();
};

/**
 * Проверка boolean
 */


/**
 * Сравнение объектов.
 */

@Override
public int compareTo(Object o) {
	/*
        int result = this.getName().compareTo(((SpaceMarine) o).getName());

        if (result == 0 && this.getHealth() != null && ((SpaceMarine) o).getHealth() != null) {
            result = this.getHealth().compareTo(((SpaceMarine) o).getHealth());
        }
        return result;
    }
*/
	int res1 = this.getName().length()+this.getAchievements().length()+
			this.getChapter().getParentLegion().length()+this.getChapter().getName().length();
	int res2 = ((SpaceMarine) o).getName().length()+((SpaceMarine) o).getAchievements().length()+
			((SpaceMarine) o).getChapter().getParentLegion().length()+((SpaceMarine) o).getChapter().getName().length();
	int result = 0;
	if (res1>res2) result = 1 ;
	if (res2>res1) result = -1 ;
	if (res1==res2) result = 0 ;
	return result;
}}


