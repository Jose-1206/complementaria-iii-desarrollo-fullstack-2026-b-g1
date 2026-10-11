package com.fullstack.productoapi.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fullstack.productoapi.entity.Producto;
import com.fullstack.productoapi.service.ProductoService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    private final ProductoService productoService;

    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    // GET - Listar todos los productos
    // También permite buscar por nombre:
    // GET /api/productos?nombre=laptop
    @GetMapping
    public ResponseEntity<List<Producto>> listar(
            @RequestParam(required = false) String nombre) {

        if (nombre != null && !nombre.isBlank()) {
            return ResponseEntity.ok(
                    productoService.buscarPorNombre(nombre)
            );
        }

        return ResponseEntity.ok(
                productoService.listarTodos()
        );
    }

    // GET - Obtener producto por ID
    @GetMapping("/{id}")
    public ResponseEntity<Producto> obtenerPorId(
            @PathVariable Long id) {

        return productoService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // POST - Crear producto
    @PostMapping
    public ResponseEntity<Producto> crear(
            @Valid @RequestBody Producto producto) {

        Producto nuevoProducto = productoService.crear(producto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(nuevoProducto);
    }

    // PUT - Actualizar producto
    @PutMapping("/{id}")
    public ResponseEntity<Producto> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody Producto producto) {

        return productoService.actualizar(id, producto)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // DELETE - Eliminar producto
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @PathVariable Long id) {

        if (!productoService.eliminar(id)) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.noContent().build();
    }
}