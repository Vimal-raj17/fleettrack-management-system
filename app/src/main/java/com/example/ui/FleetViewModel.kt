package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.FleetDatabase
import com.example.data.model.CompliancePermitEntity
import com.example.data.model.ComplianceStatus
import com.example.data.model.FitnessCertificateEntity
import com.example.data.model.InsuranceRecordEntity
import com.example.data.model.VehicleEntity
import com.example.data.model.VehicleWithCompliance
import com.example.data.mysql.MySqlExporter
import com.example.data.repository.FleetRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class FleetTab(val label: String) {
    DASHBOARD("Dashboard"),
    FLEET("Fleet Registry"),
    INSURANCE("Insurance"),
    FITNESS("FC Renewal"),
    MYSQL_HUB("MySQL DB Hub")
}

enum class FleetFilter(val label: String) {
    ALL("All Fleet"),
    CRITICAL_ATTENTION("⚠️ Critical Alerts"),
    INSURANCE_DUE("🛡️ Insurance Due"),
    FITNESS_DUE("📋 FC Due"),
    EXPIRED("❌ Expired Docs"),
    HEAVY_TRUCK("Heavy Trucks"),
    CARGO_VAN("Cargo & Vans"),
    BUS_PASSENGER("Buses & Passenger")
}

data class FleetMetrics(
    val totalVehicles: Int = 0,
    val activeVehicles: Int = 0,
    val maintenanceVehicles: Int = 0,
    val insuranceDueSoon: Int = 0,
    val insuranceExpired: Int = 0,
    val fitnessDueSoon: Int = 0,
    val fitnessExpired: Int = 0,
    val totalUrgentAlerts: Int = 0,
    val totalInsuranceAnnualPremium: Double = 0.0,
    val totalFcAnnualFees: Double = 0.0,
    val complianceRatePercent: Int = 100
)

class FleetViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: FleetRepository

    private val _selectedTab = MutableStateFlow(FleetTab.DASHBOARD)
    val selectedTab: StateFlow<FleetTab> = _selectedTab.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedFilter = MutableStateFlow(FleetFilter.ALL)
    val selectedFilter: StateFlow<FleetFilter> = _selectedFilter.asStateFlow()

    private val _selectedVehicleId = MutableStateFlow<Long?>(null)
    val selectedVehicleId: StateFlow<Long?> = _selectedVehicleId.asStateFlow()

    private val _showAddVehicleDialog = MutableStateFlow(false)
    val showAddVehicleDialog: StateFlow<Boolean> = _showAddVehicleDialog.asStateFlow()

    private val _editingVehicle = MutableStateFlow<VehicleEntity?>(null)
    val editingVehicle: StateFlow<VehicleEntity?> = _editingVehicle.asStateFlow()

    private val _renewInsuranceVehicle = MutableStateFlow<VehicleWithCompliance?>(null)
    val renewInsuranceVehicle: StateFlow<VehicleWithCompliance?> = _renewInsuranceVehicle.asStateFlow()

    private val _renewFitnessVehicle = MutableStateFlow<VehicleWithCompliance?>(null)
    val renewFitnessVehicle: StateFlow<VehicleWithCompliance?> = _renewFitnessVehicle.asStateFlow()

    private val _addPermitVehicle = MutableStateFlow<VehicleWithCompliance?>(null)
    val addPermitVehicle: StateFlow<VehicleWithCompliance?> = _addPermitVehicle.asStateFlow()

    private val _userMessage = MutableStateFlow<String?>(null)
    val userMessage: StateFlow<String?> = _userMessage.asStateFlow()

    init {
        val db = FleetDatabase.getDatabase(application)
        repository = FleetRepository(
            vehicleDao = db.vehicleDao(),
            insuranceDao = db.insuranceDao(),
            fitnessDao = db.fitnessDao(),
            complianceDao = db.complianceDao()
        )
        viewModelScope.launch {
            repository.checkAndSeedInitialData()
        }
    }

    val rawVehicles: StateFlow<List<VehicleWithCompliance>> = repository.allVehiclesWithCompliance
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val allInsuranceRecords: StateFlow<List<InsuranceRecordEntity>> = repository.allInsuranceRecords
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val allFitnessRecords: StateFlow<List<FitnessCertificateEntity>> = repository.allFitnessRecords
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val allPermits: StateFlow<List<CompliancePermitEntity>> = repository.allPermits
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val filteredVehicles: StateFlow<List<VehicleWithCompliance>> = combine(
        rawVehicles,
        _searchQuery,
        _selectedFilter
    ) { list, query, filter ->
        val now = System.currentTimeMillis()
        list.filter { item ->
            val matchesQuery = query.isBlank() ||
                    item.vehicle.regNumber.contains(query, ignoreCase = true) ||
                    item.vehicle.makeModel.contains(query, ignoreCase = true) ||
                    item.vehicle.driverName.contains(query, ignoreCase = true) ||
                    item.vehicle.vehicleType.contains(query, ignoreCase = true)

            if (!matchesQuery) return@filter false

            val insDays = item.getInsuranceDaysRemaining(now)
            val fcDays = item.getFitnessDaysRemaining(now)
            val status = item.getOverallComplianceStatus(now)

            when (filter) {
                FleetFilter.ALL -> true
                FleetFilter.CRITICAL_ATTENTION -> status == ComplianceStatus.CRITICAL || status == ComplianceStatus.EXPIRED
                FleetFilter.INSURANCE_DUE -> (insDays != null && insDays <= 30)
                FleetFilter.FITNESS_DUE -> (fcDays != null && fcDays <= 30)
                FleetFilter.EXPIRED -> status == ComplianceStatus.EXPIRED
                FleetFilter.HEAVY_TRUCK -> item.vehicle.vehicleType.contains("Truck", ignoreCase = true) || item.vehicle.vehicleType.contains("Tipper", ignoreCase = true) || item.vehicle.vehicleType.contains("Trailer", ignoreCase = true)
                FleetFilter.CARGO_VAN -> item.vehicle.vehicleType.contains("Cargo", ignoreCase = true) || item.vehicle.vehicleType.contains("Van", ignoreCase = true) || item.vehicle.vehicleType.contains("Pickup", ignoreCase = true)
                FleetFilter.BUS_PASSENGER -> item.vehicle.vehicleType.contains("Bus", ignoreCase = true) || item.vehicle.vehicleType.contains("Passenger", ignoreCase = true)
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val fleetMetrics: StateFlow<FleetMetrics> = rawVehicles.mapToMetrics(viewModelScope)

    fun selectTab(tab: FleetTab) {
        _selectedTab.value = tab
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setFilter(filter: FleetFilter) {
        _selectedFilter.value = filter
    }

    fun selectVehicle(vehicleId: Long?) {
        _selectedVehicleId.value = vehicleId
    }

    fun openAddVehicleDialog() {
        _editingVehicle.value = null
        _showAddVehicleDialog.value = true
    }

    fun openEditVehicleDialog(vehicle: VehicleEntity) {
        _editingVehicle.value = vehicle
        _showAddVehicleDialog.value = true
    }

    fun dismissAddEditVehicleDialog() {
        _showAddVehicleDialog.value = false
        _editingVehicle.value = null
    }

    fun openRenewInsurance(vehicle: VehicleWithCompliance) {
        _renewInsuranceVehicle.value = vehicle
    }

    fun dismissRenewInsurance() {
        _renewInsuranceVehicle.value = null
    }

    fun openRenewFitness(vehicle: VehicleWithCompliance) {
        _renewFitnessVehicle.value = vehicle
    }

    fun dismissRenewFitness() {
        _renewFitnessVehicle.value = null
    }

    fun openAddPermit(vehicle: VehicleWithCompliance) {
        _addPermitVehicle.value = vehicle
    }

    fun dismissAddPermit() {
        _addPermitVehicle.value = null
    }

    fun clearUserMessage() {
        _userMessage.value = null
    }

    fun saveVehicle(
        id: Long = 0,
        regNumber: String,
        makeModel: String,
        vehicleType: String,
        manufactureYear: Int,
        fuelType: String,
        chassisNumber: String,
        engineNumber: String,
        driverName: String,
        driverPhone: String,
        currentOdometer: Int,
        status: String
    ) {
        viewModelScope.launch {
            val vehicle = VehicleEntity(
                id = id,
                regNumber = regNumber.trim().uppercase(),
                makeModel = makeModel.trim(),
                vehicleType = vehicleType,
                manufactureYear = manufactureYear,
                fuelType = fuelType,
                chassisNumber = chassisNumber.trim().uppercase(),
                engineNumber = engineNumber.trim().uppercase(),
                driverName = driverName.trim(),
                driverPhone = driverPhone.trim(),
                currentOdometer = currentOdometer,
                status = status
            )
            repository.saveVehicle(vehicle)
            _showAddVehicleDialog.value = false
            _editingVehicle.value = null
            _userMessage.value = if (id == 0L) "Vehicle ${vehicle.regNumber} registered successfully" else "Vehicle updated"
        }
    }

    fun deleteVehicle(vehicle: VehicleEntity) {
        viewModelScope.launch {
            repository.deleteVehicle(vehicle)
            if (_selectedVehicleId.value == vehicle.id) {
                _selectedVehicleId.value = null
            }
            _userMessage.value = "Vehicle ${vehicle.regNumber} removed from fleet"
        }
    }

    fun processInsuranceRenewal(
        vehicleId: Long,
        policyNumber: String,
        providerName: String,
        policyType: String,
        startDate: Long,
        expiryDate: Long,
        premiumAmount: Double,
        idvAmount: Double,
        agentContact: String,
        notes: String
    ) {
        viewModelScope.launch {
            repository.renewInsurance(
                vehicleId = vehicleId,
                policyNumber = policyNumber.trim(),
                providerName = providerName.trim(),
                policyType = policyType.trim(),
                startDate = startDate,
                expiryDate = expiryDate,
                premiumAmount = premiumAmount,
                idvAmount = idvAmount,
                agentContact = agentContact.trim(),
                notes = notes.trim()
            )
            _renewInsuranceVehicle.value = null
            _userMessage.value = "Insurance policy $policyNumber renewed successfully!"
        }
    }

    fun processFitnessRenewal(
        vehicleId: Long,
        certificateNumber: String,
        rtoLocation: String,
        issueDate: Long,
        expiryDate: Long,
        inspectionFee: Double,
        speedGovernorStatus: String,
        emissionStatus: String,
        reflectiveTapeValid: Boolean,
        brakeTestPassed: Boolean,
        inspectionNotes: String
    ) {
        viewModelScope.launch {
            repository.renewFitnessCertificate(
                vehicleId = vehicleId,
                certificateNumber = certificateNumber.trim(),
                rtoLocation = rtoLocation.trim(),
                issueDate = issueDate,
                expiryDate = expiryDate,
                inspectionFee = inspectionFee,
                speedGovernorStatus = speedGovernorStatus,
                emissionStatus = emissionStatus,
                reflectiveTapeValid = reflectiveTapeValid,
                brakeTestPassed = brakeTestPassed,
                inspectionNotes = inspectionNotes.trim()
            )
            _renewFitnessVehicle.value = null
            _userMessage.value = "Fitness Certificate (FC) $certificateNumber renewed successfully!"
        }
    }

    fun processPermitAdd(
        vehicleId: Long,
        permitType: String,
        documentNumber: String,
        expiryDate: Long,
        fee: Double
    ) {
        viewModelScope.launch {
            repository.addOrUpdatePermit(
                vehicleId = vehicleId,
                permitType = permitType,
                documentNumber = documentNumber.trim(),
                expiryDate = expiryDate,
                fee = fee
            )
            _addPermitVehicle.value = null
            _userMessage.value = "Permit $documentNumber saved!"
        }
    }

    fun resetFleetData() {
        viewModelScope.launch {
            repository.resetToSampleData()
            _userMessage.value = "Fleet data restored to demonstration benchmark fleet"
        }
    }

    fun getExportSqlDump(): String {
        val vehicles = rawVehicles.value.map { it.vehicle }
        val insurance = allInsuranceRecords.value
        val fitness = allFitnessRecords.value
        val permits = allPermits.value
        return MySqlExporter.generateSqlInsertDump(vehicles, insurance, fitness, permits)
    }

    fun getMySqlDDL(): String {
        return MySqlExporter.getMySqlSchemaDDL()
    }
}

private fun StateFlow<List<VehicleWithCompliance>>.mapToMetrics(
    scope: kotlinx.coroutines.CoroutineScope
): StateFlow<FleetMetrics> {
    return this.map { list: List<VehicleWithCompliance> ->
        val now = System.currentTimeMillis()
        var active = 0
        var maintenance = 0
        var insDueSoon = 0
        var insExpired = 0
        var fcDueSoon = 0
        var fcExpired = 0
        var totalInsuranceSpend = 0.0
        var totalFcFees = 0.0
        var compliantCount = 0

        list.forEach { item: VehicleWithCompliance ->
            if (item.vehicle.status == "Active") active++
            if (item.vehicle.status == "Maintenance") maintenance++

            val insDays = item.getInsuranceDaysRemaining(now)
            val fcDays = item.getFitnessDaysRemaining(now)

            if (insDays != null) {
                if (insDays < 0) insExpired++
                else if (insDays <= 30) insDueSoon++
            }
            if (fcDays != null) {
                if (fcDays < 0) fcExpired++
                else if (fcDays <= 30) fcDueSoon++
            }

            item.currentInsurance?.let { totalInsuranceSpend += it.premiumAmount }
            item.currentFitness?.let { totalFcFees += it.inspectionFee }

            if (item.getOverallComplianceStatus(now) == ComplianceStatus.VALID) {
                compliantCount++
            }
        }

        val rate = if (list.isNotEmpty()) ((compliantCount.toDouble() / list.size) * 100).toInt() else 100

        FleetMetrics(
            totalVehicles = list.size,
            activeVehicles = active,
            maintenanceVehicles = maintenance,
            insuranceDueSoon = insDueSoon,
            insuranceExpired = insExpired,
            fitnessDueSoon = fcDueSoon,
            fitnessExpired = fcExpired,
            totalUrgentAlerts = insExpired + fcExpired + insDueSoon + fcDueSoon,
            totalInsuranceAnnualPremium = totalInsuranceSpend,
            totalFcAnnualFees = totalFcFees,
            complianceRatePercent = rate
        )
    }.stateIn(
        scope = scope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = FleetMetrics()
    )
}
