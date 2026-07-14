package com.clinica.citas.service.impl;

import com.clinica.citas.client.MedicoClient;
import com.clinica.citas.client.MedicoDTO;
import com.clinica.citas.dto.CitaDetalleDTO;
import com.clinica.citas.dto.CitaRequestDTO;
import com.clinica.citas.dto.CitaResponseDTO;
import com.clinica.citas.event.NotificadorCitas;
import com.clinica.citas.exception.BusinessException;
import com.clinica.citas.exception.ResourceNotFoundException;
import com.clinica.citas.model.Cita;
import com.clinica.citas.model.EstadoCita;
import com.clinica.citas.model.Paciente;
import com.clinica.citas.repository.CitaRepository;
import com.clinica.citas.repository.PacienteRepository;
import com.clinica.citas.service.CitaService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Implementacion de la logica de negocio de Citas.
 *
 * Al crear una cita valida, contra ms-personal-medico, que el medico exista
 * (comunicacion entre microservicios). Para enriquecer el detalle de la cita
 * vuelve a consultar al microservicio de personal medico.
 */
@Service
public class CitaServiceImpl implements CitaService {

    private final CitaRepository citaRepository;
    private final PacienteRepository pacienteRepository;
    private final MedicoClient medicoClient;
    private final NotificadorCitas notificadorCitas;

    public CitaServiceImpl(CitaRepository citaRepository,
                           PacienteRepository pacienteRepository,
                           MedicoClient medicoClient,
                           NotificadorCitas notificadorCitas) {
        this.citaRepository = citaRepository;
        this.pacienteRepository = pacienteRepository;
        this.medicoClient = medicoClient;
        this.notificadorCitas = notificadorCitas;
    }

    @Override
    @Transactional
    public CitaResponseDTO crear(CitaRequestDTO dto) {
        Paciente paciente = buscarPaciente(dto.pacienteId());

        // Validacion inter-servicio: el medico debe existir en ms-personal-medico.
        Optional<MedicoDTO> medico = medicoClient.buscarMedico(dto.medicoId());
        if (medico.isEmpty()) {
            throw new BusinessException(
                    "No existe un medico con id " + dto.medicoId() + " en ms-personal-medico.");
        }

        Cita cita = new Cita(dto.fecha(), dto.hora(), dto.motivo(),
                EstadoCita.PROGRAMADA, dto.medicoId(), paciente);

        Cita guardada = citaRepository.save(cita);

        // Publica el evento CITA_CREADA en la cola SQS (envio best effort:
        // un fallo en la notificacion no impide agendar la cita).
        notificadorCitas.publicarCitaCreada(guardada);

        return toResponse(guardada);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CitaResponseDTO> listar(Long pacienteId, Long medicoId) {
        List<Cita> citas;
        if (pacienteId != null) {
            citas = citaRepository.findByPacienteId(pacienteId);
        } else if (medicoId != null) {
            citas = citaRepository.findByMedicoId(medicoId);
        } else {
            citas = citaRepository.findAll();
        }
        return citas.stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CitaResponseDTO obtenerPorId(Long id) {
        return toResponse(buscarOExcepcion(id));
    }

    @Override
    @Transactional(readOnly = true)
    public CitaDetalleDTO obtenerDetalle(Long id) {
        Cita cita = buscarOExcepcion(id);
        // Consulta en vivo al microservicio de personal medico.
        MedicoDTO medico = medicoClient.buscarMedico(cita.getMedicoId()).orElse(null);

        String medicoNombre = (medico != null)
                ? medico.nombre() + " " + medico.apellido()
                : "(medico no encontrado)";
        String especialidad = (medico != null) ? medico.especialidadNombre() : null;

        return new CitaDetalleDTO(
                cita.getId(), cita.getFecha(), cita.getHora(), cita.getMotivo(),
                cita.getEstado().name(),
                cita.getPaciente().getId(),
                cita.getPaciente().getNombre() + " " + cita.getPaciente().getApellido(),
                cita.getMedicoId(), medicoNombre, especialidad);
    }

    @Override
    @Transactional
    public CitaResponseDTO actualizar(Long id, CitaRequestDTO dto) {
        Cita cita = buscarOExcepcion(id);
        Paciente paciente = buscarPaciente(dto.pacienteId());

        if (medicoClient.buscarMedico(dto.medicoId()).isEmpty()) {
            throw new BusinessException(
                    "No existe un medico con id " + dto.medicoId() + " en ms-personal-medico.");
        }

        cita.setFecha(dto.fecha());
        cita.setHora(dto.hora());
        cita.setMotivo(dto.motivo());
        cita.setMedicoId(dto.medicoId());
        cita.setPaciente(paciente);

        return toResponse(citaRepository.save(cita));
    }

    @Override
    @Transactional
    public CitaResponseDTO cambiarEstado(Long id, EstadoCita nuevoEstado) {
        Cita cita = buscarOExcepcion(id);
        cita.setEstado(nuevoEstado);
        return toResponse(citaRepository.save(cita));
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        Cita cita = buscarOExcepcion(id);
        citaRepository.delete(cita);
    }

    // --- Auxiliares ------------------------------------------------------

    private Cita buscarOExcepcion(Long id) {
        return citaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cita", id));
    }

    private Paciente buscarPaciente(Long pacienteId) {
        return pacienteRepository.findById(pacienteId)
                .orElseThrow(() -> new ResourceNotFoundException("Paciente", pacienteId));
    }

    private CitaResponseDTO toResponse(Cita c) {
        return new CitaResponseDTO(
                c.getId(), c.getFecha(), c.getHora(), c.getMotivo(),
                c.getEstado().name(), c.getMedicoId(),
                c.getPaciente().getId(),
                c.getPaciente().getNombre() + " " + c.getPaciente().getApellido());
    }
}
