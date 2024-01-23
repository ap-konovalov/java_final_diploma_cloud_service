package ru.netology.cloudservice.helpers;

import lombok.experimental.UtilityClass;
import ru.netology.cloudservice.entities.User;
import ru.netology.cloudservice.entities.UserFile;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertTrue;

@UtilityClass
public class FilesHelper {

    public void checkUserFileResult(User expecedUser, UserFile expectedUserFile, UserFile actualUserFile) {
        assertAll(
                () -> assertTrue(Arrays.equals(actualUserFile.getFileData(), expectedUserFile.getFileData())),
                () -> assertTrue(actualUserFile.getFileName().equals(expectedUserFile.getFileName())),
                () -> assertTrue(actualUserFile.getId() > 0)
        );
        UserHelper.checkUserData(expecedUser, actualUserFile.getUser());
    }
}
