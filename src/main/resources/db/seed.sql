-- Programas oficiales
INSERT OR IGNORE INTO programa(nombre) VALUES
 ('Ingeniería de Sistemas'),
 ('Ingeniería Electrónica y Telecomunicaciones'),
 ('Automática industrial'),
 ('Tecnología en Telemática');

-- Usuarios base (3 estudiantes, 2 docentes, 1 coordinación)
INSERT OR IGNORE INTO usuario(id,nombres,apellidos,email,password_hash,rol,celular) VALUES
 (1,'Ana','Pérez','ana@unicauca.edu.co','pbkdf2$210000$JNS5b1jabUqFEjE7vQKijg==$IBBemk8WfgsK7S6CbNvsp9OOEdLE8MNqpdbBwclt6ik=','estudiante',''),
 (2,'Luis','Gómez','luis@unicauca.edu.co','pbkdf2$210000$xf8XeoboJEH5Pjdy2nANXw==$BVvFiw32Mv6cASYfrlPLxzi9vvGTnNo+wrr8P07lb6k=','estudiante',''),
 (3,'Juan','Torres','juan.torres@unicauca.edu.co','pbkdf2$210000$H5ohMQLOsoUHNwCbIaGbjA==$UVFwpWgwgjqtBjL0KlySFn4JJ6CkOoX313+JKkEw5Lg=','docente',''),
 (4,'Sofía','Rojas','sofia@unicauca.edu.co','pbkdf2$210000$hoRuhPm/sgU6SCMZOEPSKg==$U+w1Gg7QL1Ohrxv0knCHK7koo7gOlUfbtNvqOn6EuoQ=','estudiante',''),
 (5,'Carlos','Méndez','carlos.mendez@unicauca.edu.co','pbkdf2$210000$vOD57zSjgdWl4mRUpqncRQ==$vdUW7iqFU+SzyxDJxpeGFrlWzfQNkeEmtCeDDNb2698=','docente',''),
 (9,'Coordinación','FIET','coord.fiet@unicauca.edu.co','pbkdf2$210000$B7JCJyD5zx6xUztrewrnWA==$c/z2miMrJhOjm9ZMyzRcdpCuIHzr/2+uvrJquX1VOpw=','coordinacion','');

-- Relaciones de rol
INSERT OR IGNORE INTO estudiante(id,programa_id) VALUES
 (1,1),(2,1),(4,2);
INSERT OR IGNORE INTO docente(id,programa_id) VALUES
 (3,1),(5,2);

-- Trabajos de grado base (dos proyectos)
INSERT OR IGNORE INTO trabajo_grado(id,titulo,fecha_formato_a,estudiante1_id,estudiante2_id,director_id,codirector_id,modalidad,programa_id) VALUES
 (101,'Sistema de Detección de Plagas con IA','2025-05-15',1,2,3,NULL,'Investigación',1),
 (205,'Automatización de Riego por Sensores','2025-06-20',4,NULL,5,NULL,'Práctica Profesional',2);

-- Una versión inicial de Formato A para cada TG (pendiente de evaluación)
INSERT OR IGNORE INTO formato_a_version(trabajo_grado_id,intento,objetivo_general,objetivos_especificos,pdf_path,carta_aceptacion_path) VALUES
 (101,1,'Detectar plagas en cultivos','1) Dataset\n2) Modelo CNN\n3) Validación de campo','./data/formatoA/101/v1.pdf',NULL),
 (205,1,'Automatizar riego en invernaderos','1) Sensado\n2) Control PID\n3) Ahorro hídrico','./data/formatoA/205/v1.pdf','./data/formatoA/205/carta.pdf');


INSERT OR IGNORE INTO usuario(id,nombres,apellidos,email,password_hash,rol,celular) VALUES
  -- …
  (9,'Coordinación','FIET','coord.fiet@unicauca.edu.co','pbkdf2$210000$...$...','coordinador','');
