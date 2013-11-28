package dieterbaier.tools.dpicloudps;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Stores a List of directories given by "blackList.txt" (which has to be on the
 * classpath) in a static manner. This list can't be modified eventhough you can
 * get the list; in such a case, you'll get only a copy of the list. This copy
 * you can modify but you can't save it again to this class.
 */
public class UnmodifyableBlackList
{

   private static final List<String> BLACK_LIST = readBlackList();

   private static Path getBlackListFile()
   {
      Path blackList;
      URL url = SearchDuplicates.class.getClassLoader().getResource("blackList.txt");
      try
      {
         blackList = new File(url.toURI()).toPath();
      }
      catch (URISyntaxException e)
      {
         blackList = new File(url.getPath()).toPath();
      }
      return blackList;
   }

   private static List<String> readBlackList()
   {
      return readLines(getBlackListFile());
   }

   private static List<String> readLines(Path blackList)
   {
      try
      {
         return Files.readAllLines(blackList, Charset.defaultCharset());
      }
      catch (IOException e)
      {
         throw new RuntimeException(e);
      }
   }

   public List<String> entries()
   {
      return new ArrayList<>(BLACK_LIST);
   }
}
