package duke.commands;

import duke.DukeException;
import duke.Ui;
import duke.task.Task;
import duke.task.TaskList;

import java.util.ArrayList;

/**
 * Search for a task given certain constraints.
 */
public class FindCommand extends Command {
    public static final String COMMAND_WORD = "find";

    public static final String MESSAGE_SUCCESS = "Here are the matching tasks in your list:\n";
    public static final String MESSAGE_FAILURE = "No tasks match the provided keyword.";

    private final String keyword;

    public FindCommand(String keyword) {
        this.keyword = keyword;
    }

    @Override
    public void execute(TaskList tasks, Ui ui) throws DukeException {
        ArrayList<Task> filteredTasks = tasks.filterByKeyword(this.keyword);
        if (filteredTasks.size() == 0) {
            ui.displayText(MESSAGE_FAILURE);
        } else {
            ui.displayText(MESSAGE_SUCCESS + filteredTasks);
        }
    }
}
