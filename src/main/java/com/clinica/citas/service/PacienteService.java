package com.clinica.citas.service;

import com.clinica.citas.dto.PacienteRequestDTO;
import com.clinica.citas.dto.PacienteResponseDTO;

import java.util.List;

/** Contrato del servicio de Pacientes. */
public interface PacienteService {

    PacienteResponseDTO crear(PacienteRequestDTO dto);

    List<PacienteResponseDTO> listar();

    PacienteResponseDTO obtenerPorId(Long id);

    PacienteResponseDTO actualizar(Long id, PacienteRequestDTO dto);

    void eliminar(Long id);
}
