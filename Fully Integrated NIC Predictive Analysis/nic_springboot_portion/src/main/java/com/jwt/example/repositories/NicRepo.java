package com.jwt.example.repositories;
import com.jwt.example.models.Nic;

import java.util.List;

import org.springframework.stereotype.Repository;

@Repository
public interface NicRepo {
    boolean saveNic(Nic nic);

    List<Nic> fetchAllUser();

    void deleteAllUser();
}