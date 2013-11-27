package dieterbaier.tools.dpicloudps;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Md5Checksum
{

   static class FilesHandler
   {
      byte[] readAllBytes(File file) throws IOException
      {
         return Files.readAllBytes(file.toPath());
      }
   }

   private static class AlgoFactory
   {
      private MessageDigest getAlgo()
      {
         try
         {
            return MessageDigest.getInstance("MD5");
         }
         catch (NoSuchAlgorithmException e)
         {
            throw new RuntimeException(e);
         }
      }
   }

   private AlgoFactory algoFactory = new AlgoFactory();

   private FilesHandler filesHandler = new FilesHandler();

   public String getChecksum(File file) throws Exception
   {
      return toString(createChecksum(file));
   }

   private String convertToHex(byte b)
   {
      return String.format("%02X", b);
   }

   private byte[] createChecksum(File file) throws IOException
   {
      return algoFactory.getAlgo().digest(filesHandler.readAllBytes(file));
   }

   private String toString(byte[] checksum)
   {
      StringBuilder result = new StringBuilder();
      for (byte b : checksum)
      {
         result.append(convertToHex(b));
      }
      return result.toString();
   }

}
