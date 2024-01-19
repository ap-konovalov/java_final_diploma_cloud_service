package ru.netology.cloudservice.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.netology.cloudservice.entities.User;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

@Service
@RequiredArgsConstructor
public class FileStorageServiceImpl implements FileStorageService {


    public File getFile(User user, String filename) {
//        TODO: implement method
//        String filePath = fileStorageRepository.getFilePathByUserIdAndFilename(user.getId(), filename);
        File f = new File("example.txt");
        return f;
    }

    public void saveFile(User user, String fileName, byte[] file, String hash) {
        //        TODO: implement method
        try {
            FileOutputStream outputStream = new FileOutputStream(String.format("cloudservice/%s/%s", user.getId(), fileName));
            outputStream.write(file);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
//        fileStorageRepository.saveFileInfo();
    }
}
