package com.clinica.citas.event;

import com.clinica.citas.model.Cita;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.MessageAttributeValue;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

import java.util.Map;

/**
 * Publica eventos de citas en la cola SQS de la clinica.
 *
 * El envio es "best effort": si la cola no esta configurada o AWS no
 * responde, el problema se registra en el log y la creacion de la cita
 * continua sin errores. La notificacion es asincrona por diseno: la cola
 * desacopla el agendamiento (sincrono, critico) del envio de la
 * notificacion (asincrono, tolerante a fallos).
 */
@Component
public class NotificadorCitas {

    private static final Logger log = LoggerFactory.getLogger(NotificadorCitas.class);

    private final ObjectProvider<SqsClient> sqsClientProvider;
    private final ObjectMapper objectMapper;
    private final String queueUrl;

    public NotificadorCitas(ObjectProvider<SqsClient> sqsClientProvider,
                            ObjectMapper objectMapper,
                            @Value("${clinica.notificaciones.queue-url:}") String queueUrl) {
        this.sqsClientProvider = sqsClientProvider;
        this.objectMapper = objectMapper;
        this.queueUrl = queueUrl;
    }

    public void publicarCitaCreada(Cita cita) {
        SqsClient sqs = sqsClientProvider.getIfAvailable();
        if (sqs == null || queueUrl.isBlank()) {
            log.debug("Notificaciones SQS deshabilitadas: no se publica el evento de la cita {}",
                    cita.getId());
            return;
        }
        try {
            CitaCreadaEvento evento = new CitaCreadaEvento(
                    cita.getId(),
                    cita.getPaciente().getRut(),
                    cita.getPaciente().getNombre() + " " + cita.getPaciente().getApellido(),
                    cita.getFecha() + "T" + cita.getHora(),
                    cita.getMedicoId(),
                    cita.getMotivo());

            sqs.sendMessage(SendMessageRequest.builder()
                    .queueUrl(queueUrl)
                    .messageBody(objectMapper.writeValueAsString(evento))
                    .messageAttributes(Map.of("eventType", MessageAttributeValue.builder()
                            .dataType("String")
                            .stringValue("CITA_CREADA")
                            .build()))
                    .build());

            log.info("Evento CITA_CREADA publicado en SQS para la cita {}", cita.getId());
        } catch (Exception ex) {
            log.warn("No se pudo publicar la notificacion de la cita {} en SQS: {}",
                    cita.getId(), ex.getMessage());
        }
    }
}
