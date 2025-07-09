package br.edu.ifsp.dmo1.diario_inteligente_dmo2.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import br.edu.ifsp.dmo1.diario_inteligente_dmo2.databinding.ItemDiarioBinding
import br.edu.ifsp.dmo1.diario_inteligente_dmo2.model.ResumoDiario

class ResumoDiarioAdapter(
    private val resumos: List<ResumoDiario>,
    private val onItemClick: (ResumoDiario) -> Unit
) : RecyclerView.Adapter<ResumoDiarioAdapter.ResumoViewHolder>() {

    inner class ResumoViewHolder(private val binding: ItemDiarioBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(resumo: ResumoDiario) {
            binding.tvData.text = resumo.data
            binding.root.setOnClickListener {
                onItemClick(resumo)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResumoViewHolder {
        val binding = ItemDiarioBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ResumoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ResumoViewHolder, position: Int) {
        holder.bind(resumos[position])
    }

    override fun getItemCount() = resumos.size
}
