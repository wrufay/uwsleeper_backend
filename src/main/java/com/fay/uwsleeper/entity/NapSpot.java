package com.fay.uwsleeper.entity;



import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity

public class NapSpot {

    // Getters and Setters
    @Id
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


    // Default constructor
    public NapSpot() {}

    // Constructor with all fields
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


    // Add getters and setters
    @Column(columnDefinition = "integer default 0")
    private Integer upvotes = 0;

    @Column(columnDefinition = "integer default 0")
    private Integer downvotes = 0;

}