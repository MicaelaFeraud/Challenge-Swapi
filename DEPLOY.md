# Despliegue (Gratis) — Render y Koyeb

Este documento explica cómo publicar el backend en **Render** y **Koyeb** usando sus planes gratuitos. Ambos aceptan Docker y funcionan bien con Spring Boot.

> Asegurate de tener en `src/main/resources/application.properties` o `application.yml`:
>
> ```properties
> server.port=${PORT:8080}
> ```
> 
> En muchos PaaS el puerto llega por `PORT`.

---

## Opción A — Render (plan **Free**)

**Ventajas**: gratis con límite mensual de horas por workspace; despliegue desde GitHub con `render.yaml` (Blueprint).  
**Limitación**: si consumís las horas gratuitas del mes, Render pausa el servicio hasta el siguiente mes. citeturn0search0turn0search5

### 1) Archivos listos
- `Dockerfile` (multi-stage, Java 8)
- `render.yaml` (Blueprint con `plan: free`)

### 2) Pasos
1. Subí estos archivos a la raíz del repo.
2. Crea cuenta en Render y conecta tu repositorio.
3. En el dashboard, seleccioná **New + → Blueprint** y elegí tu repo con `render.yaml`.  
4. Render creará el **Web Service** con **plan: free**. La app correrá en el puerto `${PORT}` que Render inyecta (exponemos 8080 por fallback).  
5. Probar: `https://<tu-servicio>.onrender.com/api/people?page=1&limit=5&name=sky`

> **Notas**: El free tier otorga **750 horas gratuitas por mes** por workspace; si se agotan, los servicios free quedan suspendidos hasta el mes siguiente. citeturn0search0

---

## Opción B — Koyeb (Free tier)

**Ventajas**: plan gratuito con una instancia free y soporte nativo para APIs dinámicas / Java. Despliegue desde Git o Dockerfile. citeturn1search11turn3search3

### 1) Pasos (con Dockerfile)
1. Creá cuenta en Koyeb.
2. En el panel, **Create Web Service** → *Docker* → seleccioná tu repo (o imagen de registry) y el `Dockerfile`.
3. Seteá la variable `JAVA_OPTS=-Xms256m -Xmx512m` y el puerto **8080** (HTTP).  
4. Deploy. Endpoint: `https://<service>-<random>.koyeb.app`

### 2) CI/CD opcional (GitHub Actions)
- Usá `.github/workflows/koyeb-deploy.yml` incluido. Requisitos:
  - Agregar **Repo secret** `KOYEB_API_TOKEN` (desde tu cuenta Koyeb).
  - El workflow llama a `koyeb/action-git-deploy@v1` para construir+desplegar. citeturn3search0

---

## Otras plataformas (referencia rápida)

- **Heroku**: ya **no tiene plan gratuito** desde 2022. citeturn0search3turn0search13
- **Vercel**: ideal para frontends; no soporta Spring Boot como servidor persistente. citeturn0search9turn0search19
- **Google Cloud Run**: tiene **free tier** mensual (vCPU/RAM y 2M requests) pero requiere cuenta con billing y cuidado de cuotas/regiones. citeturn1search2turn1search0
- **Fly.io**: dispone de allowances gratuitas limitadas; posible costo si excedís memoria/CPU necesarias para Spring. citeturn0search17turn0search12turn1search13

---

## Problemas comunes

- **Se cae por memoria** en free tier → reducí `JAVA_OPTS` o subí a plan pago.  
- **El puerto no coincide** → asegurate que la app use `server.port=${PORT:8080}`.  
- **Tiempo de build alto** → cacheá dependencias (el Dockerfile ya usa `dependency:go-offline`).  
- **CORS** con un frontend en otro dominio → habilitá CORS en Spring o a través de proxies del PaaS.

---

## Verificación de despliegue

- `GET /actuator/health` (si tenés Spring Actuator) o `GET /api/people` debe responder **200**.  
- Logs en Render/Koyeb deben mostrar `Started Application in ...` y el puerto asignado.
