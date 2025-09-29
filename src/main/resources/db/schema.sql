PRAGMA foreign_keys = ON;

-- Catálogo de programas
CREATE TABLE IF NOT EXISTS programa (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  nombre TEXT NOT NULL UNIQUE
);

-- Usuarios del sistema
CREATE TABLE IF NOT EXISTS usuario (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  nombres TEXT NOT NULL,
  apellidos TEXT NOT NULL,
  email TEXT NOT NULL UNIQUE,
  password_hash TEXT NOT NULL,
  rol TEXT NOT NULL CHECK (rol IN ('estudiante','docente','coordinador')),
  celular TEXT NULL,
  creado_en TEXT NOT NULL DEFAULT (datetime('now','localtime'))
);

-- Compatibilidad para el repositorio actual
CREATE TABLE IF NOT EXISTS usuarios (
  email TEXT PRIMARY KEY,
  nombres TEXT NOT NULL,
  apellidos TEXT NOT NULL,
  celular TEXT,
  programa TEXT NOT NULL,
  rol TEXT NOT NULL CHECK (rol IN ('docente','estudiante','coordinador')),
  password_hash TEXT NOT NULL,
  creado_en TEXT NOT NULL DEFAULT (datetime('now','localtime'))
);
CREATE UNIQUE INDEX IF NOT EXISTS idx_usuarios_email ON usuarios(email);


-- Estudiantes (1:1 con usuario)
CREATE TABLE IF NOT EXISTS estudiante (
  id INTEGER PRIMARY KEY,
  programa_id INTEGER NOT NULL,
  FOREIGN KEY (id) REFERENCES usuario(id) ON DELETE CASCADE,
  FOREIGN KEY (programa_id) REFERENCES programa(id) ON DELETE RESTRICT
);

-- Docentes (1:1 con usuario)
CREATE TABLE IF NOT EXISTS docente (
  id INTEGER PRIMARY KEY,
  programa_id INTEGER NOT NULL,
  FOREIGN KEY (id) REFERENCES usuario(id) ON DELETE CASCADE,
  FOREIGN KEY (programa_id) REFERENCES programa(id) ON DELETE RESTRICT
);

-- Trabajo de grado
CREATE TABLE IF NOT EXISTS trabajo_grado (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  titulo TEXT NOT NULL,
  fecha_formato_a TEXT,
  estudiante1_id INTEGER NOT NULL,
  estudiante2_id INTEGER,
  director_id INTEGER NOT NULL,
  codirector_id INTEGER,
  modalidad TEXT NOT NULL CHECK (modalidad IN ('Investigación','Práctica Profesional')),
  programa_id INTEGER NOT NULL,
  FOREIGN KEY (estudiante1_id) REFERENCES estudiante(id),
  FOREIGN KEY (estudiante2_id) REFERENCES estudiante(id),
  FOREIGN KEY (director_id) REFERENCES docente(id),
  FOREIGN KEY (codirector_id) REFERENCES docente(id),
  FOREIGN KEY (programa_id) REFERENCES programa(id)
);

-- Versiones de Formato A (histórico por intentos)
CREATE TABLE IF NOT EXISTS formato_a_version (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  trabajo_grado_id INTEGER NOT NULL,
  intento INTEGER NOT NULL CHECK (intento BETWEEN 1 AND 3),
  fecha TEXT NOT NULL DEFAULT (datetime('now','localtime')),
  objetivo_general TEXT NOT NULL,
  objetivos_especificos TEXT NOT NULL,
  pdf_path TEXT NOT NULL,
  carta_aceptacion_path TEXT,
  codirector_id INTEGER,
  UNIQUE(trabajo_grado_id, intento),
  FOREIGN KEY (trabajo_grado_id) REFERENCES trabajo_grado(id),
  FOREIGN KEY (codirector_id) REFERENCES docente(id)
);

-- Evaluación por coordinador para cada versión
CREATE TABLE IF NOT EXISTS evaluacion_formato_a (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  formato_a_version_id INTEGER NOT NULL UNIQUE,
  estado TEXT NOT NULL CHECK (estado IN ('aprobado','rechazado')),
  observaciones TEXT,
  evaluador_id INTEGER NOT NULL,
  fecha TEXT NOT NULL DEFAULT (datetime('now','localtime')),
  FOREIGN KEY (formato_a_version_id) REFERENCES formato_a_version(id),
  FOREIGN KEY (evaluador_id) REFERENCES usuario(id)
);

UPDATE usuario  SET rol='coordinador' WHERE rol IN ('coordinacion','COORDINACION');
UPDATE usuarios SET rol='coordinador' WHERE rol IN ('coordinacion','COORDINACION');

-- Único intento por TG
CREATE UNIQUE INDEX IF NOT EXISTS uq_formatoa_tg_intento
ON formato_a_version(trabajo_grado_id, intento);

-- Una evaluación por versión
CREATE UNIQUE INDEX IF NOT EXISTS uq_eval_por_version
ON evaluacion_formato_a(formato_a_version_id);


CREATE UNIQUE INDEX IF NOT EXISTS ux_eval_unica
ON evaluacion_formato_a(formato_a_version_id);
