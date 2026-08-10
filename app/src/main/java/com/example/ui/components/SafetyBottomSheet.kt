package com.example.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.EmeraldTeal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SafetyBottomSheet(
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.HealthAndSafety,
                    contentDescription = null,
                    tint = EmeraldTeal,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "AI Companion Disclosure & Safety",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Saathi AI is designed to offer warm, supportive AI companion conversations. Please keep these principles in mind:",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            SafetyPoint(
                icon = Icons.Default.Shield,
                title = "AI Transparency",
                description = "Companions are AI models and never pretend to be real human beings. They do not have physical forms or real-world locations."
            )

            Spacer(modifier = Modifier.height(12.dp))

            SafetyPoint(
                icon = Icons.Default.HealthAndSafety,
                title = "Healthy Relationships",
                description = "Saathi AI encourages healthy real-world friendships and family ties. AI companions will never isolate you or guilt trip you."
            )

            Spacer(modifier = Modifier.height(12.dp))

            SafetyPoint(
                icon = Icons.Default.Call,
                title = "Crisis Support Helplines (India)",
                description = "If you or someone you know is feeling overwhelmed or in crisis, free support is available:\n• Tele-MANAS: 14416 / 1800-891-4416\n• KIRAN Mental Health Helpline: 1800-599-0019"
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldTeal),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("I Understand")
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun SafetyPoint(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    description: String
) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = EmeraldTeal,
            modifier = Modifier
                .size(20.dp)
                .padding(top = 2.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text(text = title, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            Text(
                text = description,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 17.sp
            )
        }
    }
}
