package com.example.codingassessmentwalmart.viewModel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.codingassessmentwalmart.data.Country
import com.example.codingassessmentwalmart.network.CountryService
import com.example.codingassessmentwalmart.repo.CountryRepository
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.plugins.RxJavaPlugins
import io.reactivex.rxjava3.android.plugins.RxAndroidPlugins
import io.reactivex.rxjava3.schedulers.Schedulers
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class CountryViewModelTest {

    @get:Rule val instant = InstantTaskExecutorRule()

    @Before
    fun setupRx() {
        RxJavaPlugins.setIoSchedulerHandler { Schedulers.trampoline() }
        RxAndroidPlugins.setInitMainThreadSchedulerHandler { Schedulers.trampoline() }
    }

    @After
    fun teardownRx() {
        RxJavaPlugins.reset()
        RxAndroidPlugins.reset()
    }

    @Test
    fun loadCountries_success_updatesLiveData() {
        // Arrange
        val data = listOf(
            Country("United States of America", "NA", "US", "Washington, D.C."),
            Country("Uruguay", "SA", "UY", "Montevideo")
        )
        val fakeService = object : CountryService {
            override fun getCountries(): Single<List<Country>> = Single.just(data)
        }
        val vm = CountryViewModel(CountryRepository(fakeService))

        // Act
        vm.loadCountries(force = true)

        // Assert
        Assert.assertEquals(data, vm.countries.value)
        Assert.assertEquals(false, vm.loading.value)
        Assert.assertEquals(null, vm.error.value)
    }

    @Test
    fun loadCountries_error_setsError_andEmptyList() {
        // Arrange
        val fakeService = object : CountryService {
            override fun getCountries(): Single<List<Country>> =
                Single.error(RuntimeException("boom"))
        }
        val vm = CountryViewModel(CountryRepository(fakeService))

        // Act
        vm.loadCountries(force = true)

        // Assert
        Assert.assertTrue(vm.countries.value?.isEmpty() == true)
        Assert.assertEquals(false, vm.loading.value)
        Assert.assertTrue((vm.error.value ?: "").contains("boom"))
    }

    @Test
    fun loadCountries_skips_whenAlreadyLoaded_andForceFalse() {
        var calls = 0
        val first = listOf(Country("USA", "NA", "US", "Washington"))
        val second = listOf(Country("Uruguay", "SA", "UY", "Montevideo"))

        val fakeService = object : CountryService {
            override fun getCountries(): Single<List<Country>> {
                calls++
                return if (calls == 1) Single.just(first) else Single.just(second)
            }
        }
        val vm = CountryViewModel(CountryRepository(fakeService))

        vm.loadCountries(force = true)
        Assert.assertEquals(first, vm.countries.value)

        vm.loadCountries(force = false)
        Assert.assertEquals(first, vm.countries.value)
        Assert.assertEquals(1, calls)
    }
}
