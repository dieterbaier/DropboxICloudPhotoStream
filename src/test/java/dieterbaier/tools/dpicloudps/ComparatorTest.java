package dieterbaier.tools.dpicloudps;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

public class ComparatorTest {

	private static final String MD5_OF_PICTURE = "MD5OfPicture";
	@Mock
	private File pic1Mock;
	@Mock
	private File pic2Mock;
	@Mock
	private Md5Checksum checksumMock;

	@InjectMocks
	private Comparator comparator = new Comparator();

	@Before
	public void aSetuUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		Mockito.when(checksumMock.getChecksum(pic1Mock)).thenReturn(MD5_OF_PICTURE);
		Mockito.when(checksumMock.getChecksum(pic2Mock)).thenReturn(MD5_OF_PICTURE);
	}

	@Test
	public void comparePhotosEquals() throws Exception {
		assertTrue(comparator.equals(pic1Mock, pic2Mock));
	}

	@Test
	public void comparePhotosNotEquals() throws Exception {
		Mockito.when(checksumMock.getChecksum(pic2Mock)).thenReturn("AnotherMD5");
		assertFalse(comparator.equals(pic1Mock, pic2Mock));
	}

}
