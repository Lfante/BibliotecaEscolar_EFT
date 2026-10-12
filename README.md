# Biblioteca Escolar - Evaluación Final Transversal

Aplicación de escritorio Java para gestionar una biblioteca escolar.

## Funcionalidades

- Autenticación con roles `BIBLIOTECARIO` y `ESTUDIANTE`.
- CRUD de usuarios.
- CRUD de estudiantes.
- CRUD de categorías.
- CRUD de libros.
- Registro de préstamos y devoluciones.
- Cálculo automático de vencimiento a 7 días.
- Detección de préstamos atrasados.
- Reportes:
  - libros más prestados;
  - historial por estudiante;
  - libros actualmente en préstamo.
- Persistencia MySQL mediante JDBC.
- Patrones MVC, DAO y Singleton.
- Préstamos/devoluciones en segundo plano con `SwingWorker`.
- Sincronización de operaciones que modifican stock.

## Estructura

```text
BibliotecaEscolar_EFT/
├── database/
│   ├── PRY2203_EFT_S9_Script_crea_tablas_biblioteca.sql
│   └── PRY2203_EFT_S9_Script_poblado_tablas_biblioteca.sql
├── dist/
│   ├── BibliotecaEscolar.jar
│   ├── run.bat
│   └── lib/
│       └── mysql-connector-j-8.4.0.jar
├── src/main/java/
│   ├── config/
│   ├── controller/
│   ├── dao/
│   ├── main/
│   ├── modelo/
│   ├── service/
│   └── vista/
├── .gitignore
├── pom.xml
└── README.md
```

## Base de datos

Ejecutar en este orden:

```text
database/PRY2203_EFT_S9_Script_crea_tablas_biblioteca.sql
database/PRY2203_EFT_S9_Script_poblado_tablas_biblioteca.sql
```

La aplicación usa por defecto:

```text
jdbc:mysql://localhost:3306/biblioteca
```

Las credenciales de MySQL se configuran mediante variables de entorno:

```text
BIBLIOTECA_DB_USER=<usuario_mysql>
BIBLIOTECA_DB_PASSWORD=<password_mysql>
BIBLIOTECA_DB_URL=<url_opcional>
```

## Usuarios de demostración

Los siguientes registros son creados por el script de poblamiento:

```text
Bibliotecario:
antonia@correo.cl
clave123

Estudiante:
carlos@correo.cl
clave123
```

Estas credenciales corresponden solamente a los datos locales de demostración.

## Ejecución desde IntelliJ IDEA

1. Abrir el proyecto como proyecto Maven.
2. Configurar las variables de entorno en `Run > Edit Configurations`.
3. Ejecutar `main.Main`.

## Generar y ejecutar el JAR

Desde el panel **Maven** de IntelliJ, ejecutar el ciclo:

```text
Lifecycle > package
```

Ese proceso genera:

```text
dist/BibliotecaEscolar.jar
dist/lib/mysql-connector-j-8.4.0.jar
```

Después de configurar las variables de entorno de MySQL, se puede ejecutar:

```text
dist/run.bat
```

o:

```bash
java -jar dist/BibliotecaEscolar.jar
```
