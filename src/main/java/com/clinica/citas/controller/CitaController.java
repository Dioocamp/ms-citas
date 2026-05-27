package com.clinica.citas.controller;

import com.clinica.citas.dto.ActualizarEstadoDTO;
import com.clinica.citas.dto.CitaDetalleDTO;
import com.clinica.citas.dto.CitaRequestDTO;
import com.clinica.citas.dto.CitaResponseDTO;
import com.clinica.citas.service.CitaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

/** Controlador REST de Citas. Recurso /api/citas. */
@RestController
@RequestMapping("/api/citas")
public class CitaController {

    private final CitaService citaService;

    public CitaController(CitaService citaService) {
        this.citaService = citaService;
    }

    /** POST -> agenda una cita (valida el medico en ms-personal-medico). 201 Created. */
    @PostMapping
    public ResponseEntity<CitaResponseDTO> crear(@Valid @RequestBody CitaRequestDTO dto) {
        CitaResponseDTO creada = citaService.crear(dto);
        return ResponseEntity
                .created(URI.create("/api/citas/" + creada.id()))
                .body(creada);
    }

    /** GET -> lista citas. Filtros opcionales: ?pacienteId= o ?medicoId= */
    @GetMapping
    public ResponseEntity<List<CitaResponseDTO>> listar(
            @RequestParam(required = false) Long pacienteId,
            @RequestParam(required = false) Long medicoId) {
        return ResponseEntity.ok(citaService.listar(pacienteId, medicoId));
    }

    /** GET /{id} -> obtiene una cita. 200 / 404. */
    @GetMapping("/{id}")
    public ResponseEntity<CitaResponseDTO> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(citaService.obtenerPorId(id));
    }

    /** GET /{id}/detalle -> cita enriquecida con datos del medico (otro microservicio). */
    @GetMapping("/{id}/detalle")
    public ResponseEntity<CitaDetalleDTO> obtenerDetalle(@PathVariable Long id) {
        return ResponseEntity.ok(citaService.obtenerDetalle(id));
    }

    /** PUT /{id} -> actualiza una cita. 200 / 404. */
    @PutMapping("/{id}")
    public ResponseEntity<CitaResponseDTO> actualizar(
            @PathVariable Long id, @Valid @RequestBody CitaRequestDTO dto) {
        return ResponseEntity.ok(citaService.actualizar(id, dto));
    }

    /** PATCH /{id}/estado -> cambia el estado de la cita. 200 / 404. */
    @PatchMapping("/{id}/estado")
    public ResponseEntity<CitaResponseDTO> cambiarEstado(
            @PathVariable Long id, @Valid @RequestBody ActualizarEstadoDTO dto) {
        return ResponseEntity.ok(citaService.cambiarEstado(id, dto.estado()));
    }

    /** DELETE /{id} -> elimina una cita. 204 No Content. */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        citaService.eliminar(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
