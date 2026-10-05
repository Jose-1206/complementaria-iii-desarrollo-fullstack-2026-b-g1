package com.fullstack.productoapi.controller;

import com.fullstack.productoapi.entity.Producto;
import com.fullstack.productoapi.service.ProductoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    private final ProductoService productoService;

    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    @PostMapping
    public ResponseEntity<Producto> crear(
            @Valid @RequestBody Producto producto) {

        Producto nuevoProducto =
                productoService.crear(producto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(nuevoProducto);
    }

    @GetMapping
    public ResponseEntity<List<Producto>> listar() {

        return ResponseEntity.ok(
                productoService.listar()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<Producto> obtenerPorId(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                productoService.obtenerPorId(id)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<Producto> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody Producto producto) {

        return ResponseEntity.ok(
                productoService.actualizar(id, producto)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @PathVariable Long id) {

        productoService.eliminar(id);

        return ResponseEntity
                .noContent()
                .build();
    }
}