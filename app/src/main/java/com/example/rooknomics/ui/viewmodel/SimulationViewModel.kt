package com.example.rooknomics.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.rooknomics.data.models.BacktestResults
import com.example.rooknomics.data.models.BacktestRequest
import com.example.rooknomics.data.repository.BacktestRepository
import kotlinx.coroutines.launch

sealed class SimState {
    object Idle : SimState()
    object Loading : SimState()
    data class Success(val response: BacktestResults) : SimState()
    data class Error(val message: String) : SimState()
}

class SimulationViewModel(private val repository: BacktestRepository) : ViewModel() {
    private val _simState = MutableLiveData<SimState>(SimState.Idle)
    val simState: LiveData<SimState> = _simState

    fun runBacktest(request: BacktestRequest) {

        _simState.value = SimState.Loading

        viewModelScope.launch {
//            _simState.postValue(SimState.Loading)
            try {
                val result = repository.runBacktest(request)
                if (result.isSuccessful && result.body() != null) {
                    _simState.postValue(SimState.Success(result.body()!!))
                } else {
//                    _simState.value = SimState.Error(result.message() ?: "Execution failed")
                    val errorBody = result.errorBody()?.string() ?: "Unknown Error"
                    _simState.postValue(SimState.Error("HTTP ${result.code()}: $errorBody"))
                }
            } catch (e: Exception) {
                _simState.postValue( SimState.Error(e.message ?: "Network error"))
            }
        }
    }
    
    fun reset() {
        _simState.value = SimState.Idle
    }
}

class SimViewModelFactory(private val repository: BacktestRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SimulationViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SimulationViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
