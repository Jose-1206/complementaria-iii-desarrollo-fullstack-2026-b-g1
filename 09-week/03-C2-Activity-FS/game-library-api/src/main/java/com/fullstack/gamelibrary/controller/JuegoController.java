package com.fullstack.gamelibrary.controller;

import com.fullstack.gamelibrary.entity.Juego;
import com.fullstack.gamelibrary.service.JuegoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/juegos")
public class JuegoController {

    private final JuegoService juegoService;

    public JuegoController(JuegoService juegoService) {
        this.juegoService = juegoService;
    }

    @GetMapping
    public ResponseEntity<List<Juego>> listarTodos() {
        return ResponseEntity.ok(juegoService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Juego> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(juegoService.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<Juego> crear(
            @Valid @RequestBody Juego juego) {

        Juego nuevoJuego = juegoService.crear(juego);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(nuevoJuego);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Juego> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody Juego juego) {

        return ResponseEntity.ok(
                juegoService.actualizar(id, juego)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @PathVariable Long id) {

        juegoService.eliminar(id);

        return ResponseEntity.noContent().build();
    }
}