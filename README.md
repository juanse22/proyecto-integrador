# Sistema de Gestión para Salón de Belleza AREMI

## Descripción del Proyecto
Sistema completo de gestión empresarial (ERP) desarrollado en Java para administrar las operaciones del Salón de Belleza Aremi. Incluye gestión de clientes, citas, servicios, inventario, pagos, facturación y nómina.

**Versión:** 2.0
**Lenguaje:** Java
**Framework GUI:** Swing
**Base de Datos:** MySQL

---

## Características Principales

### 1. Sistema de Autenticación y Roles
- Login seguro con contraseñas hasheadas (SHA-256)
- Dos roles de usuario: **Administrador** y **Empleado**
- Control de acceso por permisos según rol
- Gestión de sesiones con `SeguridadManager`

**Archivos:**
- `LoginSystemAremi.java` - Interfaz de login
- `SeguridadManager.java` - Gestión centralizada de seguridad y roles

### 2. Gestión de Clientes
- Registro, edición y eliminación de clientes
- Almacenamiento de datos: nombre, dirección, teléfono
- Búsqueda y visualización de clientes

**Archivos:**
- `Cliente.java` - Modelo de datos
- `InterfazClientes.java` - GUI de gestión

### 3. Agenda de Citas
- Programación de citas con fecha y hora
- Selección de servicios disponibles
- Asociación de citas con clientes

**Archivos:**
- `AgendarCitaGUI.java` - Interfaz de agenda
- `DatabaseConnection.java` - Conexión y guardado de citas

### 4. Gestión de Servicios
- Registro de servicios realizados
- Cálculo automático de comisiones (20%)
- Asignación de empleadas por servicio
- Reportes de ingresos por empleada

**Archivos:**
- `Servicio.java` - Modelo de servicios
- `SalonBelleza.java` - Registro y resumen financiero

### 5. Sistema de Inventario ⭐ NUEVO
- Registro completo de productos: nombre, cantidad, proveedor, costo, precio venta
- **Alertas inteligentes:**
  - Stock bajo (cuando cantidad ≤ stock mínimo)
  - Productos próximos a vencer (dentro de 30 días)
  - Productos vencidos
- Categorización de productos (Tintes, Esmaltes, Cremas, etc.)
- Cálculo automático de márgenes de ganancia
- Búsqueda y filtrado en tiempo real
- **Acceso:** Solo administradores

**Archivos:**
- `ItemInventario.java` - Modelo con lógica de alertas
- `InventarioGUI.java` - Interfaz completa de gestión

**Funcionalidades:**
```java
// Ejemplo de uso del modelo
ItemInventario item = new ItemInventario(...);
if (item.tieneStockBajo()) {
    // Alerta de stock
}
if (item.estaProximoAVencer()) {
    // Alerta de vencimiento
}
double margen = item.calcularMargenGanancia(); // Calcula margen %
```

### 6. Sistema de Nómina ⭐ NUEVO
- Cálculo automático de comisiones por empleada (20% de servicios)
- Configuración de salario base y bonificaciones
- Reportes mensuales detallados
- Resumen financiero general
- Generación de reportes por periodo
- **Acceso:** Solo administradores

**Archivos:**
- `NominaGUI.java` - Interfaz de gestión de nómina

**Características:**
- Filtrado por mes y año
- Visualización de:
  - Servicios realizados por empleada
  - Ingresos generados
  - Comisión calculada (20%)
  - Salario base
  - Bonificaciones
  - Total a pagar
- Exportación de reportes a consola

### 7. Sistema de Pagos y Facturación
- Múltiples métodos de pago: efectivo, tarjeta, transferencia
- Generación de facturas en PDF con logo
- Búsqueda de facturas por nombre o teléfono
- Historial de transacciones

**Archivos:**
- `Pago.java` - Procesamiento de pagos
- `Facturas.java` - Generación de facturas PDF
- `PagoDetailsDialog.java` - Detalles de pago

### 8. Gestión de Gastos
- Registro de gastos operacionales
- Seguimiento por concepto y fecha
- Cálculo de beneficio neto (ingresos - gastos)

**Archivos:**
- `Gasto.java` - Modelo de gastos

---

## Pruebas Unitarias ⭐ NUEVO

El proyecto incluye pruebas JUnit para validar la lógica de negocio:

### ItemInventarioTest.java
Valida:
- ✓ Detección de stock bajo
- ✓ Detección de productos próximos a vencer
- ✓ Detección de productos vencidos
- ✓ Cálculo de margen de ganancia
- ✓ Cálculo de valor total de stock
- ✓ Estados del producto (NORMAL, STOCK BAJO, PRÓXIMO A VENCER, VENCIDO)

### SeguridadManagerTest.java
Valida:
- ✓ Generación de hash SHA-256
- ✓ Autenticación de administradores
- ✓ Autenticación de empleados
- ✓ Sistema de permisos por rol
- ✓ Cierre de sesión

**Ejecutar pruebas:**
```bash
# Requiere JUnit 4 en el classpath
java -cp .:junit-4.12.jar:hamcrest-core-1.3.jar org.junit.runner.JUnitCore ItemInventarioTest
java -cp .:junit-4.12.jar:hamcrest-core-1.3.jar org.junit.runner.JUnitCore SeguridadManagerTest
```

---

## Estructura del Proyecto

```
Proyecto-Integrador/
├── src/
│   ├── MainApplication.java          # Aplicación principal con menú integrado
│   ├── LoginSystemAremi.java         # Sistema de login
│   ├── SeguridadManager.java         # ⭐ Gestión de seguridad y roles
│   │
│   ├── # Modelos
│   ├── Cliente.java                  # Modelo de cliente
│   ├── Usuario.java                  # Modelo de usuario
│   ├── Servicio.java                 # Modelo de servicio
│   ├── Gasto.java                    # Modelo de gasto
│   ├── ItemInventario.java           # ⭐ Modelo de inventario con alertas
│   │
│   ├── # Interfaces Gráficas
│   ├── AgendarCitaGUI.java           # GUI de agenda de citas
│   ├── InterfazClientes.java         # GUI de gestión de clientes
│   ├── SalonBelleza.java             # GUI de servicios y gastos
│   ├── InventarioGUI.java            # ⭐ GUI de gestión de inventario
│   ├── NominaGUI.java                # ⭐ GUI de gestión de nómina
│   ├── Pago.java                     # GUI de pagos
│   ├── PagoDetailsDialog.java        # Diálogo de detalles de pago
│   │
│   ├── # Utilidades
│   ├── DatabaseConnection.java       # Conexión a base de datos
│   ├── Facturas.java                 # Generación de facturas PDF
│   │
│   └── # Pruebas
│       ├── ItemInventarioTest.java   # ⭐ Tests de inventario
│       └── SeguridadManagerTest.java # ⭐ Tests de seguridad
│
├── lib/                              # Librerías externas (MySQL, iText)
├── out/                              # Archivos compilados
└── README.md                         # Este archivo
```

---

## Configuración de Base de Datos

### Requisitos
- MySQL Server 8.0 o superior
- Base de datos: `salon_aremi`

### Tablas Necesarias

```sql
-- Base de datos principal
CREATE DATABASE IF NOT EXISTS salon_aremi;
USE salon_aremi;

-- Tabla de usuarios
CREATE TABLE usuarios (
    id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,  -- Hash SHA-256
    nombre_completo VARCHAR(100) NOT NULL,
    rol VARCHAR(20) NOT NULL         -- 'administrador' o 'empleado'
);

-- Tabla de servicios
CREATE TABLE servicios (
    id INT PRIMARY KEY AUTO_INCREMENT,
    tipo VARCHAR(100) NOT NULL,
    precio DECIMAL(10,2) NOT NULL,
    empleada VARCHAR(100) NOT NULL,
    fecha VARCHAR(20) NOT NULL
);

-- Tabla de gastos
CREATE TABLE gastos (
    id INT PRIMARY KEY AUTO_INCREMENT,
    concepto VARCHAR(200) NOT NULL,
    monto DECIMAL(10,2) NOT NULL,
    fecha VARCHAR(20) NOT NULL
);

-- Tabla de citas
CREATE TABLE citas (
    id INT PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(100) NOT NULL,
    telefono VARCHAR(20),
    servicio VARCHAR(100),
    fecha DATE,
    hora VARCHAR(10)
);

-- Tabla de inventario ⭐ NUEVA
CREATE TABLE inventario (
    id INT PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(100) NOT NULL,
    cantidad INT NOT NULL,
    proveedor VARCHAR(100),
    fecha_vencimiento DATE,
    costo DECIMAL(10,2) NOT NULL,
    precio_venta DECIMAL(10,2) NOT NULL,
    stock_minimo INT DEFAULT 5,
    categoria VARCHAR(50),
    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabla de facturas
CREATE TABLE facturas (
    id_factura VARCHAR(20) PRIMARY KEY,
    usuario VARCHAR(100),
    telefono VARCHAR(20),
    valor_venta DECIMAL(10,2),
    nombre_vendedor VARCHAR(100),
    fecha_venta DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- Tabla de clientes
CREATE DATABASE IF NOT EXISTS gestion_clientes;
USE gestion_clientes;

CREATE TABLE clientes (
    id INT PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(100) NOT NULL,
    direccion VARCHAR(200),
    telefono VARCHAR(20)
);
```

### Usuarios de Prueba

```sql
USE salon_aremi;

-- Insertar usuario administrador
-- Usuario: fernanda, Contraseña: fernanda123
INSERT INTO usuarios (username, password, nombre_completo, rol) VALUES
('fernanda', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 'Fernanda Administrador', 'administrador');

-- Insertar usuario empleado
-- Usuario: empleada1, Contraseña: emp123
INSERT INTO usuarios (username, password, nombre_completo, rol) VALUES
('empleada1', '6ca13d52ca70c883e0f0bb101e425a89e8624de51db2d2392593af6a84118090', 'María González', 'empleado');
```

---

## Instalación y Ejecución

### Requisitos Previos
1. JDK 8 o superior
2. MySQL Server
3. Librerías externas:
   - MySQL Connector/J (JDBC)
   - iText PDF (para generación de facturas)
   - JUnit 4 (para pruebas)

### Pasos de Instalación

1. **Clonar el repositorio**
```bash
git clone <url-del-repositorio>
cd Proyecto-Integrador
```

2. **Configurar la base de datos**
   - Crear las bases de datos y tablas (ver sección anterior)
   - Actualizar credenciales en los archivos si es necesario:
     - Usuario: `root`
     - Contraseña: `missgarro234`
     - Host: `127.0.0.1:3306`

3. **Compilar el proyecto**
```bash
javac -d out -cp "lib/*" src/*.java
```

4. **Ejecutar la aplicación**
```bash
java -cp "out:lib/*" MainApplication
```

### Credenciales de Acceso

**Administrador:**
- Usuario: `fernanda`
- Contraseña: `fernanda123`
- Permisos: Acceso completo a todas las funcionalidades

**Empleado:**
- Usuario: `empleada1`
- Contraseña: `emp123`
- Permisos: Citas, clientes, servicios, pagos (sin inventario ni nómina)

---

## Control de Acceso por Roles

### Permisos de Administrador
✓ Agendar citas
✓ Gestión de clientes
✓ Registrar servicios
✓ Registrar pagos
✓ Generar facturas
✓ **Gestionar inventario**
✓ **Ver nómina y reportes**
✓ **Gestionar usuarios**

### Permisos de Empleado
✓ Agendar citas
✓ Gestión de clientes
✓ Registrar servicios
✓ Registrar pagos
✓ Generar facturas
❌ Gestionar inventario
❌ Ver nómina y reportes
❌ Gestionar usuarios

---

## Funcionalidades Clave

### Sistema de Alertas del Inventario
El módulo de inventario incluye un sistema inteligente de alertas:

**Alertas de Stock:**
- Se activa cuando `cantidad <= stock_minimo`
- Aparece en la columna "Estado" de la tabla
- Botón especial "Ver Alertas" muestra resumen

**Alertas de Vencimiento:**
- **Próximo a vencer:** Productos que vencen en ≤ 30 días
- **Vencido:** Productos con fecha de vencimiento pasada
- Cálculo automático de días restantes

**Ejemplo de uso:**
```java
ItemInventario tinte = new ItemInventario(
    "Tinte Rubio", 3, "Proveedor XYZ",
    LocalDate.now().plusDays(15),
    50000, 80000, 10, "Tintes"
);

if (tinte.tieneStockBajo()) {
    System.out.println("⚠ Stock bajo! Ordenar más");
}

if (tinte.estaProximoAVencer()) {
    System.out.println("⚠ Producto próximo a vencer");
}

String estado = tinte.getEstado(); // "STOCK BAJO" o "PRÓXIMO A VENCER"
```

### Cálculo de Nómina
El sistema calcula automáticamente:

**Fórmula:**
```
Total a Pagar = Salario Base + Comisión + Bonificación
Comisión = Suma(Precio Servicios) × 20%
```

**Ejemplo:**
- Empleada: María González
- Servicios realizados: 15
- Ingresos generados: $750,000
- Comisión (20%): $150,000
- Salario base: $1,300,000
- Bonificación: $50,000
- **Total a pagar: $1,500,000**

---

## Arquitectura del Sistema

### Patrón de Diseño
El proyecto sigue principios de:
- **MVC** (Modelo-Vista-Controlador) simplificado
- **Separación de responsabilidades**
- **Singleton** para SeguridadManager

### Flujo de Autenticación
```
1. Usuario ingresa credenciales en LoginSystemAremi
2. LoginSystemAremi hashea la contraseña (SHA-256)
3. Consulta base de datos para validar
4. Si es exitoso:
   - Establece sesión en SeguridadManager
   - Abre MainApplication
5. MainApplication verifica permisos para cada acción
```

### Conexión a Base de Datos
Cada módulo maneja su propia conexión pero usando las mismas credenciales:
- Conexiones se cierran automáticamente con try-with-resources
- PreparedStatements para prevenir SQL Injection

---

## Mejoras Implementadas (Versión 2.0)

### ✨ Nuevas Funcionalidades
1. **Sistema de Inventario completo** con alertas inteligentes
2. **Sistema de Nómina** con reportes detallados
3. **Control de acceso por roles** centralizado
4. **Pruebas unitarias** con JUnit
5. **Integración completa** en menú principal

### 🔒 Seguridad
- Hash SHA-256 para contraseñas (apropiado para proyecto académico)
- Sistema de permisos por rol
- Validación de acceso en cada módulo sensible

### 📊 Reportes
- Reportes de nómina por periodo
- Alertas de inventario en tiempo real
- Resumen financiero consolidado

---

## Escalabilidad y Mantenimiento

### Agregar Nuevos Módulos
Para agregar un nuevo módulo:

1. Crear la clase modelo en `src/`
2. Crear la GUI correspondiente
3. Agregar botón en `MainApplication.java`
4. Configurar permisos en `SeguridadManager.java`
5. Actualizar base de datos si es necesario

### Agregar Nuevos Roles
```java
// En SeguridadManager.java
public static final String ROL_SUPERVISOR = "supervisor";

// Actualizar método tienePermiso()
public static boolean tienePermiso(String accion) {
    if (esAdministrador()) return true;
    if (esSupervisor()) {
        // Definir permisos específicos
    }
    // ...
}
```

---

## Tecnologías Utilizadas

| Tecnología | Uso |
|------------|-----|
| Java 8+ | Lenguaje principal |
| Swing | Interfaz gráfica |
| MySQL | Base de datos |
| JDBC | Conexión a BD |
| iText PDF | Generación de facturas |
| JUnit 4 | Pruebas unitarias |
| SHA-256 | Hash de contraseñas |

---

## Autor

**Sistema Aremi v2.0**
Proyecto de Gestión para Salón de Belleza
Desarrollado como proyecto integrador académico

---

## Licencia

Este proyecto es de uso académico.

---

## Soporte

Para reportar problemas o sugerencias:
1. Revisar la documentación en este README
2. Verificar logs en consola
3. Comprobar conexión a base de datos

---

## Notas Importantes

- **Contraseñas:** Se almacenan hasheadas con SHA-256
- **Base de datos:** Configurar antes del primer uso
- **Inventario:** Las alertas se calculan automáticamente
- **Nómina:** Los porcentajes son configurables en la interfaz
- **Roles:** Los permisos están definidos en `SeguridadManager.java`

---

**¡Proyecto completado exitosamente!** 🎉