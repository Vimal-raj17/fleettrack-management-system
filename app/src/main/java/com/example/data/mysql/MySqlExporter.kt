package com.example.data.mysql

import com.example.data.model.CompliancePermitEntity
import com.example.data.model.FitnessCertificateEntity
import com.example.data.model.InsuranceRecordEntity
import com.example.data.model.VehicleEntity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object MySqlExporter {

    fun getMySqlSchemaDDL(): String {
        return """
-- ==========================================================
-- FLEETTRACK ENTERPRISE DATABASE SCHEMA FOR MYSQL / MARIADB
-- Auto-generated Relational Schema with Foreign Keys & Indexes
-- ==========================================================

CREATE DATABASE IF NOT EXISTS `fleettrack_db` 
  DEFAULT CHARACTER SET utf8mb4 
  COLLATE utf8mb4_unicode_ci;

USE `fleettrack_db`;

-- 1. VEHICLES TABLE (Master fleet registry)
CREATE TABLE IF NOT EXISTS `vehicles` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `reg_number` VARCHAR(32) NOT NULL UNIQUE,
  `make_model` VARCHAR(128) NOT NULL,
  `vehicle_type` ENUM('Heavy Truck', 'Medium Cargo', 'Light Van', 'Passenger Bus', 'Tanker', 'Trailer', 'Pickup') NOT NULL,
  `manufacture_year` INT NOT NULL,
  `fuel_type` ENUM('Diesel', 'CNG', 'Electric', 'Petrol') NOT NULL DEFAULT 'Diesel',
  `chassis_number` VARCHAR(64) NOT NULL,
  `engine_number` VARCHAR(64) NOT NULL,
  `driver_name` VARCHAR(100) NOT NULL,
  `driver_phone` VARCHAR(32) NOT NULL,
  `current_odometer` INT NOT NULL DEFAULT 0,
  `status` ENUM('Active', 'Maintenance', 'Inactive') NOT NULL DEFAULT 'Active',
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  INDEX `idx_reg_number` (`reg_number`),
  INDEX `idx_vehicle_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 2. INSURANCE RENEWALS TABLE (Policy validity & premium tracking)
CREATE TABLE IF NOT EXISTS `insurance_renewals` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `vehicle_id` BIGINT NOT NULL,
  `policy_number` VARCHAR(64) NOT NULL,
  `provider_name` VARCHAR(128) NOT NULL,
  `policy_type` VARCHAR(64) NOT NULL,
  `start_date` DATE NOT NULL,
  `expiry_date` DATE NOT NULL,
  `premium_amount` DECIMAL(12, 2) NOT NULL DEFAULT 0.00,
  `idv_amount` DECIMAL(14, 2) NOT NULL DEFAULT 0.00,
  `agent_contact` VARCHAR(128),
  `notes` TEXT,
  `is_current` TINYINT(1) NOT NULL DEFAULT 1,
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (`vehicle_id`) REFERENCES `vehicles`(`id`) ON DELETE CASCADE,
  INDEX `idx_insurance_expiry` (`expiry_date`),
  INDEX `idx_ins_current` (`vehicle_id`, `is_current`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 3. FITNESS CERTIFICATES (FC RENEWALS) TABLE (RTO Fitness compliance)
CREATE TABLE IF NOT EXISTS `fitness_certificates` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `vehicle_id` BIGINT NOT NULL,
  `certificate_number` VARCHAR(64) NOT NULL,
  `rto_location` VARCHAR(128) NOT NULL,
  `issue_date` DATE NOT NULL,
  `expiry_date` DATE NOT NULL,
  `inspection_fee` DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
  `speed_governor_status` ENUM('Compliant', 'Calibrated', 'Pending') NOT NULL DEFAULT 'Compliant',
  `emission_status` ENUM('PUC Valid', 'PUC Expired', 'Exempt') NOT NULL DEFAULT 'PUC Valid',
  `reflective_tape_valid` TINYINT(1) NOT NULL DEFAULT 1,
  `brake_test_passed` TINYINT(1) NOT NULL DEFAULT 1,
  `inspection_notes` TEXT,
  `is_current` TINYINT(1) NOT NULL DEFAULT 1,
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (`vehicle_id`) REFERENCES `vehicles`(`id`) ON DELETE CASCADE,
  INDEX `idx_fitness_expiry` (`expiry_date`),
  INDEX `idx_fc_current` (`vehicle_id`, `is_current`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 4. COMPLIANCE PERMITS & ROAD TAX TABLE
CREATE TABLE IF NOT EXISTS `compliance_permits` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `vehicle_id` BIGINT NOT NULL,
  `permit_type` VARCHAR(64) NOT NULL,
  `document_number` VARCHAR(64) NOT NULL,
  `expiry_date` DATE NOT NULL,
  `fee` DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
  `is_current` TINYINT(1) NOT NULL DEFAULT 1,
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (`vehicle_id`) REFERENCES `vehicles`(`id`) ON DELETE CASCADE,
  INDEX `idx_permit_expiry` (`expiry_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 5. USEFUL AUDIT VIEWS FOR EXPIRATION ALERTS
CREATE OR REPLACE VIEW `v_urgent_renewals` AS
SELECT 
    v.id AS vehicle_id,
    v.reg_number,
    v.make_model,
    v.driver_name,
    v.driver_phone,
    i.policy_number,
    i.provider_name AS insurance_provider,
    i.expiry_date AS insurance_expiry,
    DATEDIFF(i.expiry_date, CURDATE()) AS insurance_days_left,
    f.certificate_number AS fc_number,
    f.rto_location,
    f.expiry_date AS fc_expiry,
    DATEDIFF(f.expiry_date, CURDATE()) AS fc_days_left
FROM `vehicles` v
LEFT JOIN `insurance_renewals` i ON v.id = i.vehicle_id AND i.is_current = 1
LEFT JOIN `fitness_certificates` f ON v.id = f.vehicle_id AND f.is_current = 1
WHERE DATEDIFF(i.expiry_date, CURDATE()) <= 30 
   OR DATEDIFF(f.expiry_date, CURDATE()) <= 30
ORDER BY LEAST(
    COALESCE(DATEDIFF(i.expiry_date, CURDATE()), 9999),
    COALESCE(DATEDIFF(f.expiry_date, CURDATE()), 9999)
) ASC;
        """.trimIndent()
    }

    private val sqlDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)

    private fun formatDate(epochMillis: Long): String {
        return sqlDateFormat.format(Date(epochMillis))
    }

    private fun escapeSql(str: String): String {
        return str.replace("'", "''")
    }

    fun generateSqlInsertDump(
        vehicles: List<VehicleEntity>,
        insuranceList: List<InsuranceRecordEntity>,
        fitnessList: List<FitnessCertificateEntity>,
        permitsList: List<CompliancePermitEntity>
    ): String {
        val sb = StringBuilder()
        sb.append("-- ==========================================================\n")
        sb.append("-- FLEETTRACK LIVE MYSQL DATA EXPORT / BACKUP DUMP\n")
        sb.append("-- Generated: ${SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(Date())}\n")
        sb.append("-- ==========================================================\n\n")
        sb.append("USE `fleettrack_db`;\n\n")

        if (vehicles.isNotEmpty()) {
            sb.append("-- Insert Vehicles\n")
            sb.append("INSERT INTO `vehicles` (`id`, `reg_number`, `make_model`, `vehicle_type`, `manufacture_year`, `fuel_type`, `chassis_number`, `engine_number`, `driver_name`, `driver_phone`, `current_odometer`, `status`) VALUES\n")
            vehicles.forEachIndexed { index, v ->
                val sep = if (index == vehicles.lastIndex) ";" else ","
                sb.append("  (${v.id}, '${escapeSql(v.regNumber)}', '${escapeSql(v.makeModel)}', '${escapeSql(v.vehicleType)}', ${v.manufactureYear}, '${escapeSql(v.fuelType)}', '${escapeSql(v.chassisNumber)}', '${escapeSql(v.engineNumber)}', '${escapeSql(v.driverName)}', '${escapeSql(v.driverPhone)}', ${v.currentOdometer}, '${escapeSql(v.status)}')")
                sb.append("$sep\n")
            }
            sb.append("\n")
        }

        if (insuranceList.isNotEmpty()) {
            sb.append("-- Insert Insurance Records\n")
            sb.append("INSERT INTO `insurance_renewals` (`id`, `vehicle_id`, `policy_number`, `provider_name`, `policy_type`, `start_date`, `expiry_date`, `premium_amount`, `idv_amount`, `agent_contact`, `notes`, `is_current`) VALUES\n")
            insuranceList.forEachIndexed { index, i ->
                val sep = if (index == insuranceList.lastIndex) ";" else ","
                sb.append("  (${i.id}, ${i.vehicleId}, '${escapeSql(i.policyNumber)}', '${escapeSql(i.providerName)}', '${escapeSql(i.policyType)}', '${formatDate(i.startDate)}', '${formatDate(i.expiryDate)}', ${i.premiumAmount}, ${i.idvAmount}, '${escapeSql(i.agentContact)}', '${escapeSql(i.notes)}', ${if (i.isCurrent) 1 else 0})")
                sb.append("$sep\n")
            }
            sb.append("\n")
        }

        if (fitnessList.isNotEmpty()) {
            sb.append("-- Insert Fitness Certificates (FC Renewal)\n")
            sb.append("INSERT INTO `fitness_certificates` (`id`, `vehicle_id`, `certificate_number`, `rto_location`, `issue_date`, `expiry_date`, `inspection_fee`, `speed_governor_status`, `emission_status`, `reflective_tape_valid`, `brake_test_passed`, `inspection_notes`, `is_current`) VALUES\n")
            fitnessList.forEachIndexed { index, f ->
                val sep = if (index == fitnessList.lastIndex) ";" else ","
                sb.append("  (${f.id}, ${f.vehicleId}, '${escapeSql(f.certificateNumber)}', '${escapeSql(f.rtoLocation)}', '${formatDate(f.issueDate)}', '${formatDate(f.expiryDate)}', ${f.inspectionFee}, '${escapeSql(f.speedGovernorStatus)}', '${escapeSql(f.emissionStatus)}', ${if (f.reflectiveTapeValid) 1 else 0}, ${if (f.brakeTestPassed) 1 else 0}, '${escapeSql(f.inspectionNotes)}', ${if (f.isCurrent) 1 else 0})")
                sb.append("$sep\n")
            }
            sb.append("\n")
        }

        if (permitsList.isNotEmpty()) {
            sb.append("-- Insert Compliance Permits & Tax Records\n")
            sb.append("INSERT INTO `compliance_permits` (`id`, `vehicle_id`, `permit_type`, `document_number`, `expiry_date`, `fee`, `is_current`) VALUES\n")
            permitsList.forEachIndexed { index, p ->
                val sep = if (index == permitsList.lastIndex) ";" else ","
                sb.append("  (${p.id}, ${p.vehicleId}, '${escapeSql(p.permitType)}', '${escapeSql(p.documentNumber)}', '${formatDate(p.expiryDate)}', ${p.fee}, ${if (p.isCurrent) 1 else 0})")
                sb.append("$sep\n")
            }
            sb.append("\n")
        }

        sb.append("-- Verification Query\n")
        sb.append("SELECT * FROM `v_urgent_renewals`;\n")
        return sb.toString()
    }
}
