# Arquitectura del Sistema

CodeMaster utiliza un modelo de **ejecución distribuida local**. Cada celda de código es un entorno independiente (Worker) que procesa Python en el navegador del usuario.

## 1. El Bus de Eventos (`api`)
Para permitir comunicación entre celdas (Pub/Sub), hemos implementado la clase `__CodeMasterAPI`.

* **Send/Listen:** Implementa una cola de mensajes efímera.
* **Seguridad:** El `send` solo guarda el dato si existe un `listener` activo.
* **Atomicidad:** Al ejecutar una celda, el bus se limpia automáticamente (`__CodeMasterAPI._bus.clear()`) para evitar datos residuales de ejecuciones previas ("datos fantasma").

## 2. Diagrama de Flujo
```mermaid
graph TD
    A[Alumno Escribe] --> B{¿Es API/Reactive?};
    B -- No --> C[Ejecuta Celda Pyodide];
    B -- Sí --> D[api.listen - Suscripción];
    D --> E[api.send - Emisor];
    E --> F[Bus de Eventos];
    F --> D;
    C --> G[api.submit];
    F --> G;
    G --> H[Validación Servidor];
    H --> I[Actualizar Ranking];
``` 


## Diagrama de Flujo del Sistema
```mermaid
graph TD
    %% Roles
    subgraph Docente_Area [Profesor]
        D1[Registro / Login] --> D2[Crear Ejercicio - Privado]
        D2 --> D3[Editar Celdas: Implementar Listen/Send/Submit]
        D3 --> D4{¿Listo para probar?}
        D4 -- "No" --> D2
        D4 -- "Sí" --> D5[Cambiar a Público]
        D5 --> D6[Transmitir Live - Activar Socket]
        D6 --> D7[Compartir Link Externo: WhatsApp/URL]
    end

    subgraph Estudiante_Area [Estudiante]
        E1[Login] --> E2{¿Live Session?}
        E2 -- "Sí" --> E3[Join via Enlace]
        E2 -- "No" --> E4[Ver Ejercicios Públicos]
        E3 --> E5[Sincronización en Tiempo Real]
        E4 --> E6[Resolver Ejercicio]
        E5 --> E6
        E6 --> E7[Uso de Herramientas: Listen/Send/Submit]
        E7 --> E8[Validar & Guardar]
    end

    subgraph Backend_Sistema [Motor & Servidor]
        E8 --> S1[Validación Lógica Pyodide]
        S1 -->|Correcto| S2[Guardar Progreso en DB]
        S2 --> S3[Actualizar Ranking Semanal]
        S3 --> E9[Ver Ranking y Detalles]
    end

    %% Conexiones Especiales
    D6 -.->|Cambios en Vivo| E5
    D7 -.->|Enlace Externo| E3
```