package com.hasnain.usermoduleupdated

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.hasnain.usermoduleupdated.databinding.ActivityBmiBinding

class BmiActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBmiBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBmiBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setting up the dropdown menu for weight units
        val weightUnits = arrayOf("Kgs", "Pounds")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, weightUnits)
        binding.spinnerWeightUnit.adapter = adapter
        binding.imgBack.setOnClickListener {
            startActivity(Intent(this,MainActivity::class.java))

        }

        binding.btnCalculate.setOnClickListener {
            val weight = binding.etWeight.text.toString().toFloatOrNull()
            val feet = binding.etFeet.text.toString().toIntOrNull()
            val inches = binding.etInches.text.toString().toIntOrNull()

            if (weight == null || feet == null || inches == null) {
                Toast.makeText(this, "Please enter valid inputs", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Convert height to total inches and then to meters
            val totalInches = (feet * 12) + inches
            val heightInMeters: Float  = (totalInches * 0.0254).toFloat() // Conversion factor from inches to meters

            // Check weight unit and convert to kg if needed
            val selectedUnit = binding.spinnerWeightUnit.selectedItem.toString()
            val weightInKgs: Float = if (selectedUnit == "Pounds") {
                (weight * 0.453592).toFloat() // Conversion factor from pounds to kg, cast to Float
            } else {
                weight.toFloat()
            }

            // Calculate BMI
            val bmi: Float = weightInKgs / (heightInMeters * heightInMeters)

            // Display result
           displayBmiResult(bmi)
        }
    }
    private fun displayBmiResult(bmi: Float) {
        val bmiCategory: String
        val feedback: String

        when {
            bmi < 18.5 -> {
                bmiCategory = "Underweight"
                feedback = "You are underweight. Consider consulting with a nutritionist for a balanced diet plan."
            }
            bmi in 18.5..24.9 -> {
                bmiCategory = "Normal weight"
                feedback = "You have a normal weight. Maintain a balanced diet and regular exercise to stay healthy."
            }
            bmi in 25.0..29.9 -> {
                bmiCategory = "Overweight"
                feedback = "You are overweight. Regular physical activity and a healthy diet can help manage your weight."
            }
            else -> {
                bmiCategory = "Obese"
                feedback = "You are in the obese range. It's recommended to seek medical advice to improve your health."
            }
        }

        binding.tvResult.text = String.format("Your BMI is: %.2f\nCategory: %s\n%s", bmi, bmiCategory, feedback)
    }
}