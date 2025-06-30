package br.edu.ifsp.dmo1.diario_inteligente_dmo2.ui.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import br.edu.ifsp.dmo1.diario_inteligente_dmo2.databinding.ActivityHomeBinding
import br.edu.ifsp.dmo1.diario_inteligente_dmo2.viewmodel.HomeViewModel
import br.edu.ifsp.dmo1.diario_inteligente_dmo2.ui.activity.NovaEntradaActivity

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private val viewModel: HomeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnNewEntry.setOnClickListener {
            val intent = Intent(this, NovaEntradaActivity::class.java)
            startActivity(intent)
        }
    }
}
