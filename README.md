# API Gateway Service

Este proyecto es el **API Gateway** central para el ecosistema de microservicios. Construido con **Spring Cloud Gateway**, actúa como el único punto de entrada para todas las peticiones de los clientes. Sus responsabilidades principales son la seguridad, el enrutamiento de peticiones y la agregación de documentación.

## ✨ Características Principales

*   **Enrutamiento Centralizado:** Redirige el tráfico entrante a los microservicios correspondientes (`user-service`, `offer-service`, `interview-service`, etc.) basándose en el path de la URL.
*   **Seguridad con JWT:** Protege los endpoints utilizando un filtro global (`JwtAuthenticationFilter`) que:
    *   Valida los tokens JWT presentes en la cabecera `Authorization`.
    *   Rechaza peticiones con tokens inválidos o ausentes con un estado `401 Unauthorized`.
    *   Si el token es válido, extrae el `user-id` y lo inyecta como una cabecera (`X-User-ID`) en la petición antes de enviarla al microservicio de destino. Esto permite que los servicios internos confíen en la identidad del usuario sin necesidad de revalidar el token.
*   **Enrutamiento de WebSockets:** Configurado para manejar y redirigir conexiones WebSocket (`ws://`) hacia el `stream-service`, permitiendo la comunicación en tiempo real.
*   **Orquestación de Autenticación:** Aunque su función principal es de enrutamiento, también se comunica con el `user-service` a través de un cliente Retrofit para gestionar operaciones de autenticación como el login y el registro.

## 🛠️ Tecnologías Utilizadas

*   **Framework:** Spring Boot con Spring Cloud Gateway
*   **Programación Reactiva:** Spring WebFlux / Project Reactor
*   **Seguridad:** JSON Web Tokens (JJWT)
*   **Cliente HTTP:** Retrofit 2
*   **Documentación:** Springdoc OpenAPI
*   **Build Tool:** Maven

## 🏛️ Arquitectura y Flujo de Autenticación

1.  Un cliente (frontend) envía una petición a un endpoint del gateway (ej. `/services/be/offer-service/offers`).
2.  La petición debe incluir una cabecera: `Authorization: Bearer <token-jwt>`.
3.  El filtro global `JwtAuthenticationFilter` intercepta la petición.
4.  Valida la firma y la expiración del token JWT.
    *   **Si es inválido:** La petición es rechazada con un `401 Unauthorized`.
    *   **Si es válido:** El filtro extrae el ID del usuario del token.
5.  Se añade una nueva cabecera a la petición, por ejemplo: `user-id: 12345-abcde`.
6.  La petición modificada es enrutada de forma segura al microservicio correspondiente (en este caso, `offer-service`).
7.  El `offer-service` recibe la petición y puede confiar en la cabecera `user-id` para identificar al usuario sin tener que procesar el JWT.


## 🛡️ Seguridad Avanzada con JWT

El API Gateway es la primera línea de defensa del sistema y utiliza un filtro global (`JwtAuthenticationFilter`) para implementar una estrategia de seguridad robusta y centralizada.

*   **Validación de Token y Rol:** El filtro intercepta **todas** las peticiones entrantes (incluyendo las conexiones WebSocket). No solo verifica que el token JWT sea válido (firma y expiración), sino que también **extrae el rol del usuario** (ej. `ADMIN`, `USER`) de los claims del token.

*   **Autorización Basada en Rutas (RBAC):** Ciertos endpoints están protegidos y solo son accesibles para roles específicos. El filtro compara el rol del usuario con una lista de roles permitidos para la ruta solicitada. Si el usuario no tiene los permisos necesarios, la petición es rechazada con un estado `403 Forbidden`.

*   **Protección de WebSockets:** Las conexiones WebSocket también están protegidas. Para establecer una conexión segura, el cliente debe incluir el token JWT como un parámetro en la URL de conexión (ej. `ws://domain.com/stream?token=...`). El gateway valida este token antes de permitir que la conexión se establezca con el `stream-service`.

*   **Inyección de Identidad Confiable:** Si la validación y la autorización son exitosas, el gateway enriquece la petición antes de reenviarla al microservicio de destino. Inyecta las siguientes cabeceras:
    *   `X-User-ID`: El ID del usuario extraído del token.
    *   `X-User-Role`: El rol del usuario.

    Esto permite que los servicios internos confíen en la identidad y los permisos del usuario sin necesidad de revalidar el token JWT, simplificando su lógica de negocio.

## Diagrama de clases 

![image](https://github.com/user-attachments/assets/bbaeb6bd-9999-4f3a-8e97-aa2804a4c805)
    
    
