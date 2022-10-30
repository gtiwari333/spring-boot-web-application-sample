package gt.app.modules.file;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.util.MimeTypeUtils;

import jakarta.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public final class FileDownloadUtil {

    private FileDownloadUtil() {
    }

    public static void downloadFile(HttpServletResponse response, File file, String originalFileName) throws IOException {
        handle(response, file, originalFileName, null);
    }

    public static void downloadFile(HttpServletResponse response, File file, String originalFileName, String mimeType) throws IOException {
        handle(response, file, originalFileName, mimeType);
    }


    private static void handle(HttpServletResponse response, File file, String originalFileName, String mimeType) throws IOException {
        try (FileInputStream in = new FileInputStream(file)) {

            // get MIME type of the file

            if (mimeType == null) {
                // set to binary type if MIME mapping not found
                mimeType = MimeTypeUtils.APPLICATION_OCTET_STREAM.getType();
            }

            // set content attributes for the response
            response.setContentType(mimeType);
            response.setContentLength((int) file.length());

            // This will download the file to the user's computer
            response.setHeader("Content-Disposition", "attachment; filename=" + originalFileName);

            IOUtils.copy(in, response.getOutputStream());

            response.getOutputStream().flush();
        }
    }

}
