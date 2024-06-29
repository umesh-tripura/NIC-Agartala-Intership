package com.jwt.example.repositories;
import com.jwt.example.models.Nic;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class NicRepoImpl implements NicRepo{
    
    @Autowired
    private RedisTemplate redisTemplate;
   
    @Override
    public boolean saveNic(Nic nic){
        try{
            redisTemplate.opsForHash().put("KEY", nic.getKey() , nic);
            return true;
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }
    @Override
    public List<Nic> fetchAllUser() {
        List<Nic> nics;
        nics=redisTemplate.opsForHash().values("KEY");
        return nics;
    }
    @Override
    public void deleteAllUser() {
        redisTemplate.getConnectionFactory().getConnection().flushDb();
    }
}
