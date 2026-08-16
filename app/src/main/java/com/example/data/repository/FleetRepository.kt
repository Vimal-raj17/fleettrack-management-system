package com.example.data.repository

import com.example.data.local.ComplianceDao
import com.example.data.local.FitnessDao
import com.example.data.local.InsuranceDao
import com.example.data.local.VehicleDao
import com.example.data.model.CompliancePermitEntity
import com.example.data.model.FitnessCertificateEntity
import com.example.data.model.InsuranceRecordEntity
import com.example.data.model.VehicleEntity
import com.example.data.model.VehicleWithCompliance
import com.example.data.seed.SampleFleetData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext

class FleetRepository(
    private val vehicleDao: VehicleDao,
    private val insuranceDao: InsuranceDao,
    private val fitnessDao: FitnessDao,
    private val complianceDao: ComplianceDao
) {
    val allVehiclesWithCompliance: Flow<List<VehicleWithCompliance>> =
        vehicleDao.getAllVehiclesWithCompliance()

    val allInsuranceRecords: Flow<List<InsuranceRecordEntity>> =
        insuranceDao.getAllInsuranceRecords()

    val allFitnessRecords: Flow<List<FitnessCertificateEntity>> =
        fitnessDao.getAllFitnessCertificates()

    val allPermits: Flow<List<CompliancePermitEntity>> =
        complianceDao.getAllPermits()

    fun getVehicleWithCompliance(vehicleId: Long): Flow<VehicleWithCompliance?> =
        vehicleDao.getVehicleWithComplianceById(vehicleId)

    suspend fun checkAndSeedInitialData() = withContext(Dispatchers.IO) {
        val existing = vehicleDao.getAllVehicles().firstOrNull()
        if (existing.isNullOrEmpty()) {
            val vehicles = SampleFleetData.getInitialVehicles()
            vehicleDao.insertVehicles(vehicles)
            insuranceDao.insertInsuranceBatch(SampleFleetData.getInitialInsurance())
            fitnessDao.insertFitnessBatch(SampleFleetData.getInitialFitness())
            complianceDao.insertPermitsBatch(SampleFleetData.getInitialPermits())
        }
    }

    suspend fun resetToSampleData() = withContext(Dispatchers.IO) {
        val existing = vehicleDao.getAllVehicles().firstOrNull() ?: emptyList()
        existing.forEach { vehicleDao.deleteVehicle(it) }
        val vehicles = SampleFleetData.getInitialVehicles()
        vehicleDao.insertVehicles(vehicles)
        insuranceDao.insertInsuranceBatch(SampleFleetData.getInitialInsurance())
        fitnessDao.insertFitnessBatch(SampleFleetData.getInitialFitness())
        complianceDao.insertPermitsBatch(SampleFleetData.getInitialPermits())
    }

    suspend fun saveVehicle(vehicle: VehicleEntity): Long = withContext(Dispatchers.IO) {
        if (vehicle.id == 0L) {
            vehicleDao.insertVehicle(vehicle)
        } else {
            vehicleDao.updateVehicle(vehicle)
            vehicle.id
        }
    }

    suspend fun deleteVehicle(vehicle: VehicleEntity) = withContext(Dispatchers.IO) {
        vehicleDao.deleteVehicle(vehicle)
    }

    suspend fun deleteVehicleById(vehicleId: Long) = withContext(Dispatchers.IO) {
        vehicleDao.deleteVehicleById(vehicleId)
    }

    suspend fun renewInsurance(
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
    ): Long = withContext(Dispatchers.IO) {
        // Archive old current insurance for this vehicle
        insuranceDao.archiveCurrentInsurance(vehicleId)
        val newRecord = InsuranceRecordEntity(
            id = 0,
            vehicleId = vehicleId,
            policyNumber = policyNumber,
            providerName = providerName,
            policyType = policyType,
            startDate = startDate,
            expiryDate = expiryDate,
            premiumAmount = premiumAmount,
            idvAmount = idvAmount,
            agentContact = agentContact,
            notes = notes,
            isCurrent = true
        )
        insuranceDao.insertInsurance(newRecord)
    }

    suspend fun renewFitnessCertificate(
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
    ): Long = withContext(Dispatchers.IO) {
        // Archive old current FC
        fitnessDao.archiveCurrentFitness(vehicleId)
        val newRecord = FitnessCertificateEntity(
            id = 0,
            vehicleId = vehicleId,
            certificateNumber = certificateNumber,
            rtoLocation = rtoLocation,
            issueDate = issueDate,
            expiryDate = expiryDate,
            inspectionFee = inspectionFee,
            speedGovernorStatus = speedGovernorStatus,
            emissionStatus = emissionStatus,
            reflectiveTapeValid = reflectiveTapeValid,
            brakeTestPassed = brakeTestPassed,
            inspectionNotes = inspectionNotes,
            isCurrent = true
        )
        fitnessDao.insertFitness(newRecord)
    }

    suspend fun addOrUpdatePermit(
        vehicleId: Long,
        permitType: String,
        documentNumber: String,
        expiryDate: Long,
        fee: Double
    ): Long = withContext(Dispatchers.IO) {
        complianceDao.archiveCurrentPermit(vehicleId, permitType)
        val newPermit = CompliancePermitEntity(
            id = 0,
            vehicleId = vehicleId,
            permitType = permitType,
            documentNumber = documentNumber,
            expiryDate = expiryDate,
            fee = fee,
            isCurrent = true
        )
        complianceDao.insertPermit(newPermit)
    }
}
