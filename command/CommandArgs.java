package command;

import spacemarine.User;

public class CommandArgs extends CommandSimple {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	static Long id;
	String smtr;

    public CommandArgs(User user, String smtr) {
        super(user, CommandsList.ADD, id);
        this.smtr = smtr;
    }

    @Override
    public String toString() {
        return "CommandArgs{" +
                "smtr=" + smtr +
                '}';
    }

    @Override
    public String returnObj() {
        return smtr;
    }
}