package command;

import spacemarine.User;

public class CommandLonArg extends CommandSimple {
    Long id;
    static Long id1;
    public CommandLonArg(User user, Long id) {
        super(user, CommandsList.REMOVE_BY_ID, id1);
        this.id = id;
    }

    @Override
    public String toString() {
        return "CommandLonArg{" +
                "id=" + id +
                '}';
    }

    @Override
    public Long returnObj() {
        return id;
    }
}