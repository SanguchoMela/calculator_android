package com.example.mycalculator

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import kotlin.math.*

class MainActivity : AppCompatActivity() {

    private lateinit var resultText: EditText
    private var inputString: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        resultText = findViewById<EditText>(R.id.result)

        val button0: Button = findViewById<Button>(R.id.button0)
        val button1: Button = findViewById<Button>(R.id.button1)
        val button2: Button = findViewById<Button>(R.id.button2)
        val button3: Button = findViewById<Button>(R.id.button3)
        val button4: Button = findViewById<Button>(R.id.button4)
        val button5: Button = findViewById<Button>(R.id.button5)
        val button6: Button = findViewById<Button>(R.id.button6)
        val button7: Button = findViewById<Button>(R.id.button7)
        val button8: Button = findViewById<Button>(R.id.button8)
        val button9: Button = findViewById<Button>(R.id.button9)
        val buttonDot: Button = findViewById<Button>(R.id.button_dot)
        val buttonAdd: Button = findViewById<Button>(R.id.button_add)
        val buttonSub: Button = findViewById<Button>(R.id.button_sus)
        val buttonMul: Button = findViewById<Button>(R.id.button_mul)
        val buttonDiv: Button = findViewById<Button>(R.id.button_div)
        val buttonSin: Button = findViewById<Button>(R.id.button_sen)
        val buttonCos: Button = findViewById<Button>(R.id.button_cos)
        val buttonTan: Button = findViewById<Button>(R.id.button_tan)
        val buttonEqual: Button = findViewById<Button>(R.id.button_equal)
        val buttonClear: Button = findViewById<Button>(R.id.button_clear)
        val buttonCloseParen: Button = findViewById(R.id.button_cor)

        val numberButtons = listOf(button0, button1, button2, button3, button4, button5, button6, button7, button8, button9)
        for (button in numberButtons) {
            button.setOnClickListener { onNumberClick((it as Button).text.toString()) }
        }

        buttonDot.setOnClickListener { onDotClick() }
        buttonCloseParen.setOnClickListener { onParentClick() }
        buttonAdd.setOnClickListener { onOperatorClick("+") }
        buttonSub.setOnClickListener { onOperatorClick("-") }
        buttonMul.setOnClickListener { onOperatorClick("*") }
        buttonDiv.setOnClickListener { onOperatorClick("/") }
        buttonSin.setOnClickListener { onFunctionClick("sin") }
        buttonCos.setOnClickListener { onFunctionClick("cos") }
        buttonTan.setOnClickListener { onFunctionClick("tan") }
        buttonEqual.setOnClickListener { onEqualClick() }
        buttonClear.setOnClickListener { onClearClick() }
    }

    private fun onNumberClick(number: String) {
        inputString += number
        resultText.setText(inputString)
    }

    private fun onDotClick() {
        if (inputString.isNotEmpty() && !inputString.endsWith(".")) {
            inputString += "."
            resultText.setText(inputString)
        }
    }

    private fun onParentClick() {
        if (inputString.isNotEmpty()) {
            inputString += ")"
            resultText.setText(inputString)
        }
    }

    private fun onOperatorClick(operator: String) {
        if (inputString.isNotEmpty() && !inputString.endsWith(operator)) {
            inputString += operator
            resultText.setText(inputString)
        }
    }

    private fun onFunctionClick(function: String) {
        // Allow functions to be the first entry and handle parentheses
        if (inputString.isEmpty() || inputString.endsWith("(") || inputString.matches(Regex(".*[\\d)]+$"))) {
            inputString += "$function("
            resultText.setText(inputString)
        }
    }

    private fun onEqualClick() {
        try {
            val result = eval(inputString)
            resultText.setText(result.toString())
            inputString = result.toString()
        } catch (e: Exception) {
            resultText.setText("Error")
            inputString = ""
        }
    }

    private fun onClearClick() {
        inputString = ""
        resultText.setText("")
    }

    private fun eval(expression: String): Double {
        val functionRegex = Regex("(sin|cos|tan)\\(([^)]+)\\)")
        var resultString = expression
        var matchResult = functionRegex.find(resultString)

        while (matchResult != null) {
            val functionName = matchResult.groupValues[1]
            val functionArgument = matchResult.groupValues[2].toDouble()
            val functionResult = when (functionName) {
                "sin" -> sin(Math.toRadians(functionArgument))
                "cos" -> cos(Math.toRadians(functionArgument))
                "tan" -> tan(Math.toRadians(functionArgument))
                else -> 0.0
            }
            resultString = resultString.replace(matchResult.value, functionResult.toString())
            matchResult = functionRegex.find(resultString)
        }

        return try {
            // Evaluate the remaining expression
            evalBasic(resultString)
        } catch (e: Exception) {
            throw ArithmeticException("Error in expression")
        }
    }

    private fun evalBasic(expression: String): Double {
        // Replace any unnecessary operators or invalid patterns
        val cleanedExpression = expression.replace(Regex("([+*/])([+*/])"), "$1")
            .replace(Regex("([+*/])$"), "$10")

        var result = 0.0
        var currentNumber = 0.0
        var operator = '+'
        var i = 0

        while (i < cleanedExpression.length) {
            val char = cleanedExpression[i]

            if (char.isDigit() || char == '.') {
                val numberStart = i
                while (i < cleanedExpression.length && (cleanedExpression[i].isDigit() || cleanedExpression[i] == '.')) {
                    i++
                }
                val numberString = cleanedExpression.substring(numberStart, i)
                currentNumber = numberString.toDouble()
                i--
            } else if (char in listOf('+', '-', '*', '/')) {
                when (operator) {
                    '+' -> result += currentNumber
                    '-' -> result -= currentNumber
                    '*' -> result *= currentNumber
                    '/' -> result /= currentNumber
                }
                currentNumber = 0.0
                operator = char
            }

            i++
        }

        // Apply the last operator
        when (operator) {
            '+' -> result += currentNumber
            '-' -> result -= currentNumber
            '*' -> result *= currentNumber
            '/' -> result /= currentNumber
        }

        return result
    }
}
