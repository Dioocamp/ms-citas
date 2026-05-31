package com.clinica.citas.service;

import com.clinica.citas.dto.PacienteRequestDTO;
import com.clinica.citas.dto.PacienteResponseDTO;
import com.clinica.citas.exception.BusinessException;
import com.clinica.citas.exception.ResourceNotFoundException;
import com.clinica.citas.model.Paciente;
import com.clinica.citas.repository.PacienteRepository;
import com.clinica.citas.service.impl.PacienteServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/** Pruebas unitarias del servicio de Pacientes con Mockito. */
@ExtendWith(MockitoExtension.class)
class PacienteServiceImplTest {

    @Mock
    private PacienteRepository pacienteRepository;

    @InjectMocks
    private PacienteServiceImpl pacienteService;

    @Test
    void crear_conRutNuevo_devuelvePacienteCreado() {
        PacienteRequestDTO dto = new PacienteRequestDTO(
                "99999999-9", "Maria", "Gonzalez", "maria@mail.cl",
                "+56911111111", LocalDate.of(1990, 5, 12));

        Paciente guardado = new Paciente(dto.rut(), dto.nombre(), dto.apellido(),
                dto.email(), dto.telefono(), dto.fechaNacimiento());
        guardado.setId(5L);

        when(pacienteRepository.existsByRut(dto.rut())).thenReturn(false);
        when(pacienteRepository.save(any(Paciente.class))).thenReturn(guardado);

        PacienteResponseDTO resultado = pacienteService.crear(dto);

        assertEquals(5L, resultado.id());
        assertEquals("Maria", resultado.nombre());
    }

    @Test
    void crear_conRutDuplicado_lanzaBusinessException() {
        PacienteRequestDTO dto = new PacienteRequestDTO(
                "99999999-9", "Maria", "Gonzalez", "maria@mail.cl", null, null);

        when(pacienteRepository.existsByRut(dto.rut())).thenReturn(true);

        assertThrows(BusinessException.class, () -> pacienteService.crear(dto));
    }

    @Test
    void obtenerPorId_inexistente_lanzaResourceNotFound() {
        when(pacienteRepository.findById(123L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> pacienteService.obtenerPorId(123L));
    }
}
