package br.edu.ifsp.dmo1.diario_inteligente_dmo2.ui.activity

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import br.edu.ifsp.dmo1.diario_inteligente_dmo2.databinding.ActivityNovaEntradaBinding
import br.edu.ifsp.dmo1.diario_inteligente_dmo2.ui.viewmodel.NovaEntradaViewModel

class NovaEntradaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNovaEntradaBinding
    private val viewModel: NovaEntradaViewModel by viewModels()
    private var humorSelecionado: String? = null

    private lateinit var cameraResultLauncher: ActivityResultLauncher<Intent>
    private var fotoBitmap: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNovaEntradaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupListeners()
        observeViewModel()
        setupCameraLauncher()
    }

    private fun setupCameraLauncher() {
        cameraResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK) {
                val bitmap = result.data?.extras?.get("fotoBitmap") as? Bitmap
                if (bitmap != null) {
                    fotoBitmap = bitmap
                    Toast.makeText(this, "Foto capturada com sucesso!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun abrirCamera() {
        val intent = Intent(this, CameraXActivity::class.java)
        cameraResultLauncher.launch(intent)
    }

    private fun setupListeners() {
        binding.btnEmojiFeliz.setOnClickListener { humorSelecionado = "feliz" }
        binding.btnEmojiTriste.setOnClickListener { humorSelecionado = "triste" }
        binding.btnEmojiNeutro.setOnClickListener { humorSelecionado = "neutro" }
        binding.btnEmojiBravo.setOnClickListener { humorSelecionado = "bravo" }

        binding.btnAddFotoCamera.setOnClickListener {
            abrirCamera()
        }

        binding.btnSalvarEntrada.setOnClickListener {
            val mensagem = binding.etMensagemDia.text.toString()

            if (mensagem.isBlank()) {
                Toast.makeText(this, "Digite uma mensagem para salvar", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (humorSelecionado == null) {
                Toast.makeText(this, "Selecione um humor", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val fotoUriStr = ""

            viewModel.salvarEntrada(mensagem, humorSelecionado!!, fotoUriStr)
        }
    }

    private fun observeViewModel() {
        viewModel.entradaSalva.observe(this) { salvo ->
            if (salvo) {
                Toast.makeText(this, "Entrada salva com sucesso!", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

}
