package com.example.agrimexapp.datos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import androidx.compose.runtime.mutableStateOf

class LoginViewModel : ViewModel() {
    val mensajeEstado = mutableStateOf("")

    fun validarCredenciales(usuario: String, pass: String, onSuccess: (Boolean, Int?) -> Unit) {
        viewModelScope.launch {
            try {
                mensajeEstado.value = "Validando..."
                val respuesta = RetrofitClient.api.iniciarSesion(LoginRequest(usuario, pass))
                onSuccess(respuesta.rol_admin, respuesta.id_departamento)
            } catch (e: Exception) {
                mensajeEstado.value = "Error: Credenciales incorrectas"
            }
        }
    }
}