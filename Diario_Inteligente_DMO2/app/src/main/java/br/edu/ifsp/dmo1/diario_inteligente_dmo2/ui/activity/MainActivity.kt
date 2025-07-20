package br.edu.ifsp.dmo1.diario_inteligente_dmo2.ui.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import br.edu.ifsp.dmo1.diario_inteligente_dmo2.databinding.ActivityMainBinding
import java.util.concurrent.Executor

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo
    private lateinit var executor: Executor

    private var autenticando = false  // controla se está autenticando para evitar múltiplas chamadas

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        executor = ContextCompat.getMainExecutor(this)

        binding.btnTentarNovamente.setOnClickListener {
            binding.btnTentarNovamente.visibility = android.view.View.GONE
            autenticar()
        }

        setupBiometricPrompt()
    }

    override fun onResume() {
        super.onResume()
        if (!autenticando) {
            autenticar()
        }
    }

    private fun autenticar() {
        autenticando = true
        biometricPrompt.authenticate(promptInfo)
    }

    private fun verificarBiometriaEDisparar() {
        val biometricManager = BiometricManager.from(this)
        when (biometricManager.canAuthenticate(
            BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL)) {
            BiometricManager.BIOMETRIC_SUCCESS -> {
                // setupBiometricPrompt() já foi chamado no onCreate
                // biometricPrompt.authenticate(promptInfo) será chamado no onResume via autenticar()
            }
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE ->
                Toast.makeText(this, "Dispositivo sem hardware biométrico", Toast.LENGTH_LONG).show()
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE ->
                Toast.makeText(this, "Hardware biométrico indisponível", Toast.LENGTH_LONG).show()
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED ->
                Toast.makeText(this, "Nenhuma biometria cadastrada. Configure uma no sistema.", Toast.LENGTH_LONG).show()
            else ->
                Toast.makeText(this, "Biometria não suportada", Toast.LENGTH_LONG).show()
        }
    }

    private fun setupBiometricPrompt() {
        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Autenticação biométrica")
            .setSubtitle("Use sua digital ou face para entrar")
            .setNegativeButtonText("Cancelar")
            .build()

        biometricPrompt = BiometricPrompt(this, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)

                    autenticando = false

                    if (errorCode == BiometricPrompt.ERROR_NEGATIVE_BUTTON ||
                        errorCode == BiometricPrompt.ERROR_USER_CANCELED ||
                        errorCode == BiometricPrompt.ERROR_CANCELED) {
                        Toast.makeText(applicationContext, "Autenticação cancelada", Toast.LENGTH_SHORT).show()
                        binding.btnTentarNovamente.visibility = android.view.View.VISIBLE
                    } else {
                        Toast.makeText(applicationContext, "Erro: $errString", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)

                    autenticando = false

                    Toast.makeText(applicationContext, "Autenticação aprovada! Bem-vindo!", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@MainActivity, HomeActivity::class.java)
                    startActivity(intent)
                    finish()
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    Toast.makeText(applicationContext, "Autenticação falhou, tente novamente.", Toast.LENGTH_SHORT).show()
                }
            })
    }
}
