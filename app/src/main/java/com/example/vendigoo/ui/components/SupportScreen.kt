import android.content.Intent
import android.net.Uri
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SupportScreen(navController: NavController) {
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Dəstək") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Geri")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier
            .padding(padding)
            .padding(16.dp)) {

            Text(
                text = "🤝 Dəstək Komandası Buradadır!",
                style = MaterialTheme.typography.headlineSmall
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Hər hansı texniki problem, təklif və ya sualınız olduqda bizimlə əlaqə saxlayın. Məqsədimiz sizə daha yaxşı xidmət göstərməkdir.",
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text("📧 E-poçt: support@vendigoo.com")

            Text(
                text = "📞 Telefon: +994 070 611 48 81",
                modifier = Modifier.clickable {
                    val intent = Intent(Intent.ACTION_DIAL).apply {
                        data = Uri.parse("tel:+9940706114881")
                    }
                    context.startActivity(intent)
                },
                style = MaterialTheme.typography.bodyLarge
            )
            Text("Nömrənin üzərinə klikləyərək zəng edə bilərsiniz.")
            Text("🕒 Dəstək saatları: Hər gün, 09:00 – 18:00")

            Spacer(modifier = Modifier.height(16.dp))
            Divider()
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Bizə yazmaqdan çəkinməyin — fikriniz bizim üçün önəmlidir!",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
