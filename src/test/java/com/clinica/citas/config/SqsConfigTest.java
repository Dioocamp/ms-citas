package com.clinica.citas.config;

import org.junit.jupiter.api.Test;
import software.amazon.awssdk.services.sqs.SqsClient;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Verifica que el cliente de SQS se pueda construir de verdad con las
 * dependencias presentes en el classpath.
 *
 * Las demas pruebas simulan (mock) el SqsClient, por lo que no detectan
 * conflictos de version en el cliente HTTP del SDK. Ese tipo de conflicto
 * no falla al compilar: falla al arrancar la aplicacion, ya desplegada.
 * Esta prueba construye el cliente real para que el pipeline lo detecte.
 */
class SqsConfigTest {

    @Test
    void sqsClient_seConstruyeConLasDependenciasDelClasspath() {
        // Credenciales ficticias: no se hace ninguna llamada a AWS, solo se
        // construye el cliente (que es donde ocurria el fallo de classpath).
        System.setProperty("aws.accessKeyId", "test");
        System.setProperty("aws.secretAccessKey", "test");
        try {
            SqsClient cliente = new SqsConfig().sqsClient("us-east-1");
            assertNotNull(cliente);
            cliente.close();
        } finally {
            System.clearProperty("aws.accessKeyId");
            System.clearProperty("aws.secretAccessKey");
        }
    }
}
