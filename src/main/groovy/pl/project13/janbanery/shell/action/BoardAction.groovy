package pl.project13.janbanery.shell.action

import jline.ANSIBuffer.ANSICodes
import jline.ConsoleReader
import pl.project13.janbanery.core.Janbanery

class BoardAction implements Action
{
   private static final def colW = 30
   private static final String emptyLine = ' ' * colW
   private static final String fineLine = '-' * colW
   private static final String thickLine = '=' * colW

   private static final int BOLD = 1
   private static final int ALL_OFF = 0

   Janbanery janbanery
   ConsoleReader console

   def estimates
   def users
   def taskTypes

   BoardAction(Janbanery janbanery, ConsoleReader console)
   {
      this.janbanery = janbanery
      this.console = console

      String.metaClass.wrap = {->
         if (delegate.size() > thickLine.size())
         {
            def sub = delegate.substring(0, thickLine.size())
            sub += "\n"
            sub += delegate.substring(thickLine.size()).wrap()

            return sub
         }
         return delegate
      }

      String.metaClass.wrap = {spaces ->
         if (delegate.size() + spaces > thickLine.size())
         {
            try
            {
               def sub = delegate.substring(0, thickLine.size())
               sub += "\n"
               sub += " " * spaces
               sub += delegate.substring(thickLine.size()).wrap(spaces)

               return sub
            }
            catch (Exception e)
            {
               return delegate
            }
         }
         return delegate
      }
   }

   @Override
   def call()
   {
      def columns = janbanery.columns().all()

      // preload some data
      estimates = fetchEstimates()
      users = fetchUsers()
      taskTypes = fetchTaskTypes()

      // fetch all tasks, prepare columns
      def drawableColumns = []
      for (column in columns)
      {
         def tasks = janbanery.tasks().allIn(column)

         drawableColumns << drawColumn(column, tasks)
      }

      // draw columns side by side
      drawSideBySide(drawableColumns)
   }

   def drawColumn(column, tasks)
   {
      def drawableTasks = drawTasks(tasks)

      def limit = ""
      if (column.capacity)
      {
         limit = "[${tasks.size()}/${column.capacity}]"
      }

      def col = []
      col << thickLine
      col << "${column.name} $limit"
      col << thickLine
      col.addAll drawableTasks

      col
   }

   def drawTasks(tasks)
   {
      def result = []
      tasks.each {task ->
         def taskId = "#${task.id}"
         def owner = users[task.ownerId]
         def estimate = estimates[task.estimateId]

         result << "$taskId [${taskTypes[task.taskTypeId].name}][${estimate?.label == null ? '-' : estimate.label }]"
         result << "${ANSICodes.attrib(BOLD)}${task.title.wrap()}${ANSICodes.attrib(ALL_OFF)}"
         result.addAll ("${task?.description}".wrap().split('\n'))
         result << "Owner: ${owner?.firstName} ${owner?.lastName}"
         result << fineLine
      }

      result
   }

   def drawSideBySide(columns)
   {
      console.clearScreen()

      int c = 0
      columns.each { column ->
         def l = 0
         column.each { colLine ->
            def pos = ANSICodes.gotoxy(l++, colW * c)
            if (colLine.contains("\n"))
            {
               colLine.split("\n").each { it ->
                  console.printString(it)
                  console.printString("$pos")
                  pos = ANSICodes.gotoxy(l++, colW * c)
               }
            }
            else
            {
               console.printString(colLine)
            }
            console.printString("$pos")
         }

         c++
      }
      console.printNewline()
   }

   private def fetchEstimates()
   {
      def map = [:]
      def estimates = janbanery.estimates().all()
      estimates.each { estimate ->
         map.put(estimate.id, estimate)
      }

      map
   }

   private def fetchUsers()
   {
      def map = [:]
      def users = janbanery.users().all()
      users.each { user ->
         map.put(user.id, user)
      }

      map
   }

   private def fetchTaskTypes()
   {
      def map = [:]
      def types = janbanery.taskTypes().all()
      types.each { type ->
         map.put(type.id, type)
      }

      map
   }
}
