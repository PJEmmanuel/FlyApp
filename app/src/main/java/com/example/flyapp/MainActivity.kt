package com.example.flyapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.flyapp.ui.theme.FlyAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /*GlobalScope.launch {
             Log.d("Database", "Intentando acceder a la base de datos...")
             try {
                 val data = FlyDatabase.getDatabase(applicationContext).flyDAO().getAllA()
                 Log.d("Database", "Datos obtenidos: ${data.size}") // Si getAllA() devuelve una lista
             } catch (e: Exception) {
                 Log.e("Database", "Error al acceder a la base de datos", e)
             }
         }*/
        enableEdgeToEdge()
        setContent {
            FlyAppTheme {
                FlyApp()
            }
        }
    }
}

