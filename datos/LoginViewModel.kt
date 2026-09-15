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
        viewModelScope.launch {
            try {
                mensajeEstado.value = "Validando en servidor..."

                // 1. Intento de conexión al servidor Python
                val respuesta = RetrofitClient.api.iniciarSesion(LoginRequest(usuario, pass))
                val idDepto = respuesta.id_departamento ?: 1

                // 2. Si es exitoso, ¡GUARDAMOS LA SESIÓN para el modo offline!
                sessionManager.guardarSesion(idDepto, respuesta.rol_admin)

                onSuccess(respuesta.rol_admin, idDepto)

            } catch (e: Exception) {
                // 3. MODO OFFLINE: El servidor no responde (no hay internet)
                val sesionGuardada = sessionManager.leerSesionOffline()

                if (sesionGuardada != null) {
                    mensajeEstado.value = "Modo Offline: Accediendo con sesión guardada..."
                    val esAdmin = sesionGuardada["esAdmin"] as Boolean
                    val idDepto = sesionGuardada["idDepto"] as Int

                    onSuccess(esAdmin, idDepto)
                } else {
                    mensajeEstado.value = "Error: Sin conexión a internet y sin sesión previa. Conéctate a la red para el primer ingreso."
                }
            }
        }
    }
}