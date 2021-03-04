package de.hsw.busplaner.services;

import javax.websocket.server.ServerEndpoint;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.Repository;
import org.springframework.stereotype.Service;

import de.hsw.busplaner.beans.Buslinie;
import de.hsw.busplaner.beans.Fahrtstrecke;
import de.hsw.busplaner.repositories.FahrtstreckeRepository;
import lombok.extern.java.Log;

@Log
@Service
public class FahrtstreckeService extends BasicService<Fahrtstrecke, Long> {

    @Autowired
    FahrtstreckeRepository repository;

    @Override
    protected FahrtstreckeRepository getRepository() {
        return repository;
    }

    public Iterable<Fahrtstrecke> findAllByBuslinieId(Buslinie buslinie) {
        return repository.findAllByBuslinieId(buslinie);
    }

}
