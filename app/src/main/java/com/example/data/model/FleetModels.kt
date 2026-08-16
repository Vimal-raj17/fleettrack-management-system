package com.example.data.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity(
    tableName = "vehicles",
    indices = [Index(value = ["regNumber"], unique = true)]
)
data class VehicleEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val regNumber: String,
    val makeModel: String,
    val vehicleType: String, // "Heavy Truck", "Medium Cargo", "Light Van", "Passenger Bus", "Tanker", "Trailer", "Pickup"
    val manufactureYear: Int,
    val fuelType: String, // "Diesel", "CNG", "Electric", "Petrol"
    val chassisNumber: String,
    val engineNumber: String,
    val driverName: String,
    val driverPhone: String,
    val currentOdometer: Int,
    val status: String = "Active", // "Active", "Maintenance", "Inactive"
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "insurance_renewals",
    foreignKeys = [
        ForeignKey(
            entity = VehicleEntity::class,
            parentColumns = ["id"],
            childColumns = ["vehicleId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("vehicleId")]
)
data class InsuranceRecordEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val vehicleId: Long,
    val policyNumber: String,
    val providerName: String, // e.g. "Tata AIG", "HDFC ERGO", "ICICI Lombard", "Oriental", "Allianz"
    val policyType: String, // "Comprehensive", "Third Party", "Bumper to Bumper", "Commercial Floater"
    val startDate: Long,
    val expiryDate: Long,
    val premiumAmount: Double,
    val idvAmount: Double,
    val agentContact: String = "",
    val notes: String = "",
    val isCurrent: Boolean = true
)

@Entity(
    tableName = "fitness_certificates",
    foreignKeys = [
        ForeignKey(
            entity = VehicleEntity::class,
            parentColumns = ["id"],
            childColumns = ["vehicleId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("vehicleId")]
)
data class FitnessCertificateEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val vehicleId: Long,
    val certificateNumber: String,
    val rtoLocation: String, // e.g. "Pune RTO Central", "Mumbai Andheri RTO", "Bengaluru West RTO"
    val issueDate: Long,
    val expiryDate: Long,
    val inspectionFee: Double,
    val speedGovernorStatus: String = "Compliant", // "Compliant", "Calibrated", "Pending"
    val emissionStatus: String = "PUC Valid", // "PUC Valid", "PUC Expired", "Exempt"
    val reflectiveTapeValid: Boolean = true,
    val brakeTestPassed: Boolean = true,
    val inspectionNotes: String = "",
    val isCurrent: Boolean = true
)

@Entity(
    tableName = "compliance_permits",
    foreignKeys = [
        ForeignKey(
            entity = VehicleEntity::class,
            parentColumns = ["id"],
            childColumns = ["vehicleId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("vehicleId")]
)
data class CompliancePermitEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val vehicleId: Long,
    val permitType: String, // "National Goods Permit", "State Stage Carriage", "Road Tax (Quarterly)", "Road Tax (Annual)", "Green Tax"
    val documentNumber: String,
    val expiryDate: Long,
    val fee: Double,
    val isCurrent: Boolean = true
)

data class VehicleWithCompliance(
    @Embedded val vehicle: VehicleEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "vehicleId"
    )
    val insuranceRecords: List<InsuranceRecordEntity>,
    @Relation(
        parentColumn = "id",
        entityColumn = "vehicleId"
    )
    val fitnessCertificates: List<FitnessCertificateEntity>,
    @Relation(
        parentColumn = "id",
        entityColumn = "vehicleId"
    )
    val compliancePermits: List<CompliancePermitEntity>
) {
    val currentInsurance: InsuranceRecordEntity?
        get() = insuranceRecords.firstOrNull { it.isCurrent } ?: insuranceRecords.maxByOrNull { it.expiryDate }

    val currentFitness: FitnessCertificateEntity?
        get() = fitnessCertificates.firstOrNull { it.isCurrent } ?: fitnessCertificates.maxByOrNull { it.expiryDate }

    val currentPermit: CompliancePermitEntity?
        get() = compliancePermits.firstOrNull { it.isCurrent } ?: compliancePermits.maxByOrNull { it.expiryDate }

    fun getInsuranceDaysRemaining(now: Long = System.currentTimeMillis()): Long? {
        val ins = currentInsurance ?: return null
        val diffMillis = ins.expiryDate - now
        return (diffMillis / (1000L * 60 * 60 * 24))
    }

    fun getFitnessDaysRemaining(now: Long = System.currentTimeMillis()): Long? {
        val fc = currentFitness ?: return null
        val diffMillis = fc.expiryDate - now
        return (diffMillis / (1000L * 60 * 60 * 24))
    }

    fun getOverallComplianceStatus(now: Long = System.currentTimeMillis()): ComplianceStatus {
        val insDays = getInsuranceDaysRemaining(now)
        val fcDays = getFitnessDaysRemaining(now)

        if ((insDays != null && insDays < 0) || (fcDays != null && fcDays < 0)) {
            return ComplianceStatus.EXPIRED
        }
        if ((insDays != null && insDays <= 15) || (fcDays != null && fcDays <= 15)) {
            return ComplianceStatus.CRITICAL
        }
        if ((insDays != null && insDays <= 30) || (fcDays != null && fcDays <= 30)) {
            return ComplianceStatus.DUE_SOON
        }
        if (insDays == null || fcDays == null) {
            return ComplianceStatus.INCOMPLETE
        }
        return ComplianceStatus.VALID
    }
}

enum class ComplianceStatus(val label: String) {
    VALID("Valid & Compliant"),
    DUE_SOON("Renewal Due Soon (<30d)"),
    CRITICAL("Expiring Urgently (<15d)"),
    EXPIRED("Certificate Expired"),
    INCOMPLETE("Missing Documents")
}

enum class RenewalType(val title: String) {
    INSURANCE("Insurance Policy Renewal"),
    FITNESS("Fitness Certificate (FC) Renewal"),
    PERMIT("Permit / Tax Renewal")
}
