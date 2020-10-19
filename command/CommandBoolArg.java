package command;

import spacemarine.User;

public class CommandBoolArg extends CommandSimple {
    String l;
    static Long id;
    public CommandBoolArg(User user, String l) {
        super(user, CommandsList.FILTER_LESS_THAN_LOYAL, id);
        this.l = l;
    }

    @Override
    public String toString() {
        return "CommandBoolArg{" +
                "l=" + l +
                '}';
    }

    @Override
    public String returnObj() {
        return l;
    }
}
