-- Datos de ejemplo. Solo se ejecutan en el perfil 'h2'.
-- 'estado' se guarda como texto (EnumType.STRING). 'medico_id' referencia un medico de ms-personal-medico.

INSERT INTO pacientes (rut, nombre, apellido, email, telefono, fecha_nacimiento) VALUES ('99999999-9', 'Maria', 'Gonzalez', 'maria.gonzalez@mail.cl', '+56911111111', '1990-05-12');
INSERT INTO pacientes (rut, nombre, apellido, email, telefono, fecha_nacimiento) VALUES ('88888888-8', 'Pedro', 'Rojas', 'pedro.rojas@mail.cl', '+56922222222', '1985-03-08');

INSERT INTO citas (fecha, hora, motivo, estado, medico_id, paciente_id) VALUES ('2026-06-10', '09:30:00', 'Control general', 'PROGRAMADA', 1, 1);
INSERT INTO citas (fecha, hora, motivo, estado, medico_id, paciente_id) VALUES ('2026-06-11', '11:00:00', 'Dolor toracico', 'CONFIRMADA', 1, 2);
