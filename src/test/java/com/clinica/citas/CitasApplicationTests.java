package com.clinica.citas;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Prueba de humo: el contexto de Spring arranca correctamente con el perfil 'h2'.
 */
@SpringBootTest
@ActiveProfiles("h2")
class CitasApplicationTests {

    @Test
    void contextLoads() {
    }
}
