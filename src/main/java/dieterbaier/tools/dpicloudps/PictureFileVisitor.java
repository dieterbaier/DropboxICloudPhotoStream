/**
 * PictureFileVisitor.java
 *
 * (c) 2013 NovaTec GmbH Germany
 *
 * History:
 * 28.11.2013 - DB - TODO add to history
 */
package dieterbaier.tools.dpicloudps;

import java.io.File;
import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PictureFileVisitor implements FileVisitor<Path>
{
   public static class PictureFileVisitorFactory
   {
      public FileVisitor<Path> create(Map<String, List<File>> allPictures, List<String> listOfDirsWithPictures)
      {
         return new PictureFileVisitor(allPictures, listOfDirsWithPictures);
      }
   }

   private static final UnmodifyableBlackList blackList = new UnmodifyableBlackList();

   private Map<String, List<File>> allPictures;

   private List<String> listOfDirsWithPictures;

   private Md5Checksum checksum = new Md5Checksum();

   private PictureFileVisitor(Map<String, List<File>> allPictures, List<String> listOfDirsWithPictures)
   {
      this.allPictures = allPictures;
      this.listOfDirsWithPictures = listOfDirsWithPictures;
   }

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
      for (String blackListPath : blackList.entries())
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
      return allPictures.get(hash).add(file);
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
      return allPictures.containsKey(hash);
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
      allPictures.put(hash, listOfFiles);
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
      if (!listOfDirsWithPictures.contains(dir))
         listOfDirsWithPictures.add(dir);
   }

   private FileVisitResult stopInvastigatingThisDir()
   {
      return FileVisitResult.SKIP_SUBTREE;
   }

   private FileVisitResult stopInvestigating()
   {
      return FileVisitResult.TERMINATE;
   }
}
