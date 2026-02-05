package com.example.aemix.repositories;

import com.example.aemix.entities.User;
import com.example.aemix.entities.enums.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    @Query("select u from User u where u.emailOrTelegramId = :identifier")
    Optional<User> findByIdentifier(@Param("identifier") String identifier);

    @Query("select u from User u join u.passwordResetToken prt where prt.resetToken = :token")
    Optional<User> findByResetToken(@Param("token") String token);

    @Query("""
    SELECT u FROM User u
    WHERE (CAST(:text AS string) IS NULL OR LOWER(u.emailOrTelegramId) LIKE LOWER(CONCAT('%', CAST(:text AS string), '%')))
      AND (CAST(:role AS string) IS NULL OR u.role = :role)
      AND (CAST(:isVerified AS boolean) IS NULL OR u.isVerified = :isVerified)
    ORDER BY CASE u.role
               WHEN com.example.aemix.entities.enums.Role.SUPER_ADMIN THEN 1
               WHEN com.example.aemix.entities.enums.Role.ADMIN THEN 2
               WHEN com.example.aemix.entities.enums.Role.USER THEN 3
             END,
             u.createdAt DESC
    """)
    Page<User> findUsers(
            @Param("text") String text,
            @Param("role") Role role,
            @Param("isVerified") Boolean isVerified,
            Pageable pageable
    );

}
