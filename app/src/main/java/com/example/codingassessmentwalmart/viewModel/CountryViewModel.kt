package com.example.codingassessmentwalmart.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.codingassessmentwalmart.data.Country
import com.example.codingassessmentwalmart.network.CountryService
import com.example.codingassessmentwalmart.repo.CountryRepository
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CountryViewModel(
    private val repository:CountryRepository = CountryRepository(CountryService.instance)
) : ViewModel() {

    private val disposables = CompositeDisposable()

    private val _countries = MutableLiveData<List<Country>>()
    val countries: LiveData<List<Country>> = _countries

    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> = _loading

    private val _error = MutableLiveData<String?>(null)
    val error: LiveData<String?> = _error

    fun loadCountries(force: Boolean = false) {
        if (!force && !_countries.value.isNullOrEmpty()) return

        _loading.value = true
        _error.value = null

        val d = repository.fetchCountries()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { list ->
                    _loading.value = false
                    _countries.value = list
                },
                { e ->
                    _loading.value = false
                    _error.value = e.message ?: "Network error"
                    _countries.value = emptyList()
                }
            )
        disposables.add(d)
    }

    override fun onCleared() {
        disposables.clear()
        super.onCleared()
    }
}
