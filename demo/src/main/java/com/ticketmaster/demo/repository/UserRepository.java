package com.ticketmaster.demo.repository;

import com.ticketmaster.demo.model.Usuario.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

        @Query("SELECT u FROM User u WHERE u.userName = :username OR u.email = :email")
        Optional<User> findByUserNameOrEmail(@Param("username") String username, @Param("email") String email);

        Optional<User> findByTelefono(String telefono);
        Optional<User> findUserById(String id);
        Optional<User> findByUserName(String username);

        Optional<User> findByEmail(String email);
}
