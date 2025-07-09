package br.edu.ifsp.dmo1.diario_inteligente_dmo2.ui.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import br.edu.ifsp.dmo1.diario_inteligente_dmo2.database.AppDatabase
import br.edu.ifsp.dmo1.diario_inteligente_dmo2.model.Diario
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class NovaEntradaViewModel(application: Application) : AndroidViewModel(application) {

    private val diarioDao = AppDatabase.getDatabase(application).diarioDao()

    private val _entradaSalva = MutableLiveData<Boolean>()
    val entradaSalva: LiveData<Boolean> get() = _entradaSalva

    fun salvarEntrada(texto: String, humor: String, fotoUri: String = "") {
        val dataAtual = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())

        val entrada = Diario(
            data = dataAtual,
            texto = texto.trim(),
            humor = humor,
            fotoUri = fotoUri
        )

        Log.d("NovaEntradaViewModel", "Salvando entrada: $entrada")

        viewModelScope.launch {
            val id = diarioDao.inserir(entrada)
            Log.d("NovaEntradaViewModel", "Entrada salva com ID: $id")
            _entradaSalva.postValue(true)
        }
    }
}
