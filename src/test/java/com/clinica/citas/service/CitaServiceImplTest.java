package com.clinica.citas.service;

import com.clinica.citas.client.MedicoClient;
import com.clinica.citas.client.MedicoDTO;
import com.clinica.citas.dto.CitaRequestDTO;
import com.clinica.citas.dto.CitaResponseDTO;
import com.clinica.citas.event.NotificadorCitas;
import com.clinica.citas.exception.BusinessException;
import com.clinica.citas.model.Cita;
import com.clinica.citas.model.EstadoCita;
import com.clinica.citas.model.Paciente;
import com.clinica.citas.repository.CitaRepository;
import com.clinica.citas.repository.PacienteRepository;
import com.clinica.citas.service.impl.CitaServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/** Pruebas unitarias del servicio de Citas con Mockito. */
@ExtendWith(MockitoExtension.class)
class CitaServiceImplTest {

    @Mock
    private CitaRepository citaRepository;

    @Mock
    private PacienteRepository pacienteRepository;

    @Mock
    private MedicoClient medicoClient;

    @Mock
    private NotificadorCitas notificadorCitas;

    @InjectMocks
    private CitaServiceImpl citaService;

    private static final MedicoDTO MEDICO = new MedicoDTO(
            3L, "Carla", "Soto", "carla@clinica.cl", 1L, "Cardiologia");

    private static Paciente pacienteDePrueba() {
        Paciente paciente = new Paciente("11111111-1", "Juan", "Perez",
                "juan@mail.cl", "+56922222222", LocalDate.of(1985, 3, 20));
        paciente.setId(9L);
        return paciente;
    }

    private static CitaRequestDTO requestDePrueba() {
        return new CitaRequestDTO(LocalDate.now().plusDays(7),
                LocalTime.of(10, 30), "Control anual", 3L, 9L);
    }

    @Test
    void crear_conMedicoYPacienteValidos_creaCitaYPublicaNotificacion() {
        Paciente paciente = pacienteDePrueba();
        CitaRequestDTO dto = requestDePrueba();

        Cita guardada = new Cita(dto.fecha(), dto.hora(), dto.motivo(),
                EstadoCita.PROGRAMADA, dto.medicoId(), paciente);
        guardada.setId(7L);

        when(pacienteRepository.findById(9L)).thenReturn(Optional.of(paciente));
        when(medicoClient.buscarMedico(3L)).thenReturn(Optional.of(MEDICO));
        when(citaRepository.save(any(Cita.class))).thenReturn(guardada);

        CitaResponseDTO resultado = citaService.crear(dto);

        assertEquals(7L, resultado.id());
        assertEquals("PROGRAMADA", resultado.estado());
        // La creacion de la cita debe disparar el evento hacia la cola SQS.
        verify(notificadorCitas).publicarCitaCreada(guardada);
    }

    @Test
    void crear_conMedicoInexistente_lanzaBusinessExceptionYNoNotifica() {
        when(pacienteRepository.findById(9L)).thenReturn(Optional.of(pacienteDePrueba()));
        when(medicoClient.buscarMedico(3L)).thenReturn(Optional.empty());

        assertThrows(BusinessException.class, () -> citaService.crear(requestDePrueba()));

        verify(citaRepository, never()).save(any());
        verify(notificadorCitas, never()).publicarCitaCreada(any());
    }
}
