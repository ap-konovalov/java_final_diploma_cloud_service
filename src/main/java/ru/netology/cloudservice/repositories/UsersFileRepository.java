package ru.netology.cloudservice.repositories;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.netology.cloudservice.entities.UserFile;

import java.util.List;

@Repository
public interface UsersFileRepository extends JpaRepository<UserFile, Long> {

    List<UserFile> findByUserId(long userId, Pageable pageable);

    UserFile findByUserIdAndFileName(Long userId, String fileName);
}
