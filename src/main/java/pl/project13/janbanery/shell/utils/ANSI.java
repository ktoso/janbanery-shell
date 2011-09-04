package pl.project13.janbanery.shell.utils;

import jline.ANSIBuffer;

public class ANSI
{
   public static final int BOLD = 1;
   public static final int ALL_OFF = 0;

   public static String bold(Object message)
   {
      return ANSIBuffer.ANSICodes.attrib(BOLD)
            + message
            + ANSIBuffer.ANSICodes.attrib(ALL_OFF);
   }
}
