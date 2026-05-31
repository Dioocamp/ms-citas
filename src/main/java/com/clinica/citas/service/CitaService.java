package com.clinica.citas.service;

import com.clinica.citas.dto.CitaDetalleDTO;
import com.clinica.citas.dto.CitaRequestDTO;
import com.clinica.citas.dto.CitaResponseDTO;
import com.clinica.citas.model.EstadoCita;

import java.util.List;

/** Contrato del servicio de Citas. */
public interface CitaService {

    CitaResponseDTO crear(CitaRequestDTO dto);

    List<CitaResponseDTO> listar(Long pacienteId, Long medicoId);

    CitaResponseDTO obtenerPorId(Long id);

    /** Devuelve la cita con los datos del medico obtenidos desde ms-personal-medico. */
    CitaDetalleDTO obtenerDetalle(Long id);

    CitaResponseDTO actualizar(Long id, CitaRequestDTO dto);

    CitaResponseDTO cambiarEstado(Long id, EstadoCita nuevoEstado);

    void eliminar(Long id);
}
