package com.example.data.seed

import com.example.data.model.CompliancePermitEntity
import com.example.data.model.FitnessCertificateEntity
import com.example.data.model.InsuranceRecordEntity
import com.example.data.model.VehicleEntity

object SampleFleetData {
    private val ONE_DAY_MS = 24L * 60 * 60 * 1000

    fun getInitialVehicles(): List<VehicleEntity> {
        return listOf(
            VehicleEntity(
                id = 1,
                regNumber = "MH-12-RN-8821",
                makeModel = "Tata Signa 4825.T Heavy Truck",
                vehicleType = "Heavy Truck",
                manufactureYear = 2022,
                fuelType = "Diesel",
                chassisNumber = "MAT723011N2A45901",
                engineNumber = "CUMMINS6ISBE250",
                driverName = "Rajesh Sharma",
                driverPhone = "+91 98231 44521",
                currentOdometer = 142800,
                status = "Active"
            ),
            VehicleEntity(
                id = 2,
                regNumber = "KA-01-MJ-4390",
                makeModel = "Ashok Leyland 2820 Cargo Hauler",
                vehicleType = "Medium Cargo",
                manufactureYear = 2021,
                fuelType = "Diesel",
                chassisNumber = "MAL412098M1B32014",
                engineNumber = "H-SERIES6CYL200",
                driverName = "Manoj Kumar",
                driverPhone = "+91 94480 11923",
                currentOdometer = 210450,
                status = "Active"
            ),
            VehicleEntity(
                id = 3,
                regNumber = "DL-01-AX-9912",
                makeModel = "Eicher Pro 3015 City Van",
                vehicleType = "Light Van",
                manufactureYear = 2023,
                fuelType = "CNG",
                chassisNumber = "MEP3015XN4C90812",
                engineNumber = "E483-4CYL-CNG",
                driverName = "Vikram Verma",
                driverPhone = "+91 99100 88234",
                currentOdometer = 64200,
                status = "Active"
            ),
            VehicleEntity(
                id = 4,
                regNumber = "TN-09-CB-3104",
                makeModel = "BharatBenz 3528C Heavy Tipper",
                vehicleType = "Heavy Truck",
                manufactureYear = 2020,
                fuelType = "Diesel",
                chassisNumber = "MBB3528PK0D54321",
                engineNumber = "OM906-LA-TURBO",
                driverName = "Suresh Pillai",
                driverPhone = "+91 97908 67210",
                currentOdometer = 312000,
                status = "Maintenance"
            ),
            VehicleEntity(
                id = 5,
                regNumber = "GJ-06-TT-7744",
                makeModel = "Volvo FM 420 Petroleum Tanker",
                vehicleType = "Tanker",
                manufactureYear = 2023,
                fuelType = "Diesel",
                chassisNumber = "YV2FM4200PA11920",
                engineNumber = "D13A-420-EC06",
                driverName = "Jignesh Patel",
                driverPhone = "+91 98250 33419",
                currentOdometer = 98500,
                status = "Active"
            ),
            VehicleEntity(
                id = 6,
                regNumber = "TS-07-UB-5510",
                makeModel = "Force Urbania 17-Seater Passenger Bus",
                vehicleType = "Passenger Bus",
                manufactureYear = 2024,
                fuelType = "Diesel",
                chassisNumber = "FOR33017RN6E88120",
                engineNumber = "FM2.6-CR-ED4",
                driverName = "Anand Reddy",
                driverPhone = "+91 94901 22488",
                currentOdometer = 32400,
                status = "Active"
            )
        )
    }

    fun getInitialInsurance(now: Long = System.currentTimeMillis()): List<InsuranceRecordEntity> {
        return listOf(
            // Vehicle 1: Expiring in 8 days (CRITICAL WARNING)
            InsuranceRecordEntity(
                id = 1,
                vehicleId = 1,
                policyNumber = "TATA-AIG-COM-8839210",
                providerName = "Tata AIG General Insurance",
                policyType = "Comprehensive Commercial",
                startDate = now - (357 * ONE_DAY_MS),
                expiryDate = now + (8 * ONE_DAY_MS), // 8 days left!
                premiumAmount = 48500.0,
                idvAmount = 2400000.0,
                agentContact = "Arun Verma (TATA AIG Direct)",
                notes = "Zero-depreciation add-on included. Engine protect active.",
                isCurrent = true
            ),
            // Vehicle 2: Expired 5 days ago (EXPIRED ALERT)
            InsuranceRecordEntity(
                id = 2,
                vehicleId = 2,
                policyNumber = "HDFC-ERGO-FL-330198",
                providerName = "HDFC ERGO Commercial",
                policyType = "Comprehensive",
                startDate = now - (370 * ONE_DAY_MS),
                expiryDate = now - (5 * ONE_DAY_MS), // EXPIRED 5 days ago
                premiumAmount = 39200.0,
                idvAmount = 1850000.0,
                agentContact = "Sunil Rao (+91 98450 77123)",
                notes = "Grace period renewal pending immediately to avoid RTO fine.",
                isCurrent = true
            ),
            // Vehicle 3: Valid for 240 days
            InsuranceRecordEntity(
                id = 3,
                vehicleId = 3,
                policyNumber = "ICICI-LOMB-7721094",
                providerName = "ICICI Lombard General",
                policyType = "Third Party + Fire & Theft",
                startDate = now - (125 * ONE_DAY_MS),
                expiryDate = now + (240 * ONE_DAY_MS),
                premiumAmount = 21400.0,
                idvAmount = 950000.0,
                agentContact = "Kavita Singh (ICICI Fleet Desk)",
                notes = "CNG cylinder kit certified and endorsed.",
                isCurrent = true
            ),
            // Vehicle 4: Expiring in 22 days (DUE SOON)
            InsuranceRecordEntity(
                id = 4,
                vehicleId = 4,
                policyNumber = "ORIENTAL-33100-291",
                providerName = "Oriental Insurance Co.",
                policyType = "Comprehensive Heavy Tipper",
                startDate = now - (343 * ONE_DAY_MS),
                expiryDate = now + (22 * ONE_DAY_MS), // 22 days left
                premiumAmount = 56000.0,
                idvAmount = 2900000.0,
                agentContact = "Chennai RTO Desk",
                notes = "Inspection required before policy endorsement.",
                isCurrent = true
            ),
            // Vehicle 5: Valid for 180 days
            InsuranceRecordEntity(
                id = 5,
                vehicleId = 5,
                policyNumber = "ALLIANZ-HAZ-99014",
                providerName = "Bajaj Allianz Hazardous Goods",
                policyType = "Comprehensive Tanker Coverage",
                startDate = now - (185 * ONE_DAY_MS),
                expiryDate = now + (180 * ONE_DAY_MS),
                premiumAmount = 72000.0,
                idvAmount = 4500000.0,
                agentContact = "Ahmedabad Corporate Office",
                notes = "Hazardous chemical transit clause active with third-party liability 50L.",
                isCurrent = true
            ),
            // Vehicle 6: Valid for 310 days
            InsuranceRecordEntity(
                id = 6,
                vehicleId = 6,
                policyNumber = "CHOLA-MS-BUS-4491",
                providerName = "Cholamandalam MS General",
                policyType = "Passenger Carrier Comprehensive",
                startDate = now - (55 * ONE_DAY_MS),
                expiryDate = now + (310 * ONE_DAY_MS),
                premiumAmount = 34500.0,
                idvAmount = 2100000.0,
                agentContact = "Hyderabad Bus Fleet Division",
                notes = "Passenger legal liability 17 occupants covered.",
                isCurrent = true
            )
        )
    }

    fun getInitialFitness(now: Long = System.currentTimeMillis()): List<FitnessCertificateEntity> {
        return listOf(
            // Vehicle 1: Valid for 110 days
            FitnessCertificateEntity(
                id = 1,
                vehicleId = 1,
                certificateNumber = "FC-MH12-2025-004812",
                rtoLocation = "Pune RTO Central (MH-12)",
                issueDate = now - (255 * ONE_DAY_MS),
                expiryDate = now + (110 * ONE_DAY_MS),
                inspectionFee = 1500.0,
                speedGovernorStatus = "Compliant",
                emissionStatus = "PUC Valid",
                reflectiveTapeValid = true,
                brakeTestPassed = true,
                inspectionNotes = "Chassis visual inspection passed. Speed limiter sealed at 80 km/h.",
                isCurrent = true
            ),
            // Vehicle 2: Expiring in 12 days (CRITICAL URGENT FC RENEWAL)
            FitnessCertificateEntity(
                id = 2,
                vehicleId = 2,
                certificateNumber = "FC-KA01-2024-991204",
                rtoLocation = "Bengaluru Central RTO (KA-01)",
                issueDate = now - (353 * ONE_DAY_MS),
                expiryDate = now + (12 * ONE_DAY_MS), // 12 days left!
                inspectionFee = 1200.0,
                speedGovernorStatus = "Calibrated",
                emissionStatus = "PUC Valid",
                reflectiveTapeValid = true,
                brakeTestPassed = true,
                inspectionNotes = "Upcoming RTO automated testing track appointment needed before expiry.",
                isCurrent = true
            ),
            // Vehicle 3: Expired 10 days ago (EXPIRED FC ALERT)
            FitnessCertificateEntity(
                id = 3,
                vehicleId = 3,
                certificateNumber = "FC-DL01-2024-551029",
                rtoLocation = "Delhi Mall Road RTO (DL-01)",
                issueDate = now - (375 * ONE_DAY_MS),
                expiryDate = now - (10 * ONE_DAY_MS), // EXPIRED 10 days ago!
                inspectionFee = 1000.0,
                speedGovernorStatus = "Pending",
                emissionStatus = "PUC Expired",
                reflectiveTapeValid = false,
                brakeTestPassed = true,
                inspectionNotes = "FC expired! Heavy penalty per day under Central Motor Vehicle Rules. Urgent renewal test scheduled.",
                isCurrent = true
            ),
            // Vehicle 4: Expiring in 5 days (CRITICAL URGENT)
            FitnessCertificateEntity(
                id = 4,
                vehicleId = 4,
                certificateNumber = "FC-TN09-2024-338291",
                rtoLocation = "Chennai South RTO (TN-09)",
                issueDate = now - (360 * ONE_DAY_MS),
                expiryDate = now + (5 * ONE_DAY_MS), // 5 days left!
                inspectionFee = 1800.0,
                speedGovernorStatus = "Compliant",
                emissionStatus = "PUC Valid",
                reflectiveTapeValid = true,
                brakeTestPassed = true,
                inspectionNotes = "Brake pad replacement done in shop; ready for RTO physical inspection slot.",
                isCurrent = true
            ),
            // Vehicle 5: Valid for 290 days
            FitnessCertificateEntity(
                id = 5,
                vehicleId = 5,
                certificateNumber = "FC-GJ06-2025-771920",
                rtoLocation = "Vadodara RTO (GJ-06)",
                issueDate = now - (75 * ONE_DAY_MS),
                expiryDate = now + (290 * ONE_DAY_MS),
                inspectionFee = 2500.0,
                speedGovernorStatus = "Compliant",
                emissionStatus = "PUC Valid",
                reflectiveTapeValid = true,
                brakeTestPassed = true,
                inspectionNotes = "Hydrostatic pressure test for tanker shell passed. PESO compliance endorsed.",
                isCurrent = true
            ),
            // Vehicle 6: Valid for 320 days
            FitnessCertificateEntity(
                id = 6,
                vehicleId = 6,
                certificateNumber = "FC-TS07-2025-110482",
                rtoLocation = "Hyderabad Khairatabad RTO (TS-07)",
                issueDate = now - (45 * ONE_DAY_MS),
                expiryDate = now + (320 * ONE_DAY_MS),
                inspectionFee = 1600.0,
                speedGovernorStatus = "Compliant",
                emissionStatus = "PUC Valid",
                reflectiveTapeValid = true,
                brakeTestPassed = true,
                inspectionNotes = "Passenger emergency exit door test passed. First aid kit verified.",
                isCurrent = true
            )
        )
    }

    fun getInitialPermits(now: Long = System.currentTimeMillis()): List<CompliancePermitEntity> {
        return listOf(
            CompliancePermitEntity(
                id = 1,
                vehicleId = 1,
                permitType = "National Goods Permit",
                documentNumber = "NP-IND-2024-99881",
                expiryDate = now + (90 * ONE_DAY_MS),
                fee = 16500.0,
                isCurrent = true
            ),
            CompliancePermitEntity(
                id = 2,
                vehicleId = 2,
                permitType = "State Stage Carriage",
                documentNumber = "KA-PERMIT-2024-4412",
                expiryDate = now + (45 * ONE_DAY_MS),
                fee = 8200.0,
                isCurrent = true
            ),
            CompliancePermitEntity(
                id = 3,
                vehicleId = 3,
                permitType = "Road Tax (Quarterly)",
                documentNumber = "DL-TAX-Q3-2026",
                expiryDate = now - (2 * ONE_DAY_MS),
                fee = 4500.0,
                isCurrent = true
            ),
            CompliancePermitEntity(
                id = 4,
                vehicleId = 4,
                permitType = "Road Tax (Annual)",
                documentNumber = "TN-TAX-ANN-2025",
                expiryDate = now + (140 * ONE_DAY_MS),
                fee = 22000.0,
                isCurrent = true
            ),
            CompliancePermitEntity(
                id = 5,
                vehicleId = 5,
                permitType = "National Goods Permit (Hazmat)",
                documentNumber = "NP-HAZ-2025-3391",
                expiryDate = now + (210 * ONE_DAY_MS),
                fee = 25000.0,
                isCurrent = true
            ),
            CompliancePermitEntity(
                id = 6,
                vehicleId = 6,
                permitType = "All India Tourist Permit",
                documentNumber = "AITP-TS-2025-7710",
                expiryDate = now + (300 * ONE_DAY_MS),
                fee = 18000.0,
                isCurrent = true
            )
        )
    }
}
