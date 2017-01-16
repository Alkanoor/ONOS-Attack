import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.ArrayList;


public class Main {

	/**
	 * @param args
	 * @throws URISyntaxException
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException, URISyntaxException {
		System.out.println(getPid());
		System.out.println(getAllFiles(new File("/proc/"+getPid()+"")));
		System.out.println(readFile("/proc/"+getPid()+"/cmdline"));

	}

	static String readFile(String path) throws IOException {
		InputStream is = new FileInputStream(path);
        byte[] encoded = new byte[1024];
        is.read(encoded);
        return new String(encoded);
    }

	static String getPid() throws IOException {
	    byte[] bo = new byte[256];
	    InputStream is = new FileInputStream("/proc/self/stat");
	    is.read(bo);
	    for (int i = 0; i < bo.length; i++) {
	        if ((bo[i] < '0') || (bo[i] > '9')) {
	            return new String(bo, 0, i);
	        }
	    }
	    return "-1";
	}

	static ArrayList getAllFiles(File curDir) {
		ArrayList r = new ArrayList();
        File[] filesList = curDir.listFiles();
        for(File f : filesList){
            System.out.println(f.getName());
            r.add(f.getName());
        }
        return r;
    }
}
