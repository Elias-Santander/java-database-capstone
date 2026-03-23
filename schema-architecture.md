📝**Resumen de la arquitectura**

Esta aplicación está desarrollada con Spring Boot bajo una arquitectura híbrida que combina controladores MVC y servicios REST. Los módulos de administración y gestión médica utilizan plantillas Thymeleaf para el frontend, mientras que el resto de las funcionalidades se exponen mediante una API REST estandarizada. Para la persistencia de datos, el sistema implementa una solución dual: MySQL (vía JPA) para la gestión de entidades relacionales como pacientes, doctores y citas, y MongoDB para el almacenamiento flexible de recetas médicas.

La lógica de negocio se centraliza en una capa de servicio unificada que actúa como intermediaria entre los controladores y los repositorios. Este diseño asegura que todas las solicitudes sigan un flujo de datos coherente, delegando las operaciones a la base de datos correspondiente de forma transparente. Gracias a esta separación de responsabilidades, el sistema garantiza una alta mantenibilidad y facilita la escalabilidad de cada módulo de forma independiente.

🧩**Flujo de datos basado en el diagrama de arquitectura**

1. Capa de Interfaz de Usuario:
   Soporta un modelo híbrido de interacción. Utiliza Thymeleaf para renderizar paneles administrativos en el servidor (HTML) y una API REST para servir datos en formato JSON a aplicaciones móviles o módulos frontend independientes.

2. Capa de Controladores:
   Gestiona los puntos de entrada del sistema. Los controladores de Thymeleaf procesan las solicitudes de vistas dinámicas, mientras que los RestControllers coordinan la lógica de la API, aplicando validaciones iniciales y dirigiendo el flujo de respuesta.

3. Capa de Servicios:
   Constituye el núcleo lógico de la aplicación. Aquí se aplican las reglas de negocio, se validan los flujos de trabajo complejos (como la disponibilidad médica) y se garantiza el desacoplamiento entre la entrada de datos y su persistencia.

4. Capa de Repositorios:
   Abstrae la complejidad del acceso a datos mediante Spring Data. Implementa repositorios JPA para la gestión de datos relacionales en MySQL y repositorios específicos de MongoDB para el manejo de documentos.

5. Acceso a Base de Datos:
   Emplea una persistencia políglota para optimizar el rendimiento. MySQL almacena entidades estructuradas y normalizadas (usuarios, citas), mientras que MongoDB gestiona estructuras flexibles y anidadas (recetas médicas).

6. Vinculación de Modelos (Model Binding):
    Realiza el mapeo de registros físicos a objetos Java. Los datos de MySQL se transforman en entidades JPA (@Entity) y los de MongoDB en documentos (@Document), proporcionando una representación orientada a objetos consistente.

7. Modelos de Aplicación en Uso:
    Cierra el ciclo de vida de la solicitud. Dependiendo del consumidor, los modelos procesados se inyectan en plantillas HTML para su visualización en el navegador o se serializan en JSON para su consumo vía API.
