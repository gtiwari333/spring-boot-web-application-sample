package gt.app.frwk;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;

import static java.lang.Thread.currentThread;

public class TestUtil {

    public static File fileFromClassPath(String name) {
        URL resource = currentThread().getContextClassLoader().getResource(name);
        if (resource == null) {
            throw new IllegalArgumentException("File not found in classpath: " + name);
        }
        try {
            return new File(resource.toURI());
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException(e);
        }
    }


}
