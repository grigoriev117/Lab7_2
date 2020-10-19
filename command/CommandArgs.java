package command;

import spacemarine.User;

public class CommandArgs extends CommandSimple {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	static Long id;
	SpaceMarine sm;

    public CommandArgs(User user, SpaceMarine sm) {
        super(user, CommandsList.ADD, id);
        this.sm = sm;
    }

    @Override
    public String toString() {
        return "CommandArgs{" +
                "sm" + sm +
                '}';
    }

    @Override
    public SpaceMarine returnObj() {
        return sm;
    }
}
