package com.iberosolutions.shifterup.ui.kpi

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.iberosolutions.shifterup.ui.theme.BlueActive

@Composable
fun KpiScreen() {
    Column(
        modifier = Modifier.fillMaxSize().padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Tu Progreso", fontSize = 28.sp, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(30.dp))

        // Tarjeta de Resumen Grande
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(BlueActive)
                .padding(20.dp)
        ) {
            Column {
                Text("Entrenamientos este mes", color = Color.White.copy(alpha = 0.8f))
                Text("12", color = Color.White, fontSize = 48.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Dos tarjetas pequeñas
        Row(modifier = Modifier.fillMaxWidth()) {
            EstadisticaCard(titulo = "Peso Total", valor = "5.2 Ton", modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.width(16.dp))
            EstadisticaCard(titulo = "Récord", valor = "100 kg", modifier = Modifier.weight(1f))
        }
    }
}

@Composable
fun EstadisticaCard(titulo: String, valor: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .height(120.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color.LightGray.copy(alpha = 0.3f))
            .padding(16.dp)
    ) {
        Column {
            Text(titulo, color = Color.Gray, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(valor, color = Color.Black, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        }
    }
}