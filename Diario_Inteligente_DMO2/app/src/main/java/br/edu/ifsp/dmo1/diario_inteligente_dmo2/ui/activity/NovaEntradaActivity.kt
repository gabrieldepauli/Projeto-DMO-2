package br.edu.ifsp.dmo1.diario_inteligente_dmo2.ui.activity

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import br.edu.ifsp.dmo1.diario_inteligente_dmo2.App
import br.edu.ifsp.dmo1.diario_inteligente_dmo2.databinding.ActivityNovaEntradaBinding
import br.edu.ifsp.dmo1.diario_inteligente_dmo2.ui.viewmodel.NovaEntradaViewModel
import androidx.exifinterface.media.ExifInterface
import android.graphics.Matrix
import android.speech.RecognizerIntent
import java.io.*

class NovaEntradaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNovaEntradaBinding
    private val viewModel: NovaEntradaViewModel by viewModels()
    private var humorSelecionado: String? = null

    private lateinit var cameraResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var galleryResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var speechRecognizerLauncher: ActivityResultLauncher<Intent>

    private var uriSelecionado: Uri? = null
    private var fotoBitmap: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNovaEntradaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener { finish() }

        setupCameraLauncher()
        setupGalleryLauncher()
        setupSpeechRecognizer()
        setupListeners()
        observeViewModel()
    }

    private fun corrigirRotacaoImagem(inputStream: InputStream, bitmapOriginal: Bitmap): Bitmap {
        val exif = ExifInterface(inputStream)
        val orientacao = exif.getAttributeInt(
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_NORMAL
        )
        val matrix = Matrix()
        when (orientacao) {
            ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
            ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
            ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
            else -> return bitmapOriginal
        }
        return Bitmap.createBitmap(bitmapOriginal, 0, 0, bitmapOriginal.width, bitmapOriginal.height, matrix, true)
    }

    private fun setupCameraLauncher() {
        cameraResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK) {
                val uriStr = result.data?.getStringExtra("captured_image_uri")
                if (!uriStr.isNullOrEmpty()) {
                    try {
                        val uri = Uri.parse(uriStr)
                        uriSelecionado = uri
                        val inputStream = contentResolver.openInputStream(uri)
                        val bitmapOriginal = BitmapFactory.decodeStream(inputStream)
                        inputStream?.close()

                        if (bitmapOriginal != null) {
                            val inputStreamRot = contentResolver.openInputStream(uri)
                            val bitmapCorrigido = corrigirRotacaoImagem(inputStreamRot!!, bitmapOriginal)
                            inputStreamRot.close()

                            fotoBitmap = bitmapCorrigido
                            binding.ivFotoCapturada.setImageBitmap(bitmapCorrigido)
                            binding.ivFotoCapturada.visibility = View.VISIBLE
                            Toast.makeText(this, "Foto exibida com sucesso!", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this, "Erro ao processar a imagem", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: Exception) {
                        Toast.makeText(this, "Erro: ${e.message}", Toast.LENGTH_LONG).show()
                    }
                } else {
                    Toast.makeText(this, "Imagem não recebida", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setupGalleryLauncher() {
        galleryResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            (application as App).ignorarVerificacaoFoco = false

            if (result.resultCode == RESULT_OK) {
                val data: Intent? = result.data
                val selectedImageUri: Uri? = data?.data
                if (selectedImageUri != null) {
                    try {
                        val inputStream = contentResolver.openInputStream(selectedImageUri)
                        if (inputStream != null) {
                            val fileName = "galeria_${System.currentTimeMillis()}.jpg"
                            val file = File(filesDir, fileName)
                            val outputStream = FileOutputStream(file)
                            inputStream.copyTo(outputStream)
                            inputStream.close()
                            outputStream.close()

                            val uri = Uri.fromFile(file)
                            uriSelecionado = uri

                            val bitmapOriginal = BitmapFactory.decodeFile(file.absolutePath)
                            val bitmapCorrigido = corrigirRotacaoImagem(FileInputStream(file), bitmapOriginal)

                            fotoBitmap = bitmapCorrigido
                            binding.ivFotoCapturada.setImageBitmap(bitmapCorrigido)
                            binding.ivFotoCapturada.visibility = View.VISIBLE
                            Toast.makeText(this, "Imagem da galeria exibida com sucesso!", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this, "Erro ao ler imagem da galeria", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: Exception) {
                        Toast.makeText(this, "Erro: ${e.message}", Toast.LENGTH_LONG).show()
                    }
                } else {
                    Toast.makeText(this, "Nenhuma imagem selecionada", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun abrirCamera() {
        val intent = Intent(this, CameraXActivity::class.java)
        cameraResultLauncher.launch(intent)
    }

    private fun abrirGaleria() {
        (application as App).ignorarVerificacaoFoco = true

        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        galleryResultLauncher.launch(intent)
    }

    private fun atualizarSelecaoHumor(selecionado: View) {
        val botoes = listOf(binding.btnEmojiFeliz, binding.btnEmojiTriste, binding.btnEmojiNeutro, binding.btnEmojiBravo)
        botoes.forEach { it.isSelected = (it == selecionado) }
    }

    private fun setupSpeechRecognizer() {
        speechRecognizerLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK && result.data != null) {
                val results = result.data!!.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                if (!results.isNullOrEmpty()) {
                    val transcricao = results[0]
                    val mensagemAtual = binding.etMensagemDia.text.toString()
                    binding.etMensagemDia.setText("$mensagemAtual $transcricao".trim())
                }
            }
        }
    }

    private fun iniciarReconhecimentoVoz() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "pt-BR")
            putExtra(RecognizerIntent.EXTRA_PROMPT, "Fale agora...")
        }

        try {
            speechRecognizerLauncher.launch(intent)
        } catch (e: Exception) {
            Toast.makeText(this, "Reconhecimento de voz não suportado neste dispositivo", Toast.LENGTH_LONG).show()
        }
    }

    private fun setupListeners() {
        binding.btnEmojiFeliz.setOnClickListener {
            humorSelecionado = "feliz"
            atualizarSelecaoHumor(binding.btnEmojiFeliz)
        }

        binding.btnEmojiTriste.setOnClickListener {
            humorSelecionado = "triste"
            atualizarSelecaoHumor(binding.btnEmojiTriste)
        }

        binding.btnEmojiNeutro.setOnClickListener {
            humorSelecionado = "neutro"
            atualizarSelecaoHumor(binding.btnEmojiNeutro)
        }

        binding.btnEmojiBravo.setOnClickListener {
            humorSelecionado = "bravo"
            atualizarSelecaoHumor(binding.btnEmojiBravo)
        }

        binding.btnAddFotoCamera.setOnClickListener {
            abrirCamera()
        }

        binding.btnAddFotoGaleria.setOnClickListener {
            abrirGaleria()
        }

        binding.btnTranscreverVoz.setOnClickListener {
            iniciarReconhecimentoVoz()
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

            val uriString = uriSelecionado?.toString() ?: ""

            viewModel.salvarEntrada(mensagem, humorSelecionado!!, uriString)
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