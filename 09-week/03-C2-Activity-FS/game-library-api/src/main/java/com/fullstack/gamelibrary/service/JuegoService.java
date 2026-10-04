package com.fullstack.gamelibrary.service;

import com.fullstack.gamelibrary.entity.Juego;
import com.fullstack.gamelibrary.exception.ResourceNotFoundException;
import com.fullstack.gamelibrary.repository.JuegoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JuegoService {

    private final JuegoRepository juegoRepository;

    public JuegoService(JuegoRepository juegoRepository) {
        this.juegoRepository = juegoRepository;
    }

    public List<Juego> listarTodos() {
        return juegoRepository.findAll();
    }

    public Juego buscarPorId(Long id) {
        return juegoRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Juego no encontrado con id: " + id
                        ));
    }

    public Juego crear(Juego juego) {
        return juegoRepository.save(juego);
    }

    public Juego actualizar(Long id, Juego datos) {

        Juego juego = buscarPorId(id);

        juego.setTitulo(datos.getTitulo());
        juego.setGenero(datos.getGenero());
        juego.setPlataforma(datos.getPlataforma());
        juego.setPrecio(datos.getPrecio());

        return juegoRepository.save(juego);
    }

    public void eliminar(Long id) {

        Juego juego = buscarPorId(id);

        juegoRepository.delete(juego);
    }
}