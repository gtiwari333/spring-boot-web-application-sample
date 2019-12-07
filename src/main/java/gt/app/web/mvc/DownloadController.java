package gt.app.web.mvc;


import gt.app.domain.ReceivedFile;
import gt.app.service.file.FileDownloadUtil;
import gt.app.service.file.FileService;
import gt.app.service.file.ReceivedFileService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@Controller
@RequestMapping("/download")
@RequiredArgsConstructor
public class DownloadController {

    final ReceivedFileService receivedFileService;
    final FileService fileService;

    @GetMapping("/file/{id}")
    //no security check needed
    public void downloadFile(@PathVariable UUID id, HttpServletResponse response) throws IOException {

        Optional<ReceivedFile> fileOpt = receivedFileService.findById(id);

        if (fileOpt.isEmpty()) {
            throw new RuntimeException("File not found");
        }

        ReceivedFile receivedFile = fileOpt.get();

        Resource fileRes = fileService.loadAsResource(receivedFile.getFileGroup(), receivedFile.getStoredName());

        if (!fileRes.exists()) {
            throw new RuntimeException("File not found");
        }

        FileDownloadUtil.downloadFile(response, fileRes.getFile(), receivedFile.getOriginalFileName());
    }
}
