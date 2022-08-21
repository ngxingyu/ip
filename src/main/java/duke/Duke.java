package duke;

import duke.task.Deadline;
import duke.task.Event;
import duke.task.Task;
import duke.task.TaskList;
import duke.task.ToDo;

import java.util.Scanner;
import java.util.stream.IntStream;

public class Duke {
    private final TaskList tasks;
    private final Ui ui;

    public Duke(String fileName) {
        this.ui = new Ui();

        Storage storage;
        try {
            storage = new Storage(fileName);
        } catch (DukeException e) {
            ui.showLoadingError();
            storage = null;
        }
        this.tasks = new TaskList(storage);
    }

    public static void main(String[] args) {
        new Duke("tasks.txt").run();
    }

    public void run() {
        ui.showWelcome();
        Scanner sc = new Scanner(System.in);
        while (true) {
            String command = sc.next();
            String arguments = sc.nextLine();
            if (!callback(command, arguments)) {
                break;
            }
        }
    }

    /**
     * Callback for all commands.
     *
     * @param command First word of user input
     * @param input   Remaining words of user input
     * @return Whether the program should continue running.
     */
    private boolean callback(String command, String input) {
        input = input.strip();
        try {
            try {
                switch (Commands.getCommand(command)) {
                    case BYE:
                        ui.showGoodbye();
                        return false;
                    case LIST:
                        listHistory();
                        break;
                    case TODO:
                        addTodo(input);
                        break;
                    case EVENT:
                        addEvent(input);
                        break;
                    case DEADLINE:
                        addDeadline(input);
                        break;
                    case MARK:
                        setTaskCompletionStatus(input, true);
                        break;
                    case UNMARK:
                        setTaskCompletionStatus(input, false);
                        break;
                    case DELETE:
                        delete(input);
                        break;
                }
            } catch (IllegalArgumentException e) {
                throw new DukeException("I'm sorry, but I don't know what that means :-(");
            }
        } catch (DukeException e) {
            ui.displayText(e.getMessage());
        }
        return true;
    }

    /**
     * Handle the todo command
     *
     * @param input the input to be handled
     * @throws DukeException if the input is invalid
     */
    private void addTodo(String input) throws DukeException {
        ToDo todo = new ToDo(input);
        if (tasks.addTask(todo)) {
            ui.displayText("Got it. I've added this todo:\n  %s\nNow you have %d tasks in your list", todo, tasks.size());
        } else {
            ui.displayText("Unable to add task.");
        }
    }


    /**
     * Handle the event command
     *
     * @param input the input to be handled
     * @throws DukeException if the input is invalid
     */
    private void addEvent(String input) throws DukeException {
        if (input.matches("^.* /at .*$")) {
            String[] parts = input.split(" /at ");
            Event event = new Event(parts[0].strip(), parts[1].strip());
            tasks.add(event);
            ui.displayText("Got it. I've added this event:\n  %s\nNow you have %d tasks in your list", event, tasks.size());
        } else {
            throw new DukeException("Invalid event format");
        }
    }

    /**
     * Handle the deadline command
     *
     * @param input the input to be handled
     * @throws DukeException if the input is invalid
     */
    private void addDeadline(String input) throws DukeException {
        if (input.matches("^.* /by .*$")) {
            String[] parts = input.split(" /by ");
            Deadline deadline = new Deadline(parts[0].strip(), parts[1].strip());
            tasks.add(deadline);
            ui.displayText("Got it. I've added this deadline:\n  %s\nNow you have %d tasks in your list",
                    deadline, tasks.size());
        } else {
            throw new DukeException("Invalid event format");
        }
    }

    /**
     * Handle the input for the mark and unmark commands.
     *
     * @param input     the input to be handled
     * @param completed the status to be set
     * @throws DukeException if the input is invalid
     */
    private void setTaskCompletionStatus(String input, boolean completed) throws DukeException {
        Task task = tasks.setCompletion(input, completed);
        if (completed) {
            ui.displayText("Nice! I've marked this task as done:\n  %s", task);
        } else {
            ui.displayText("Ok, I've marked this task as not done yet:\n  %s", task);
        }
    }

    /**
     * Handle the input for the delete command.
     *
     * @param input the input to be handled
     * @throws DukeException if the input is invalid
     */
    private void delete(String input) throws DukeException {
        Task task = tasks.removeTask(input);
        ui.displayText("Noted. I've removed this task:\n  %s\nNow you have %d tasks in your list", task, tasks.size());
    }

    /**
     * Displays all current tasks.
     */
    public void listHistory() {
        StringBuilder output = new StringBuilder();
        output.append("Here are the tasks in your list:\n");
        IntStream.range(0, tasks.size()).forEach(i -> output.append(String.format("%d. %s%n", i + 1, tasks.get(i))));
        ui.displayText(output.toString());
    }
}
