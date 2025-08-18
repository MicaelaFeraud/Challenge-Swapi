# SWAPI Challenge — Backend (Spring Boot, Java 8)

Este proyecto expone endpoints REST para consultar **People**, **Films**, **Starships** y **Vehicles** de SWAPI, con soporte de **búsqueda por nombre** y **búsqueda exacta por id**, paginación, y pruebas **unitarias** + **de integración**.

> **Compatibilidad:** Java 8. Evitamos `List.of(...)` (Java 9+) y usamos `Collections.singletonList(...)` / `Collections.emptyList()`.

---

## 🚀 Quickstart

```bash
# 1) Requisitos
# - Java 8
# - Maven 3.6+ o Gradle 7+

# 2) Variables de entorno (ejemplo)
export SWAPI_BASE_URL=https://www.swapi.tech/api

# 3) Ejecutar
mvn spring-boot:run
# o
./gradlew bootRun

# 4) Probar
curl -s "http://localhost:8080/api/people?page=1&limit=5&name=sky"
```

---

## ⚙️ Configuración

### application.yml (mínimo)

```yaml
server:
  port: 8080

swapi:
  base-url: ${SWAPI_BASE_URL:https://www.swapi.tech/api}
  default-page-size: 10
  bulk-limit: 100
```

### Seguridad (JWT)

El proyecto incluye un filtro `JwtAuthFilter` con dependencia de `JwtService`. Para desarrollo local, podés:
- Deshabilitar filtros en tests (`@AutoConfigureMockMvc(addFilters=false)`).
- Proveer un `JwtService` real o mock según tu configuración.
- En endpoints protegidos, enviar `Authorization: Bearer <token>`.

---

## 🧭 Endpoints

> Reemplazá `{resource}` por `people`, `films`, `starships`, `vehicles`.

### Listado
```
GET /api/{resource}
```
**Parámetros**

| Param | Tipo | Descripción |
|------|------|-------------|
| `page` | `Integer` | Página (base 1). Por defecto 1 si es nula/menor a 1. |
| `limit` | `Integer` | Tamaño de página. Por defecto `defaultPageSize`. |
| `name` | `String` | Filtro por nombre (contiene, case-insensitive). |
| `id` | `String` | **Búsqueda exacta por id**. Si viene, **tiene precedencia** sobre `name` y retorna 0/1 elemento. |

**Respuesta (`PageDto<T>`):**
```json
{
  "items": [{ "id": "1", "name": "Luke Skywalker" }],
  "page": 1,
  "limit": 10,
  "totalPages": 1,
  "totalRecords": 1,
  "next": null,
  "previous": null
}
```

### Detalle
```
GET /api/{resource}/{id}
```
- `404 Not Found` si no existe.

**Ejemplos**
```bash
# People por nombre
curl -s "http://localhost:8080/api/people?page=1&limit=10&name=sky" | jq .

# Starships por id
curl -s "http://localhost:8080/api/starships?id=9" | jq .

# Films detalle
curl -s "http://localhost:8080/api/films/1" | jq .
```

---

## 🧪 Pruebas

Se incluyen **unitarias** y **de integración**.

### Unitarias
- `*ServiceTest` (People, Films, Starships, Vehicles)
    - Mock de `SwapiClient`.
    - Verifican: búsqueda por `name` (mapeo y paginación), búsqueda por `id` (0/1 ítem; no llama a `listXxx`), y `getById`.

- `*ControllerTest` (slice MVC)
    - `@WebMvcTest(Controller.class)`
    - `@MockBean Service`, `@MockBean JwtService`
    - `@AutoConfigureMockMvc(addFilters=false)` para evitar filtros.

### Integración (funcionales)
- `*ApiFunctionalTest` con `@SpringBootTest` + `@AutoConfigureMockMvc(addFilters=false)`
- Mockean **`SwapiClient`** y **`JwtService`**; ejercitan Controller + Service + configuración.

**Ejecución**
```
mvn test
```

---

## 🧩 Decisiones de diseño

- `id` exacto y con prioridad sobre `name`.
- Búsqueda local por `name` (hasta `bulkLimit`), case-insensitive.
- Clampeo de `page` post-filtrado para evitar páginas vacías por arrastre.
- Compatibilidad Java 8 en colecciones.

---

## 🧑‍💻 Guía de **documentación del código** (Javadoc)

1. **Clase Controller** (ejemplo)
```java
/**
 * Endpoints de People.
 * Soporta listado con filtros (name/id) y detalle por id.
 */
@RestController
@RequestMapping("/api/people")
public class PeopleController {
    /**
     * Lista personas con paginación.
     * @param page página base 1 (opcional)
     * @param limit tamaño de página (opcional)
     * @param name filtro por nombre (contains, case-insensitive; opcional)
     * @param id   búsqueda exacta por id (opcional; tiene precedencia sobre name)
     * @return página con resúmenes de personas
     */
    @GetMapping
    public ResponseEntity<PageDto<PersonSummaryDto>> list(
        @RequestParam(required=false) Integer page,
        @RequestParam(required=false) Integer limit,
        @RequestParam(required=false) String name,
        @RequestParam(required=false) String id) { ... }
}
```

2. **Clase Service** (ejemplo)
```java
/**
 * Lógica de negocio de People.
 */
@Service
public class PeopleService {
    /**
     * Obtiene una página de personas.
     * Si id está presente, retorna 0/1 elemento exacto.
     * Si no, aplica filtro por name (contains) y paginación.
     */
    public PageDto<PersonSummaryDto> list(Integer page, Integer limit, String name, String id) { ... }

    /**
     * Obtiene el detalle por id.
     * @param id identificador exacto (string numérico en SWAPI)
     * @return detalle o null si no existe
     */
    public PersonDetailDto getById(String id) { ... }
}
```

3. **DTOs**
```java
/**
 * Resumen genérico con id y nombre visible.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PersonSummaryDto {
    /** Identificador único de SWAPI (string). */
    private String id;
    /** Nombre visible del recurso. */
    private String name;
}
```

**Reglas**:
- Documentar **parámetros** y **valores de retorno**.
- Aclarar **precedencias** (id vs name) y comportamientos borde (sin resultados, páginas fuera de rango).
- Mantener **consistencia** en todos los recursos (People/Films/Starships/Vehicles).

---

## 📁 Estructura sugerida

```
src/main/java/com/swapi/challenge/
 ├─ auth/
 ├─ swapi/
 ├─ people/
 ├─ films/
 ├─ starships/
 ├─ vehicles/
 └─ api/       

src/test/java/com/swapi/challenge/
 ├─ people/
 ├─ films/
 ├─ starships/
 └─ vehicles/
```

---

## 🧯 Troubleshooting

- **`NoSuchBeanDefinitionException: JwtService` en tests**  
  Agregar `@MockBean JwtService` y `@AutoConfigureMockMvc(addFilters=false)` en tests que cargan contexto.

- **Búsqueda por name no encuentra**  
  Verificar normalización (lowercase, sin acentos) y que `bulkLimit` permita traer suficientes elementos.

---

## 📜 Licencia
Uso educativo / Challenge.
