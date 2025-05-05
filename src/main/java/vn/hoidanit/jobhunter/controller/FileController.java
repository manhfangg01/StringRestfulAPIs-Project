package vn.hoidanit.jobhunter.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import vn.hoidanit.jobhunter.domain.response.file.ResFileUploadDTO;
import vn.hoidanit.jobhunter.service.FileService;
import vn.hoidanit.jobhunter.service.JobService;
import vn.hoidanit.jobhunter.util.annotation.ApiMessage;
import vn.hoidanit.jobhunter.util.error.FileStorageException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/api/v1")
public class FileController {
    @Value("${hoidanit.upload-file.base-uri}")
    private String baseURI;
    private FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    // @GetMapping("/files")
    // @ApiMessage("download single file")
    // public ResponseEntity<Resource> getMethodName(@RequestParam(name =
    // "fileName", required = false) String fileName,
    // @RequestParam(name = "folder", required = false) String folder)
    // throws FileStorageException, URISyntaxException, IOException {
    // if (fileName == null || folder == null) {
    // throw new FileStorageException("Missing required params : (fileName or
    // folder)");
    // }

    // // check file existed (and not a directory) with file length
    // long fileLength = this.fileService.getFileLength(fileName, folder);
    // if (fileLength == -1) {
    // throw new FileStorageException("File not found");
    // }

    // // download file
    // InputStreamResource resource = this.fileService.getResource(fileName,
    // folder);

    // return ResponseEntity.ok()
    // .header(HttpHeaders.CONTENT_DISPOSITION,
    // "attachment; filename=\"" + fileName + "\"")
    // .contentLength(fileLength)
    // .contentType(MediaType.APPLICATION_OCTET_STREAM)
    // .body(resource);

    // }// todo

    @PostMapping("/files")
    @ApiMessage("upload Single file")
    public ResponseEntity<ResFileUploadDTO> uploadFile(
            @RequestParam(name = "file", required = false) MultipartFile file,
            @RequestParam(name = "folder", required = false) String folder)
            throws URISyntaxException, IOException, FileStorageException {

        // handle empty file
        if (file.isEmpty() || file == null) {
            throw new FileStorageException("File is empty");
        }

        // handle file extension
        String fileName = file.getOriginalFilename();
        List<String> allowedExtensions = Arrays.asList("pdf", "jpg", "jpeg", "png", "doc", "docx");
        boolean isValid = allowedExtensions.stream().anyMatch(item -> fileName.toLowerCase().endsWith(item));

        if (!isValid) {
            throw new FileStorageException("Invalid file extension. only allows: " + allowedExtensions.toString());
        }

        // Create folder path
        this.fileService.createUploadFolder(baseURI + folder);

        // Store file in folder
        String uploadedFile = this.fileService.store(file, folder);

        return ResponseEntity.ok().body(new ResFileUploadDTO(uploadedFile, Instant.now()));

    }

}
