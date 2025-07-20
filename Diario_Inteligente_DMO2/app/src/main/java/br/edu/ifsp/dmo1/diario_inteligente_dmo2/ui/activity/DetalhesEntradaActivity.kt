package br.edu.ifsp.dmo1.diario_inteligente_dmo2.ui.activity

import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import br.edu.ifsp.dmo1.diario_inteligente_dmo2.R
import br.edu.ifsp.dmo1.diario_inteligente_dmo2.databinding.ActivityDetalhesEntradaBinding
import br.edu.ifsp.dmo1.diario_inteligente_dmo2.database.AppDatabase
import kotlinx.coroutines.*

class DetalhesEntradaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetalhesEntradaBinding
    private val coroutineScope = CoroutineScope(Dispatchers.Main + Job())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetalhesEntradaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener {
            finish()
        }

        val id = intent.getIntExtra("id", -1)
        if (id == -1) {
            finish()
            return
        }

        coroutineScope.launch {
            val diario = withContext(Dispatchers.IO) {
                AppDatabase.getDatabase(applicationContext).diarioDao().buscarPorId(id)
            }
            diario?.let {
                binding.tvData.text = it.data

                // Carrega drawable do emoji no ImageView
                val emojiResId = obterEmojiDrawable(it.humor)
                if (emojiResId != 0) {
                    binding.ivEmoji.setImageResource(emojiResId)
                    binding.ivEmoji.visibility = android.view.View.VISIBLE
                } else {
                    binding.ivEmoji.visibility = android.view.View.GONE
                }

                binding.tvHumor.text = it.humor.replaceFirstChar { c -> c.uppercase() }

                binding.tvTexto.text = it.texto
                if (it.fotoUri.isNotEmpty()) {
                    binding.ivFoto.setImageURI(Uri.parse(it.fotoUri))
                }
            } ?: finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        coroutineScope.cancel()
    }

    private fun obterEmojiDrawable(humor: String): Int {
        return when (humor.lowercase()) {
            "feliz" -> R.drawable.ic_emoji_excited
            "triste" -> R.drawable.ic_emoji_sad
            "bravo" -> R.drawable.ic_emoji_angry
            "neutro" -> R.drawable.ic_emoji_neutral
            else -> 0
        }
    }
}
