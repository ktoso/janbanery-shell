package pl.project13.janbanery.shell;

import org.junit.Test;
import pl.project13.janbanery.shell.completor.DefinedVariablesCompletor;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static org.fest.assertions.Assertions.assertThat;

public class DefinedVariablesCompletorTest
{
   List<String> vars = newArrayList();

   DefinedVariablesCompletor completor = new DefinedVariablesCompletor(new HasDefinedVariables()
   {
      @Override
      public List<String> getDefinedVariables()
      {
         return vars;
      }
   });

   @Test
   public void testComplete() throws Exception
   {
      // given
      String buffer = "";

      vars.add("r0");
      vars.add("r1");

      // when
      ArrayList<Object> candidates = newArrayList();
      completor.complete(buffer, 0, candidates);

      // then
      assertThat(candidates).contains(newArrayList("r0", "r1").toArray());
   }

      @Test
   public void testCompleteWithFiltering() throws Exception
   {
      // given
      String buffer = "s";

      vars.add("something");
      vars.add("r1");

      // when
      ArrayList<Object> candidates = newArrayList();
      completor.complete(buffer, 0, candidates);

      // then
      assertThat(candidates).contains(newArrayList("something").toArray());
   }
}
