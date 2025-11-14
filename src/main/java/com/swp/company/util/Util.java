package com.swp.company.util;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.UUID;


public class Util {
    public static final String UPLOAD_DIR =
            System.getenv().getOrDefault("UPLOAD_DIR",
                    System.getProperty("user.dir") + "/uploads");

    public static LocalDate parseDate(String date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return LocalDate.parse(date, formatter);
    }

    public static String uploadImage(MultipartFile file) throws IOException {
        String extension = "";
        String fileName = file.getOriginalFilename();
        if (fileName != null && fileName.contains(".")) {
            extension = fileName.substring(fileName.lastIndexOf("."));
        }
        String newName = UUID.randomUUID().toString() + extension;
        Path filePath = Paths.get(UPLOAD_DIR, newName);
        file.transferTo(filePath.toFile());

        // Chỉ trả về đường dẫn tương đối
        return "/uploads/" + newName;
    }

}

//startTime , endTime (String) =>