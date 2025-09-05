# 🚀 REVFA_BackEnd 🚀

Este proyecto es el backend para la aplicación REVFA, construido con Quarkus.

## 📝 Documentación

¡Bienvenido al proyecto REVFA_BackEnd! Este documento te guiará a través de las nuevas funcionalidades, comandos importantes y mejores prácticas para trabajar con Quarkus.

### 1. ✨ Nuevas Funcionalidades

### 2. 💻 Comandos Importantes (pwd - Directorio de Trabajo del Proyecto)

Cuando trabajes en este proyecto, interactuarás principalmente con la línea de comandos desde el directorio raíz del proyecto `REVFA_BackEnd`.

- **`./mvnw quarkus:dev`**: Este es el comando más importante para el desarrollo. Inicia Quarkus en modo de desarrollo, lo que proporciona:
  - **Live Coding:** Los cambios en tu código Java, recursos y configuración se recargan automáticamente sin necesidad de reiniciar la aplicación.
  - **Depuración:** Puedes adjuntar un depurador a la aplicación en ejecución.
  - **Hot Reloading:** La mayoría de los cambios se reflejan instantáneamente.
- **`./mvnw package`**: Este comando empaqueta tu aplicación en un archivo JAR ejecutable. Esto es lo que usarás para construir la aplicación para su despliegue.
- **`java -jar target/quarkus-app/quarkus-run.jar`**: Después de empaquetar, puedes ejecutar la aplicación usando este comando.
- **`./mvnw clean`**: Limpia el directorio de compilación, eliminando las clases compiladas y los artefactos generados. Útil antes de una compilación limpia.
- **`./mvnw test`**: Ejecuta todas las pruebas unitarias y de integración.
- **`./mvnw quarkus:add-extension`**: Úsalo para agregar fácilmente nuevas extensiones de Quarkus (ej., `quarkus-resteasy-reactive`).
- **`./mvnw clean verify`**: Este comando es una combinación de `clean` y `verify`. Primero, limpia el proyecto y luego ejecuta el ciclo de vida `verify` de Maven, que incluye la ejecución de pruebas y la verificación de la calidad del código (ahorita usando Jacoco).
- **`./mvnw pmd:pmd`**: Ejecuta el análisis de código estático con PMD. Esto ayuda a identificar posibles problemas en el código, como bugs, código muerto, código subóptimo, expresiones complicadas y código duplicado. Los resultados se generan en `target/pmd.xml`.
- **`./mvnw formatter:format`**: Aplica un formato de código consistente a todo el código fuente del proyecto. Esto ayuda a mantener la legibilidad y un estilo uniforme, basándose en las reglas definidas en la configuración del plugin.

### 3. ✅ Nota Importante: Verificación Local antes del CI

Para asegurar que el código cumple con los estándares de calidad y formato del proyecto, y para evitar fallos en el pipeline de Integración Continua (CI), es **crucial** ejecutar las siguientes verificaciones en tu entorno local **antes** de subir tus cambios (`git push`).

Estos comandos revisarán y aplicarán el formato correcto al código, buscarán posibles errores y ejecutarán todas las pruebas.

#### Pasos recomendados antes de subir tu código:

1.  **Aplica el formato al código:**
    ```bash
    ./mvnw formatter:format
    ```
2.  **Ejecuta la verificación completa (pruebas y análisis estático):**
    ```bash
    ./mvnw clean verify pmd:pmd
    ```

Si ambos comandos se completan con `BUILD SUCCESS`, es muy probable que tu código pase las verificaciones automáticas del CI. Si alguno falla, revisa los mensajes en la consola para identificar y corregir los errores reportados por Maven, PMD o las pruebas.

### 4. 🌿 Flujo de Trabajo y Contribución

Para mantener el repositorio organizado y facilitar la colaboración, es fundamental seguir el siguiente flujo de trabajo:

1.  **Básate en `development`**: Antes de iniciar cualquier cambio, asegúrate de tener la última versión de la rama `development`.

    ```bash
    git checkout development
    git pull origin development
    ```

2.  **Crea tu propia rama**: Crea una nueva rama a partir de `development` para trabajar en tus cambios. Usa un nombre descriptivo.

    ```bash
    # Ejemplo: git checkout -b feature/autenticacion-jwt
    git checkout -b <tipo>/<descripcion-corta>
    ```

3.  **Trabaja y haz Commits**: Realiza tus cambios en tu nueva rama. Al hacer commits, utiliza la especificación de **Conventional Commits**. Esto es crucial para la automatización y la claridad del historial.

    - **`feat:`**: Para una nueva funcionalidad.
    - **`fix:`**: Para una corrección de un bug.
    - **`docs:`**: Para cambios en la documentación.
    - **`style:`**, **`refactor:`**, **`test:`**, **`chore:`** para otras tareas.

    _Ejemplo de commit:_

    ```bash
    git commit -m "feat: agregar endpoint para registro de usuarios"
    ```

4.  **Crea un Pull Request (PR)**: Una vez que hayas terminado y verificado tu código localmente (ver sección 3), sube tu rama al repositorio (`git push`) y crea un **Pull Request** (PR) desde tu rama hacia `development`.

5.  **Solicita una Revisión**: En la descripción del PR, detalla los cambios que realizaste y solicita una revisión a los miembros correspondientes del equipo. Esto asegura que el código sea revisado antes de ser integrado.
