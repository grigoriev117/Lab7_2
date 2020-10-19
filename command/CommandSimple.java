package command;
import spacemarine.User;

import java.io.Serializable;

public class CommandSimple implements Serializable {
    CommandsList current;
    
    
    public User getUser() {
        return user;
    }

    User user;
	Long id;

    public CommandSimple(User user, CommandsList com, Long id) {
        current = com;
        this.user = user;
        this.id = id;
    }


	public CommandsList getCurrent() {
        return current;
    }

    public Object returnObj() {
        return null;
    }
    
    public void setID(Long id) {
    	this.id = id;
    }
    
    public Long returnID() {
    	return id;
    }

    @Override
    public String toString() {
        return "Command{" +
                "current=" + current +
                '}';
    }
}