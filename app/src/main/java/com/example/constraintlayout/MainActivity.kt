package com.example.constraintlayout

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.*

class MainActivity : AppCompatActivity() , TextWatcher, TextToSpeech.OnInitListener {
    private lateinit var tts: TextToSpeech
    private lateinit var edtConta: EditText
    private lateinit var qtdPessoas: EditText
    private var ttsSucess: Boolean = false;
    private var resultants = 0.0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        edtConta = findViewById<EditText>(R.id.edtConta)
        edtConta.addTextChangedListener(this)
        qtdPessoas = findViewById(R.id.qtdPessoas)
        qtdPessoas.addTextChangedListener(this)

        // Initialize TTS engine
        tts = TextToSpeech(this, this)

        // Botão Compartilhar
        val btCompartilhar = findViewById<FloatingActionButton>(R.id.btCompartilhar)
        btCompartilhar.setOnClickListener{
            compartilharResultado()
        }



    }
    
    fun calcularResultants() {
        var resultadoTextView = findViewById<TextView>(R.id.resultado)
        var pessoas = qtdPessoas.text.toString().toIntOrNull()
        var conta = edtConta.text.toString().toDoubleOrNull()

        if (pessoas != null && conta != null) {
            if (pessoas > 0 && conta >0){
                resultants = conta / pessoas
                Log.d("oresultado", "Cálculo feito: resultants=$resultants")
                resultadoTextView.text = "R$ %.2f".format(resultants)
            } else {
                resultadoTextView.text = "Erro: Divisão por zero."
            }
        }
    }
    
    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
       Log.d("PDM24","Antes de mudar")

    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        calcularResultants()
        Log.d("PDM24","Mudando")
    }

    override fun afterTextChanged(s: Editable?) {
        Log.d ("PDM24", "Depois de mudar")

        val valor: Double

        if(s.toString().length>0) {
             valor = s.toString().toDouble()
            Log.d("PDM24", "v: " + valor)
        //    edtConta.setText("9")
        }
    }

    fun clickFalar(v: View) {
        if (tts.isSpeaking) {
            tts.stop()
        }
        if (ttsSucess) {
            tts.language = Locale("pt", "BR")
            Log.d("PDM23", tts.language.toString())
            val texto = String.format(Locale.getDefault(), "%.2f", resultants)
            tts.speak(texto.toString(), TextToSpeech.QUEUE_FLUSH, null, null)
        }
    }

    private fun compartilharResultado() = if (resultants!= 0.0) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, "O valor final calculado foi: $resultants")
        }
        startActivity(Intent.createChooser(intent, "Compartilhar via"))
    } else {
        Toast.makeText(this, "Nenhum valor para compartilhar.", Toast.LENGTH_SHORT).show()
    }
    
    override fun onDestroy() {
            // Release TTS engine resources
            tts.stop()
            tts.shutdown()
            super.onDestroy()
        }

    override fun onInit(status: Int) {
            if (status == TextToSpeech.SUCCESS) {
                // TTS engine is initialized successfully
                tts.language = Locale.getDefault()
                ttsSucess=true
                Log.d("PDM23","Sucesso na Inicialização")
            } else {
                // TTS engine failed to initialize
                Log.e("PDM23", "Failed to initialize TTS engine.")
                ttsSucess=false
            }
        }


}

