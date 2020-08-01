package gt.mail.utils;

import java.io.PrintWriter;
import java.io.StringWriter;

public class Utils {

    public static String getStackTrace(Throwable throwable) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw, true);
        throwable.printStackTrace(pw);
        return sw.getBuffer().toString();
    }
}
