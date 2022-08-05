package com.company.webfluxone;

import com.company.webfluxone.models.dao.ProductoDao;
import com.company.webfluxone.models.documents.Categoria;
import com.company.webfluxone.models.documents.Producto;
import com.company.webfluxone.models.dto.ComentariosDTO;
import com.company.webfluxone.models.dto.UsuarioComentariosDTO;
import com.company.webfluxone.models.dto.UsuarioDTO;
import com.company.webfluxone.services.IproductoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@SpringBootApplication
public class WebfluxOneApplication implements CommandLineRunner {

    @Autowired
    private IproductoService service;

    @Autowired
    private ReactiveMongoTemplate mongoTemplate;

    public static void main(String[] args) {
        SpringApplication.run(WebfluxOneApplication.class, args);
    }

    @Override
    public void run(String... args) {
        this.dbdataTest();
        // this.ejemploIterable();
        // this.ejemploFlatMap();
        // this.ejemploFlatMap2();
        // this.ejemploToString();
        // this.ejemploCollectList();
        // this.ejemploUsuarioComentariosFlatMap();
        // this.ejemploUsuarioComentariosZipWith();
        // this.ejemploUsuarioComentariosZipWithForma2();
        // this.ejemploZipWithRangos();
    }

    public void dbdataTest() {
        mongoTemplate.dropCollection("productos").subscribe();
        mongoTemplate.dropCollection("categorias").subscribe();

        Categoria electronico = Categoria.builder().nombre("Electrónico").build();
        Categoria deporte = Categoria.builder().nombre("Deporte").build();
        Categoria computacion = Categoria.builder().nombre("Computación").build();
        Categoria muebles = Categoria.builder().nombre("Muebles").build();
        Flux.just(electronico, deporte, computacion, muebles)
                .flatMap(service::saveCategoria)
                .doOnNext(c ->{
                    log.info("Categoria creada: " + c.getNombre() + ", Id: " + c.getId());
                }).thenMany(
                        Flux.just(Producto.builder().
                                                nombre("TV Panasonic Pantalla LCD").
                                                precio(BigDecimal.valueOf(456.89)).
                                                categoria(electronico).
                                                build(),
                                        Producto.builder().
                                                nombre("Sony Camara HD Digital").
                                                precio(BigDecimal.valueOf(177.89)).
                                                categoria(electronico).
                                                build(),
                                        Producto.builder().
                                                nombre("Apple iPod").
                                                precio(BigDecimal.valueOf(46.89)).
                                                categoria(electronico).
                                                build(),
                                        Producto.builder().
                                                nombre("Sony Notebook").
                                                precio(BigDecimal.valueOf(846.89)).
                                                categoria(computacion).
                                                build(),
                                        Producto.builder().
                                                nombre("Hewlett Packard Multifuncional").
                                                precio(BigDecimal.valueOf(200.89)).
                                                categoria(computacion).
                                                build(),
                                        Producto.builder().
                                                nombre("Bianchi Bicicleta").
                                                precio(BigDecimal.valueOf(70.89)).
                                                categoria(deporte).
                                                build(),
                                        Producto.builder().
                                                nombre("HP Notebook Omen 17").
                                                precio(BigDecimal.valueOf(2500.89)).
                                                categoria(computacion).
                                                build(),
                                        Producto.builder().
                                                nombre("Mica Cómoda 5 Cajones").
                                                precio(BigDecimal.valueOf(150.89)).
                                                categoria(muebles).
                                                build(),
                                        Producto.builder().
                                                nombre("TV Sony Bravia OLED 4K Ultra HD").
                                                precio(BigDecimal.valueOf(2255.89)).
                                                categoria(electronico).
                                                build()
                                )
                                .flatMap(producto -> {
                                    producto.setCreateAt(new Date());
                                    return service.save(producto);
                                })
                )
                .subscribe(producto -> log.info("Insert: " + producto.getId() + " " + producto.getNombre()));
    }

    public void ejemploZipWithRangos() {
        Flux<Integer> rangos = Flux.range(0, 4);
        Flux.just(1, 2, 3, 4).map(i -> (i * 2))
                .zipWith(rangos, (uno, dos) -> String.format("Primer Flux: %d, Segundo Flux: %d", uno, dos))
                .subscribe(log::info);
    }

    public void ejemploUsuarioComentariosZipWithForma2() {
        Mono<UsuarioDTO> usuarioMono = Mono.fromCallable(() -> new UsuarioDTO("John", "Doe"));

        Mono<ComentariosDTO> comentariosUsuarioMono = Mono.fromCallable(() -> {
            ComentariosDTO comentarios = new ComentariosDTO();
            comentarios.addComentario("Hola pepe, qué tal!");
            comentarios.addComentario("Mañana voy a la playa!");
            comentarios.addComentario("Estoy tomando el curso de spring con reactor");
            return comentarios;
        });

        Mono<UsuarioComentariosDTO> usuarioConComentarios = usuarioMono.zipWith(comentariosUsuarioMono).map(tuple -> {
            UsuarioDTO u = tuple.getT1();
            ComentariosDTO c = tuple.getT2();
            return new UsuarioComentariosDTO(u, c);
        });

        usuarioConComentarios.subscribe(uc -> log.info(uc.toString()));
    }

    public void ejemploUsuarioComentariosZipWith() {
        Mono<UsuarioDTO> usuarioMono = Mono.fromCallable(() -> new UsuarioDTO("John", "Doe"));

        Mono<ComentariosDTO> comentariosUsuarioMono = Mono.fromCallable(() -> {
            ComentariosDTO comentarios = new ComentariosDTO();
            comentarios.addComentario("Hola pepe, qué tal!");
            comentarios.addComentario("Mañana voy a la playa!");
            comentarios.addComentario("Estoy tomando el curso de spring con reactor");
            return comentarios;
        });

        Mono<UsuarioComentariosDTO> usuarioConComentarios = usuarioMono.zipWith(comentariosUsuarioMono,
                (usuario, comentariosUsuario) -> new UsuarioComentariosDTO(new UsuarioDTO(), comentariosUsuario));

        usuarioConComentarios.subscribe(uc -> log.info(uc.toString()));
    }

    public void ejemploUsuarioComentariosFlatMap() {
        Mono<UsuarioDTO> usuarioMono = Mono.fromCallable(() -> new UsuarioDTO("John", "Doe"));

        Mono<ComentariosDTO> comentariosUsuarioMono = Mono.fromCallable(() -> {
            ComentariosDTO comentarios = new ComentariosDTO();
            comentarios.addComentario("Hola pepe, qué tal!");
            comentarios.addComentario("Mañana voy a la playa!");
            comentarios.addComentario("Estoy tomando el curso de spring con reactor");
            return comentarios;
        });

        Mono<UsuarioComentariosDTO> usuarioConComentarios = usuarioMono
                .flatMap(u -> comentariosUsuarioMono.map(c -> new UsuarioComentariosDTO(u, c)));
        usuarioConComentarios.subscribe(uc -> log.info("-- UC: " +uc.toString()));
    }

    public void ejemploCollectList() {

        List<UsuarioDTO> usuariosList = new ArrayList<>();
        usuariosList.add(new UsuarioDTO("Andres", "Guzman"));
        usuariosList.add(new UsuarioDTO("Pedro", "Fulano"));
        usuariosList.add(new UsuarioDTO("Maria", "Fulana"));
        usuariosList.add(new UsuarioDTO("Diego", "Sultano"));
        usuariosList.add(new UsuarioDTO("Juan", "Mengano"));
        usuariosList.add(new UsuarioDTO("Bruce", "Lee"));
        usuariosList.add(new UsuarioDTO("Bruce", "Willis"));

        Flux.fromIterable(usuariosList).collectList().subscribe(lista -> {
            lista.forEach(item -> log.info(item.toString()));
        });
    }

    public void ejemploToString() {

        List<UsuarioDTO> usuariosList = new ArrayList<>();
        usuariosList.add(new UsuarioDTO("Andres", "Guzman"));
        usuariosList.add(new UsuarioDTO("Pedro", "Fulano"));
        usuariosList.add(new UsuarioDTO("Maria", "Fulana"));
        usuariosList.add(new UsuarioDTO("Diego", "Sultano"));
        usuariosList.add(new UsuarioDTO("Juan", "Mengano"));
        usuariosList.add(new UsuarioDTO("Bruce", "Lee"));
        usuariosList.add(new UsuarioDTO("Bruce", "Willis"));

        Flux.fromIterable(usuariosList).map(
                        usuario -> usuario.getNombre().toUpperCase().concat(" ").concat(usuario.getApellido().toUpperCase()))
                .flatMap(nombre -> {
                    if (nombre.contains("bruce".toUpperCase())) {
                        return Mono.just(nombre);
                    } else {
                        return Mono.empty();
                    }
                }).map(String::toLowerCase).subscribe(log::info);
    }

    public void ejemploFlatMap() {

        List<String> usuariosList = new ArrayList<>();
        usuariosList.add("Andres Guzman");
        usuariosList.add("Pedro Fulano");
        usuariosList.add("Maria Fulana");
        usuariosList.add("Diego Sultano");
        usuariosList.add("Juan Mengano");
        usuariosList.add("Bruce Lee");
        usuariosList.add("Bruce Willis");

        Flux.fromIterable(usuariosList)
                .map(nombre -> new UsuarioDTO(nombre.split(" ")[0].toUpperCase(), nombre.split(" ")[1].toUpperCase()))
                .flatMap(usuario -> {
                    if (usuario.getNombre().equalsIgnoreCase("bruce")) {
                        return Mono.just(usuario);
                    } else {
                        return Mono.empty();
                    }
                }).map(usuario -> {
                    String nombre = usuario.getNombre().toLowerCase();
                    usuario.setNombre(nombre);
                    return usuario;
                }).subscribe(u -> log.info(u.toString()));
    }



    public void ejemploFlatMap2() {
        List<Integer> a = Arrays.asList(1, 2, 3);
        List<Integer> b = Arrays.asList(4, 5);
        List<Integer> c = Arrays.asList(6, 7, 8);

        List<List<Integer>> listOfListOfInts = Arrays.asList(a, b, c);

        System.out.println("Before flattening: " + listOfListOfInts);
        System.out.println("--Usando FLAMTMAP");
        List<Integer> listofInts = listOfListOfInts.stream()
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
        System.out.println("After flattening  : " + listofInts);
    }

    public void ejemploIterable() {

        List<String> usuariosList = new ArrayList<>();
        usuariosList.add("Andres Guzman");
        usuariosList.add("Pedro Fulano");
        usuariosList.add("Maria Fulana");
        usuariosList.add("Diego Sultano");
        usuariosList.add("Juan Mengano");
        usuariosList.add("Bruce Lee");
        usuariosList.add("Bruce Willis");

        Flux<String> nombres = Flux
                .fromIterable(usuariosList);

        Flux<UsuarioDTO> usuarios = nombres
                .map(nombre -> UsuarioDTO.builder().
                        nombre(nombre.split(" ")[0].toUpperCase()).
                        apellido(nombre.split(" ")[1].toUpperCase()).
                        build())
                .filter(usuario -> usuario.getNombre().equalsIgnoreCase("bruce")).doOnNext(usuario -> {
                    if (usuario == null) {
                        throw new RuntimeException("Nombres no pueden ser vacíos");
                    }
                    System.out.println("Concurrencia: "+usuario.getNombre().concat(" ").concat(usuario.getApellido()));
                }).map(usuario -> {
                    String nombre = usuario.getNombre().toLowerCase();
                    usuario.setNombre(nombre);
                    return usuario;
                });

        usuarios.subscribe(e -> log.info("Final: " + e.toString()), error -> log.error(error.getMessage()), () -> log.info("Ha finalizado la ejecución del observable con éxito!"));

    }
}
