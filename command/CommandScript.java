package command;

import spacemarine.User;

public class CommandScript extends CommandSimple {
    String script;
    static Long id;
    public CommandScript(User user, String script) {
        super(user, CommandsList.EXECUTE_SCRIPT, id);
        this.script = script;
    }

    @Override
    public String toString() {
        return "CommandScript{" +
                "script='" + script + '\'' +
                '}';
    }

    @Override
    public String returnObj() {
        return script;
    }
}