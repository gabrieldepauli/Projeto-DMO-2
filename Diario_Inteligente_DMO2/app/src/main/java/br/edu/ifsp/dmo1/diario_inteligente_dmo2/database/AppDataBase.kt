package br.edu.ifsp.dmo1.diario_inteligente_dmo2.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import br.edu.ifsp.dmo1.diario_inteligente_dmo2.model.Diario
import br.edu.ifsp.dmo1.diario_inteligente_dmo2.dao.DiarioDao

@Database(entities = [Diario::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun diarioDao(): DiarioDao

    companion object {
        @Volatile private var instance: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "diario.db"
                ).build().also { instance = it }
            }
    }
}
