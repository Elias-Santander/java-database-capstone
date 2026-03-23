# Historias de Usuario => Administrador

**Título:**
 Como **Administrador**, quiero **iniciar sesión** con mi usuario y contraseña, para **gestionar la plataforma de manera segura**.

**Criterios de Aceptación:**
1. El sistema debe validar las credenciales contra la base de datos MySQL.
2. Tras una autenticación exitosa, el usuario debe ser redirigido al `AdminDashboard`.
3. Se debe mostrar un mensaje de error si las credenciales son incorrectas.

**Prioridad:** Alta
**Story Points:** 3
**Notas:**
- La autenticación se gestiona mediante Spring Security.

---

**Título:**
 Como **Administrador**, quiero **cerrar sesión** en el portal, para **proteger el acceso al sistema**.

**Criterios de Aceptación:**
1. La sesión activa debe invalidarse inmediatamente al hacer clic en "Cerrar sesión".
2. El usuario debe ser redirigido automáticamente a la página de inicio de sesión.
3. El acceso a rutas protegidas (dashboards) debe quedar restringido tras el cierre de sesión.

**Prioridad:** Alta
**Story Points:** 1
**Notas:**
- Asegurar la limpieza de cookies de sesión en el navegador.

---

**Título:**
 Como **Administrador**, quiero **agregar doctores** al portal, para que puedan **comenzar a gestionar sus citas médicas**.

**Criterios de Aceptación:**
1. El administrador debe completar los campos obligatorios: nombre, especialidad y credenciales.
2. El nuevo registro de doctor debe persistirse correctamente en la base de datos MySQL.
3. El sistema debe confirmar la creación exitosa mediante una notificación en pantalla.

**Prioridad:** Alta
**Story Points:** 5
**Notas:**
- El sistema debería generar una contraseña temporal por defecto para el nuevo perfil.

---

**Título:**
 Como **Administrador**, quiero **eliminar el perfil de un doctor**, para **mantener actualizado el directorio del personal médico**.

**Criterios de Aceptación:**
1. El sistema debe solicitar una confirmación antes de proceder con la eliminación permanente.
2. Se debe manejar la integridad referencial en MySQL (por ejemplo, qué sucede con sus citas pasadas).
3. El doctor eliminado no debe aparecer en los listados del `DoctorDashboard`.

**Prioridad:** Media
**Story Points:** 3
**Notas:**
- Evaluar si se requiere un "borrado lógico" (soft delete) para preservar el historial clínico.

---

**Título:**
 Como **Administrador**, quiero **ejecutar un procedimiento almacenado en la CLI de MySQL**, para **rastrear las estadísticas de uso mediante el conteo de citas por mes**.

**Criterios de Aceptación:**
1. El procedimiento almacenado debe calcular el total de citas agrupadas por mes.
2. La salida debe ser legible y accesible a través de la interfaz de línea de comandos.
3. Los resultados deben reflejar con precisión los datos almacenados en la base de datos relacional.

**Prioridad:** Media
**Story Points:** 2
**Notas:**
- Esta es una tarea técnica de backend; el script SQL debe estar documentado en el repositorio.

---

# Historias de Usuario => Pacientes

**Título:**
 Como **Paciente**, quiero **ver una lista de doctores sin iniciar sesión**, para **explorar las opciones disponibles antes de registrarme**.

**Criterios de Aceptación:**
1. El sistema debe permitir el acceso público a la lista de doctores.
2. La lista debe mostrar información básica: nombre, especialidad y disponibilidad general.
3. No se deben mostrar datos sensibles o privados de los doctores en esta vista.

**Prioridad:** Media
**Story Points:** 3
**Notas:**
- Esta vista es gestionada por un controlador de Thymeleaf con acceso anónimo en Spring Security.

---

**Título:**
 Como **Paciente**, quiero **registrarme usando mi correo y contraseña**, para **poder reservar mis citas médicas**.

**Criterios de Aceptación:**
1. El sistema debe validar que el correo electrónico no esté registrado previamente.
2. La contraseña debe cumplir con los requisitos mínimos de seguridad establecidos.
3. El nuevo perfil de paciente debe persistirse correctamente en la base de datos MySQL.

**Prioridad:** Alta
**Story Points:** 5
**Notas:**
- Se debe asegurar el cifrado de la contraseña antes de guardarla en la base de datos.

---

**Título:**
 Como **Paciente**, quiero **iniciar sesión en el portal**, para **gestionar mis reservas de manera personalizada**.

**Criterios de Aceptación:**
1. El sistema debe autenticar al paciente mediante su correo y contraseña en MySQL.
2. Tras el éxito, el usuario debe ser redirigido a su `PatientDashboard`.
3. Se debe mantener una sesión activa para navegar por las funciones de reserva.

**Prioridad:** Alta
**Story Points:** 3
**Notas:**
- Utilizar la configuración de inicio de sesión estándar de la aplicación.

---

**Título:**
 Como **Paciente**, quiero **cerrar sesión en el portal**, para **asegurar la privacidad de mi cuenta**.

**Criterios de Aceptación:**
1. El sistema debe invalidar el token o sesión activa al seleccionar "Cerrar sesión".
2. El usuario debe ser redirigido a la página de inicio pública.
3. El acceso al historial de citas debe quedar bloqueado tras el cierre de sesión.

**Prioridad:** Alta
**Story Points:** 1
**Notas:**
- Acción crítica para cumplir con la protección de datos de salud.

---

**Título:**
 Como **Paciente**, quiero **reservar una cita de una hora con un doctor**, para **recibir atención médica profesional**.

**Criterios de Aceptación:**
1. El sistema debe permitir seleccionar un doctor y un bloque de tiempo de 60 minutos.
2. La cita no puede solaparse con otra ya existente en la base de datos MySQL.
3. Se debe generar una confirmación de la reserva vinculada al perfil del paciente.

**Prioridad:** Alta
**Story Points:** 8
**Notas:**
- La capa de servicio debe verificar la disponibilidad del doctor antes de confirmar la transacción.

---

**Título:**
 Como **Paciente**, quiero **ver mis próximas citas**, para **poder prepararme adecuadamente para mis consultas**.

**Criterios de Aceptación:**
1. El `PatientDashboard` debe listar cronológicamente las citas pendientes.
2. Cada cita debe mostrar la fecha, hora y el nombre del doctor asignado.
3. Los datos deben recuperarse en tiempo real desde la base de datos MySQL.

**Prioridad:** Media
**Story Points:** 3
**Notas:**
- Considerar la implementación de un filtro para separar citas pasadas de las futuras.

---

# Historias de Usuario => Doctores

**Título:**
 Como **Doctor**, quiero **iniciar sesión en el portal**, para **gestionar mis citas médicas y disponibilidad de manera personalizada**.

**Criterios de Aceptación:**
1. El sistema debe autenticar al doctor mediante sus credenciales almacenadas en MySQL.
2. Tras el inicio de sesión exitoso, el usuario debe ser redirigido al `DoctorDashboard`.
3. El acceso debe estar restringido únicamente a las funciones de gestión médica.

**Prioridad:** Alta
**Story Points:** 3
**Notas:**
- Se utiliza el rol `ROLE_DOCTOR` en Spring Security para filtrar el acceso.

---

**Título:**
 Como **Doctor**, quiero **cerrar sesión en el portal**, para **proteger mis datos y la información confidencial de mis pacientes**.

**Criterios de Aceptación:**
1. La sesión debe invalidarse inmediatamente al seleccionar la opción de salida.
2. El sistema debe redirigir al usuario a la página de inicio de sesión pública.
3. Se debe garantizar que no queden datos sensibles en la memoria del navegador tras el cierre.

**Prioridad:** Alta
**Story Points:** 1
**Notas:**
- Crucial para el cumplimiento de normativas de privacidad médica.

---

**Título:**
 Como **Doctor**, quiero **ver mi calendario de citas**, para **mantenerme organizado con mi agenda diaria**.

**Criterios de Aceptación:**
1. El sistema debe mostrar una vista cronológica de las citas confirmadas.
2. Cada entrada debe incluir el nombre del paciente y el horario de la consulta.
3. Los datos deben ser recuperados eficientemente desde la base de datos MySQL.

**Prioridad:** Alta
**Story Points:** 5
**Notas:**
- Se recomienda una integración con una interfaz de calendario dinámica en el `DoctorDashboard`.

---

**Título:**
 Como **Doctor**, quiero **marcar mi indisponibilidad**, para **informar a los pacientes únicamente sobre los horarios en los que puedo atender**.

**Criterios de Aceptación:**
1. El doctor debe poder seleccionar bloques de tiempo o días específicos como "No disponibles".
2. Estos bloques deben bloquear la creación de citas en la base de datos para esos periodos.
3. El sistema debe reflejar esta actualización en tiempo real para los pacientes que consulten la lista.

**Prioridad:** Alta
**Story Points:** 5
**Notas:**
- Requiere validación en la Capa de Servicio para evitar conflictos con citas ya existentes.

---

**Título:**
 Como **Doctor**, quiero **actualizar mi perfil con mi especialización e información de contacto**, para **que los pacientes tengan información veraz y actualizada**.

**Criterios de Aceptación:**
1. El sistema debe permitir la edición de los campos de especialidad, teléfono y correo.
2. Los cambios deben persistirse correctamente en la tabla de doctores en MySQL.
3. Se debe mostrar un mensaje de confirmación tras guardar los cambios exitosamente.

**Prioridad:** Media
**Story Points:** 3
**Notas:**
- La validación de formato de contacto (teléfono/email) debe realizarse en el frontend y backend.

---

**Título:**
 Como **Doctor**, quiero **ver los detalles del paciente para las citas próximas**, para **poder estar preparado antes de iniciar la consulta**.

**Criterios de Aceptación:**
1. Al seleccionar una cita, el sistema debe mostrar el perfil básico y el motivo de consulta del paciente.
2. El acceso a esta información debe estar limitado estrictamente al doctor asignado a dicha cita.
3. Los detalles deben presentarse de forma clara y estructurada en el panel del doctor.

**Prioridad:** Alta
**Story Points:** 3
**Notas:**
- Considerar la futura integración con la visualización de recetas históricas almacenadas en MongoDB.

---



