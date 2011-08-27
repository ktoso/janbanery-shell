package pl.project13.janbanery.shell;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import pl.project13.janbanery.shell.completor.JanbaneryCompletor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;

@RunWith(Parameterized.class)
public class JanbaneryCompletorTest
{

   private JanbaneryCompletor janbaneryCompletor = new JanbaneryCompletor(mock(HasDefinedVariables.class));

   @Parameterized.Parameters
   public static Collection<Object[]> data()
   {
      return Arrays.asList(new Object[][]{
            {
                  "jan",
                  newArrayList("janbanery.")
            },
            {
                  "janbanery.",
                  newArrayList("janbanery.workspaces()",
                               "janbanery.tasks()",
                               "janbanery.users()",
                               "janbanery.taskTypes()",
                               "janbanery.log()"
                              )
            },
            {
                  "janbanery.workspaces().",
                  newArrayList("janbanery.workspaces().all()",
                               "janbanery.workspaces().current()",
                               "janbanery.workspaces().byName("
                              )
            },
            {
                  "janbanery.tasks().",
                  newArrayList("janbanery.tasks().create(",
                               "janbanery.tasks().delete(",
                               "janbanery.tasks().all()",
                               "janbanery.tasks().allIn(",
                               "janbanery.tasks().allByTitle(",
                               "janbanery.tasks().assign("
                              )
            },
            {
                  "janbanery.tasks().mark().",
                  newArrayList("janbanery.tasks().mark().asReadyToPull()",
                               "janbanery.tasks().mark().asNotReadyToPull()"
                              )
            },
            {
                  "janbanery.tasks().move().",
                  newArrayList("janbanery.tasks().move().toNextColumn()",
                               "janbanery.tasks().move().toPreviousColumn()",
                               "janbanery.tasks().move().to(",
                               "janbanery.tasks().move().toIceBox()",
                               "janbanery.tasks().move().toArchive()",
                               "janbanery.tasks().move().toBoard()",
                               "janbanery.tasks().move().toFirstColumn()",
                               "janbanery.tasks().move().toLastColumn()"
                              )
            },
            {
                  "janbanery.tasks().move().toNextColumn().",
                  newArrayList("janbanery.tasks().move().toNextColumn().get()",
                               "janbanery.tasks().move().toNextColumn().move()",
                               "janbanery.tasks().move().toNextColumn().delete()",
                               "janbanery.tasks().move().toNextColumn().mark()",
                               "janbanery.tasks().move().toNextColumn().assign()"
                              )
            },
            {
                  "janbanery.tasks().move().toNextColumn()",
                  newArrayList("janbanery.tasks().move().toNextColumn()")
            },
            {
                  "janbanery.tasks().move().toNext",
                  newArrayList("janbanery.tasks().move().toNextColumn()")
            }
      });
   }

   String input;
   List<String> expectedCandidates;

   public JanbaneryCompletorTest(String input, List<String> expectedCandidates)
   {
      this.input = input;
      this.expectedCandidates = expectedCandidates;
   }

   @Test
   public void shouldProvideExpectedCandidates() throws Exception
   {
      // when
      ArrayList<Object> responseCandidates = newArrayList();
      janbaneryCompletor.complete(input, 0, responseCandidates);

      // then
      assertThat(responseCandidates).contains(expectedCandidates.toArray());
   }
}
