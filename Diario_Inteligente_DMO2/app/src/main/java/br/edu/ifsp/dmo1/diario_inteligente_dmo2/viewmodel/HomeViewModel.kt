package br.edu.ifsp.dmo1.diario_inteligente_dmo2.viewmodel

import android.app.Application
import androidx.lifecycle.*
import br.edu.ifsp.dmo1.diario_inteligente_dmo2.database.AppDatabase
import br.edu.ifsp.dmo1.diario_inteligente_dmo2.model.ResumoDiario
import kotlinx.coroutines.launch

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val diarioDao = AppDatabase.getDatabase(application).diarioDao()

    private val _resumos = MutableLiveData<List<ResumoDiario>>()
    val resumos: LiveData<List<ResumoDiario>> = _resumos

    fun carregarResumos() {
        viewModelScope.launch {
            val lista = diarioDao.listarResumos()
            _resumos.postValue(lista)
        }
    }
}
