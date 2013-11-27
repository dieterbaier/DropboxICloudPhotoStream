package dieterbaier.tools.dpicloudps;

import static org.junit.Assert.assertEquals;
import dieterbaier.tools.dpicloudps.Md5Checksum.FilesHandler;

import java.io.File;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

public class Md5ChecksumTest
{

   @Mock
   private File fileMock;

   @Mock
   private FilesHandler filesHandlerMock;

   @InjectMocks
   private Md5Checksum classUnderTest = new Md5Checksum();

   private final byte[] source = "Die Daten".getBytes();

   @Before
   public void aSetUp() throws IOException
   {
      MockitoAnnotations.initMocks(this);
      Mockito.when(filesHandlerMock.readAllBytes(fileMock)).thenReturn(source);
   }

   @Test
   public void getCheckSum() throws Exception
   {
      assertEquals("254B89C48B3DDB6A2EBD8C0F5113826A", new String(
            classUnderTest.getChecksum(fileMock)));
   }
}
