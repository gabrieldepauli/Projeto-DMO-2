package br.edu.ifsp.dmo1.diario_inteligente_dmo2.dao

import androidx.room.*
import br.edu.ifsp.dmo1.diario_inteligente_dmo2.model.Diario
import br.edu.ifsp.dmo1.diario_inteligente_dmo2.model.ResumoDiario

@Dao
interface DiarioDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserir(d: Diario): Long

    @Delete
    suspend fun deletar(d: Diario)

    @Query("SELECT * FROM diario ORDER BY id DESC")
    suspend fun listar(): List<Diario>

    @Query("SELECT id, data FROM diario ORDER BY id DESC")
    suspend fun listarResumos(): List<ResumoDiario>

    @Query("SELECT id, data, texto, humor, fotoUri FROM diario WHERE id = :id")
    suspend fun buscarPorId(id: Int): Diario?

}