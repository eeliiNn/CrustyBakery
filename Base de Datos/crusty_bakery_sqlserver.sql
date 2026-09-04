-- ============================================================
-- CRUSTY BAKERY - Script de Base de Datos (SQL Server 2019+)
-- Incluye: Tablas, Auditoría, Vistas, Procedimientos Almacenados
-- Convertido desde MySQL 8.0 a Transact-SQL
-- ============================================================
-- NOTAS DE CONVERSIÓN:
--  * ENUM(...) no existe en SQL Server -> VARCHAR + CHECK CONSTRAINT
--  * AUTO_INCREMENT -> IDENTITY(1,1)
--  * BOOLEAN -> BIT
--  * JSON (tipo nativo de MySQL) -> NVARCHAR(MAX) + CHECK (ISJSON(...)=1)
--    Las funciones JSON_OBJECT(...) de MySQL se sustituyen por el patrón
--    (SELECT ... FOR JSON PATH, WITHOUT_ARRAY_WRAPPER) de SQL Server.
--  * DELIMITER $$ no es necesario en T-SQL; se usa GO para separar lotes.
--  * SIGNAL SQLSTATE '45000' -> THROW (o RAISERROR)
--  * LAST_INSERT_ID() -> SCOPE_IDENTITY()
--  * ON DUPLICATE KEY UPDATE -> MERGE
--  * Los TRIGGER de MySQL son "por fila" (FOR EACH ROW); en SQL Server
--    los triggers son "por conjunto" (operan sobre las tablas virtuales
--    inserted / deleted), así que se reescribieron para procesar
--    varias filas a la vez en una sola sentencia.
--  * RESTRICT no existe como palabra clave en SQL Server -> NO ACTION.
--  * Las FK idUsuarioEmpleado e idUsuarioRepostero (ambas apuntan a
--    "usuario") se dejaron en NO ACTION para evitar el error de SQL
--    Server "may cause cycles or multiple cascade paths"; el borrado en
--    cascada / SET NULL original de MySQL debe manejarse a nivel de
--    aplicación o con un trigger INSTEAD OF si se necesita.
-- ============================================================

IF DB_ID(N'crusty_bakery') IS NOT NULL
BEGIN
    ALTER DATABASE crusty_bakery SET SINGLE_USER WITH ROLLBACK IMMEDIATE;
    DROP DATABASE crusty_bakery;
END
GO

CREATE DATABASE crusty_bakery;
GO

USE crusty_bakery;
GO

-- ============================================================
-- 1. TABLAS PRINCIPALES
-- ============================================================

CREATE TABLE usuario (
    idUsuario       INT IDENTITY(1,1) PRIMARY KEY,
    nombre          NVARCHAR(100)   NOT NULL,
    correo          NVARCHAR(150)   NOT NULL UNIQUE,
    contrasena      NVARCHAR(255)   NOT NULL,
    telefono        NVARCHAR(20)    NULL,
    fechaRegistro   DATETIME2       NOT NULL DEFAULT SYSDATETIME(),
    rol             VARCHAR(20)     NOT NULL,
    activo          BIT             NOT NULL DEFAULT 1,
    CONSTRAINT ck_usuario_rol CHECK (rol IN ('ADMINISTRADOR','EMPLEADO','REPOSTERO'))
);
GO

CREATE TABLE cliente (
    idCliente       INT IDENTITY(1,1) PRIMARY KEY,
    nombre          NVARCHAR(100)   NOT NULL,
    correo          NVARCHAR(150)   NOT NULL UNIQUE,
    contrasena      NVARCHAR(255)   NOT NULL,
    telefono        NVARCHAR(20)    NULL,
    direccion       NVARCHAR(255)   NULL,
    fechaRegistro   DATETIME2       NOT NULL DEFAULT SYSDATETIME(),
    activo          BIT             NOT NULL DEFAULT 1
);
GO

CREATE TABLE categoria (
    idCategoria     INT IDENTITY(1,1) PRIMARY KEY,
    nombre          NVARCHAR(80)    NOT NULL UNIQUE,
    descripcion     NVARCHAR(255)   NULL
);
GO

CREATE TABLE producto (
    idProducto      INT IDENTITY(1,1) PRIMARY KEY,
    idCategoria     INT             NOT NULL,
    nombre          NVARCHAR(120)   NOT NULL,
    descripcion     NVARCHAR(255)   NULL,
    precio          DECIMAL(10,2)   NOT NULL CHECK (precio >= 0),
    imagenUrl       NVARCHAR(255)   NULL,
    disponible      BIT             NOT NULL DEFAULT 1,
    CONSTRAINT fk_producto_categoria
        FOREIGN KEY (idCategoria) REFERENCES categoria(idCategoria)
        ON UPDATE CASCADE ON DELETE NO ACTION
);
GO

CREATE TABLE pedido (
    idPedido            INT IDENTITY(1,1) PRIMARY KEY,
    idCliente           INT             NOT NULL,
    idUsuarioEmpleado   INT             NULL,
    idUsuarioRepostero  INT             NULL,
    fechaPedido         DATETIME2       NOT NULL DEFAULT SYSDATETIME(),
    fechaEntrega        DATETIME2       NULL,
    estado              VARCHAR(20)     NOT NULL DEFAULT 'PENDIENTE',
    total               DECIMAL(10,2)   NOT NULL DEFAULT 0,
    CONSTRAINT ck_pedido_estado CHECK (estado IN ('PENDIENTE','EN_PREPARACION','LISTO','ENTREGADO','CANCELADO')),
    CONSTRAINT fk_pedido_cliente
        FOREIGN KEY (idCliente) REFERENCES cliente(idCliente)
        ON UPDATE NO ACTION ON DELETE NO ACTION,
    CONSTRAINT fk_pedido_empleado
        FOREIGN KEY (idUsuarioEmpleado) REFERENCES usuario(idUsuario)
        ON UPDATE NO ACTION ON DELETE NO ACTION,
    CONSTRAINT fk_pedido_repostero
        FOREIGN KEY (idUsuarioRepostero) REFERENCES usuario(idUsuario)
        ON UPDATE NO ACTION ON DELETE NO ACTION
);
GO

CREATE TABLE detalle_pedido (
    idDetalle       INT IDENTITY(1,1) PRIMARY KEY,
    idPedido        INT             NOT NULL,
    idProducto      INT             NOT NULL,
    cantidad        INT             NOT NULL CHECK (cantidad > 0),
    precioUnitario  DECIMAL(10,2)   NOT NULL CHECK (precioUnitario >= 0),
    subtotal        AS (cantidad * precioUnitario) PERSISTED,
    CONSTRAINT fk_detalle_pedido
        FOREIGN KEY (idPedido) REFERENCES pedido(idPedido)
        ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT fk_detalle_producto
        FOREIGN KEY (idProducto) REFERENCES producto(idProducto)
        ON UPDATE CASCADE ON DELETE NO ACTION
);
GO

CREATE TABLE pago (
    idPago          INT IDENTITY(1,1) PRIMARY KEY,
    idPedido        INT             NOT NULL UNIQUE,
    monto           DECIMAL(10,2)   NOT NULL CHECK (monto >= 0),
    fechaPago       DATETIME2       NOT NULL DEFAULT SYSDATETIME(),
    metodoPago      VARCHAR(20)     NOT NULL,
    estado          VARCHAR(20)     NOT NULL DEFAULT 'PENDIENTE',
    CONSTRAINT ck_pago_metodo CHECK (metodoPago IN ('EFECTIVO','TARJETA','TRANSFERENCIA')),
    CONSTRAINT ck_pago_estado CHECK (estado IN ('PENDIENTE','COMPLETADO','RECHAZADO')),
    CONSTRAINT fk_pago_pedido
        FOREIGN KEY (idPedido) REFERENCES pedido(idPedido)
        ON UPDATE CASCADE ON DELETE CASCADE
);
GO

-- Índices de apoyo para consultas frecuentes
CREATE INDEX idx_pedido_cliente ON pedido(idCliente);
CREATE INDEX idx_pedido_estado ON pedido(estado);
CREATE INDEX idx_producto_categoria ON producto(idCategoria);
CREATE INDEX idx_detalle_pedido ON detalle_pedido(idPedido);
GO

-- ============================================================
-- 2. AUDITORÍA
-- ============================================================
-- Tabla central de auditoría: registra cambios en las tablas
-- sensibles (producto, pedido, pago) con el detalle antes/después.

CREATE TABLE auditoria (
    idAuditoria     BIGINT IDENTITY(1,1) PRIMARY KEY,
    tablaAfectada   NVARCHAR(50)    NOT NULL,
    idRegistro      INT             NOT NULL,
    accion          VARCHAR(10)     NOT NULL,
    datosAnteriores NVARCHAR(MAX)   NULL,
    datosNuevos     NVARCHAR(MAX)   NULL,
    fechaHora       DATETIME2       NOT NULL DEFAULT SYSDATETIME(),
    usuarioBD       NVARCHAR(100)   NOT NULL DEFAULT SYSTEM_USER,
    CONSTRAINT ck_auditoria_accion CHECK (accion IN ('INSERT','UPDATE','DELETE')),
    CONSTRAINT ck_auditoria_json_ant CHECK (datosAnteriores IS NULL OR ISJSON(datosAnteriores) = 1),
    CONSTRAINT ck_auditoria_json_nue CHECK (datosNuevos IS NULL OR ISJSON(datosNuevos) = 1)
);
GO

-- --- Auditoría de PRODUCTO ---
CREATE TRIGGER trg_producto_after_insert
ON producto
AFTER INSERT
AS
BEGIN
    SET NOCOUNT ON;
    INSERT INTO auditoria (tablaAfectada, idRegistro, accion, datosNuevos)
    SELECT 'producto', i.idProducto, 'INSERT',
        (SELECT i.nombre AS nombre, i.precio AS precio, i.disponible AS disponible
         FOR JSON PATH, WITHOUT_ARRAY_WRAPPER)
    FROM inserted i;
END;
GO

CREATE TRIGGER trg_producto_after_update
ON producto
AFTER UPDATE
AS
BEGIN
    SET NOCOUNT ON;
    INSERT INTO auditoria (tablaAfectada, idRegistro, accion, datosAnteriores, datosNuevos)
    SELECT 'producto', i.idProducto, 'UPDATE',
        (SELECT d.nombre AS nombre, d.precio AS precio, d.disponible AS disponible
         FOR JSON PATH, WITHOUT_ARRAY_WRAPPER),
        (SELECT i.nombre AS nombre, i.precio AS precio, i.disponible AS disponible
         FOR JSON PATH, WITHOUT_ARRAY_WRAPPER)
    FROM inserted i
    JOIN deleted d ON d.idProducto = i.idProducto;
END;
GO

CREATE TRIGGER trg_producto_after_delete
ON producto
AFTER DELETE
AS
BEGIN
    SET NOCOUNT ON;
    INSERT INTO auditoria (tablaAfectada, idRegistro, accion, datosAnteriores)
    SELECT 'producto', d.idProducto, 'DELETE',
        (SELECT d.nombre AS nombre, d.precio AS precio
         FOR JSON PATH, WITHOUT_ARRAY_WRAPPER)
    FROM deleted d;
END;
GO

-- --- Auditoría de PEDIDO (cambios de estado) ---
CREATE TRIGGER trg_pedido_after_update
ON pedido
AFTER UPDATE
AS
BEGIN
    SET NOCOUNT ON;
    INSERT INTO auditoria (tablaAfectada, idRegistro, accion, datosAnteriores, datosNuevos)
    SELECT 'pedido', i.idPedido, 'UPDATE',
        (SELECT d.estado AS estado, d.total AS total FOR JSON PATH, WITHOUT_ARRAY_WRAPPER),
        (SELECT i.estado AS estado, i.total AS total FOR JSON PATH, WITHOUT_ARRAY_WRAPPER)
    FROM inserted i
    JOIN deleted d ON d.idPedido = i.idPedido
    WHERE ISNULL(d.estado, '') <> ISNULL(i.estado, '');
END;
GO

-- --- Auditoría de PAGO ---
CREATE TRIGGER trg_pago_after_insert
ON pago
AFTER INSERT
AS
BEGIN
    SET NOCOUNT ON;
    INSERT INTO auditoria (tablaAfectada, idRegistro, accion, datosNuevos)
    SELECT 'pago', i.idPago, 'INSERT',
        (SELECT i.idPedido AS idPedido, i.monto AS monto, i.estado AS estado
         FOR JSON PATH, WITHOUT_ARRAY_WRAPPER)
    FROM inserted i;
END;
GO

CREATE TRIGGER trg_pago_after_update
ON pago
AFTER UPDATE
AS
BEGIN
    SET NOCOUNT ON;
    INSERT INTO auditoria (tablaAfectada, idRegistro, accion, datosAnteriores, datosNuevos)
    SELECT 'pago', i.idPago, 'UPDATE',
        (SELECT d.estado AS estado FOR JSON PATH, WITHOUT_ARRAY_WRAPPER),
        (SELECT i.estado AS estado FOR JSON PATH, WITHOUT_ARRAY_WRAPPER)
    FROM inserted i
    JOIN deleted d ON d.idPago = i.idPago;
END;
GO

-- --- Recalcular total del pedido automáticamente ---
CREATE TRIGGER trg_detalle_after_insert
ON detalle_pedido
AFTER INSERT
AS
BEGIN
    SET NOCOUNT ON;
    UPDATE pe
    SET total = ISNULL(sub.totalCalculado, 0)
    FROM pedido pe
    JOIN (SELECT DISTINCT idPedido FROM inserted) i ON i.idPedido = pe.idPedido
    OUTER APPLY (
        SELECT SUM(subtotal) AS totalCalculado
        FROM detalle_pedido
        WHERE idPedido = pe.idPedido
    ) sub;
END;
GO

CREATE TRIGGER trg_detalle_after_update
ON detalle_pedido
AFTER UPDATE
AS
BEGIN
    SET NOCOUNT ON;
    ;WITH pedidosAfectados AS (
        SELECT idPedido FROM inserted
        UNION
        SELECT idPedido FROM deleted
    )
    UPDATE pe
    SET total = ISNULL(sub.totalCalculado, 0)
    FROM pedido pe
    JOIN pedidosAfectados pa ON pa.idPedido = pe.idPedido
    OUTER APPLY (
        SELECT SUM(subtotal) AS totalCalculado
        FROM detalle_pedido
        WHERE idPedido = pe.idPedido
    ) sub;
END;
GO

CREATE TRIGGER trg_detalle_after_delete
ON detalle_pedido
AFTER DELETE
AS
BEGIN
    SET NOCOUNT ON;
    UPDATE pe
    SET total = ISNULL(sub.totalCalculado, 0)
    FROM pedido pe
    JOIN (SELECT DISTINCT idPedido FROM deleted) d ON d.idPedido = pe.idPedido
    OUTER APPLY (
        SELECT SUM(subtotal) AS totalCalculado
        FROM detalle_pedido
        WHERE idPedido = pe.idPedido
    ) sub;
END;
GO

-- ============================================================
-- 3. VISTAS
-- ============================================================

-- Catálogo público: solo productos disponibles, con su categoría
CREATE VIEW vista_catalogo AS
SELECT
    p.idProducto,
    p.nombre        AS producto,
    p.descripcion,
    p.precio,
    p.imagenUrl,
    c.nombre        AS categoria
FROM producto p
JOIN categoria c ON c.idCategoria = p.idCategoria
WHERE p.disponible = 1;
GO

-- Pedidos con datos del cliente y del personal asignado
CREATE VIEW vista_pedidos AS
SELECT
    pe.idPedido,
    cl.nombre           AS cliente,
    cl.telefono         AS telefonoCliente,
    emp.nombre          AS empleadoAsignado,
    rep.nombre          AS reposteroAsignado,
    pe.fechaPedido,
    pe.fechaEntrega,
    pe.estado,
    pe.total
FROM pedido pe
JOIN cliente cl ON cl.idCliente = pe.idCliente
LEFT JOIN usuario emp ON emp.idUsuario = pe.idUsuarioEmpleado
LEFT JOIN usuario rep ON rep.idUsuario = pe.idUsuarioRepostero;
GO

-- Detalle de productos por pedido
CREATE VIEW vista_detalle_pedido AS
SELECT
    dp.idPedido,
    pr.nombre           AS producto,
    dp.cantidad,
    dp.precioUnitario,
    dp.subtotal
FROM detalle_pedido dp
JOIN producto pr ON pr.idProducto = dp.idProducto;
GO

-- Ventas: pedidos con pago completado
CREATE VIEW vista_ventas AS
SELECT
    pe.idPedido,
    cl.nombre           AS cliente,
    pe.fechaPedido,
    pa.fechaPago,
    pa.metodoPago,
    pe.total
FROM pedido pe
JOIN cliente cl ON cl.idCliente = pe.idCliente
JOIN pago pa ON pa.idPedido = pe.idPedido
WHERE pa.estado = 'COMPLETADO';
GO

-- Producto más vendido (cantidad total vendida por producto)
CREATE VIEW vista_productos_mas_vendidos AS
SELECT
    pr.idProducto,
    pr.nombre           AS producto,
    SUM(dp.cantidad)    AS unidadesVendidas,
    SUM(dp.subtotal)    AS totalGenerado
FROM detalle_pedido dp
JOIN producto pr ON pr.idProducto = dp.idProducto
JOIN pedido pe ON pe.idPedido = dp.idPedido
WHERE pe.estado <> 'CANCELADO'
GROUP BY pr.idProducto, pr.nombre;
GO

-- ============================================================
-- 4. PROCEDIMIENTOS ALMACENADOS
-- ============================================================

-- Crea un pedido vacío para un cliente y devuelve el id generado
CREATE PROCEDURE sp_crear_pedido
    @idCliente INT,
    @idPedido  INT OUTPUT
AS
BEGIN
    SET NOCOUNT ON;
    INSERT INTO pedido (idCliente, estado, total)
    VALUES (@idCliente, 'PENDIENTE', 0);
    SET @idPedido = SCOPE_IDENTITY();
END;
GO

-- Agrega un producto a un pedido existente (toma el precio vigente del producto)
CREATE PROCEDURE sp_agregar_producto_pedido
    @idPedido   INT,
    @idProducto INT,
    @cantidad   INT
AS
BEGIN
    SET NOCOUNT ON;
    DECLARE @precio DECIMAL(10,2);

    IF @cantidad <= 0
    BEGIN
        THROW 50000, 'La cantidad debe ser mayor a 0', 1;
    END

    SELECT @precio = precio
    FROM producto
    WHERE idProducto = @idProducto AND disponible = 1;

    IF @precio IS NULL
    BEGIN
        THROW 50001, 'Producto no disponible o inexistente', 1;
    END

    INSERT INTO detalle_pedido (idPedido, idProducto, cantidad, precioUnitario)
    VALUES (@idPedido, @idProducto, @cantidad, @precio);
    -- El total del pedido se recalcula automáticamente vía trigger
END;
GO

-- Actualiza el estado de un pedido (y opcionalmente asigna repostero)
CREATE PROCEDURE sp_actualizar_estado_pedido
    @idPedido           INT,
    @nuevoEstado        VARCHAR(20),
    @idUsuarioRepostero INT = NULL
AS
BEGIN
    SET NOCOUNT ON;
    UPDATE pedido
    SET estado = @nuevoEstado,
        idUsuarioRepostero = COALESCE(@idUsuarioRepostero, idUsuarioRepostero),
        fechaEntrega = CASE WHEN @nuevoEstado = 'ENTREGADO' THEN SYSDATETIME() ELSE fechaEntrega END
    WHERE idPedido = @idPedido;
END;
GO

-- Registra el pago de un pedido
CREATE PROCEDURE sp_registrar_pago
    @idPedido   INT,
    @monto      DECIMAL(10,2),
    @metodoPago VARCHAR(20)
AS
BEGIN
    SET NOCOUNT ON;
    DECLARE @total      DECIMAL(10,2);
    DECLARE @estadoPago VARCHAR(20);

    SELECT @total = total FROM pedido WHERE idPedido = @idPedido;

    IF @total IS NULL
    BEGIN
        THROW 50002, 'El pedido no existe', 1;
    END

    SET @estadoPago = CASE WHEN @monto >= @total THEN 'COMPLETADO' ELSE 'PENDIENTE' END;

    MERGE pago AS target
    USING (SELECT @idPedido AS idPedido) AS src
        ON target.idPedido = src.idPedido
    WHEN MATCHED THEN
        UPDATE SET monto = @monto,
                   metodoPago = @metodoPago,
                   estado = @estadoPago,
                   fechaPago = SYSDATETIME()
    WHEN NOT MATCHED THEN
        INSERT (idPedido, monto, metodoPago, estado)
        VALUES (@idPedido, @monto, @metodoPago, @estadoPago);
END;
GO

-- Consulta de ventas en un rango de fechas (para el administrador)
CREATE PROCEDURE sp_consultar_ventas_periodo
    @fechaInicio DATE,
    @fechaFin    DATE
AS
BEGIN
    SET NOCOUNT ON;
    SELECT *
    FROM vista_ventas
    WHERE CAST(fechaPago AS DATE) BETWEEN @fechaInicio AND @fechaFin
    ORDER BY fechaPago;
END;
GO

-- ============================================================
-- FIN DEL SCRIPT
-- ============================================================
