package pl.project13.janbanery.shell;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import jline.CandidateListCompletionHandler;
import jline.ConsoleReader;
import pl.project13.janbanery.core.Janbanery;
import pl.project13.janbanery.core.flow.TaskFlow;
import pl.project13.janbanery.resources.Estimate;
import pl.project13.janbanery.resources.Project;
import pl.project13.janbanery.resources.Task;
import pl.project13.janbanery.resources.TaskType;
import pl.project13.janbanery.resources.User;
import pl.project13.janbanery.resources.Workspace;
import pl.project13.janbanery.shell.action.BoardAction;
import pl.project13.janbanery.shell.completor.JanbaneryCompletor;
import pl.project13.janbanery.shell.utils.ANSI;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Lists.newArrayList;

public class JanbaneryShell implements Runnable, HasDefinedVariables
{
   private static final String EXIT_CMD = "exit";
   private static final String WORKSPACES_CMD = "workspaces";
   private static final String BOARD_CMD = "board";
   private static final String TASK_CMD = "task";
   private static final String TASK_SHORT_CMD = "t";


   private Janbanery janbanery;

   private ConsoleReader console;
   private GroovyShell groovyShell;
   private Binding bindings;

   private int savedResults = 0;

   private List<String> definedVariables = newArrayList();

   public JanbaneryShell(Janbanery janbanery) throws IOException
   {
      this.janbanery = janbanery;

      console = new ConsoleReader();
      console.setDefaultPrompt("> ");
      console.setUseHistory(true);
      console.setCompletionHandler(new CandidateListCompletionHandler());
      console.addCompletor(new JanbaneryCompletor(this));
//      console.addCompletor(new DefinedVariablesCompletor(this)); // todo would be a lot better

      bindings = new Binding();
      bindings.setVariable("janbanery", janbanery);
      bindings.setVariable("me", janbanery.users().current());

      groovyShell = new GroovyShell(getClass().getClassLoader(), bindings);
   }

   @Override
   public void run()
   {
      String command = WORKSPACES_CMD;

      while (exitWasNotCalled(command))
      {

         if (command.trim().length() == 0)
         {
            // do nothing
         }
         else if (command.equals(TASK_CMD) || command.equals(TASK_SHORT_CMD))
         {
            try
            {
               doTask();
            }
            catch (Exception e)
            {
               e.printStackTrace();
            }
         }
         else if (command.equals(WORKSPACES_CMD))
         {
            doWorkspaces();
         }
         else if (command.equals(BOARD_CMD))
         {
            doBoard();
         }
         else
         {
            try
            {
               runInGroovyShell(command);
            }
            catch (Exception e)
            {
               printException(e);
            }
         }

         try
         {
            command = console.readLine("> ");
         }
         catch (IOException e)
         {
            throw new RuntimeException("Unable to read using JConsole", e);
         }
      }

      janbanery.close();
      System.exit(0);
   }

   private void doTask() throws IOException
   {
      console.printString("Creating new task...");
      console.printNewline();

      String taskTitle = console.readLine("Task title: ");
      TaskType taskType = selectFrom(janbanery.taskTypes().all(), "Select Type: ");
      String description = readOrNull("Task Description [none]: ");
      Estimate estimate = selectFrom(janbanery.estimates().all(), "Select Estimate [none]:");
      User owner = selectFromOrNull(janbanery.users().all(), "Assign to [noone]:");

      Task task = new Task.Builder(taskTitle, taskType)
            .description(description)
            .estimate(estimate)
            .build();
      TaskFlow taskFlow = janbanery.tasks().create(task);

      println("Created task: %s", ANSI.bold(taskFlow.get().getId()));

      if (owner != null)
      {
         taskFlow.assign().to(owner);
         println("Assigned it to: %s", displayName(owner));
      }
   }

   private String displayName(User user)
   {
      return String.format("%s %s <%s>",
                           ANSI.bold(user.getFirstName()),
                           ANSI.bold(user.getLastName()),
                           user.getEmail());
   }

   private String readOrNull(String message) throws IOException
   {
      String s = console.readLine(message);
      return s.trim().length() > 0 ? s : null;
   }

   private <T> T selectFromOrNull(List<T> all, String message) throws IOException
   {
      println(message);

      printSelectionMenu(all);
      int i = readNumber(message);

      return i > all.size() ? null : all.get(i);
   }

   private <T> T selectFrom(List<T> all, String message) throws IOException
   {
      println(message);

      int i;
      do
      {
         printSelectionMenu(all);
         i = readNumber(message);
      } while (i < 0 || i > all.size());

      return all.get(i);
   }

   private int readNumber(String message) throws IOException
   {
      String s = console.readLine(message);
      int i;
      try
      {
         i = Integer.parseInt(s);
      }
      catch (NumberFormatException e)
      {
         return -1;
      }
      return i;
   }

   private void printSelectionMenu(List<?> all) throws IOException
   {
      int i = 0;
      for (Object t : all)
      {
         String name = "";
         if (t instanceof TaskType)
         {
            name = ((TaskType) t).getName();
         }
         else
         {
            name = t.toString();
         }

         console.printString(String.format("%d %s", i++, name));
         console.printNewline();
      }
   }

   private void doBoard()
   {
      new BoardAction(janbanery, console).call();
   }

   private void runInGroovyShell(String command)
   {
      Object evaluate = groovyShell.evaluate(command);

      printEvaluated(evaluate);

      String canonicalName = evaluate.getClass().getCanonicalName();
      if (canonicalName.contains("janbanery") || canonicalName.contains("List"))
      {
         if (command.contains("def "))
         {
            String[] split = command.split(" ");
            persistVariable(split[1], evaluate);
         }
         else
         {
            persistVariable(rVarName(), evaluate);
         }
      }
   }

   private void printException(Exception e)
   {
      println("~~~~~~~~~~~~~~~~~~~ exception ~~~~~~~~~~~~~~~~~~~");
      e.printStackTrace();
      println("~~~~~~~~~~~~~~~~~~~ end of exception ~~~~~~~~~~~~~~~~~~~");
   }

   private void printEvaluated(Object evaluate)
   {
      println("------------------- evaluated -------------------");

      if (List.class.isAssignableFrom(evaluate.getClass()))
      {
         List evalList = (List) evaluate;
         printCollection(evalList);
      }
      else
      {
         println(evaluate.toString());
      }

      println("------------------- end of evaluated -------------------");
   }

   private void printCollection(List<?> evalList)
   {
      try
      {
         List<String> columnStrings = newArrayList(Collections2.transform(evalList, new Function<Object, String>()
         {
            @Override
            public String apply(Object input)
            {
               return input.toString();
            }
         }));
         console.printColumns(columnStrings);
      }
      catch (IOException e)
      {
         throw new RuntimeException(e);
      }
   }

   private void persistVariable(String variableName, Object evaluate)
   {
      println("+++++++++++++++++++ persisting +++++++++++++++++++");
      println("+   " + variableName + " = " + evaluate);
      println("+   class = " + evaluate.getClass().getCanonicalName());

//      groovyShell.setVariable(rVarName(), evaluate); // somehow, didn't work
      bindings.setVariable(variableName, evaluate);
      groovyShell = new GroovyShell(getClass().getClassLoader(), bindings);
      println("+++++++++++++++++++ end of persisting +++++++++++++++++++");
   }

   private String rVarName()
   {
      return "r" + savedResults++;
   }

   private void doWorkspaces()
   {
      Map<Integer, String> selectAProject = new HashMap<Integer, String>();
      Map<Integer, Workspace> selectAWorkspace = new HashMap<Integer, Workspace>();
      int i = 0;

      List<Workspace> all = janbanery.workspaces().all();
      println();

      println("Workspaces: ");
      for (Workspace workspace : all)
      {
         println("* %s", workspace.getName());
         for (Project project : workspace.getProjects())
         {
            i++;
            selectAWorkspace.put(i, workspace);
            selectAProject.put(i, project.getName());

            println("* %s / %s", workspace.getName(), project.getName());
         }
         println();
      }

      try
      {
         Integer selectedProjectId = null;
         do
         {
            console.printString("Projects: ");
            console.printNewline();

            for (Integer key : selectAProject.keySet())
            {
               console.printString(String.format("%d - %s", key, selectAProject.get(key)));
               console.printNewline();
            }

            String s = console.readLine("Select project: ");
            try
            {
               selectedProjectId = Integer.parseInt(s);
            }
            catch (NumberFormatException ignored)
            {
               // ignored it's ok...
            }

         } while (selectedProjectId == null);

         Workspace workspace = selectAWorkspace.get(selectedProjectId);
         String projectName = selectAProject.get(selectedProjectId);
         janbanery.usingProject(workspace, projectName);
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }

   private void println()
   {
      try
      {
         console.printNewline();
      }
      catch (IOException e)
      {
         throw new RuntimeException(e);
      }
   }

   private void println(String format, String... strings)
   {
      try
      {
         console.printString(String.format(format, strings));
         println();
      }
      catch (IOException e)
      {
         throw new RuntimeException(e);
      }
   }

   private boolean exitWasNotCalled(String cmd)
   {
      return !cmd.equals(EXIT_CMD);
   }

   @Override
   public List<String> getDefinedVariables()
   {
      return definedVariables;
   }
}
