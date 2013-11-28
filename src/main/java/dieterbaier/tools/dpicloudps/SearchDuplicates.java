package dieterbaier.tools.dpicloudps;

import dieterbaier.tools.dpicloudps.PictureFileVisitor.PictureFileVisitorFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchDuplicates
{

   private static final List<String> LIST_OF_DIRS_WITH_PICTURES = new ArrayList<String>();

   private static final Map<String, List<File>> ALL_PICTURES = new HashMap<>();

   private PictureFileVisitorFactory fileVisitorFactory = new PictureFileVisitorFactory();

   public static void main(String[] args)
   {

      try
      {
         new SearchDuplicates().start("c:\\");
      }
      catch (Throwable e)
      {
         e.printStackTrace();
      }

   }

   private void examineDirectories(String startDir) throws IOException
   {
      System.out.println("Start examining: " + new Date());
      System.out.println("Running...");
      Files.walkFileTree(new File(startDir).toPath(), getFileVisitor());
      System.out.println("End examining: " + new Date());
   }
   
   private List<String> getDuplicates()
   {
      Collection<List<File>> allFiles = ALL_PICTURES.values();
      List<String> duplicates = new ArrayList<>();
      for (List<File> listOfFile : allFiles)
      {
         if (listOfFile.size() > 1)
         {
            for (File file : listOfFile)
            {
               duplicates.add(file.getAbsolutePath());
            }
            duplicates.add("");
         }
      }
      return duplicates;
   }

   private FileVisitor<Path> getFileVisitor()
   {
      return fileVisitorFactory.create(ALL_PICTURES, LIST_OF_DIRS_WITH_PICTURES);
   }

   private void printDirectoriesWithPictures()
   {
      System.out.println("Directories with pictures in it: ");
      for (String dir : LIST_OF_DIRS_WITH_PICTURES)
      {
         System.out.println(dir);
      }
   }

   private void printDuplicates()
   {
      List<String> duplicates = getDuplicates();

      if (duplicates.size() > 0)
      {
         System.out.println("There are duplicates: ");
         for (String file : duplicates)
         {
            System.out.println(file);
         }
      }
   }

   private void start(String startDir) throws IOException
   {
      examineDirectories(startDir);
      printDuplicates();
      printDirectoriesWithPictures();
   }
}
