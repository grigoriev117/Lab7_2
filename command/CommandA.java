package command;

import spacemarine.*;

@SuppressWarnings("serial")
public class CommandA extends CommandSimple {
    static Long id1;
	/**
	 * 
	 */
	//private static final long serialVersionUID = 1L;
	String smm;
	Long id;
    public CommandA(User user, SpaceMarine smm, Long id) {
        super(user, CommandsList.UPDATE, id1);
        this.smm = smm;
        this.id = id;
    }

    @Override
    public String toString() {
        return "CommandA{" +
                "smm=" + smm + "id=" + id +
                '}';
    }

    @Override
    public SpaceMarine returnObj() {
        return smm;
    }
    @Override
    public Long returnID() {
		return id;
    }
}
