# 🍽️ Sabor Gourmet - Sistema de Gestión de Restaurante

Sistema de gestión para restaurantes desarrollado con Spring Boot, que permite administrar clientes, mesas y su asignación de manera eficiente.

## 📋 Características

### Gestión de Clientes
- ✅ Registrar, editar y eliminar clientes
- ✅ Búsqueda avanzada por nombre, apellido o DNI
- ✅ Activar/desactivar clientes
- ✅ Vista detallada de cada cliente
- ✅ Dashboard con estadísticas en tiempo real

### Gestión de Mesas
- ✅ Crear y configurar mesas (número, capacidad)
- ✅ Asignar clientes a mesas disponibles
- ✅ Control de estados: disponible, ocupada, reservada, mantenimiento
- ✅ Ocupar y liberar mesas
- ✅ Dashboard con disponibilidad en tiempo real

## 🛠️ Tecnologías Utilizadas

- **Backend:** Spring Boot 3.5.7
- **Base de datos:** MySQL / H2 (en memoria)
- **Frontend:** Thymeleaf, Bootstrap 5.3.3, Bootstrap Icons
- **Seguridad:** Spring Security (deshabilitado para desarrollo)
- **ORM:** JPA/Hibernate
- **Gestión de dependencias:** Maven

## 📦 Requisitos Previos

- Java 17 o superior
- Maven 3.6+
- MySQL 8.0+ (opcional, se puede usar H2)
- IDE recomendado: IntelliJ IDEA o Eclipse

## 🚀 Instalación y Configuración

### 1. Clonar el repositorio

```bash
git clone https://github.com/tu-usuario/sabor-gourmet.git
cd sabor-gourmet
```

### 2. Configurar la Base de Datos

Usar MySQL**

1. Crea la base de datos:
```sql
CREATE DATABASE sabor_gourmet;
```

2. Edita `src/main/resources/application.properties`:

```properties
# Configuración MySQL
spring.datasource.url=jdbc:mysql://localhost:3306/sabor_gourmet?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=tu_contraseña
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

spring.jpa.hibernate.ddl-auto=update
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect
```

### 3. Compilar el proyecto

```bash
./mvnw clean install
```

O en Windows:
```bash
mvnw.cmd clean install
```

### 4. Ejecutar la aplicación

```bash
./mvnw spring-boot:run
```

O en Windows:
```bash
mvnw.cmd spring-boot:run
```

La aplicación estará disponible en: **http://localhost:8080**

## 📱 Uso de la Aplicación

### Página de Inicio
Accede a `http://localhost:8080` para ver el dashboard principal con acceso a:
- Gestión de Clientes
- Gestión de Mesas

### Flujo de Trabajo Típico

1. **Registrar Clientes**
   - Ve a "Gestión de Clientes" → "Nuevo Cliente"
   - Completa el formulario con DNI, nombres, apellidos, etc.
   - El DNI debe ser único y tener 8 dígitos

2. **Crear Mesas**
   - Ve a "Gestión de Mesas" → "Nueva Mesa"
   - Define número de mesa, capacidad y estado inicial

3. **Asignar Cliente a Mesa**
   - En la lista de mesas, busca una mesa "Disponible"
   - Haz clic en el botón "Asignar Cliente" (icono de persona +)
   - Selecciona el cliente y número de personas
   - La mesa cambiará automáticamente a "Ocupada"

4. **Liberar Mesa**
   - Cuando el cliente termine, haz clic en "Liberar Mesa"
   - La mesa volverá al estado "Disponible"

## 🗂️ Estructura del Proyecto

```
sabor-gourmet/
├── src/
│   ├── main/
│   │   ├── java/com/sabor/gourmet/
│   │   │   ├── controller/          # Controladores
│   │   │   │   ├── ClienteController.java
│   │   │   │   ├── MesaController.java
│   │   │   │   └── HomeController.java
│   │   │   ├── model/                # Entidades
│   │   │   │   ├── Cliente.java
│   │   │   │   └── Mesa.java
│   │   │   ├── repository/           # Repositorios JPA
│   │   │   │   ├── ClienteRepository.java
│   │   │   │   └── MesaRepository.java
│   │   │   ├── config/               # Configuraciones
│   │   │   │   └── SecurityConfig.java
│   │   │   └── SaborgourmetdemoApplication.java
│   │   └── resources/
│   │       ├── templates/            # Vistas Thymeleaf
│   │       │   ├── clientes/
│   │       │   │   ├── lista.html
│   │       │   │   ├── form.html
│   │       │   │   └── detalle.html
│   │       │   ├── mesas/
│   │       │   │   ├── lista.html
│   │       │   │   ├── form.html
│   │       │   │   └── asignar.html
│   │       │   └── index.html
│   │       └── application.properties
│   └── test/
├── pom.xml
└── README.md
```


### Dashboard Principal
Acceso rápido a todos los módulos del sistema.

### Gestión de Clientes
- Lista con búsqueda y filtros
- Formulario de registro
- Vista de perfil detallado

### Gestión de Mesas
- Dashboard con estadísticas
- Asignación de clientes
- Control de disponibilidad

## 🔧 Configuración Adicional

### Cambiar Puerto del Servidor

Edita `application.properties`:
```properties
server.port=8081
```

### Habilitar Logs Detallados

```properties
logging.level.org.springframework.web=DEBUG
logging.level.com.sabor.gourmet=DEBUG
```

### Deshabilitar Spring Security (Ya configurado)

```properties
spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration
```

##  Solución de Problemas

### Error de Conexión a MySQL
- Verifica que MySQL esté ejecutándose
- Confirma usuario y contraseña en `application.properties`
- Asegúrate de que la base de datos existe

### Error 404 en las páginas
- Verifica que los archivos HTML estén en `src/main/resources/templates/`
- Reinicia la aplicación

### Error al guardar clientes/mesas
- Revisa los logs en la consola
- Verifica que los campos obligatorios estén completos
- Confirma que el DNI no esté duplicado

##  Endpoints Principales

| Ruta | Descripción |
|------|-------------|
| `/` | Página de inicio |
| `/clientes` | Lista de clientes |
| `/clientes/nuevo` | Formulario nuevo cliente |
| `/clientes/editar/{id}` | Editar cliente |
| `/clientes/ver/{id}` | Detalle del cliente |
| `/mesas` | Lista de mesas |
| `/mesas/nueva` | Formulario nueva mesa |
| `/mesas/asignar/{id}` | Asignar cliente a mesa |
| `/mesas/liberar/{id}` | Liberar mesa |




Desarrollado con  usando Spring Boot

