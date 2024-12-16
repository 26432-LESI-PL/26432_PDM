package pt.ipca.n26432.news.screen

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil3.compose.rememberAsyncImagePainter
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import pt.ipca.n26432.news.BottomNavItem
import pt.ipca.n26432.news.R

@Composable
fun LoggedInScreen(innerPadding: PaddingValues, onLogout: () -> Unit, navController: NavController) {
    val auth = remember { FirebaseAuth.getInstance() }
    val user = auth.currentUser

    LaunchedEffect(user) {
        if (user == null) {
            navController.navigate(BottomNavItem.Login.route) {
                popUpTo(BottomNavItem.Me.route) {
                    inclusive = true
                }
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(innerPadding),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (user != null) {
            Text(text = "Hello, ${user.displayName}")
            user.photoUrl?.let { photoUrl ->
                Image(
                    painter = rememberAsyncImagePainter(model = photoUrl),
                    contentDescription = "User Photo",
                    modifier = Modifier.size(100.dp)
                )
            }
            Button(onClick = {
                auth.signOut()
                onLogout()
            }) {
                Text(text = "Sign Out")
            }

            Button(onClick = {
                navController.navigate(BottomNavItem.Favorite.route)
            }) {
                Text(text = "Favorites")
            }
        }
    }
}

@Composable
fun LogInScreen(innerPadding: PaddingValues, onLoginSuccess: () -> Unit) {
    val context = LocalContext.current
    val auth = remember { FirebaseAuth.getInstance() }
    val googleSignInClient = remember { getGoogleSignInClient(context) }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        val account = task.result
        if (account != null) {
            firebaseAuthWithGoogle(auth, account, onLoginSuccess)
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(innerPadding),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = {
            val signInIntent = googleSignInClient.signInIntent
            launcher.launch(signInIntent)
        }) {
            Text(text = "Login with Google")
        }
    }
}

private fun getGoogleSignInClient(context: android.content.Context): GoogleSignInClient {
    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(context.getString(R.string.your_web_client_id))
        .requestEmail()
        .build()
    return GoogleSignIn.getClient(context, gso)
}

private fun firebaseAuthWithGoogle(auth: FirebaseAuth, account: GoogleSignInAccount, onLoginSuccess: () -> Unit) {
    val credential = GoogleAuthProvider.getCredential(account.idToken, null)
    auth.signInWithCredential(credential)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d("LogInScreen", "signInWithCredential:success")
                onLoginSuccess()
            } else {
                Log.w("LogInScreen", "signInWithCredential:failure", task.exception)
            }
        }
}