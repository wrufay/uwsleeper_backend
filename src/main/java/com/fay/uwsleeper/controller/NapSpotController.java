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

@RestController
@RequestMapping("/api/spots")
@CrossOrigin(origins = "*")
public class NapSpotController {

    @Autowired
    private NapSpotRepository repository;

    @Value("${anthropic.api-key}")
    private String anthropicApiKey;

    public NapSpotController(NapSpotRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<NapSpot> getAllSpots() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public NapSpot getSpotById(@PathVariable Long id) {
        return repository.findById(id).orElse(null);
    }

    @GetMapping("/building/{building}")
    public List<NapSpot> getSpotsByBuilding(@PathVariable String building) {
        return repository.findByBuildingIgnoreCase(building);
    }

    // random
    @GetMapping("/random")
    public NapSpot getRandomSpot() {
        List<NapSpot> spots = repository.findAll();
        if (spots.isEmpty()) return null;
        return spots.get(new Random().nextInt(spots.size()));
    }

    @PostMapping
    public NapSpot createSpot(@RequestBody NapSpot spot) {
        return repository.save(spot);
    }

    @PutMapping("/{id}")
    public NapSpot updateSpot(@PathVariable Long id, @RequestBody NapSpot updatedSpot) {
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
    @DeleteMapping("/{id}")
    public void deleteSpot(@PathVariable Long id) {
        repository.deleteById(id);
    }
    // Upvote: increases upvotes by 1
    @PostMapping("/{id}/upvote")
    public NapSpot upvote(@PathVariable Long id) {
        NapSpot spot = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Spot not found"));
        spot.setUpvotes(spot.getUpvotes() + 1);
        return repository.save(spot);
    }

    // Downvote: increases downvotes by 1
    @PostMapping("/{id}/downvote")
    public NapSpot downvote(@PathVariable Long id) {
        NapSpot spot = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Spot not found"));
        spot.setDownvotes(spot.getDownvotes() + 1);
        return repository.save(spot);
    }

    // Remove upvote: decreases upvotes by 1
    @PostMapping("/{id}/remove-upvote")
    public NapSpot removeUpvote(@PathVariable Long id) {
        NapSpot spot = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Spot not found"));
        if (spot.getUpvotes() > 0) {
            spot.setUpvotes(spot.getUpvotes() - 1);
        }
        return repository.save(spot);
    }

    // Remove downvote: decreases downvotes by 1
    @PostMapping("/{id}/remove-downvote")
    public NapSpot removeDownvote(@PathVariable Long id) {
        NapSpot spot = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Spot not found"));
        if (spot.getDownvotes() > 0) {
            spot.setDownvotes(spot.getDownvotes() - 1);
        }
        return repository.save(spot);
    }
    @PostMapping("/search")
    public ResponseEntity<?> searchByAI(@RequestBody SearchRequest request) {
        List<NapSpot> allSpots = repository.findAll();

        //prompt
        StringBuilder spotsInfo = new StringBuilder();
        for (NapSpot spot : allSpots) {
            spotsInfo.append(String.format(
                    "ID: %d, Building: %s, Location: %s, Type: %s, Comfort: %d/5, Noise: %d/5, Foot Traffic: %d/5, Description: %s\n",
                    spot.getId(), spot.getBuilding(), spot.getLocation(), spot.getSpotType(),
                    spot.getComfortRating(), spot.getNoiseLevel(), spot.getFootTraffic(), spot.getDescription()
            ));
        }

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
                spotsInfo,
                request.getDescription()
        );

        try {
            String claudeResponse = callClaudeAPI(prompt);
            String cleanResponse = claudeResponse.trim()
                    .replaceAll("[^0-9]", "")
                    .trim();
            Long spotId = Long.parseLong(cleanResponse);
            NapSpot spot = repository.findById(spotId).orElse(null);
            return ResponseEntity.ok(spot);

        } catch (Exception e) {
            return ResponseEntity.status(500).body("AI search failed");
        }
    }

    private String callClaudeAPI(String prompt) throws Exception {
        String apiKey = anthropicApiKey;
        String url = "https://api.anthropic.com/v1/messages";
        String requestBody = String.format(
                "{\"model\":\"claude-3-5-haiku-20241022\",\"max_tokens\":100,\"messages\":[{\"role\":\"user\",\"content\":\"%s\"}]}",
                prompt.replace("\"", "\\\"").replace("\n", "\\n")
        );
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