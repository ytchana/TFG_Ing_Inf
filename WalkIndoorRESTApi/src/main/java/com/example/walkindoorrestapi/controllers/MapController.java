package com.example.walkindoorrestapi.controllers;

import com.example.walkindoorrestapi.entities.Map;
import com.example.walkindoorrestapi.entities.User;
import com.example.walkindoorrestapi.services.MapService;
import com.example.walkindoorrestapi.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/maps")
public class MapController {

    @Autowired
    private MapService mapService;

    @Autowired
    private UserService userService;

    // Constructor
    public MapController(MapService mapService) {
        this.mapService = mapService;
    }

    // Crear un mapa
    @PostMapping("/create")
    public ResponseEntity<Map> createMap(@RequestBody Map map) {
        if (map.getOwner() == null || map.getOwner().getId() == null) { // Para evitar NullPointException
            return ResponseEntity.badRequest().body(null);
        }

        Optional<User> user = userService.getUserById(map.getOwner().getId());

        if (user.isEmpty()) {
            return ResponseEntity.badRequest().body(null); // No se encontró el usuario
        }

        map.setOwner(user.get());
        if (map.getIsPublic() == null) {
            map.setIsPublic(false);
        }
        return ResponseEntity.ok(mapService.createMap(map));
    }

    // Buscar un mapa por distintos criterios
    @GetMapping("/search")
    public ResponseEntity<List<Map>> searchMaps(
            @RequestParam(required = false) String note,
            @RequestParam(required = false) String flag,
            @RequestParam(required = false) Long userId
    ) {
        List<Map> maps = mapService.findMaps(userId);
        return ResponseEntity.ok(maps);
    }

    // Obtener todos los mapas
    @GetMapping
    public ResponseEntity<List<Map>> getAllMaps() {
        return ResponseEntity.ok(mapService.getAllMaps());
    }

    // Obtener mapa por ID
    @GetMapping("/{id}")
    public ResponseEntity<Map> getMapById(@PathVariable Long id) {
        return mapService.getMapById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }


    // Obtener mapas públicos
    @GetMapping("/public")
    public ResponseEntity<List<Map>> getPublicMaps() {
        return ResponseEntity.ok(mapService.getPublicMaps());
    }

    // Eliminar un mapa
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteMap(@PathVariable Long id) {
        mapService.deleteMap(id);
        return ResponseEntity.ok("Mapa eliminado correctamente.");
    }

    @GetMapping("/find")
    public ResponseEntity<List<Map>> getMapsByName(@RequestParam String name) {
        List<Map> maps = mapService.findMapByName(name);
        if (maps.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(maps);
    }


}