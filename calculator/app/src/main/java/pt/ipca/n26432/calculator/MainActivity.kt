package pt.ipca.n26432.calculator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import pt.ipca.n26432.calculator.ui.theme.CalculadoraTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CalculadoraTheme {
               Scaffold(modifier = Modifier.fillMaxSize()) { paddingValues ->
                   CalculatorScreen(modifier = Modifier.padding(paddingValues))
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

@Composable
fun CalculatorScreen(modifier: Modifier = Modifier) {
    var display by remember { mutableStateOf("0") }
    var lastButtonWasEquals by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = display,
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .align(Alignment.End)
        )
        Spacer(modifier = Modifier.weight(1f)) // Spacer to push buttons to the bottom
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val buttons = listOf(
                listOf("C"),
                listOf("7", "8", "9", "/"),
                listOf("4", "5", "6", "*"),
                listOf("1", "2", "3", "-"),
                listOf("0", ".", "=", "+")
            )
            buttons.forEach { row ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    row.forEach { label ->
                        Button(
                            onClick = { onButtonClick(label, display, lastButtonWasEquals) { newDisplay, wasEquals ->
                                display = newDisplay
                                lastButtonWasEquals = wasEquals
                            } },
                            modifier = Modifier.weight(1f).height(64.dp),
                        ) {
                            Text(text = label)
                        }
                    }
                }
            }
            // Spacer to push buttons to the bottom
            //Spacer(modifier = Modifier.weight(1f))

        }
    }
}


fun onButtonClick(label: String, display: String, lastButtonWasEquals: Boolean, updateDisplay: (String, Boolean) -> Unit) {
    when (label) {
        "C" -> updateDisplay("0", false)
        "=" -> {
            try {
                updateDisplay(eval(display).toString(), true)
            } catch (e: Exception) {
                updateDisplay("Error", false)
            }
        }
        else -> {
            if (lastButtonWasEquals && label.all { it.isDigit() }) {
                updateDisplay(label, false)
            } else {
                if (display == "0" || lastButtonWasEquals) {
                    updateDisplay(label, false)
                } else {
                    updateDisplay(display + label, false)
                }
            }
        }
    }
}

fun eval(display: String): Any {
    return when {
        display.contains("+") -> {
            val numbers = display.split("+")
            numbers[0].toDouble() + numbers[1].toDouble()
        }
        display.contains("-") -> {
            val numbers = display.split("-")
            numbers[0].toDouble() - numbers[1].toDouble()
        }
        display.contains("*") -> {
            val numbers = display.split("*")
            numbers[0].toDouble() * numbers[1].toDouble()
        }
        display.contains("/") -> {
            val numbers = display.split("/")
            numbers[0].toDouble() / numbers[1].toDouble()
        }
        else -> display.toDouble()
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    CalculadoraTheme {
        Greeting("Android")
    }
}