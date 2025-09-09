package br.com.example.davidarchanjo.controller;

import br.com.example.davidarchanjo.model.dto.AppDTO;
import br.com.example.davidarchanjo.service.AppService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/apps")
public class AppController {

    private final AppService service;

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody AppDTO dto, UriComponentsBuilder uriComponentsBuilder) {
        Long appId = service.createNewApp(dto);
        UriComponents uriComponents = uriComponentsBuilder
            .path("/api/v1/apps/{id}")
            .buildAndExpand(appId);

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(uriComponents.toUri());

        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(service.getAllApps());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getAppById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @Valid @RequestBody AppDTO dto) {
        service.updateApp(id, dto);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        service.deleteAppById(id);
        return ResponseEntity.ok().build();
    }

}
