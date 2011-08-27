package pl.project13.janbanery.shell.runner;

import com.google.common.io.Closeables;
import pl.project13.janbanery.core.Janbanery;
import pl.project13.janbanery.core.JanbaneryFactory;
import pl.project13.janbanery.shell.JanbaneryShell;

import java.io.BufferedWriter;
import java.io.Console;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import static org.codehaus.groovy.runtime.DefaultGroovyMethods.newWriter;

public class Main
{

   Console console = System.console();

   public static void main(String[] args) throws IOException
   {
      new Main().run();
   }

   private void run() throws IOException
   {
      console.format("Welcome to Janbanery-shell!\n");
      console.format("---------------------------------\n");

      Janbanery janbanery = null;

      if (foundJanbaneryShellProperties())
      {
         console.format("Logging in using api key found in ~/.janbanery\n");

         janbanery = loginUsingPersistedApiKey();
      }

      if (janbanery == null)
      {
         console.format("Manual login needed...\n");
         janbanery = loginUsingUsernameAndPassword();
      }

      new JanbaneryShell(janbanery).run();
   }

   private boolean foundJanbaneryShellProperties()
   {
      File janbaneryPropsFile = getPropertiesFile();

      return janbaneryPropsFile.exists();
   }

   private Janbanery loginUsingPersistedApiKey() throws IOException
   {
      File janbaneryPropsFile = getPropertiesFile();

      Properties properties = new Properties();
      properties.load(new FileInputStream(janbaneryPropsFile));

      String apikey = (String) properties.get("apikey");

      console.format("Logging into kanbanery.com using apiKey from ~/.janbanery...\n");
      console.format("Creating Janbanery instance...\n");

      return new JanbaneryFactory().connectUsing(apikey)
                                   .notDeclaringWorkspaceYet();
   }

   private File getPropertiesFile()
   {
      String userHome = System.getProperty("user.home");
      File userHomeDir = new File(userHome);
      return new File(userHomeDir, ".janbanery");
   }

   private Janbanery loginUsingUsernameAndPassword() throws IOException
   {
      console.format("Login: ");
      String login = console.readLine();
      console.format("Pass: ");
      char[] chars = console.readPassword();

      console.format("Logging into kanbanery.com...\n");
      console.format("Creating Janbanery instance...\n");

      Janbanery janbanery = new JanbaneryFactory().connectUsing(login, String.valueOf(chars))
                                                  .notDeclaringWorkspaceYet();

      persistApiKey(janbanery.getAuthMode().getAuthHeader().getValue());

      return janbanery;
   }

   private void persistApiKey(String apikey) throws IOException
   {
      console.format("Persisting your apiKey: " + apikey + "...\n");

      Properties properties = new Properties();
      properties.put("apikey", apikey);

      BufferedWriter writer = newWriter(getPropertiesFile());
      properties.store(writer, "Janbanery properties");
      Closeables.closeQuietly(writer);

      console.format("Done!\n");
   }

}
