package com.summarise.AIRecap;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/summarise")
@CrossOrigin(origins = "*")
@AllArgsConstructor
public class SummariseController {

    private final SummariseService summariseService;

    @PostMapping
    public ResponseEntity<String> processContent(@RequestBody SummariseRequest request){
        String result = summariseService.processContent(request);
        return ResponseEntity.ok(result);
    }

    // /api/summarise/
}
