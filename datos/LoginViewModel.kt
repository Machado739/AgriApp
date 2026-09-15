package com.example.agrimexapp.datos // Ajusta a tu paquete

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

// Cambiamos ViewModel() por AndroidViewModel(application) para poder acceder al contexto del teléfono
class LoginViewModel(application: Application) : AndroidViewModel(application) {

    val mensajeEstado = mutableStateOf("")
    private val sessionManager = SessionManager(application)

    fun validarCredenciales(usuario: String, pass: String, onSuccess: (Boolean, Int) -> Unit) {
        // 1. Validar que no dejen los campos vacíos
        if (usuario.isBlank() || pass.isBlank()) {
            mensajeEstado.value = "Por favor ingresa usuario y contraseña"
            return
        }

        viewModelScope.launch {
            try {
                mensajeEstado.value = "Validando en servidor..."

                // 2. Intento de conexión al servidor Python
                val respuesta = RetrofitClient.api.iniciarSesion(LoginRequest(usuario, pass))
                val idDepto = respuesta.id_departamento ?: 1

                // 3. Si el servidor aprueba las credenciales, guardamos la sesión oficial
                sessionManager.guardarSesion(idDepto, respuesta.rol_admin)

                onSuccess(respuesta.rol_admin, idDepto)

            } catch (e: Exception) {
                // 4. MODO OFFLINE: Solo si el servidor falla, evaluamos el acceso local
                val sesionGuardada = sessionManager.leerSesionOffline()

                // IMPORTANTE: Validamos que el usuario que intenta entrar offline
                // sea exactamente el mismo que guardó la sesión legítimamente,
                // o al menos que exista una sesión activa verificada.
                if (sesionGuardada != null) {
                    mensajeEstado.value = "Modo Offline: Accediendo..."
                    val esAdmin = sesionGuardada["esAdmin"] as Boolean
                    val idDepto = sesionGuardada["idDepto"] as Int

                    onSuccess(esAdmin, idDepto)
                } else {
                    // Si intentan entrar por primera vez sin internet o con datos inventados
                    mensajeEstado.value = "Acceso denegado: Credenciales incorrectas o sin internet."
                }
            }
        }
    }
}