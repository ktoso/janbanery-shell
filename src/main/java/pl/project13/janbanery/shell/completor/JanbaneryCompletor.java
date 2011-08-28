package pl.project13.janbanery.shell.completor;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import jline.Completor;
import pl.project13.janbanery.core.Janbanery;
import pl.project13.janbanery.shell.HasDefinedVariables;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

@SuppressWarnings({"unchecked"})
public class JanbaneryCompletor implements Completor
{

   private DefinedVariablesCompletor variablesCompletor;

   public JanbaneryCompletor(HasDefinedVariables hasDefinedVariables)
   {
      variablesCompletor = new DefinedVariablesCompletor(hasDefinedVariables);
   }

   @Override
   public int complete(String buffer, int cursor, List candidates)
   {
      if (buffer.trim().isEmpty())
      {
         // todo hacky way...
         variablesCompletor.complete(buffer, cursor, candidates);
         candidates.add("janbanery.");

         Collections.sort(candidates);
         return 0;
      }

      if (buffer.equals("janbanery."))
      {
         addJanbaneryMethods(candidates, buffer);

         Collections.sort(candidates);
         return 0;
      }

      if (buffer.startsWith("janbanery."))
      {
         List<String> split = newArrayList(buffer.split("\\."));

         HeadAndList hal = headAndList(headAndList(split).calls); // in order to skip the "janbanery." part ;-) \hacky...

         addReflectiveDiscoveredMethods(hal.callNow, hal.calls, buffer, candidates);
      }

      if (buffer.isEmpty()
            || buffer.equals("b")
            || buffer.equals("bo")
            || buffer.equals("boar")
            || buffer.equals("board"))
      {
         candidates.add("board");
      }

      if (buffer.isEmpty()
            || buffer.equals("e")
            || buffer.equals("ex")
            || buffer.equals("exi")
            || buffer.equals("exit")                                                            )
      {
         candidates.add("exit");
      }

      if (buffer.isEmpty()
            || buffer.equals("j")
            || buffer.equals("ja")
            || buffer.equals("ja")
            || buffer.equals("jan")
            || buffer.equals("janb")
            || buffer.equals("janba")
            || buffer.equals("janban")
            || buffer.equals("janbane")
            || buffer.equals("janbaner")
            || buffer.equals("janbanery"))
      {
         candidates.add("janbanery.");
      }

      Collections.sort(candidates);

      return 0;
   }

   private void addReflectiveDiscoveredMethods(String callNow, List<String> calls, String buffer, List candidates)
   {
      deeperReflectiveDiscoverMethods(Janbanery.class, callNow, calls, buffer, candidates);
   }

   private void deeperReflectiveDiscoverMethods(Class<?> previouslyReturnedType, String callNow, List<String> calls, String buffer, List candidates)
   {
      Method[] methods = previouslyReturnedType.getDeclaredMethods();
      for (Method method : methods)
      {
         if (calls.isEmpty())
         {
            // last level, add all that match
            if (method.getName().startsWith(callNow))
            {
               if (buffer.endsWith("()."))
               {
                  List<Method> allAreCandidates = getPublicMethods(method.getReturnType());
                  addCandidates(candidates, buffer, allAreCandidates);
               }
               else
               {
                  addCandidate(candidates, buffer, method);
               }
            }
         }
         else
         {
            // we need to go deeper
            if (method.getName().equals(callNow))
            {
               HeadAndList hal = headAndList(calls);

               deeperReflectiveDiscoverMethods(method.getReturnType(), hal.callNow, hal.calls, buffer, candidates);
            }
         }
      }
   }

   private void addCandidates(List candidates, String buffer, List<Method> allAreCandidates)
   {
      for (Method candidate : allAreCandidates)
      {
         addCandidate(candidates, buffer, candidate);
      }
   }

   private List<Method> getPublicMethods(Class<?> clazz)
   {
      List<Method> methods = newArrayList(clazz.getDeclaredMethods());

      return newArrayList(Collections2.filter(methods, new Predicate<Method>()
      {
         @Override
         public boolean apply(Method input)
         {
            return isPublic(input);
         }
      }));
   }

   private void addJanbaneryMethods(List candidates, String buffer)
   {
      List<Method> methods = getPublicMethods(Janbanery.class);
      for (Method method : methods)
      {
         addCandidate(candidates, buffer, method);
      }

   }

   private void addCandidate(List candidates, String buffer, Method method)
   {
      String trail = "(";
      if (method.getParameterTypes().length == 0)
      {
         trail = "()";
      }

      if (!buffer.endsWith("."))
      {
         buffer = cutLastPart(buffer);
      }

      candidates.add(buffer + method.getName() + trail);
   }

   private HeadAndList headAndList(List<String> split)
   {
      List<String> calls = newArrayList(split);

      String callNow = "";
      if (!calls.isEmpty())
      {
         callNow = calls.get(0);
         calls.remove(0);
      }

      return new HeadAndList(callNow, calls);
   }

   private boolean isPublic(Method method)
   {
      return Modifier.isPublic(method.getModifiers());
   }

   String cutLastPart(String buffer)
   {
      int i = buffer.lastIndexOf(".");

      if (i == -1)
      {
         return buffer;
      }

      return buffer.substring(0, i + 1); // include the dot
   }

   private class HeadAndList
   {
      final String callNow;
      final List<String> calls;

      public HeadAndList(String callNow, List<String> calls)
      {
         this.callNow = removeBraces(callNow);

         this.calls = newArrayList(Collections2.transform(calls, new Function<String, String>()
         {
            @Override
            public String apply(String input)
            {
               return removeBraces(input);
            }
         }));
      }

      private String removeBraces(String methodCall)
      {
         return methodCall.replaceAll("\\(.*\\)", "");
      }

      @Override
      public String toString()
      {
         return "HeadAndList{" +
               "callNow='" + callNow + '\'' +
               ", calls=" + calls +
               '}';
      }
   }
}
