package dieterbaier.tools.dpicloudps;

import java.io.File;

public class Comparator {

	private Md5Checksum checksum = new Md5Checksum();

	public boolean equals(File fileA, File fileB) throws Exception {
		return checksum(fileA).equals(checksum(fileB));
	}

	private String checksum(File fileA) throws Exception {
		return checksum.getChecksum(fileA);
	}

}
