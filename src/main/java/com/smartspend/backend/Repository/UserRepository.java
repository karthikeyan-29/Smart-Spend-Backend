package com.smartspend.backend.Repository;

import com.smartspend.backend.Entity.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByUid(String uid);
//    Optional<User> findUserByUid(String uid);
    @Transactional
    void deleteByUid(String uid);
}
