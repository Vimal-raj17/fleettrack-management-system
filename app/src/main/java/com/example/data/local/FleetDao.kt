package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.data.model.CompliancePermitEntity
import com.example.data.model.FitnessCertificateEntity
import com.example.data.model.InsuranceRecordEntity
import com.example.data.model.VehicleEntity
import com.example.data.model.VehicleWithCompliance
import kotlinx.coroutines.flow.Flow

@Dao
interface VehicleDao {
    @Query("SELECT * FROM vehicles ORDER BY regNumber ASC")
    fun getAllVehicles(): Flow<List<VehicleEntity>>

    @Transaction
    @Query("SELECT * FROM vehicles ORDER BY regNumber ASC")
    fun getAllVehiclesWithCompliance(): Flow<List<VehicleWithCompliance>>

    @Transaction
    @Query("SELECT * FROM vehicles WHERE id = :vehicleId")
    fun getVehicleWithComplianceById(vehicleId: Long): Flow<VehicleWithCompliance?>

    @Query("SELECT * FROM vehicles WHERE id = :vehicleId")
    suspend fun getVehicleById(vehicleId: Long): VehicleEntity?

    @Query("SELECT COUNT(*) FROM vehicles")
    fun getVehicleCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVehicle(vehicle: VehicleEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVehicles(vehicles: List<VehicleEntity>): List<Long>

    @Update
    suspend fun updateVehicle(vehicle: VehicleEntity)

    @Delete
    suspend fun deleteVehicle(vehicle: VehicleEntity)

    @Query("DELETE FROM vehicles WHERE id = :vehicleId")
    suspend fun deleteVehicleById(vehicleId: Long)
}

@Dao
interface InsuranceDao {
    @Query("SELECT * FROM insurance_renewals WHERE vehicleId = :vehicleId ORDER BY expiryDate DESC")
    fun getInsuranceForVehicle(vehicleId: Long): Flow<List<InsuranceRecordEntity>>

    @Query("SELECT * FROM insurance_renewals ORDER BY expiryDate ASC")
    fun getAllInsuranceRecords(): Flow<List<InsuranceRecordEntity>>

    @Query("SELECT * FROM insurance_renewals WHERE isCurrent = 1 AND expiryDate < :timestamp")
    fun getExpiredPolicies(timestamp: Long): Flow<List<InsuranceRecordEntity>>

    @Query("SELECT * FROM insurance_renewals WHERE isCurrent = 1 AND expiryDate BETWEEN :now AND :threshold")
    fun getExpiringPolicies(now: Long, threshold: Long): Flow<List<InsuranceRecordEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInsurance(record: InsuranceRecordEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInsuranceBatch(records: List<InsuranceRecordEntity>)

    @Query("UPDATE insurance_renewals SET isCurrent = 0 WHERE vehicleId = :vehicleId")
    suspend fun archiveCurrentInsurance(vehicleId: Long)

    @Update
    suspend fun updateInsurance(record: InsuranceRecordEntity)

    @Delete
    suspend fun deleteInsurance(record: InsuranceRecordEntity)
}

@Dao
interface FitnessDao {
    @Query("SELECT * FROM fitness_certificates WHERE vehicleId = :vehicleId ORDER BY expiryDate DESC")
    fun getFitnessForVehicle(vehicleId: Long): Flow<List<FitnessCertificateEntity>>

    @Query("SELECT * FROM fitness_certificates ORDER BY expiryDate ASC")
    fun getAllFitnessCertificates(): Flow<List<FitnessCertificateEntity>>

    @Query("SELECT * FROM fitness_certificates WHERE isCurrent = 1 AND expiryDate < :timestamp")
    fun getExpiredFitnessCertificates(timestamp: Long): Flow<List<FitnessCertificateEntity>>

    @Query("SELECT * FROM fitness_certificates WHERE isCurrent = 1 AND expiryDate BETWEEN :now AND :threshold")
    fun getExpiringFitnessCertificates(now: Long, threshold: Long): Flow<List<FitnessCertificateEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFitness(record: FitnessCertificateEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFitnessBatch(records: List<FitnessCertificateEntity>)

    @Query("UPDATE fitness_certificates SET isCurrent = 0 WHERE vehicleId = :vehicleId")
    suspend fun archiveCurrentFitness(vehicleId: Long)

    @Update
    suspend fun updateFitness(record: FitnessCertificateEntity)

    @Delete
    suspend fun deleteFitness(record: FitnessCertificateEntity)
}

@Dao
interface ComplianceDao {
    @Query("SELECT * FROM compliance_permits WHERE vehicleId = :vehicleId ORDER BY expiryDate DESC")
    fun getPermitsForVehicle(vehicleId: Long): Flow<List<CompliancePermitEntity>>

    @Query("SELECT * FROM compliance_permits ORDER BY expiryDate ASC")
    fun getAllPermits(): Flow<List<CompliancePermitEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPermit(record: CompliancePermitEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPermitsBatch(records: List<CompliancePermitEntity>)

    @Query("UPDATE compliance_permits SET isCurrent = 0 WHERE vehicleId = :vehicleId AND permitType = :permitType")
    suspend fun archiveCurrentPermit(vehicleId: Long, permitType: String)

    @Delete
    suspend fun deletePermit(record: CompliancePermitEntity)
}
