package io.alron.fixall.presentation.cars

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.alron.fixall.BuildConfig
import io.alron.fixall.R
import io.alron.fixall.domain.model.Car
import io.alron.fixall.domain.model.CarBrand
import io.alron.fixall.domain.model.CarModel
import io.alron.fixall.domain.repository.CarsRepository
import io.alron.fixall.presentation.util.UiText
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

data class AddCarUiState(
    val carId: String? = null,
    val brands: List<CarBrand> = emptyList(),
    val models: List<CarModel> = emptyList(),
    val selectedBrand: CarBrand? = null,
    val selectedModel: CarModel? = null,
    val year: String = "",
    val licensePlate: String = "",
    val vin: String = "",
    val imageUri: Uri? = null,
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val yearError: UiText? = null,
    val licensePlateError: UiText? = null,
    val vinError: UiText? = null,
    val brandError: UiText? = null,
    val modelError: UiText? = null,
    val error: String? = null
)
sealed class AddCarEvent {
    data class ShowSnackbar(val message: String) : AddCarEvent()
    object CarSaved : AddCarEvent()
}

@HiltViewModel
class AddCarViewModel @Inject constructor(
    private val repository: CarsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddCarUiState())
    val uiState: StateFlow<AddCarUiState> = _uiState.asStateFlow()

    private val _eventChannel = Channel<AddCarEvent>()
    val events = _eventChannel.receiveAsFlow()

    private val russianPlateRegex =
        Regex("^[АВЕКМНОРСТУХ]\\d{3}[АВЕКМНОРСТУХ]{2}$", RegexOption.IGNORE_CASE)
    private val vinRegex = Regex("^[A-HJ-NPR-Z0-9]{17}$", RegexOption.IGNORE_CASE)

    init {
        loadBrands()
    }

    private fun loadBrands() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            repository.getBrands()
                .onSuccess { brands ->
                    _uiState.update { it.copy(brands = brands, isLoading = false) }
                    syncSelectedBrandAndLoadModels()
                }
                .onFailure { throwable ->
                    _uiState.update {
                        it.copy(
                            error = throwable.localizedMessage,
                            isLoading = false
                        )
                    }
                }
        }
    }

    private fun syncSelectedBrandAndLoadModels() {
        val state = _uiState.value
        if (state.selectedBrand != null && state.brands.isNotEmpty()) {
            val brandFromList =
                state.brands.find { it.name.equals(state.selectedBrand.name, ignoreCase = true) }
            if (brandFromList != null) {
                _uiState.update { it.copy(selectedBrand = brandFromList) }
                loadModels(brandFromList.id)
            }
        }
    }

    fun onEditInit(car: Car) {
        if (_uiState.value.carId != null) return

        val absolutePhotoUrl = car.photoUrl?.let { url ->
            if (url.startsWith("http")) url else "${BuildConfig.BASE_URL}$url"
        }

        _uiState.update {
            it.copy(
                carId = car.id,
                year = car.year.toString(),
                licensePlate = car.licensePlate,
                vin = car.vin ?: "",
                imageUri = absolutePhotoUrl?.let { url -> Uri.parse(url) },
                selectedBrand = CarBrand("", car.brandName),
                selectedModel = CarModel(car.modelId, car.modelName, "", car.brandName)
            )
        }
        if (_uiState.value.brands.isNotEmpty()) {
            syncSelectedBrandAndLoadModels()
        }
    }

    fun onBrandSelected(brand: CarBrand) {
        _uiState.update {
            it.copy(
                selectedBrand = brand,
                selectedModel = null,
                models = emptyList(),
                brandError = null
            )
        }
        loadModels(brand.id)
    }

    private fun loadModels(brandId: String) {
        viewModelScope.launch {
            repository.getModels(brandId)
                .onSuccess { models ->
                    _uiState.update { currentState ->
                        val selectedModel =
                            if (currentState.carId != null && currentState.selectedModel != null) {
                                models.find {
                                    it.name.equals(
                                        currentState.selectedModel.name,
                                        ignoreCase = true
                                    )
                                } ?: currentState.selectedModel
                            } else {
                                currentState.selectedModel
                            }
                        currentState.copy(models = models, selectedModel = selectedModel)
                    }
                }
                .onFailure { throwable ->
                    _uiState.update { it.copy(error = throwable.localizedMessage) }
                }
        }
    }

    fun onModelSelected(model: CarModel) {
        _uiState.update { it.copy(selectedModel = model, modelError = null) }
    }

    fun onYearChanged(year: String) {
        if (year.all { it.isDigit() } || year.isEmpty()) {
            _uiState.update { it.copy(year = year, yearError = null) }
        }
    }

    fun onLicensePlateChanged(plate: String) {
        _uiState.update { it.copy(licensePlate = plate.uppercase(), licensePlateError = null) }
    }

    fun onVinChanged(vin: String) {
        _uiState.update { it.copy(vin = vin.uppercase(), vinError = null) }
    }

    fun onImagePicked(uri: Uri?) {
        if (uri != null) {
            _uiState.update { it.copy(imageUri = uri) }
        }
    }

    private fun validateForm(): Boolean {
        val state = _uiState.value
        var isValid = true

        if (state.selectedBrand == null) {
            _uiState.update { it.copy(brandError = UiText.StringResource(R.string.field_cant_be_blank)) }
            isValid = false
        }

        if (state.selectedModel == null) {
            _uiState.update { it.copy(modelError = UiText.StringResource(R.string.field_cant_be_blank)) }
            isValid = false
        }

        val yearInt = state.year.toIntOrNull()
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        if (yearInt == null || yearInt < 1900 || yearInt > currentYear) {
            _uiState.update { it.copy(yearError = UiText.StringResource(R.string.invalid_year)) }
            isValid = false
        }

        if (state.licensePlate.isBlank()) {
            _uiState.update { it.copy(licensePlateError = UiText.StringResource(R.string.field_cant_be_blank)) }
            isValid = false
        } else if (!russianPlateRegex.matches(state.licensePlate.replace(" ", ""))) {
            _uiState.update { it.copy(licensePlateError = UiText.StringResource(R.string.invalid_license_plate)) }
            isValid = false
        }

        if (state.vin.isNotBlank() && !vinRegex.matches(state.vin)) {
            _uiState.update { it.copy(vinError = UiText.StringResource(R.string.invalid_vin)) }
            isValid = false
        }

        return isValid
    }

    fun saveCar() {
        if (!validateForm()) return

        val state = _uiState.value
        val modelId = state.selectedModel!!.id
        val yearInt = state.year.toInt()

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            val result = if (state.carId != null) {
                repository.updateCar(
                    id = state.carId,
                    modelId = modelId,
                    year = yearInt,
                    licensePlate = state.licensePlate,
                    vin = state.vin,
                    imageUri = state.imageUri
                )
            } else {
                repository.addCar(
                    modelId = modelId,
                    year = yearInt,
                    licensePlate = state.licensePlate,
                    vin = state.vin.takeIf { it.isNotBlank() },
                    imageUri = state.imageUri
                )
            }

            result.onSuccess {
                val message =
                    if (state.carId != null) "Автомобиль успешно обновлен" else "Автомобиль успешно добавлен"
                _eventChannel.send(AddCarEvent.ShowSnackbar(message))
                _eventChannel.send(AddCarEvent.CarSaved)
            }.onFailure { throwable ->
                _uiState.update { it.copy(error = throwable.localizedMessage) }
            }
            _uiState.update { it.copy(isSaving = false) }
        }
    }
}
