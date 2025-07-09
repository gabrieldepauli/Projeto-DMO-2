package br.edu.ifsp.dmo1.diario_inteligente_dmo2.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "diario")
data class Diario(
    @PrimaryKey(autoGenerate = true) val id: Int? = null,
    val data: String,
    val texto: String,
    val humor: String,
    val fotoUri: String
)