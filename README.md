# CrustyBakery – Pastelería y Repostería
<table>
    <tr>
        <td width="40%">
            <p>Sistema de gestión para una pastelería orientado a la administración de productos, clientes, pedidos y ventas, acompañado de una aplicación móvil para que los clientes puedan consultar el catálogo y realizar pedidos. <br>
            ## Descripción

**CrustyBakery – Pastelería y Repostería** es un proyecto de software desarrollado para digitalizar y mejorar la gestión de una pastelería dedicada a la elaboración y venta de pasteles personalizados, cupcakes, galletas y diferentes tipos de postres. <br>

Actualmente, la gestión de pedidos puede realizarse de manera presencial o mediante mensajes, utilizando registros manuales. Esto puede dificultar el control de productos, clientes y pedidos.
</p>
        </td>
        <td width="60%" align="center">
            <img src="Image/logo.png" width="450"/>
            </td>
    </tr>
    </table>

El sistema busca centralizar esta información y facilitar tanto la administración del negocio como la interacción con los clientes.

## Objetivo

Desarrollar un sistema que permita gestionar de manera organizada los productos, clientes, pedidos y ventas de la pastelería, proporcionando una aplicación móvil para los clientes y una plataforma web para la administración del negocio.

## Funcionalidades principales

### Aplicación móvil para clientes

* Registro e inicio de sesión de clientes.
* Visualización del catálogo de productos.
* Consulta de información de los productos.
* Realización de pedidos.
* Consulta del estado de los pedidos.
* Gestión de la información del cliente.

### Sistema web administrativo

* Gestión de productos.
* Gestión de clientes.
* Gestión de pedidos.
* Gestión de ventas.
* Administración de usuarios y roles.
* Consulta y control de la información registrada.

### Gestión de pedidos

El sistema permite registrar y administrar los pedidos realizados por los clientes, facilitando su seguimiento y organización dentro de la pastelería.

## Roles del sistema

El sistema contempla diferentes roles según las responsabilidades dentro del negocio:

* **Administrador:** administra usuarios, productos, clientes, pedidos y demás información del sistema.
* **Vendedor:** gestiona la atención y el registro de pedidos realizados por los clientes.
* **Repostero:** consulta los pedidos que deben prepararse y se encarga de su elaboración.
* **Cliente:** consulta el catálogo y realiza pedidos mediante la aplicación móvil.

## Arquitectura

El proyecto está dividido en diferentes componentes:

```text
                    ┌─────────────────────┐
                    │   Aplicación móvil  │
                    │      Clientes       │
                    └──────────┬──────────┘
                               │
                               ▼
                    ┌─────────────────────┐
                    │      API REST       │
                    │      Backend        │
                    └──────────┬──────────┘
                               │
                               ▼
                    ┌─────────────────────┐
                    │     Base de datos   │
                    └─────────────────────┘
                               ▲
                               │
                    ┌──────────┴──────────┐
                    │  Sistema Web        │
                    │  Administrativo     │
                    └─────────────────────┘
```

La aplicación móvil y el sistema web se comunican con el backend mediante una **API REST**, que centraliza la lógica del sistema y el acceso a la base de datos.

## Tecnologías

Las tecnologías utilizadas pueden incluir:

* **Frontend web:** Spring
* **Aplicación móvil:** Kotlin
* **Backend:** [tecnología utilizada]
* **API:** ASP .Net Core
* **Base de datos:** SQL Server
* **Control de versiones:** Git y GitHub

## Alcance

El sistema está enfocado en la gestión de:

* Productos.
* Clientes.
* Usuarios y roles.
* Pedidos.
* Ventas.
* Catálogo de productos.

El proyecto **no contempla** módulos especializados para proveedores, contabilidad, nómina o gestión detallada del proceso de producción.

## Autores

Proyecto desarrollado como parte de un proyecto académico de desarrollo de software. Gabriela Elizabeth Nuñez Navidad

---

**Dulce Aroma – Pastelería y Repostería**
Sistema de gestión y digitalización para una pastelería.
