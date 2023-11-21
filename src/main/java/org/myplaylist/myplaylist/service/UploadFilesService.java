package org.myplaylist.myplaylist.service;

import org.springframework.web.multipart.MultipartFile;

public interface UploadFilesService {

    boolean upload(String username, MultipartFile[] files);

}
