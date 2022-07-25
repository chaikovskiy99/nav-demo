package com.learn.nav_demo

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.learn.nav_demo.ui.theme.NavdemoTheme

/**
 * This example demonstrates two concepts, namely :-
 * 1. Receive intents with any mimeType or intent action
 * from any application using compose navigation library.
 * 2. Pass arguments or optional args with default values
 * in a type safe way between composable functions
 * in your application.
 */

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NavdemoTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background) {
                    val navController = rememberNavController()
                    NavHost(navController, Screens.FirstScreen.route) {
                        composable(Screens.FirstScreen.route) {
                            FirstScreen(navController = navController)
                        }
                        composable(
                            // nav argument can be specified at call site( navController.navigate(){ navArgument(){}}
                            // or like this in the target composable
                            Screens.SecondScreen.withOptionalArg("message"), listOf(
                                navArgument("message") {
                                    defaultValue = "hola"
                                }
                            ),
                            listOf(navDeepLink {
                                action = Intent.ACTION_SEND
                                mimeType = "text/*"
                            })
                        ) {
                            val intent: Intent? =
                                it.arguments?.getParcelable(NavController.KEY_DEEP_LINK_INTENT)
                            intent?.let { incomingIntent ->
                                if (incomingIntent.action == Intent.ACTION_SEND) {
                                    SecondScreen(intent.getStringExtra(Intent.EXTRA_TEXT))
                                } else {
                                    SecondScreen(it.arguments?.getString("message"))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FirstScreen(navController: NavController) {
    Column(Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally) {
        Text("first")
        var text by remember { mutableStateOf("") }

        BasicTextField(value = text, onValueChange = { text = it })
        Spacer(modifier = Modifier.size(20.dp))
        Button(onClick = {
            // NOTE: here we are not passing the optional parameter, do not specify optional param string
            // if you want to pass a default value to the target component. NEVER DO THIS
            // navController.navigate("second?optionalParam=")
            // If you are not passing param, just use base uri ("second") here
            // CALL NAVIGATE IN THIS WAY
            navController.navigate(Screens.SecondScreen.route)
        }) {
            Text("Go to second")
        }
    }
}

@Composable
fun SecondScreen(textReceivedFromFirst: String? = null) {
    Column(Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally) {
        if (textReceivedFromFirst != null) {
            Text(textReceivedFromFirst)
        }
        Text("second")
    }
}

sealed class Screens(val route: String) {
    object FirstScreen : Screens("first")
    object SecondScreen : Screens("second")

    fun withArg(arg: String) = "$route/$arg"
    fun withOptionalArg(arg: String) = "$route?$arg={$arg}"
}