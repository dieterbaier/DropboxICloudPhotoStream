package dieterbaier.tools.dpicloudps;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class SyncTest {

	@Test
	public void getDropboxPhotoDir() {
		Dropbox dropbox = new Dropbox();
		assertEquals("C:/Users/DB/Dropbox/Kamera-Uploads",
				dropbox.getPictureUploadFoalder());
	}

	@Test
	public void getICloudPhotoDir() {
		ICloud iCloud = new ICloud();
		assertEquals("C:/Users/DB/Pictures/iCloud Photos/Uploads",
				iCloud.getPhotoStreamUploadFoalder());
	}

}
