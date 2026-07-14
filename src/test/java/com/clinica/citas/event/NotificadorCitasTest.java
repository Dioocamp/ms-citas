package com.clinica.citas.event;

import com.clinica.citas.model.Cita;
import com.clinica.citas.model.EstadoCita;
import com.clinica.citas.model.Paciente;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.ObjectProvider;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/** Pruebas unitarias del publicador de eventos hacia SQS. */
@ExtendWith(MockitoExtension.class)
class NotificadorCitasTest {

    private static final String QUEUE_URL =
            "https://sqs.us-east-1.amazonaws.com/123456789012/clinica-citas-queue";

    @Mock
    private ObjectProvider<SqsClient> sqsClientProvider;

    @Mock
    private SqsClient sqsClient;

    private static Cita citaDePrueba() {
        Paciente paciente = new Paciente("12345678-5", "Ana", "Rojas",
                "ana@mail.cl", "+56933333333", LocalDate.of(1992, 8, 15));
        paciente.setId(4L);
        Cita cita = new Cita(LocalDate.of(2026, 8, 1), LocalTime.of(9, 0),
                "Control cardiologico", EstadoCita.PROGRAMADA, 3L, paciente);
        cita.setId(7L);
        return cita;
    }

    @Test
    void publicarCitaCreada_conColaConfigurada_enviaMensajeConDatosDeLaCita() {
        when(sqsClientProvider.getIfAvailable()).thenReturn(sqsClient);
        NotificadorCitas notificador =
                new NotificadorCitas(sqsClientProvider, new ObjectMapper(), QUEUE_URL);

        notificador.publicarCitaCreada(citaDePrueba());

        ArgumentCaptor<SendMessageRequest> captor =
                ArgumentCaptor.forClass(SendMessageRequest.class);
        verify(sqsClient).sendMessage(captor.capture());

        SendMessageRequest enviado = captor.getValue();
        assertEquals(QUEUE_URL, enviado.queueUrl());
        assertTrue(enviado.messageBody().contains("\"idCita\":7"));
        assertTrue(enviado.messageBody().contains("12345678-5"));
        assertTrue(enviado.messageBody().contains("2026-08-01T09:00"));
        assertEquals("CITA_CREADA",
                enviado.messageAttributes().get("eventType").stringValue());
    }

    @Test
    void publicarCitaCreada_sinClienteSqs_noEnviaNadaNiFalla() {
        when(sqsClientProvider.getIfAvailable()).thenReturn(null);
        NotificadorCitas notificador =
                new NotificadorCitas(sqsClientProvider, new ObjectMapper(), QUEUE_URL);

        assertDoesNotThrow(() -> notificador.publicarCitaCreada(citaDePrueba()));

        verify(sqsClient, never()).sendMessage(any(SendMessageRequest.class));
    }

    @Test
    void publicarCitaCreada_siSqsFalla_noPropagaLaExcepcion() {
        when(sqsClientProvider.getIfAvailable()).thenReturn(sqsClient);
        when(sqsClient.sendMessage(any(SendMessageRequest.class)))
                .thenThrow(new RuntimeException("AWS no disponible"));
        NotificadorCitas notificador =
                new NotificadorCitas(sqsClientProvider, new ObjectMapper(), QUEUE_URL);

        // El agendamiento de la cita nunca debe romperse por la notificacion.
        assertDoesNotThrow(() -> notificador.publicarCitaCreada(citaDePrueba()));
    }
}
