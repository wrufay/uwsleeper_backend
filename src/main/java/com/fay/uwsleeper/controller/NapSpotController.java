package com.fay.uwsleeper.controller;

import com.fay.uwsleeper.dto.SearchRequest;
import com.fay.uwsleeper.entity.NapSpot;
import com.fay.uwsleeper.repository.NapSpotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.Random;

// spring annotations
@RestController // tell Spring to handle HTTP request + automatically convert to JSON
@RequestMapping("/api/spots") // set base URL
@CrossOrigin(origins = "*") // allow any domain to call the API


public class NapSpotController {

    // setup repository (database access) + api key
    @Autowired
    private NapSpotRepository repository;

    @Value("${anthropic.api-key}")
    private String anthropicApiKey;

    // api endpoints to create/read/update/delete NapSpots -------------------------

    @GetMapping
    public List<NapSpot> getAllSpots() {
        // return list of all NapSpots in the database
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public NapSpot getSpotById(@PathVariable Long id) {
        // return single NapSpot by its ID
        return repository.findById(id).orElse(null);
    }

    @GetMapping("/building/{building}")
    public List<NapSpot> getSpotsByBuilding(@PathVariable String building) {
        // return list of NapSpots matching the specified building using custom query method
        return repository.findByBuildingIgnoreCase(building);
    }

    @GetMapping("/random")
    public NapSpot getRandomSpot() {
        List<NapSpot> spots = repository.findAll();
        if (spots.isEmpty()) return null;

        // return a spot based on a random index in the list
        return spots.get(new Random().nextInt(spots.size()));
    }
    

    @PutMapping("/{id}")
    public NapSpot updateSpot(@PathVariable Long id, @RequestBody NapSpot updatedSpot) {
        // given the ID of the spot you want to update and the new updated body, save & return that spot with each of the fields mapped to the new data.
        return repository.findById(id)
                .map(spot -> {
                    spot.setBuilding(updatedSpot.getBuilding());
                    spot.setLocation(updatedSpot.getLocation());
                    spot.setSpotType(updatedSpot.getSpotType());
                    spot.setComfortRating(updatedSpot.getComfortRating());
                    spot.setNoiseLevel(updatedSpot.getNoiseLevel());
                    spot.setFootTraffic(updatedSpot.getFootTraffic());
                    spot.setTagLine(updatedSpot.getTagLine());
                    spot.setDescription(updatedSpot.getDescription());
                    return repository.save(spot);
                })
                .orElse(null);
    }

    @PostMapping
    public NapSpot createSpot(@RequestBody NapSpot spot) {
        // note: @RequestBody turns JSON send by front-end into a NapSpot Java object.

        // return the newly saved NapSpot object w/ auto-generated ID
        return repository.save(spot);
    }

    @DeleteMapping("/{id}")
    public void deleteSpot(@PathVariable Long id) {
        // delete a spot by ID, doesn't return anything
        repository.deleteById(id);
    }


    // endpoints to manage upvotes & downvotes -------------------------
    
    @PostMapping("/{id}/upvote")
    public NapSpot upvote(@PathVariable Long id) {
        NapSpot spot = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Spot not found")); // error handling
        spot.setUpvotes(spot.getUpvotes() + 1); // increase upvote by 1, then save NapSpot + return it
        return repository.save(spot);
    }

    @PostMapping("/{id}/downvote")
    public NapSpot downvote(@PathVariable Long id) {
        NapSpot spot = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Spot not found"));
        spot.setDownvotes(spot.getDownvotes() + 1); // increase downvote by 1, then save NapSpot + return it
        return repository.save(spot);
    }

    @PostMapping("/{id}/remove-upvote")
    public NapSpot removeUpvote(@PathVariable Long id) {
        NapSpot spot = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Spot not found"));
        if (spot.getUpvotes() > 0) {
            spot.setUpvotes(spot.getUpvotes() - 1); // decrease upvote by 1, then save NapSpot + return it
        }
        return repository.save(spot);
    }

    @PostMapping("/{id}/remove-downvote")
    public NapSpot removeDownvote(@PathVariable Long id) {
        NapSpot spot = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Spot not found"));
        if (spot.getDownvotes() > 0) {
            spot.setDownvotes(spot.getDownvotes() - 1); // decrease downvote by 1, then save NapSpot + return it
        }
        return repository.save(spot);
    }

    @PostMapping("/search")
    public ResponseEntity<?> searchByAI(@RequestBody SearchRequest request) {
        List<NapSpot> allSpots = repository.findAll();

        StringBuilder spotsInfo = new StringBuilder();
        for (NapSpot spot : allSpots) {
            spotsInfo.append(String.format(
                    "ID: %d, Building: %s, Location: %s, Type: %s, Comfort: %d/5, Noise: %d/5, Foot Traffic: %d/5, Description: %s\n",
                    spot.getId(), spot.getBuilding(), spot.getLocation(), spot.getSpotType(),
                    spot.getComfortRating(), spot.getNoiseLevel(), spot.getFootTraffic(), spot.getDescription()
            ));
        }

        // construct prompt for claude AI
        String prompt = String.format(
                """
                        Given these nap spots on Waterloo campus:
                        
                        %s
                        
                        Rating scale explanation:
                        - Comfort: 1=uncomfortable, 5=very comfortable
                        - Noise: 1=silent, 5=very loud
                        - Foot Traffic: 1=isolated, 5=very busy
                        
                        User wants: %s
                        
                        Return ONLY a single integer ID number. No explanations, no colons, no text, no punctuation. Just the number. Example: 6""",
                spotsInfo, // pass in the information for all spots
                request.getDescription() // user prompt
        );

        // note on effeciency and api costs - for a larger scale vector search (eg. cosine similarity) would be better (probably more reliable too)

        try {
            // call helper method to send the prompt to Claude
            String claudeResponse = callClaudeAPI(prompt);

            // clean up the response from claude to only consist of a single number (id)
            String cleanResponse = claudeResponse.trim()
                    .replaceAll("[^0-9]", "");
            
            // change to a Long type to match ID data type
            Long spotId = Long.parseLong(cleanResponse);
            // actually get the spot based on ID and return it
            NapSpot spot = repository.findById(spotId).orElse(null);
            return ResponseEntity.ok(spot);

        } catch (Exception e) {
            // error handling, catch all
            return ResponseEntity.status(500).body("AI search failed");
        }
    }

    // note for JSON parsing - required a lot of debugging, might be a more robust solution to use a JSON library like Jackson (included in Spring Boot)

    // helper method to call Claude API
    private String callClaudeAPI(String prompt) throws Exception {
        String apiKey = anthropicApiKey;
        String url = "https://api.anthropic.com/v1/messages";

        // edit model and prompt settings here
        String requestBody = String.format(
                "{\"model\":\"claude-3-5-haiku-20241022\",\"max_tokens\":100,\"messages\":[{\"role\":\"user\",\"content\":\"%s\"}]}",

                // escaping special chars to properly construct the JSON request body (necessary when not using JSON library)
                prompt.replace("\"", "\\\"").replace("\n", "\\n")
        );

        // create Java HTTP client to send request
        java.net.http.HttpClient client = java.net.http.HttpClient.newHttpClient();
        java.net.http.HttpRequest httpRequest = java.net.http.HttpRequest.newBuilder()
                .uri(java.net.URI.create(url))
                .header("Content-Type", "application/json")
                .header("x-api-key", apiKey)
                .header("anthropic-version", "2023-06-01")
                .POST(java.net.http.HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        java.net.http.HttpResponse<String> response = client.send(
                httpRequest,
                java.net.http.HttpResponse.BodyHandlers.ofString()
        );

        String responseBody = response.body();
        int textMarker = responseBody.indexOf("\"text\":\"");
        int textStart = textMarker + 8; // Length of "\"text\":\""
        int textEnd = responseBody.indexOf("\"", textStart);
        return responseBody.substring(textStart, textEnd);
    }
}