package br.edu.ifsp.dmo1.diario_inteligente_dmo2.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import br.edu.ifsp.dmo1.diario_inteligente_dmo2.databinding.ItemDiarioBinding
import br.edu.ifsp.dmo1.diario_inteligente_dmo2.model.Diario

class DiarioAdapter(
    private val diarios: List<Diario>,
    private val onItemClick: (Diario) -> Unit
) : RecyclerView.Adapter<DiarioAdapter.DiarioViewHolder>() {

    inner class DiarioViewHolder(private val binding: ItemDiarioBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(diario: Diario) {
            binding.tvData.text = diario.data
            binding.root.setOnClickListener {
                onItemClick(diario)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiarioViewHolder {
        val binding = ItemDiarioBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DiarioViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DiarioViewHolder, position: Int) {
        holder.bind(diarios[position])
    }

    override fun getItemCount() = diarios.size
}
