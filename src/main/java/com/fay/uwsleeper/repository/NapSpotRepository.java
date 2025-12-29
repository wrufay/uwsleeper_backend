package com.fay.uwsleeper.repository;

import com.fay.uwsleeper.entity.NapSpot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NapSpotRepository extends JpaRepository<NapSpot, Long> {
    List<NapSpot> findByBuildingIgnoreCase(String building);
}