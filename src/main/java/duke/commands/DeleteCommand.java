package duke.commands;

import duke.DukeException;
import duke.Ui;
import duke.task.Task;
import duke.task.TaskList;

/**
 * Create new ToDo.
 */
public class DeleteCommand extends Command {
    public static final String COMMAND_WORD = "delete";
    public static final String MESSAGE_DELETE_SUCCESS = "Noted. I've removed this task:\n  %s\nNow you have %d tasks in your list";

    private final int indexToDelete;

    public DeleteCommand(int task) {
        this.indexToDelete = task - 1;
    }

    @Override
    public void execute(TaskList tasks, Ui ui) throws DukeException {
        Task task = tasks.removeTask(indexToDelete);
        ui.displayText(MESSAGE_DELETE_SUCCESS, task, tasks.size());
    }
}
