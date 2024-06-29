package com.jwt.example.services;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jwt.example.models.Nic;
import com.jwt.example.repositories.NicRepo;

@Service
public class NicServiceImpl implements NicService {

    @Autowired
    private NicRepo nicRepo;

    @Override
    public boolean saveNic(Nic nic){
        return nicRepo.saveNic(nic);
    }

    @Override
    public List<Nic> fetchAllUser() {
        return nicRepo.fetchAllUser();
    }

    @Override
    public void deleteAllUser() {
        nicRepo.deleteAllUser();
    }
}
