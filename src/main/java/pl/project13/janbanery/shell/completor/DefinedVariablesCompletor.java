package pl.project13.janbanery.shell.completor;

import com.google.common.base.Predicate;
import jline.Completor;
import pl.project13.janbanery.shell.HasDefinedVariables;

import java.util.Collections;
import java.util.List;

import static com.google.common.collect.Collections2.filter;
import static com.google.common.collect.Lists.newArrayList;

@SuppressWarnings({"unchecked"})
public class DefinedVariablesCompletor implements Completor
{
   private HasDefinedVariables hasDefinedVars;

   public DefinedVariablesCompletor(HasDefinedVariables hasDefinedVariables)
   {
      this.hasDefinedVars = hasDefinedVariables;
   }

   @Override
   public int complete(final String buffer, int cursor, List candidates)
   {
      List<String> newCandidates = hasDefinedVars.getDefinedVariables();

      if (buffer.trim().isEmpty())
      {
         addAllVariables(candidates, newCandidates);
      }
      else
      {
         newCandidates = newArrayList(filter(newCandidates, new Predicate<String>()
         {
            @Override
            public boolean apply(String input)
            {
               return input.startsWith(buffer);
            }
         }));

         addAllVariables(candidates, newCandidates);
      }


      Collections.sort(candidates);

      return 0;
   }

   private void addAllVariables(List<String> candidates, List<String> newCandidates)
   {
      candidates.addAll(newCandidates);
   }
}
