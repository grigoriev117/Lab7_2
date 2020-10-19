package command;

import spacemarine.User;

public class CommandWArg extends CommandSimple {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	 static Long id;
	String l;

    public CommandWArg(User user, String l) {
        super(user, CommandsList.REMOVE_ALL_BY_WEAPON_TYPE, id);
        this.l = l;
    }

    @Override
    public String toString() {
        return "CommandWArg{" +
                "l=" + l +
                '}';
    }

    @Override
    public String returnObj() {
        return l;
    }
}
