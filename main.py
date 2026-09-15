from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
import pyodbc

app = FastAPI(title="API Agrimex")

# Configuración de conexión a SQL Server
# Ajustamos el string para usar Windows Authentication a tu servidor local
DB_CONNECTION_STRING = (
    "Driver={ODBC Driver 17 for SQL Server};"
    "Server=localhost\\SQLEXPRESS;"
    "Database=AgrimexDB;"
    "Trusted_Connection=yes;"
)

def get_db_connection():
    try:
        conn = pyodbc.connect(DB_CONNECTION_STRING)
        return conn
    except Exception as e:
        print(f"Error al conectar a la BD: {e}")
        return None

# Ruta de prueba para saber si la API funciona
@app.get("/")
def home():
    return {"mensaje": "Bienvenido a la API de Agrimex"}

# Endpoint para obtener todos los equipos de cómputo
@app.get("/api/equipos")
def obtener_equipos():
    conn = get_db_connection()
    if not conn:
        raise HTTPException(status_code=500, detail="Error de conexión a la base de datos")
    
    cursor = conn.cursor()
    # Consulta a la tabla que creamos en el paso 1
    cursor.execute("""
        SELECT e.id_equipo, e.marca, e.modelo, e.numero_serie, e.estado, d.nombre_departamento
        FROM EquiposComputo e
        JOIN Departamentos d ON e.id_departamento = d.id_departamento
    """)
    
    columnas = [column[0] for column in cursor.description]
    equipos = []
    for fila in cursor.fetchall():
         equipos.append(dict(zip(columnas, fila)))
         
    conn.close()
    return equipos


@app.get("/api/equipos/{id_depto}")
def obtener_equipos_depto(id_depto: int):
    conn = get_db_connection()
    if not conn:
            raise HTTPException(status_code=500, detail="Error de conexión a la base de datos")
    cursor = conn.cursor()
    
    cursor.execute(
        "SELECT id_equipo, marca, modelo, numero_serie, estado, id_departamento FROM EquiposComputo WHERE id_departamento = ?",
        (id_depto,)
    )
    equipos = cursor.fetchall()
    
    # Formateamos la respuesta para que Jetpack Compose la entienda
    resultado = []
    for e in equipos:
        resultado.append({
            "id_equipo": e[0], "marca": e[1], "modelo": e[2], 
            "numero_serie": e[3], "estado": e[4], "id_departamento": e[5]
        })
    
    conn.close()    
    return resultado


class LoginRequest(BaseModel):
    nombre: str
    credenciales: str

@app.post("/api/login")
def verificar_acceso(datos: LoginRequest):
    conn = get_db_connection()
    if not conn:
        raise HTTPException(status_code=500, detail="Error de conexión a la base de datos")

    cursor = conn.cursor()
    # Consulta a tu base de datos SQL Server local
    cursor.execute(
        "SELECT id_usuario, rol_admin, id_departamento FROM Usuarios WHERE nombre = ? AND credenciales = ?", 
        (datos.nombre, datos.credenciales)
    )
    usuario = cursor.fetchone()
    
    if usuario:
        return {"id_usuario": usuario[0], "rol_admin": usuario[1], "id_departamento": usuario[2]}
    raise HTTPException(status_code=401, detail="Credenciales incorrectas")
