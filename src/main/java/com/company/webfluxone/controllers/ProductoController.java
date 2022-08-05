package com.company.webfluxone.controllers;

import com.company.webfluxone.models.dao.ProductoDao;
import com.company.webfluxone.models.documents.Producto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    @Autowired
    private ProductoDao productoDao;

    @GetMapping
    public Flux<Producto> getAll() {
        // return productoDao.findAll();
        return productoDao.findAll().map(producto -> {
            producto.setNombre(producto.getNombre().toUpperCase());
            return producto;
        }).doOnNext(producto -> log.info(producto.getNombre()));
    }

    @GetMapping("/{id}")
    public Mono<Producto> getById(@PathVariable String id) {
        // return productoDao.findById(id);
        Flux<Producto> productoFlux = productoDao.findAll();
        return productoFlux.filter(producto -> producto.getId().equals(id)).next();

    }
}
