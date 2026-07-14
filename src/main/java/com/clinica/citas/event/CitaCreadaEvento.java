package com.clinica.citas.event;

/**
 * Mensaje JSON que se publica en la cola SQS 'clinica-citas-queue' cada vez
 * que se agenda una nueva cita. Lo consume la funcion serverless
 * 'clinica-notificador' (AWS Lambda) para generar la notificacion al paciente.
 */
public record CitaCreadaEvento(
        Long idCita,
        String rutPaciente,
        String nombrePaciente,
        String fechaHora,
        Long medicoId,
        String motivo
) {
}
