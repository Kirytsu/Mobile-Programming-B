package com.example.simplecalculator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.simplecalculator.ui.theme.SimpleCalculatorTheme
import java.util.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SimpleCalculatorTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    CalculatorScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun CalculatorScreen(modifier: Modifier = Modifier) {
    var expression by remember { mutableStateOf("") }
    var result by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        // Display Area
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.End
        ) {
            if (result.isNotEmpty()) {
                Text(
                    text = expression,
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.End,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            
            Text(
                text = if (result.isNotEmpty()) result else expression.ifEmpty { "0" },
                style = MaterialTheme.typography.displayLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.End,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Buttons Grid
        val buttons = listOf(
            listOf("C", "(", ")", "/"),
            listOf("7", "8", "9", "*"),
            listOf("4", "5", "6", "-"),
            listOf("1", "2", "3", "+"),
            listOf("0", ".", "DEL", "=")
        )

        buttons.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                row.forEach { label ->
                    CalculatorButton(
                        label = label,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            when (label) {
                                "C" -> { expression = ""; result = "" }
                                "DEL" -> {
                                    if (result.isNotEmpty()) { 
                                        expression = ""; result = "" 
                                    }
                                    else if (expression.isNotEmpty()) {
                                        expression = expression.dropLast(1)
                                    }
                                }
                                "=" -> if (expression.isNotEmpty()) result = evaluate(expression)
                                else -> {
                                    if (result.isNotEmpty()) {
                                        expression = if (label in "+-*/") result + label else label
                                        result = ""
                                    } else {
                                        expression += label
                                    }
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun CalculatorButton(label: String, modifier: Modifier, onClick: () -> Unit) {
    val isOp = label in "+-*/="
    Button(
        onClick = onClick,
        modifier = modifier.aspectRatio(1f),
        shape = CircleShape,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isOp) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
            contentColor = if (isOp) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
        ),
        contentPadding = PaddingValues(0.dp)
    ) {
        Text(text = label, fontSize = 22.sp, fontWeight = FontWeight.Bold)
    }
}

fun evaluate(expr: String): String = try {
    val tokens = Regex("""[0-9.]+|[+\-*/()]""").findAll(expr).map { it.value }
    val values = Stack<Double>()
    val ops = Stack<String>()
    val prec = mapOf("+" to 1, "-" to 1, "*" to 2, "/" to 2)

    fun apply() {
        val b = values.pop(); val a = values.pop()
        values.push(when (ops.pop()) {
            "+" -> a + b; "-" -> a - b; "*" -> a * b; "/" -> a / b else -> 0.0
        })
    }

    tokens.forEach { t ->
        when {
            t == "(" -> ops.push(t)
            t == ")" -> { while (ops.peek() != "(") apply(); ops.pop() }
            t in prec -> {
                while (ops.isNotEmpty() && ops.peek() != "(" && (prec[ops.peek()] ?: 0) >= prec[t]!!) apply()
                ops.push(t)
            }
            else -> values.push(t.toDouble())
        }
    }
    while (ops.isNotEmpty()) apply()
    val res = values.pop()
    if (res % 1 == 0.0) res.toLong().toString() else "%.4f".format(Locale.US, res).trimEnd('0').trimEnd('.')
} catch (e: Exception) { "Error" }

@Preview(showBackground = true)
@Composable
fun DefaultPreview() { SimpleCalculatorTheme { CalculatorScreen() } }
