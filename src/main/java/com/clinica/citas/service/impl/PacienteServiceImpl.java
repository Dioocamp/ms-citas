package com.clinica.citas.service.impl;

import com.clinica.citas.dto.PacienteRequestDTO;
import com.clinica.citas.dto.PacienteResponseDTO;
import com.clinica.citas.exception.BusinessException;
import com.clinica.citas.exception.ResourceNotFoundException;
import com.clinica.citas.model.Paciente;
import com.clinica.citas.repository.PacienteRepository;
import com.clinica.citas.service.PacienteService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/** Implementacion de la logica de negocio de Pacientes. */
@Service
public class PacienteServiceImpl implements PacienteService {

    private final PacienteRepository pacienteRepository;

    public PacienteServiceImpl(PacienteRepository pacienteRepository) {
        this.pacienteRepository = pacienteRepository;
    }

    @Override
    @Transactional
    public PacienteResponseDTO crear(PacienteRequestDTO dto) {
        if (pacienteRepository.existsByRut(dto.rut())) {
            throw new BusinessException("Ya existe un paciente con el RUT: " + dto.rut());
        }
        Paciente paciente = new Paciente(dto.rut(), dto.nombre(), dto.apellido(),
                dto.email(), dto.telefono(), dto.fechaNacimiento());
        return toResponse(pacienteRepository.save(paciente));
    }

    @Override
    @Transactional(readOnly = true)
    public List<PacienteResponseDTO> listar() {
        return pacienteRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public PacienteResponseDTO obtenerPorId(Long id) {
        return toResponse(buscarOExcepcion(id));
    }

    @Override
    @Transactional
    public PacienteResponseDTO actualizar(Long id, PacienteRequestDTO dto) {
        Paciente paciente = buscarOExcepcion(id);

        if (!paciente.getRut().equals(dto.rut()) && pacienteRepository.existsByRut(dto.rut())) {
            throw new BusinessException("Ya existe otro paciente con el RUT: " + dto.rut());
        }

        paciente.setRut(dto.rut());
        paciente.setNombre(dto.nombre());
        paciente.setApellido(dto.apellido());
        paciente.setEmail(dto.email());
        paciente.setTelefono(dto.telefono());
        paciente.setFechaNacimiento(dto.fechaNacimiento());

        return toResponse(pacienteRepository.save(paciente));
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        Paciente paciente = buscarOExcepcion(id);
        pacienteRepository.delete(paciente);
    }

    // --- Auxiliares ------------------------------------------------------

    private Paciente buscarOExcepcion(Long id) {
        return pacienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Paciente", id));
    }

    private PacienteResponseDTO toResponse(Paciente p) {
        return new PacienteResponseDTO(
                p.getId(), p.getRut(), p.getNombre(), p.getApellido(),
                p.getEmail(), p.getTelefono(), p.getFechaNacimiento(),
                p.getCitas() != null ? p.getCitas().size() : 0);
    }
}
