package com.jwt.example.services;
import com.jwt.example.models.Nic;
import java.util.List;
public interface NicService {
    boolean saveNic(Nic nic);

    List<Nic> fetchAllUser();

    void deleteAllUser();
}
