package br.edu.ifsp.dmo1.diario_inteligente_dmo2.dao

import androidx.room.*
import br.edu.ifsp.dmo1.diario_inteligente_dmo2.model.Diario

@Dao
interface DiarioDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserir(d: Diario)

    @Query("SELECT * FROM diario ORDER BY id DESC")
    suspend fun listar(): List<Diario>

    @Delete
    suspend fun deletar(d: Diario)
}
