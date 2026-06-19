---

## 3. `docs/USER_GUIDE.md` (El Manual de Usuario)

```markdown
# Guía de Usuario CodeMaster

## 1. Conceptos Básicos
* **Celda:** Unidad mínima de código.
* **Celda Receptora:** Aquella que utiliza `api.listen` para esperar datos.
* **Celda Emisora:** Aquella que usa `api.send` para propagar datos.
* **api.submit():** La función mágica que envía tu resultado al servidor para ser evaluado. Es como el print() a evaluar.

## 2. Modos de Trabajo

| Característica | Ejercicio Estándar | Ejercicio API (Reactive) |
| :--- | :--- | :--- |
| **Flujo** | Secuencial | Event-Driven (Eventos) |
| **Uso** | Resolución directa | Integración de microservicios |
| **Ejecución** | Ejecutar celda y submit | Ejecutar escucha, luego emisor |

## 3. Flujo de Trabajo para Ejercicios API
Si el ejercicio requiere `api.listen`:
1. **Paso 1:** Ejecuta la celda que contiene el `api.listen`. Verás el mensaje `⏳ Celda suscrita`.
2. **Paso 2:** Ejecuta la celda que contiene el `api.send`.
3. **Paso 3:** El sistema procesará el evento automáticamente y, si el resultado coincide, verás el mensaje `✓ SUPERADO`.

> **⚠️ Importante:** Si ejecutas el emisor antes de suscribirte, el mensaje se descartará. Sigue siempre el orden: **Escuchar -> Emitir**.

## 4. Solución de Problemas
* **"Inconsistent use of tabs and spaces":** El editor está configurado para convertir Tab en 4 espacios. Si aparece este error, borra la indentación y presiona Tab nuevamente.
* **"Salida incorrecta":** Revisa que el JSON enviado en el emisor coincida exactamente con la estructura esperada por el receptor.
* **El Ranking no se actualiza:** Asegúrate de ejecutar `api.submit(resultado)` con el formato exacto requerido por el ejercicio.