package com.jwt.example.repositories;

import com.jwt.example.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;


public interface UserRepository extends JpaRepository<User,String>{
 //then we get the data

     //custom method

    public Optional<User> findByEmail(String email);
        //its implementation will be given by jpa
    //so we will only use its services


}
