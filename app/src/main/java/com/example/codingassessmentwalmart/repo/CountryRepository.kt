package com.example.codingassessmentwalmart.repo

import com.example.codingassessmentwalmart.data.Country
import com.example.codingassessmentwalmart.network.CountryService
import io.reactivex.rxjava3.core.Single

class CountryRepository(private val service: CountryService) {
    fun fetchCountries(): Single<List<Country>> = service.getCountries()
}