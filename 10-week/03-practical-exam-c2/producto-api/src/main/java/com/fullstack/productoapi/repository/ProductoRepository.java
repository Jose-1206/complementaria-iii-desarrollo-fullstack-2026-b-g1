package com.fullstack.productoapi.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fullstack.productoapi.entity.Producto;

public interface ProductoRepository extends JpaRepository<Producto, Long> {

    List<Producto> findByNombreContainingIgnoreCase(String nombre);
}