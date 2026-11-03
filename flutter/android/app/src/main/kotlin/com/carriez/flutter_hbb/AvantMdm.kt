// Avant Soporte (fork de RustDesk, AGPL-3.0). Añadido por Avant IT, 2026.
//
// Lee los «Application settings» que pone el MDM de Avant (managed configurations de Android) y se
// los pasa al núcleo de RustDesk: `contrasena` (contraseña permanente estándar, sin aviso para
// aceptar), `nombre` (nombre del equipo en la agenda) y `grupo` (local). Se aplica al arrancar el
// servicio y cada vez que el MDM los cambia.

package com.carriez.flutter_hbb

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.RestrictionsManager
import android.os.Build
import android.util.Log
import ffi.FFI

object AvantMdm {
    private const val TAG = "AvantMdm"
    private var receptor: BroadcastReceiver? = null

    fun aplicar(contexto: Context) {
        try {
            val rm = contexto.getSystemService(Context.RESTRICTIONS_SERVICE) as RestrictionsManager
            val datos = rm.applicationRestrictions
            // El agente del MDM tipa los valores por su forma (un número llega como entero)
            fun texto(clave: String): String = datos.get(clave)?.toString() ?: ""
            val contrasena = texto("contrasena")
            val nombre = texto("nombre")
            val grupo = texto("grupo")
            Log.i(TAG, "Ajustes del MDM: contraseña=${if (contrasena.isEmpty()) "no" else "sí"}, nombre=$nombre, grupo=$grupo")
            FFI.avantAplicarMdm(contrasena, nombre, grupo)
        } catch (e: Throwable) {
            Log.w(TAG, "No se han podido aplicar los ajustes del MDM", e)
        }
    }

    fun escucharCambios(contexto: Context) {
        if (receptor != null) return
        val r = object : BroadcastReceiver() {
            override fun onReceive(c: Context, i: Intent) {
                aplicar(c)
            }
        }
        val filtro = IntentFilter(Intent.ACTION_APPLICATION_RESTRICTIONS_CHANGED)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Difusión del sistema: no necesita exportarse
            contexto.registerReceiver(r, filtro, Context.RECEIVER_NOT_EXPORTED)
        } else {
            contexto.registerReceiver(r, filtro)
        }
        receptor = r
    }
}
