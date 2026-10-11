package com.fullstack.productoapi.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.fullstack.productoapi.entity.Producto;
import com.fullstack.productoapi.repository.ProductoRepository;

@Service
public class ProductoService {

    private final ProductoRepository productoRepository;

    public ProductoService(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    // Listar todos los productos
    public List<Producto> listarTodos() {
        return productoRepository.findAll();
    }

    // Buscar productos por nombre
    public List<Producto> buscarPorNombre(String nombre) {
        return productoRepository.findByNombreContainingIgnoreCase(nombre);
    }

    // Buscar producto por ID
    public Optional<Producto> obtenerPorId(Long id) {
        return productoRepository.findById(id);
    }

    // Crear producto
    public Producto crear(Producto producto) {
        return productoRepository.save(producto);
    }

    // Actualizar producto
    public Optional<Producto> actualizar(Long id, Producto productoActualizado) {

        return productoRepository.findById(id)
                .map(producto -> {
                    producto.setNombre(productoActualizado.getNombre());
                    producto.setPrecio(productoActualizado.getPrecio());
                    producto.setCategoria(productoActualizado.getCategoria());

                    return productoRepository.save(producto);
                });
    }

    // Eliminar producto
    public boolean eliminar(Long id) {

        if (!productoRepository.existsById(id)) {
            return false;
        }

        productoRepository.deleteById(id);
        return true;
    }
}