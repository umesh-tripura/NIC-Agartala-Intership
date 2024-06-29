package com.jwt.example.services;

import com.jwt.example.entities.User;
import com.jwt.example.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailService implements UserDetailsService {

     //autowiring the userrepository to get the service or implementaion of it here

     @Autowired
     private UserRepository userRepository;



    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //load user from database
        //now if we want to fetch database user we will have to write the databse interaction code so we will make repositories
        //which will extend jparepository

          //here username is our email   //ifwe donot find the user through exception
        User user=  userRepository.findByEmail(username).orElseThrow(()->  new RuntimeException("User not found!!"));


     //else if get the user

        return user;

    }
}
