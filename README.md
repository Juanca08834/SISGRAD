# SISGRAD – Formato A (Prototipo)

## 1) ¿Qué es?
Aplicación de escritorio (**Java Swing + SQLite**) para gestionar el **Formato A (propuesta de Trabajo de Grado)** con tres roles:

- **Estudiante**: crea versiones del Formato A, consulta estado e historial.  
- **Docente (director/codirector)**: puede subir Formato A y consultar historial del TG asociado.  
- **Coordinador**: revisa la última versión pendiente por TG, aprueba/rechaza y registra observaciones.  

---

## 2) Reglas de negocio (clave)
- Máximo **3 intentos** por Trabajo de Grado.  
- No se puede subir nueva versión si existe una pendiente de evaluación.  
- Una versión **Aprobada** bloquea nuevos envíos (cierre definitivo).  
- Tras **3 rechazos**, el TG queda **Rechazado definitivo**.  
- Si la modalidad es **Práctica Profesional**, además del Formato A (PDF) se exige **Carta de aceptación (PDF)**.  

---

## 3) Requisitos
- **JDK 17+**  
- **SQLite** (embebido vía JDBC; no requiere servidor)  
- Entorno de escritorio: **Windows / Linux / macOS**  

---

## 4) Cómo ejecutar
1. Abre el proyecto en tu IDE (**NetBeans / IntelliJ / Eclipse**) y ejecuta la clase:
  - com.unicauca.proyectosofv1.Principal
2. Al iniciar:
- Se aplica **Look & Feel Nimbus**.  
- Se abre una **conexión única a SQLite**.  
- Se corre `DBInit.init(conn)` y la migración desde la tabla *legacy usuarios* (si existe).  
- Se muestra la **ventana de Login**.  

---

## 5) Base de datos y migración
- La BD se crea/abre automáticamente (ver `FabricaConexionSQLite`).  
- `DBInit.init(conn)` crea el esquema normalizado:  
`usuario`, `programa`, `estudiante`, `docente`, `trabajo_grado`, `formato_a_version`, `evaluacion_formato_a`, etc.  
- Si existe la tabla *legacy usuarios*:  
- Se normalizan y copian datos a `usuario`.  
- Se crean/vinculan entradas en `programa`, `estudiante/docente`.  
- Se expone la vista `usuarios_view` para compatibilidad.  
- La conexión se cierra con un **shutdown hook** al terminar la app.  

---

## 6) Política de contraseñas
(Validación en tiempo real)  
- Mínimo **6 caracteres**.  
- Al menos **1 mayúscula, 1 minúscula, 1 dígito, 1 símbolo**.  
- El formulario de registro muestra **barra de fuerza** y tips.  
- El backend aplica la misma política (regex).  

---

## 7) Flujos por rol
### Estudiante
1. Iniciar sesión.  
2. Iniciar nuevo TG → completar datos, adjuntar PDF (y Carta si es Práctica).  
3. Ver Estado e Intentos; abrir Historial.  
4. Si fue rechazado y quedan intentos, subir reintento (algunos campos precargados y bloqueados).  

### Docente
1. Iniciar sesión.  
2. Ver Estado/Intentos del TG asociado (si existe).  
3. Subir Formato A (prefill en reintentos) y ver Historial.  
4. Abrir último PDF con un clic (atajo).  

### Coordinador
1. Iniciar sesión.  
2. Ver pendientes (solo última versión sin evaluación por TG).  
3. Abrir PDF/Carta, escribir observaciones y Aprobar/Rechazar.  
4. Se envían notificaciones (logger simulado) a los involucrados.  

---

## 8) Almacenamiento de archivos
Módulo **ArchivoStorage** (carpeta local).  
Nombres determinísticos:  
- `storage/TG_<id>/tg<id>_i<intento>_formatoa.pdf`  
- `storage/TG_<id>/tg<id>_i<intento>_carta.pdf` (si aplica)  

El Coordinador puede abrir archivos con el visor por defecto del sistema.  

---

## 9) Pantallas
- **VentanaLogin**: login, registro, recuperación simulada.  
- **TableroEstudiante**: estado, historial, iniciar/reintentar.  
- **TableroDocente**: estado, historial, subir, abrir último PDF.  
- **TableroCoordinador**: pendientes, abrir PDF/Carta, aprobar/rechazar, filtro, observaciones.  
- Todos los tableros tienen **Cerrar sesión** (retorna al login sin cerrar la app).  

---

## 10) Validaciones clave (backend)
- **ServicioFormatoAImpl.subirFormatoA(...)**
- Normaliza modalidad.  
- Bloquea si hay versión pendiente o última aprobada.  
- Limita a 3 intentos (rechazo definitivo).  
- Guarda PDF/Carta con nombres determinísticos.  
- **ServicioFormatoAImpl.evaluarFormatoA(...)**
- Evalúa una única vez cada versión.  
- Notifica a director, codirector y estudiantes (logger).  
- **ServicioRegistroImpl**
- Normaliza programa.  
- Valida correo `@unicauca.edu.co`, celular, rol y contraseña.  
- Hash de contraseña con `EncriptadorContrasenia`.  

---

## 11) Pruebas rápidas (aceptación)
### Escenario básico
1. Registrar Estudiante y Docente (programas válidos).  
2. Iniciar sesión como Estudiante → subir Formato A (PDF).  
- Esperado: **“En primera evaluación” e Intentos 1/3**.  
3. Iniciar sesión como Coordinador → rechazar con observación.  
- Esperado: Estudiante/Docente ven **Intentos 2/3** y estado avanza.  
4. Repetir hasta aprobar o llegar a rechazo definitivo (3/3).  
5. Ver Historial y abrir PDF (Docente/Coordinador).  
6. Probar Cerrar sesión (debe volver al Login).  

### Escenario práctica profesional
1. Subir Formato A seleccionando **Práctica Profesional**.  
2. Adjuntar **Carta de aceptación (PDF)** además del Formato A.  
- Esperado: si no hay carta, el sistema **bloquea el envío**.  

---

## 12) Estructura de paquetes (resumen)
- `modelo/` – Entidades y enums.  
- `repositorio/` y `repositorio/sqlite/` – Acceso a datos.  
- `servicios/` y `servicios/impl/` – Reglas de negocio.  
- `infraestructura/sqlite/` – Conexión y DBInit.  
- `infraestructura/archivos/` – ArchivoStorage (PDFs).  
- `ui/` – Ventanas Swing.  
- `seguridad/` – Hashing de contraseñas.  
- `notificacion/` – Notificador (logger).  

---

## 13) Problemas comunes (y solución)
- **“No fue posible abrir la base de datos”**  
→ Revisa permisos y ruta en `FabricaConexionSQLite`.  
- **“Ya existe una versión pendiente de evaluación”**  
→ El Coordinador debe evaluar antes de subir otra versión.  
- **No abre PDF/Carta**  
→ Asegura que el SO tiene un visor por defecto para PDFs.  
- **Contraseña rechazada**  
→ Usa la barra de fuerza y cumple los 4 criterios.  

---

## 14) Licencia
**Uso académico – prototipo para primer corte.**
