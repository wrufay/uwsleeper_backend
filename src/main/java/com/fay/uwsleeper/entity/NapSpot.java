package com.fay.uwsleeper.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

// annotations
@Setter
@Getter
@Entity // tells Hibernate "this class represents a db table"


// define NapSpot object
public class NapSpot {
    @Id // id = primary key

    // auto generate id (Long type) for newly created NapSpots
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String building;
    private String location;
    private String spotType;
    private String tagLine;
    private int comfortRating;
    private int noiseLevel;
    private int footTraffic;
    @Column(columnDefinition = "TEXT")
    private String description;
    private Integer upvotes = 0;
    private Integer downvotes = 0;


    // default (empty) constructor
    public NapSpot() {}

    // constructor with all fields for convenience when adding seed data
    public NapSpot(String building, String location, String spotType,
                   int comfortRating, int noiseLevel, int footTraffic, String tagLine, String description) {
        this.building = building;
        this.location = location;
        this.spotType = spotType;
        this.comfortRating = comfortRating;
        this.noiseLevel = noiseLevel;
        this.footTraffic = footTraffic;
        this.tagLine = tagLine;
        this.description = description;
    }

}