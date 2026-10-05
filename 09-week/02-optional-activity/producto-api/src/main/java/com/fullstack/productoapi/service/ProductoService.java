package com.fullstack.productoapi.service;

import com.fullstack.productoapi.entity.Producto;
import com.fullstack.productoapi.exception.ResourceNotFoundException;
import com.fullstack.productoapi.repository.ProductoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductoService {

    private final ProductoRepository productoRepository;

    public ProductoService(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    public Producto crear(Producto producto) {
        return productoRepository.save(producto);
    }

    public List<Producto> listar() {
        return productoRepository.findAll();
    }

    public Producto obtenerPorId(Long id) {
        return productoRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Producto con id " + id + " no encontrado"
                        )
                );
    }

    public Producto actualizar(Long id, Producto datos) {

        Producto producto = obtenerPorId(id);

        producto.setNombre(datos.getNombre());
        producto.setDescripcion(datos.getDescripcion());
        producto.setPrecio(datos.getPrecio());
        producto.setStock(datos.getStock());

        return productoRepository.save(producto);
    }

    public void eliminar(Long id) {

        Producto producto = obtenerPorId(id);

        productoRepository.delete(producto);
    }
}