package pl.project13.janbanery.shell.completor;

import org.junit.Test;
import pl.project13.janbanery.shell.HasDefinedVariables;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class StringManipulationTest
{
   private JanbaneryCompletor janbaneryCompletor = new JanbaneryCompletor(mock(HasDefinedVariables.class));

   @Test
   public void shouldCutLastPartOfMethodCallCain() throws Exception
   {
      // given
      String s = "pl.project13.SomeCall()";

      // when
      String result = janbaneryCompletor.cutLastPart(s);

      // then
      assertThat(result).isEqualTo("pl.project13.");
   }
}
