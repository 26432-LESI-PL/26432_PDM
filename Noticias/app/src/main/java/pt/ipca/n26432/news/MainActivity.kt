package pt.ipca.n26432.news

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import pt.ipca.n26432.news.screen.FavoriteScreen
import pt.ipca.n26432.news.screen.LogInScreen
import pt.ipca.n26432.news.screen.LoggedInScreen
import pt.ipca.n26432.news.screen.MainScreen
import pt.ipca.n26432.news.screen.SearchNewsScreen
import pt.ipca.n26432.news.ui.theme.NoticiasTheme



class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        enableEdgeToEdge()
        setContent {
            NoticiasTheme {
                val navController = rememberNavController()
                val auth = remember { FirebaseAuth.getInstance() }
                val isLoggedIn = remember { mutableStateOf(auth.currentUser != null) }

                Scaffold(modifier = Modifier.fillMaxSize(), bottomBar = {
                    BottomNavigation(navController)
                }) { innerPadding ->
                    NavHost(navController = navController, startDestination = BottomNavItem.Home.route) {
                        composable(BottomNavItem.Login.route) {
                            LogInScreen(innerPadding = innerPadding, onLoginSuccess = {
                                isLoggedIn.value = true
                                Log.d("MainActivity", "Logged in")
                                navController.navigate(BottomNavItem.LoggedIn.route) {
                                    popUpTo(BottomNavItem.Login.route) { inclusive = true }
                                }
                            })
                        }
                        composable(BottomNavItem.LoggedIn.route) {
                            LoggedInScreen(innerPadding = innerPadding, onLogout = {
                                isLoggedIn.value = false
                                Log.d("MainActivity", "Logged out")
                                navController.navigate(BottomNavItem.Login.route) {
                                    popUpTo(BottomNavItem.LoggedIn.route) { inclusive = true }
                                }
                            }, navController = navController)
                        }
                        composable(BottomNavItem.Home.route) { MainScreen(innerPadding) }
                        composable(BottomNavItem.Search.route) { SearchNewsScreen(innerPadding) }
                        composable(BottomNavItem.Me.route) { LoggedInScreen(innerPadding, onLogout = {
                            isLoggedIn.value = false
                            navController.navigate(BottomNavItem.Login.route) {
                                popUpTo(BottomNavItem.Me.route) { inclusive = true }
                            }
                        }, navController = navController) }
                        composable(BottomNavItem.Favorite.route) { FavoriteScreen(innerPadding) }
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    NoticiasTheme {
        Greeting("Android")
    }
}