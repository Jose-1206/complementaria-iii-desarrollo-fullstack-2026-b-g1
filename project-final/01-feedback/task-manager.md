# Retroalimentación · Avance hacia la Fase 1 (Funcionalidad)

**Proyecto:** Task Manager (team `task-manager`, repositorios `tm-fs-*`)
**Integrantes declarados en el README:** Andrés Felipe Gómez Anacona, Julio César Lozano Lozano y Juan José Horta Vanegas
**Revisión:** sábado 7 de noviembre de 2026 · Docente Jesús Ariel González Bonilla

Técnicamente, este es un proyecto muy sólido: la API, el portal y la documentación son coherentes entre sí. Los riesgos no están en la calidad del código, sino en el **alcance** (solo una entidad tiene CRUD completo), en el **trabajo en equipo** (todo el código tiene un solo autor) y en varios **requisitos formales** de la guía que aún faltan.

Esta revisión se hizo leyendo los repositorios; todavía no se ejecutó el proyecto. Los puntos siguientes son para resolver antes del cierre de la Fase 1 (domingo 8 de noviembre, 23:59), la sustentación (lunes 9) y la entrega (sábado 14 de noviembre, 23:59).

## 1. Lo que ya está bien

- **Backend en capas:** módulos `auth`, `users` y `tasks` con `controller`, `service`, `repository`, `entity` y `dto`, sobre Spring Boot 3.5.7 y Java 21. Una prueba de arquitectura vigila la separación.
- **Seguridad con buena base:** JWT sin estado, BCrypt, 401 y 403 con cuerpo JSON uniforme y `GET /users` restringido con `hasRole('ADMIN')`. El registro público siempre crea `USER`.
- **Cada usuario solo toca sus tareas:** el servicio consulta siempre por `id` y dueño, de modo que una tarea ajena responde 404.
- **Errores y validación:** Bean Validation y `@RestControllerAdvice` con 400, 401, 403, 404 y 409.
- **Base de datos:** migraciones Flyway con restricciones reales y `ddl-auto: validate`.
- **Portal:** login, registro, rutas protegidas, lista, formulario y calendario de tareas contra la API real, con los errores visibles para el usuario.
- **Pruebas:** servicio, controlador y arquitectura en `-api`, colección de Postman en `postman/` y pruebas de componentes en `-portal`.
- **Infraestructura:** `docker-compose.yml` con base, API y portal, y `.env.example`.
- **Documentación primero:** el `-docs` quedó cerrado antes del primer commit de código, y las 11 rutas documentadas coinciden con las 11 del código, igual que las 2 entidades y los 2 roles.

## 2. Lo más urgente: el alcance del MVP

La guía exige **tantas entidades con CRUD completo como integrantes, con un mínimo de dos**, y que dos de ellas estén relacionadas. Hoy:

| Entidad | Crear | Leer | Actualizar | Eliminar | ¿CRUD completo? |
|---|---|---|---|---|---|
| `Task` | ✅ | ✅ | ✅ | ✅ | **Sí** |
| `User` | Solo registro | Solo lista para `ADMIN` | No | No | **No** |

Con tres integrantes se necesitan tres CRUD, y hoy hay uno. Aunque el equipo fuera de una sola persona, faltaría uno. Así, el ítem 5 de la lista de chequeo no se cumpliría.

Recomendación:

1. Complete el CRUD de `User` para `ADMIN` (consultar uno, editar y eliminar), sin permitir que un administrador se elimine a sí mismo.
2. Agregue una entidad nueva relacionada con `Task`, por ejemplo `Category`, con `@ManyToOne`, su CRUD y su pantalla.
3. Actualice primero la documentación (`02-domain`, `06-data`, `07-api` y `12-ux-ui`) y después el código.

El alcance solo se cambia avisando por Moodle, así que informe cuál será la tercera entidad.

## 3. Quiénes son los integrantes

El README nombra a tres personas, pero las demás fuentes no coinciden:

| Integrante | README `-docs` | Grupo de Moodle | Acceso a `tm-fs-*` | Commits en código |
|---|---|---|---|---|
| Andrés Felipe Gómez | Sí | `task-manager` | Sí | Todos |
| Julio César Lozano | Sí | Sin grupo | **No** | **Ninguno** (2 commits en `-docs`, septiembre) |
| Juan José Horta | Sí | `wellness-habits-system` | **No** | **Ninguno** |

Definan esto hoy y confírmenlo por Moodle: cada persona debe tener un solo equipo y aparecer en el README, en el grupo de Moodle y en los repositorios.

La guía es clara: quien no tenga commits propios **en los repositorios de código** queda con 0,0. La documentación no reemplaza ese requisito.

## 4. El historial de commits

Todo entró por Pull Request y con commits bien nombrados. Sin embargo, el ritmo del trabajo preocupa:

| Repositorio | Cuándo se subió todo el código |
|---|---|
| `tm-fs-db` | 2-nov, 22:06 a 22:09 |
| `tm-fs-api` | 2-nov, 22:20 a 23:16 |
| `tm-fs-portal` | 3-nov, 7:46 a 7:53 |
| `tm-fs-infra` | 3-nov, 8:01 a 8:03 |

El portal completo aparece en siete minutos y la API en menos de una hora, sin actividad en otros días. La guía define la **«aplicación de golpe»** como partes completas hechas con segundos o minutos de diferencia, y fija un **tope de 3,0 para la nota del equipo**. Este historial puede quedar dentro de esa regla. Además, el ítem 10 pide que **cada integrante** tenga commits propios repartidos en varios días, y hoy hay un solo autor en apenas dos momentos de trabajo.

No reescriba la historia. Lo que resta del trabajo debe hacerse en commits reales, repartidos entre integrantes y días. En la sustentación, cada integrante deberá explicar el código que lleva su nombre.

## 5. Seguridad: secretos en los repositorios

El ítem 6 exige que no haya secretos en ningún repositorio:

1. **`application.yml` trae valores por defecto** para `JWT_SECRET`, `POSTGRES_PASSWORD` y `ADMIN_PASSWORD`. Si falta una variable, la API arranca con una clave conocida. Quite esos valores para que el arranque falle si la variable no existe.
2. **`docker-compose.yml` repite esos valores** con `${VAR:-valor}`. Déjelos solo en `.env.example`, donde sí corresponden como valores de ejemplo (incluida la contraseña del usuario de prueba que pide el manual), y use `${VAR:?}` en el compose para que el error sea claro.
3. **CORS admite dos orígenes por defecto** (`3000` y `5173`). La guía pide solo el del portal: use `3000` en Docker y deje `5173` únicamente para el modo de desarrollo.

## 6. Lo que falta frente a la guía

| Requisito | Cómo está hoy | Qué hacer |
|---|---|---|
| Ficha del MVP en el README del `-docs` (Fase 0, vencida el 1-nov) | **No está** | Copie la ficha de la sección 4 de la guía, con usuarios de GitHub y responsable de cada parte |
| Sección «Overview & tech» en inglés | **No está con ese título.** El README tiene contenido parecido, disperso | Escriba mínimo seis oraciones: problem, users, architecture, stack, key decisions y how to run |
| Promoción a `qa` | `qa` no tiene nada del proyecto; `develop` va adelante en los cuatro repositorios | Abra el PR de `develop` a `qa` en los cuatro antes de la sustentación |
| Etiqueta `v1.0.0` en `qa` | No existe | Generarla en los cuatro repositorios al cierre |
| Manual de implementación en el README de `-infra` | Solo trae el texto inicial del repositorio. `10-devops/local-setup.md` tiene parte del contenido | Escriba los **nueve puntos** de la sección 6 de la guía, con los tres «Problemas frecuentes» |
| Semilla con un usuario por rol | Solo `ADMIN`. No hay `USER` de prueba | Agregue un usuario `USER` y documente sus credenciales de ejemplo |
| Diagrama entidad-relación en imagen en `-db` | No está en `tm-fs-db`. Solo hay un diagrama en texto en `06-data/models.md` del `-docs` | Expórtelo como imagen y agréguelo a `tm-fs-db` |
| Manual de uso, FO-IV-159 y anexos | Pendientes de la entrega | Prepárelos después de la sustentación, con capturas reales |

Dos notas menores:

- **Interceptor.** `apiClient.js` agrega el token en un solo punto. Cumple esa función aunque no se llame «interceptor»: conviene explicarlo así en la sustentación.
- **Migraciones duplicadas.** Las de `tm-fs-api` y `tm-fs-db` hoy son idénticas. Al agregar la tercera entidad, cámbielas en ambos repositorios.

## 7. Prioridad hasta el lunes

1. Definir los integrantes y dar acceso a los repositorios.
2. Publicar en el README la ficha y «Overview & tech», con los integrantes correctos.
3. Documentar y construir los CRUD que faltan, repartidos entre los integrantes.
4. Quitar los secretos de `application.yml` y `docker-compose.yml`, y ajustar CORS.
5. Agregar la semilla `USER`, pasar `develop` a `qa` y probar el arranque desde un clon limpio siguiendo el manual.

Para el lunes, tenga listos dos usuarios, uno por rol, y un endpoint que muestre 401 y 403 en vivo: `GET /users` sirve para eso. Declare también cuál es la regla de negocio que va a demostrar; completar y reabrir una tarea, con su fecha de finalización, es una buena candidata.
