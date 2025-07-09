package br.edu.ifsp.dmo1.diario_inteligente_dmo2.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import br.edu.ifsp.dmo1.diario_inteligente_dmo2.databinding.ActivityHomeBinding
import br.edu.ifsp.dmo1.diario_inteligente_dmo2.model.ResumoDiario
import br.edu.ifsp.dmo1.diario_inteligente_dmo2.ui.adapter.ResumoDiarioAdapter
import br.edu.ifsp.dmo1.diario_inteligente_dmo2.viewmodel.HomeViewModel
import java.text.SimpleDateFormat
import java.util.*

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private val viewModel: HomeViewModel by viewModels()
    private lateinit var adapter: ResumoDiarioAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val dataAtual = Calendar.getInstance().time
        val formatador = SimpleDateFormat("EEEE, dd 'de' MMMM", Locale("pt", "BR"))
        val dataFormatada = formatador.format(dataAtual).replaceFirstChar { it.uppercase() }
        binding.tvDate.text = dataFormatada

        binding.btnNewEntry.setOnClickListener {
            startActivity(Intent(this, NovaEntradaActivity::class.java))
        }

        adapter = ResumoDiarioAdapter(emptyList()) { resumo ->
            abrirDetalhes(resumo)
        }

        binding.rvRecentEntries.layoutManager = LinearLayoutManager(this)
        binding.rvRecentEntries.adapter = adapter

        viewModel.resumos.observe(this) { lista ->
            if (lista.isNullOrEmpty()) {
                binding.rvRecentEntries.visibility = View.GONE
                binding.tvHint.visibility = View.GONE
                binding.tvRecentLabel.text = "Nenhuma anotação ainda."
            } else {
                binding.rvRecentEntries.visibility = View.VISIBLE
                binding.tvHint.visibility = View.VISIBLE
                binding.tvRecentLabel.text = "Últimas anotações"
                adapter = ResumoDiarioAdapter(lista) { resumo ->
                    abrirDetalhes(resumo)
                }
                binding.rvRecentEntries.adapter = adapter
            }
        }

        viewModel.carregarResumos()
    }

    override fun onResume() {
        super.onResume()
        viewModel.carregarResumos()
    }

    private fun abrirDetalhes(resumo: ResumoDiario) {
        val intent = Intent(this, DetalhesEntradaActivity::class.java)
        intent.putExtra("id", resumo.id)
        startActivity(intent)
    }
}