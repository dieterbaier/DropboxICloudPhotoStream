package dieterbaier.tools.dpicloudps;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.AccessDeniedException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchDuplicates
{

   private static final List<String> BLACK_LIST = readBlackList();

   private static final List<String> LIST_OF_DIRS_WITH_PICTURES = new ArrayList<String>();

   private static final Map<String, List<File>> ALL_PICTURES = new HashMap<>();

   private Md5Checksum checksum = new Md5Checksum();

   public static void main(String[] args)
   {

      try
      {
         new SearchDuplicates().start("c:\\");
      }
      catch (IOException e)
      {
         e.printStackTrace();
      }

   }

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

   private void examineDirectories(String startDir) throws IOException
   {
      System.out.println("Start examining: " + new Date());
      System.out.println("Running...");
      Files.walkFileTree(new File(startDir).toPath(), myFileVisitor());
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

   private FileVisitor<Path> myFileVisitor()
   {
      return new FileVisitor<Path>()
      {
         @Override
         public FileVisitResult postVisitDirectory(Path dir, IOException exc)
               throws IOException
         {
            return continueInvastigating();
         }

         @Override
         public FileVisitResult preVisitDirectory(Path dir,
               BasicFileAttributes attrs) throws IOException
         {
            for (String blackListPath : BLACK_LIST)
            {
               if (isCurrentDirInBlacklist(dir, blackListPath))
                  return stopInvastigatingThisDir();
            }
            return continueInvastigating();
         }

         @Override
         public FileVisitResult visitFile(Path file,
               BasicFileAttributes attrs) throws IOException
         {
            if (isPicture(file))
            {
               savePicture(file);
            }
            return continueInvastigating();
         }

         @Override
         public FileVisitResult visitFileFailed(Path file, IOException exc)
               throws IOException
         {
            if (fileCantBeAccessed(exc))
            {
               return continueInvastigating();
            }
            else
            {
               printReasonOfFailure(file, exc);
               return stopInvestigating();
            }
         }

         private boolean addDuplicate(File file, String hash)
         {
            return ALL_PICTURES.get(hash).add(file);
         }

         private FileVisitResult continueInvastigating()
         {
            return FileVisitResult.CONTINUE;
         }

         private boolean fileCantBeAccessed(IOException exc)
         {
            return exc instanceof AccessDeniedException;
         }

         private void handleException(Exception e) throws IOException
         {
            if (e instanceof IOException)
               throw (IOException) e;
            else
               throw new IOException(e);
         }

         private boolean isCurrentDirInBlacklist(Path dir, String blackListPath)
         {
            return dir.toFile().getAbsolutePath().toLowerCase()
                  .startsWith(blackListPath.toLowerCase());
         }

         private boolean isPicture(Path file)
         {
            return file.toFile().isFile()
                  && file.toFile().getName().toLowerCase()
                        .endsWith(".jpg");
         }

         private boolean pictureExistsAlready(String hash)
         {
            return ALL_PICTURES.containsKey(hash);
         }

         private void printReasonOfFailure(Path file, IOException exc)
         {
            System.out.println("Failed path"
                  + file.toFile().getAbsolutePath());
            exc.printStackTrace();
         }

         private void savePicture(File file) throws Exception
         {
            savePictureDirectory(file);

            String hash = checksum.getChecksum(file);
            if (pictureExistsAlready(hash))
            {
               addDuplicate(file, hash);
            }
            else
            {
               savePicture(file, hash);
            }
         }

         private void savePicture(File file, String hash)
         {
            List<File> listOfFiles = new ArrayList<>();
            ALL_PICTURES.put(hash, listOfFiles);
            listOfFiles.add(file);
         }

         private void savePicture(Path file) throws IOException
         {
            try
            {
               savePicture(file.toFile());
            }
            catch (Exception e)
            {
               handleException(e);
            }
         }

         private void savePictureDirectory(File file)
         {
            String dir = file.getParent().toString();
            if (!LIST_OF_DIRS_WITH_PICTURES.contains(dir))
               LIST_OF_DIRS_WITH_PICTURES.add(dir);
         }

         private FileVisitResult stopInvastigatingThisDir()
         {
            return FileVisitResult.SKIP_SUBTREE;
         }

         private FileVisitResult stopInvestigating()
         {
            return FileVisitResult.TERMINATE;
         }
      };
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
