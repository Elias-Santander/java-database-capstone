## Diseño de Base de Datos MySQL

### 1. Tabla: `patient` (Paciente)
Gestiona la información personal de los usuarios que reciben atención.
* **id:** `INT`, Primary Key, **AUTO_INCREMENT**.
* **first_name:** `VARCHAR(50)`, **NOT NULL**.
* **last_name:** `VARCHAR(50)`, **NOT NULL**.
* **email:** `VARCHAR(100)`, **UNIQUE**, **NOT NULL**. (Validación de formato vía Regex en la capa de Aplicación).
* **phone:** `VARCHAR(20)`.
* **password:** `VARCHAR(255)`, **NOT NULL**. (Almacenada con hash de BCrypt).

### 2. Tabla: `doctor` (Doctor)
Almacena el registro del personal médico y su especialidad.
* **id:** `INT`, Primary Key, **AUTO_INCREMENT**.
* **full_name:** `VARCHAR(100)`, **NOT NULL**.
* **specialization:** `VARCHAR(100)`, **NOT NULL**.
* **license_number:** `VARCHAR(50)`, **UNIQUE**, **NOT NULL**.
* **email:** `VARCHAR(100)`, **UNIQUE**, **NOT NULL**.
* **contact_info:** `TEXT`.

### 3. Tabla: `appointment` (Cita)
La entidad central que conecta pacientes y doctores.
* **id:** `INT`, Primary Key, **AUTO_INCREMENT**.
* **doctor_id:** `INT`, **Foreign Key** → `doctors(id)`, **NOT NULL**.
* **patient_id:** `INT`, **Foreign Key** → `patients(id)`, **NOT NULL**.
* **appointment_time:** `DATETIME`, **NOT NULL**.
* **status:** `INT` (0 = Programada, 1 = Completada, 2 = Cancelada).
* **notes:** `TEXT`.

### 4. Tabla: `admin` (Administrador)
Usuarios con privilegios elevados para la gestión del sistema.
* **id:** `INT`, Primary Key, **AUTO_INCREMENT**.
* **username:** `VARCHAR(50)`, **UNIQUE**, **NOT NULL**.
* **password:** `VARCHAR(255)`, **NOT NULL**.
* **role:** `VARCHAR(20)`, Default 'ADMIN'.

### 5. Tabla: `clinic_location` (Sucursal)
Gestiona las sedes físicas donde se prestan los servicios médicos.
* **id:** `INT`, Primary Key, **AUTO_INCREMENT**.
* **name:** `VARCHAR(100)`, **NOT NULL**. (Ej: "Sede Norte", "Clínica Central").
* **address:** `VARCHAR(255)`, **NOT NULL**.
* **phone:** `VARCHAR(20)`, **NOT NULL**.
* **city:** `VARCHAR(50)`, **NOT NULL**.
* **operating_hours:** `VARCHAR(100)`. (Ej: "Mon-Fri 08:00-20:00").

### 6. Tabla: `payment` (Pago)
Registra las transacciones financieras asociadas a las citas médicas.
* **id:** `INT`, Primary Key, **AUTO_INCREMENT**.
* **appointment_id:** `INT`, **Foreign Key** → `appointments(id)`, **UNIQUE**, **NOT NULL**.
* **amount:** `DECIMAL(10, 2)`, **NOT NULL**. (Uso de `DECIMAL` para evitar errores de redondeo en moneda).
* **payment_date:** `DATETIME`, **DEFAULT CURRENT_TIMESTAMP**.
* **payment_method:** `VARCHAR(50)`. (Ej: "Credit Card", "Cash", "Insurance").
* **transaction_status:** `INT` (0 = Pendiente, 1 = Completado, 2 = Reembolsado).

---

## Diseño de Colección MongoDB

### 1. Colección: `prescription` (Prescripción)
Almacena las recetas médicas emitidas. Se utiliza MongoDB para permitir que el detalle de medicamentos sea dinámico (un paciente puede recibir uno o diez fármacos en una sola receta).

```json
{
  "_id": "ObjectId('65f1a2b3c4d5e6f7a8b90123')",
  "appointment_id": 501,
  "patient_id": 1024,
  "issue_date": "2026-03-23T14:30:00Z",
  "medications": [
    {
      "name": "Amoxicilina",
      "dosage": "500mg",
      "frequency": "Cada 8 horas",
      "duration": "7 días"
    },
    {
      "name": "Paracetamol",
      "dosage": "1g",
      "frequency": "Si hay dolor o fiebre",
      "duration": "3 días"
    }
  ],
  "pharmacy_instructions": "No sustituir por genéricos de bioequivalencia dudosa.",
  "digital_signature": "SIG_8822_AFF99"
}
```

---

### 2. Colección: `comment` (Comentario)
Ideal para almacenar el *feedback* o reseñas que los pacientes dejan sobre los doctores o las clínicas. El uso de arreglos permite manejar etiquetas de satisfacción de forma sencilla.

```json
{
  "_id": "ObjectId('65f1a2b3c4d5e6f7a8b90124')",
  "author_id": 1024,
  "target_doctor_id": 45,
  "rating": 5,
  "content": "Excelente atención, el doctor fue muy paciente y explicó todo con claridad.",
  "tags": ["Puntualidad", "Claridad", "Amabilidad"],
  "is_public": true,
  "created_at": "2026-03-23T16:00:00Z"
}
```

---

### 3. Colección: `record` (Registro)
Esta colección funciona como un log de eventos o historial clínico de cambios. Permite guardar "metadatos" de lo que sucede en el sistema (quién cambió qué y cuándo).

```json
{
  "_id": "ObjectId('65f1a2b3c4d5e6f7a8b90125')",
  "entity_type": "APPOINTMENT",
  "entity_id": 501,
  "action": "STATUS_CHANGE",
  "details": {
    "old_status": "Scheduled",
    "new_status": "Completed",
    "changed_by": "admin_user_01"
  },
  "system_info": {
    "ip_address": "192.168.1.45",
    "user_agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64)"
  },
  "timestamp": "2026-03-23T15:00:00Z"
}
```

---

### 4. Colección: `message` (Mensaje)
Gestiona la comunicación interna entre pacientes y doctores. Al ser un documento, permite guardar hilos de conversación anidados o archivos adjuntos sin complicaciones de esquema.

```json
{
  "_id": "ObjectId('65f1a2b3c4d5e6f7a8b90126')",
  "sender_id": 45,
  "receiver_id": 1024,
  "subject": "Resultados de laboratorio",
  "body": "Hola, ya están listos tus resultados. Por favor, revisa el archivo adjunto.",
  "attachments": [
    {
      "file_name": "analisis_sangre.pdf",
      "url": "https://storage.clinica.com/msg/f_992.pdf"
    }
  ],
  "is_read": false,
  "thread_id": "TH_001_AAB"
}
```

---
