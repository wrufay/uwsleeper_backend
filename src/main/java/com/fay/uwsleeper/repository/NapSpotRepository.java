// layer that talks to the database, gives you the CRUD operations


package com.fay.uwsleeper.repository;

import com.fay.uwsleeper.entity.NapSpot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository // tells Spring "this is a database component"

// interface that extends to the CRUD operations given by the JpaRepository interfcae
public interface NapSpotRepository extends JpaRepository<NapSpot, Long> {
    // + custom query method to find a NapSpot by building name (used when filtering by building on the front-end)
    List<NapSpot> findByBuildingIgnoreCase(String building);
}