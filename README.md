# CodeMaster: Plataforma Educativa con Inteligencia Artificial

**CodeMaster** es una plataforma de aprendizaje asíncrono y resolución de problemas de programación, impulsada por Inteligencia Artificial. Su propósito es guiar al estudiante paso a paso en el desarrollo de software, desde la lógica base hasta la construcción de arquitecturas completas.

## 🎯 Visiones del Proyecto

[cite_start]El desarrollo y la implementación de CodeMaster se fundamentan en tres visiones estratégicas, diseñadas para satisfacer a todas las partes involucradas[cite: 15, 16]:

### 1. Visión Académica (Universidad)
* [cite_start]**Rendimiento:** Aumentar significativamente la tasa de aprobación y reducir la deserción en los cursos de programación[cite: 18, 28].
* [cite_start]**Práctica Autónoma:** Proveer un entorno donde el estudiante reciba retroalimentación técnica inmediata sin depender exclusivamente del horario del docente[cite: 4].
* [cite_start]**Prestigio:** Integrar IA educativa de vanguardia para posicionar a la institución como líder en metodologías de enseñanza práctica[cite: 29, 50].

### 2. Visión Comercial (Inversionistas)
* [cite_start]**Rentabilidad:** Generar un retorno de inversión claro a través del éxito de la plataforma[cite: 31].
* [cite_start]**Escalabilidad Geográfica:** Validar el sistema en una sede piloto (ej. UTP) con la proyección de expandir el modelo a otras universidades y centros de estudio[cite: 20, 40].
* [cite_start]**Innovación Continua:** Participar en un mercado EdTech en crecimiento con un producto que compite con estándares de la industria (tipo LeetCode o HackerRank)[cite: 32, 51].

### 3. Visión Técnica (Desarrolladores)
* [cite_start]**Arquitectura Resiliente:** Construir un producto altamente escalable [cite: 35] capaz de manejar evaluaciones concurrentes de IA sin comprometer el rendimiento del servidor.
* [cite_start]**Mantenibilidad:** Establecer un código base limpio y modular que asegure beneficios económicos a largo plazo mediante contratos de soporte y actualización continua[cite: 34, 67].

---

## ⚙️ Stack Tecnológico

* **Lenguaje:** Java 25
* **Framework:** Spring Boot 3 (Web, Data JPA, Validation)
* **Base de Datos:** PostgreSQL (alojada en Supabase)
* **Arquitectura de Datos:** Modelo relacional transaccional optimizado con columnas `JSONB` para manejar la asimetría de los prompts y respuestas de la IA.

## 🚀 Características Principales

* **Resolución Asíncrona:** Retos de programación de acceso público o mediante enlaces privados, evaluados en segundo plano.
* **Análisis Cognitivo:** La IA no solo valida si el código funciona, sino que lee el historial de intentos del alumno para identificar fallas conceptuales (ej. falta de entendimiento de punteros o inyección de dependencias).
* **Gestión de Salas:** Entornos virtuales donde los docentes asignan problemas y supervisan el rendimiento a través de reportes generados a partir de las evaluaciones de la IA.
* **Seguridad y Rendimiento:** Uso intensivo de hilos virtuales y procesamiento `@Async` para aislar la latencia de las llamadas a la API de los LLMs.

---