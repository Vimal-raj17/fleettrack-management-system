package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.outlined.Assignment
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.ComplianceStatus
import com.example.data.model.FitnessCertificateEntity
import com.example.data.model.InsuranceRecordEntity
import com.example.data.model.VehicleEntity
import com.example.data.model.VehicleWithCompliance
import com.example.ui.theme.AmberAlert
import com.example.ui.theme.AmberAlertContainer
import com.example.ui.theme.BluePrimary
import com.example.ui.theme.GreenValid
import com.example.ui.theme.GreenValidContainer
import com.example.ui.theme.RedExpired
import com.example.ui.theme.RedExpiredContainer
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

private val dateFormatter = SimpleDateFormat("dd MMM yyyy", Locale.US)
private val ONE_DAY_MS = 24L * 60 * 60 * 1000

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditVehicleDialog(
    vehicle: VehicleEntity?,
    onDismiss: () -> Unit,
    onSave: (
        id: Long,
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
    ) -> Unit
) {
    var regNumber by remember { mutableStateOf(vehicle?.regNumber ?: "") }
    var makeModel by remember { mutableStateOf(vehicle?.makeModel ?: "") }
    var vehicleType by remember { mutableStateOf(vehicle?.vehicleType ?: "Heavy Truck") }
    var manufactureYear by remember { mutableStateOf(vehicle?.manufactureYear?.toString() ?: "2023") }
    var fuelType by remember { mutableStateOf(vehicle?.fuelType ?: "Diesel") }
    var chassisNumber by remember { mutableStateOf(vehicle?.chassisNumber ?: "") }
    var engineNumber by remember { mutableStateOf(vehicle?.engineNumber ?: "") }
    var driverName by remember { mutableStateOf(vehicle?.driverName ?: "") }
    var driverPhone by remember { mutableStateOf(vehicle?.driverPhone ?: "") }
    var currentOdometer by remember { mutableStateOf(vehicle?.currentOdometer?.toString() ?: "50000") }
    var status by remember { mutableStateOf(vehicle?.status ?: "Active") }

    val vehicleTypes = listOf("Heavy Truck", "Medium Cargo", "Light Van", "Passenger Bus", "Tanker", "Trailer", "Pickup")
    val fuelTypes = listOf("Diesel", "CNG", "Electric", "Petrol")
    val statusOptions = listOf("Active", "Maintenance", "Inactive")

    var typeExpanded by remember { mutableStateOf(false) }
    var fuelExpanded by remember { mutableStateOf(false) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .fillMaxHeight(0.9f)
                .clip(RoundedCornerShape(24.dp)),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Title & Close
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = if (vehicle == null) "Register New Fleet Vehicle" else "Edit Vehicle Information",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Manage master registration details and compliance links",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Registration Number
                    OutlinedTextField(
                        value = regNumber,
                        onValueChange = { regNumber = it.uppercase() },
                        label = { Text("Registration Number (e.g. MH-12-RN-8821)*") },
                        placeholder = { Text("STATE-CODE-SERIES-NUM") },
                        modifier = Modifier.fillMaxWidth().testTag("input_reg_number"),
                        singleLine = true
                    )

                    // Make & Model
                    OutlinedTextField(
                        value = makeModel,
                        onValueChange = { makeModel = it },
                        label = { Text("Make & Model (e.g. Tata Signa 4825.T)*") },
                        modifier = Modifier.fillMaxWidth().testTag("input_make_model"),
                        singleLine = true
                    )

                    // Vehicle Type Dropdown
                    ExposedDropdownMenuBox(
                        expanded = typeExpanded,
                        onExpandedChange = { typeExpanded = !typeExpanded },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = vehicleType,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Vehicle Category") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = typeExpanded) },
                            modifier = Modifier.menuAnchor().fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = typeExpanded,
                            onDismissRequest = { typeExpanded = false }
                        ) {
                            vehicleTypes.forEach { type ->
                                DropdownMenuItem(
                                    text = { Text(type) },
                                    onClick = {
                                        vehicleType = type
                                        typeExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    // Row: Year & Fuel Type
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = manufactureYear,
                            onValueChange = { manufactureYear = it },
                            label = { Text("Mfg Year") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )

                        ExposedDropdownMenuBox(
                            expanded = fuelExpanded,
                            onExpandedChange = { fuelExpanded = !fuelExpanded },
                            modifier = Modifier.weight(1f)
                        ) {
                            OutlinedTextField(
                                value = fuelType,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Fuel") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = fuelExpanded) },
                                modifier = Modifier.menuAnchor().fillMaxWidth()
                            )
                            ExposedDropdownMenu(
                                expanded = fuelExpanded,
                                onDismissRequest = { fuelExpanded = false }
                            ) {
                                fuelTypes.forEach { fuel ->
                                    DropdownMenuItem(
                                        text = { Text(fuel) },
                                        onClick = {
                                            fuelType = fuel
                                            fuelExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    // Chassis & Engine Number
                    OutlinedTextField(
                        value = chassisNumber,
                        onValueChange = { chassisNumber = it.uppercase() },
                        label = { Text("Chassis / VIN Number") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = engineNumber,
                        onValueChange = { engineNumber = it.uppercase() },
                        label = { Text("Engine Serial Number") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    // Driver Info Section
                    Text(
                        text = "Assigned Driver Details",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    OutlinedTextField(
                        value = driverName,
                        onValueChange = { driverName = it },
                        label = { Text("Driver Full Name*") },
                        modifier = Modifier.fillMaxWidth().testTag("input_driver_name"),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = driverPhone,
                        onValueChange = { driverPhone = it },
                        label = { Text("Driver Contact Number") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    // Odometer & Status
                    OutlinedTextField(
                        value = currentOdometer,
                        onValueChange = { currentOdometer = it },
                        label = { Text("Current Odometer (km)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Text(
                        text = "Operational Status",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        statusOptions.forEach { opt ->
                            FilterChip(
                                selected = status == opt,
                                onClick = { status = opt },
                                label = { Text(opt) }
                            )
                        }
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                // Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cancel")
                    }

                    Button(
                        onClick = {
                            val year = manufactureYear.toIntOrNull() ?: 2023
                            val odo = currentOdometer.toIntOrNull() ?: 0
                            if (regNumber.isNotBlank() && makeModel.isNotBlank()) {
                                onSave(
                                    vehicle?.id ?: 0L,
                                    regNumber,
                                    makeModel,
                                    vehicleType,
                                    year,
                                    fuelType,
                                    chassisNumber,
                                    engineNumber,
                                    driverName.ifBlank { "Unassigned" },
                                    driverPhone,
                                    odo,
                                    status
                                )
                            }
                        },
                        enabled = regNumber.isNotBlank() && makeModel.isNotBlank(),
                        modifier = Modifier.weight(1f).testTag("save_vehicle_button")
                    ) {
                        Text(if (vehicle == null) "Register Vehicle" else "Save Changes")
                    }
                }
            }
        }
    }
}

@Composable
fun RenewInsuranceDialog(
    vehicleWithCompliance: VehicleWithCompliance,
    onDismiss: () -> Unit,
    onRenew: (
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
    ) -> Unit
) {
    val existing = vehicleWithCompliance.currentInsurance
    val defaultStartDate = System.currentTimeMillis()
    val defaultExpiryDate = defaultStartDate + (365L * ONE_DAY_MS) // 1 year forward

    var policyNumber by remember { mutableStateOf(existing?.policyNumber?.let { "${it}-RN" } ?: "POL-${System.currentTimeMillis() % 100000}") }
    var providerName by remember { mutableStateOf(existing?.providerName ?: "Tata AIG General Insurance") }
    var policyType by remember { mutableStateOf(existing?.policyType ?: "Comprehensive Commercial") }
    var durationMonths by remember { mutableIntStateOf(12) }
    var premiumAmount by remember { mutableStateOf(existing?.premiumAmount?.toString() ?: "45000") }
    var idvAmount by remember { mutableStateOf(existing?.idvAmount?.toString() ?: "2000000") }
    var agentContact by remember { mutableStateOf(existing?.agentContact ?: "") }
    var notes by remember { mutableStateOf("Policy renewed on ${dateFormatter.format(Date())} with zero depreciation cover.") }

    val providers = listOf("Tata AIG General Insurance", "HDFC ERGO Commercial", "ICICI Lombard", "Oriental Insurance", "Bajaj Allianz", "Cholamandalam MS", "New India Assurance")
    val policyTypes = listOf("Comprehensive Commercial", "Third Party Only", "Comprehensive + Zero Dep", "Hazardous Goods Coverage", "Commercial Floater")

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .fillMaxHeight(0.9f)
                .clip(RoundedCornerShape(24.dp)),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(BluePrimary.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Shield, contentDescription = null, tint = BluePrimary)
                        }
                        Column {
                            Text(
                                text = "Renew Insurance Policy",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Vehicle: ${vehicleWithCompliance.vehicle.regNumber} (${vehicleWithCompliance.vehicle.makeModel})",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Quick Duration selector
                    Text(
                        text = "Policy Duration / Term",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(12 to "1 Year", 24 to "2 Years", 36 to "3 Years").forEach { (months, label) ->
                            FilterChip(
                                selected = durationMonths == months,
                                onClick = { durationMonths = months },
                                label = { Text(label) }
                            )
                        }
                    }

                    val computedExpiry = defaultStartDate + (durationMonths * 30.5 * ONE_DAY_MS).toLong()

                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("New Policy Period", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(
                                    text = "${dateFormatter.format(Date(defaultStartDate))} → ${dateFormatter.format(Date(computedExpiry))}",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        }
                    }

                    // Policy Number
                    OutlinedTextField(
                        value = policyNumber,
                        onValueChange = { policyNumber = it },
                        label = { Text("New Policy / Certificate Number*") },
                        modifier = Modifier.fillMaxWidth().testTag("input_policy_number"),
                        singleLine = true
                    )

                    // Insurance Provider
                    OutlinedTextField(
                        value = providerName,
                        onValueChange = { providerName = it },
                        label = { Text("Insurance Underwriter / Company*") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    // Policy Type
                    OutlinedTextField(
                        value = policyType,
                        onValueChange = { policyType = it },
                        label = { Text("Policy Coverage Type") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    // Premium & IDV
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = premiumAmount,
                            onValueChange = { premiumAmount = it },
                            label = { Text("Annual Premium (₹)*") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f).testTag("input_premium_amount"),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = idvAmount,
                            onValueChange = { idvAmount = it },
                            label = { Text("Vehicle IDV (₹)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }

                    // Agent Contact & Notes
                    OutlinedTextField(
                        value = agentContact,
                        onValueChange = { agentContact = it },
                        label = { Text("Agent / Broker Contact") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text("Policy Remarks / Endorsement Notes") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 3
                    )
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cancel")
                    }

                    Button(
                        onClick = {
                            val prem = premiumAmount.toDoubleOrNull() ?: 0.0
                            val idv = idvAmount.toDoubleOrNull() ?: 0.0
                            val computedExpiry = defaultStartDate + (durationMonths * 30.5 * ONE_DAY_MS).toLong()
                            if (policyNumber.isNotBlank() && providerName.isNotBlank()) {
                                onRenew(
                                    vehicleWithCompliance.vehicle.id,
                                    policyNumber,
                                    providerName,
                                    policyType,
                                    defaultStartDate,
                                    computedExpiry,
                                    prem,
                                    idv,
                                    agentContact,
                                    notes
                                )
                            }
                        },
                        enabled = policyNumber.isNotBlank() && providerName.isNotBlank(),
                        modifier = Modifier.weight(1f).testTag("confirm_renew_insurance_button")
                    ) {
                        Text("Confirm Renewal")
                    }
                }
            }
        }
    }
}

@Composable
fun RenewFitnessDialog(
    vehicleWithCompliance: VehicleWithCompliance,
    onDismiss: () -> Unit,
    onRenew: (
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
    ) -> Unit
) {
    val existing = vehicleWithCompliance.currentFitness
    val defaultIssueDate = System.currentTimeMillis()
    val defaultExpiryDate = defaultIssueDate + (365L * ONE_DAY_MS) // 1 year forward

    var certificateNumber by remember { mutableStateOf(existing?.certificateNumber?.let { "${it}-RN" } ?: "FC-${vehicleWithCompliance.vehicle.regNumber.take(4)}-${System.currentTimeMillis() % 10000}") }
    var rtoLocation by remember { mutableStateOf(existing?.rtoLocation ?: "Regional Transport Office (RTO)") }
    var durationYears by remember { mutableIntStateOf(1) }
    var inspectionFee by remember { mutableStateOf(existing?.inspectionFee?.toString() ?: "1500") }
    var speedGovernorStatus by remember { mutableStateOf(existing?.speedGovernorStatus ?: "Compliant") }
    var emissionStatus by remember { mutableStateOf(existing?.emissionStatus ?: "PUC Valid") }
    var reflectiveTapeValid by remember { mutableStateOf(existing?.reflectiveTapeValid ?: true) }
    var brakeTestPassed by remember { mutableStateOf(existing?.brakeTestPassed ?: true) }
    var inspectionNotes by remember { mutableStateOf("Passed automated inspection track test. Speed limiter & reflectors verified.") }

    val speedGovernorOptions = listOf("Compliant", "Calibrated", "Pending")
    val emissionOptions = listOf("PUC Valid", "PUC Expired", "Exempt")

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .fillMaxHeight(0.92f)
                .clip(RoundedCornerShape(24.dp)),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(AmberAlert.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Assignment, contentDescription = null, tint = AmberAlert)
                        }
                        Column {
                            Text(
                                text = "Renew Fitness Certificate (FC)",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Vehicle: ${vehicleWithCompliance.vehicle.regNumber} (${vehicleWithCompliance.vehicle.makeModel})",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Validity period
                    Text(
                        text = "FC Validity Period",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(1 to "1 Year (Standard)", 2 to "2 Years (New Vehicle)").forEach { (yrs, label) ->
                            FilterChip(
                                selected = durationYears == yrs,
                                onClick = { durationYears = yrs },
                                label = { Text(label) }
                            )
                        }
                    }

                    val computedExpiry = defaultIssueDate + (durationYears * 365L * ONE_DAY_MS)

                    Card(
                        colors = CardDefaults.cardColors(containerColor = AmberAlertContainer.copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("New FC Validity", fontSize = 11.sp, color = AmberAlert)
                                Text(
                                    text = "${dateFormatter.format(Date(defaultIssueDate))} → ${dateFormatter.format(Date(computedExpiry))}",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = AmberAlert)
                        }
                    }

                    // Certificate Number & RTO
                    OutlinedTextField(
                        value = certificateNumber,
                        onValueChange = { certificateNumber = it.uppercase() },
                        label = { Text("New FC Certificate Number*") },
                        modifier = Modifier.fillMaxWidth().testTag("input_fc_number"),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = rtoLocation,
                        onValueChange = { rtoLocation = it },
                        label = { Text("Testing Center / RTO Authority*") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = inspectionFee,
                        onValueChange = { inspectionFee = it },
                        label = { Text("RTO Inspection Fee (₹)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    // Mandatory RTO Inspection Checklist
                    Text(
                        text = "RTO Compliance Checklist Inspection",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            // Speed Governor
                            Text("Speed Governor Limiter:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                speedGovernorOptions.forEach { opt ->
                                    FilterChip(
                                        selected = speedGovernorStatus == opt,
                                        onClick = { speedGovernorStatus = opt },
                                        label = { Text(opt, fontSize = 11.sp) }
                                    )
                                }
                            }

                            // Emission Status
                            Text("Emission / PUC Status:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                emissionOptions.forEach { opt ->
                                    FilterChip(
                                        selected = emissionStatus == opt,
                                        onClick = { emissionStatus = opt },
                                        label = { Text(opt, fontSize = 11.sp) }
                                    )
                                }
                            }

                            // Checklist checks
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = reflectiveTapeValid,
                                    onCheckedChange = { reflectiveTapeValid = it }
                                )
                                Text("High-Intensity Reflective Tape Verified (AIS-090)", fontSize = 12.sp)
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = brakeTestPassed,
                                    onCheckedChange = { brakeTestPassed = it }
                                )
                                Text("Roller Brake & Dynamic Stopping Test Passed", fontSize = 12.sp)
                            }
                        }
                    }

                    OutlinedTextField(
                        value = inspectionNotes,
                        onValueChange = { inspectionNotes = it },
                        label = { Text("RTO Inspector Remarks / Fitness Report") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 3
                    )
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cancel")
                    }

                    Button(
                        onClick = {
                            val fee = inspectionFee.toDoubleOrNull() ?: 0.0
                            val computedExpiry = defaultIssueDate + (durationYears * 365L * ONE_DAY_MS)
                            if (certificateNumber.isNotBlank() && rtoLocation.isNotBlank()) {
                                onRenew(
                                    vehicleWithCompliance.vehicle.id,
                                    certificateNumber,
                                    rtoLocation,
                                    defaultIssueDate,
                                    computedExpiry,
                                    fee,
                                    speedGovernorStatus,
                                    emissionStatus,
                                    reflectiveTapeValid,
                                    brakeTestPassed,
                                    inspectionNotes
                                )
                            }
                        },
                        enabled = certificateNumber.isNotBlank() && rtoLocation.isNotBlank(),
                        modifier = Modifier.weight(1f).testTag("confirm_renew_fc_button")
                    ) {
                        Text("Renew FC Certificate")
                    }
                }
            }
        }
    }
}

@Composable
fun AddPermitDialog(
    vehicleWithCompliance: VehicleWithCompliance,
    onDismiss: () -> Unit,
    onSave: (
        vehicleId: Long,
        permitType: String,
        documentNumber: String,
        expiryDate: Long,
        fee: Double
    ) -> Unit
) {
    var permitType by remember { mutableStateOf("National Goods Permit") }
    var documentNumber by remember { mutableStateOf("NP-${vehicleWithCompliance.vehicle.regNumber.take(4)}-${System.currentTimeMillis() % 10000}") }
    var fee by remember { mutableStateOf("15000") }
    var durationMonths by remember { mutableIntStateOf(12) }

    val permitTypes = listOf("National Goods Permit", "State Stage Carriage", "Road Tax (Quarterly)", "Road Tax (Annual)", "Green Tax", "Hazardous Material Permit")

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp)),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "Add Permit / Tax Document",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "For ${vehicleWithCompliance.vehicle.regNumber}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Permit types chips
                Text("Document Type:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    permitTypes.chunked(2).forEach { row ->
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            row.forEach { type ->
                                FilterChip(
                                    selected = permitType == type,
                                    onClick = { permitType = type },
                                    label = { Text(type, fontSize = 11.sp) }
                                )
                            }
                        }
                    }
                }

                OutlinedTextField(
                    value = documentNumber,
                    onValueChange = { documentNumber = it.uppercase() },
                    label = { Text("Permit / Tax Challan Number*") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = fee,
                    onValueChange = { fee = it },
                    label = { Text("Govt Fee Paid (₹)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f)) {
                        Text("Cancel")
                    }
                    Button(
                        onClick = {
                            val f = fee.toDoubleOrNull() ?: 0.0
                            val exp = System.currentTimeMillis() + (durationMonths * 30.5 * ONE_DAY_MS).toLong()
                            if (documentNumber.isNotBlank()) {
                                onSave(vehicleWithCompliance.vehicle.id, permitType, documentNumber, exp, f)
                            }
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Save Record")
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VehicleDetailDossierSheet(
    item: VehicleWithCompliance,
    onDismiss: () -> Unit,
    onRenewInsurance: () -> Unit,
    onRenewFitness: () -> Unit,
    onAddPermit: () -> Unit,
    onEditVehicle: () -> Unit,
    onDeleteVehicle: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val now = System.currentTimeMillis()
    var showDeleteConfirm by remember { mutableStateOf(false) }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Delete Vehicle from Fleet?") },
            text = { Text("Are you sure you want to remove ${item.vehicle.regNumber}? All associated insurance records, fitness certificates, and tax history will also be permanently deleted.") },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteConfirm = false
                        onDeleteVehicle()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RedExpired)
                ) {
                    Text("Delete Forever")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showDeleteConfirm = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        dragHandle = null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.92f)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Header Bar
            Surface(
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 2.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(MaterialTheme.colorScheme.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocalShipping,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(26.dp)
                            )
                        }
                        Column {
                            Text(
                                text = item.vehicle.regNumber,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 0.8.sp,
                                fontFamily = FontFamily.Monospace,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "${item.vehicle.makeModel} • ${item.vehicle.manufactureYear}",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Row {
                        IconButton(onClick = onEditVehicle) {
                            Icon(Icons.Default.Refresh, contentDescription = "Edit")
                        }
                        IconButton(onClick = { showDeleteConfirm = true }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = RedExpired)
                        }
                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Default.Close, contentDescription = "Close")
                        }
                    }
                }
            }

            // Scrollable Dossier Content
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Status Summary
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    StatusBadge(status = item.getOverallComplianceStatus(now))
                    Text(
                        text = "Status: ${item.vehicle.status}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (item.vehicle.status == "Active") GreenValid else AmberAlert
                    )
                }

                // 1. VEHICLE SPECS CARD
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "Vehicle Specifications & Driver",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )

                        DossierRow(label = "Chassis / VIN", value = item.vehicle.chassisNumber.ifBlank { "N/A" })
                        DossierRow(label = "Engine Number", value = item.vehicle.engineNumber.ifBlank { "N/A" })
                        DossierRow(label = "Fuel Type", value = item.vehicle.fuelType)
                        DossierRow(label = "Odometer Reading", value = "${NumberFormat.getNumberInstance().format(item.vehicle.currentOdometer)} km")
                        DossierRow(label = "Assigned Driver", value = "${item.vehicle.driverName} (${item.vehicle.driverPhone})")
                    }
                }

                // 2. ACTIVE INSURANCE SECTION
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(Icons.Default.Shield, contentDescription = null, tint = BluePrimary)
                                Text(
                                    text = "Insurance Coverage",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            Button(
                                onClick = onRenewInsurance,
                                modifier = Modifier.testTag("dossier_renew_insurance_button")
                            ) {
                                Text("Renew Policy", fontSize = 12.sp)
                            }
                        }

                        val curIns = item.currentInsurance
                        if (curIns != null) {
                            val insDays = item.getInsuranceDaysRemaining(now) ?: 0
                            val isExp = insDays < 0

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isExp) RedExpiredContainer else if (insDays <= 30) AmberAlertContainer else GreenValidContainer)
                                    .padding(10.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = if (isExp) "Policy Expired (${-insDays} days ago)" else "Active: $insDays days remaining",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = if (isExp) RedExpired else if (insDays <= 30) AmberAlert else GreenValid
                                    )
                                    Text(
                                        text = "Expires ${dateFormatter.format(Date(curIns.expiryDate))}",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }

                            DossierRow(label = "Policy Number", value = curIns.policyNumber)
                            DossierRow(label = "Underwriter", value = curIns.providerName)
                            DossierRow(label = "Coverage Type", value = curIns.policyType)
                            DossierRow(label = "Annual Premium", value = "₹${NumberFormat.getNumberInstance().format(curIns.premiumAmount)}")
                            DossierRow(label = "Insured Declared Value (IDV)", value = "₹${NumberFormat.getNumberInstance().format(curIns.idvAmount)}")
                            if (curIns.agentContact.isNotBlank()) {
                                DossierRow(label = "Agent / Helpdesk", value = curIns.agentContact)
                            }
                            if (curIns.notes.isNotBlank()) {
                                DossierRow(label = "Notes", value = curIns.notes)
                            }

                            if (item.insuranceRecords.size > 1) {
                                Text(
                                    text = "Policy History (${item.insuranceRecords.size - 1} archived records)",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                item.insuranceRecords.filter { !it.isCurrent }.forEach { hist ->
                                    Text(
                                        text = "• ${hist.policyNumber} (${hist.providerName}) — Expired ${dateFormatter.format(Date(hist.expiryDate))}",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        } else {
                            Text("No active insurance policy recorded for this vehicle.", color = RedExpired, fontSize = 12.sp)
                        }
                    }
                }

                // 3. ACTIVE FITNESS CERTIFICATE (FC) SECTION
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(Icons.Default.Assignment, contentDescription = null, tint = AmberAlert)
                                Text(
                                    text = "Fitness Certificate (FC)",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            Button(
                                onClick = onRenewFitness,
                                colors = ButtonDefaults.buttonColors(containerColor = AmberAlert),
                                modifier = Modifier.testTag("dossier_renew_fc_button")
                            ) {
                                Text("Renew FC", fontSize = 12.sp)
                            }
                        }

                        val curFc = item.currentFitness
                        if (curFc != null) {
                            val fcDays = item.getFitnessDaysRemaining(now) ?: 0
                            val isExp = fcDays < 0

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isExp) RedExpiredContainer else if (fcDays <= 30) AmberAlertContainer else GreenValidContainer)
                                    .padding(10.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = if (isExp) "FC Expired (${-fcDays} days ago)" else "FC Valid: $fcDays days remaining",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = if (isExp) RedExpired else if (fcDays <= 30) AmberAlert else GreenValid
                                    )
                                    Text(
                                        text = "Expires ${dateFormatter.format(Date(curFc.expiryDate))}",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }

                            DossierRow(label = "FC Certificate No.", value = curFc.certificateNumber)
                            DossierRow(label = "RTO Location", value = curFc.rtoLocation)
                            DossierRow(label = "Issue Date", value = dateFormatter.format(Date(curFc.issueDate)))
                            DossierRow(label = "Speed Governor", value = curFc.speedGovernorStatus)
                            DossierRow(label = "Emission Status", value = curFc.emissionStatus)
                            DossierRow(label = "Reflective Tape", value = if (curFc.reflectiveTapeValid) "Compliant" else "Non-Compliant")
                            DossierRow(label = "Brake Test", value = if (curFc.brakeTestPassed) "Passed" else "Failed")
                            if (curFc.inspectionNotes.isNotBlank()) {
                                DossierRow(label = "Inspection Report", value = curFc.inspectionNotes)
                            }
                        } else {
                            Text("No Fitness Certificate (FC) registered for this vehicle.", color = RedExpired, fontSize = 12.sp)
                        }
                    }
                }

                // 4. PERMITS & ROAD TAX SECTION
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Permits & Road Tax Records",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            IconButton(onClick = onAddPermit) {
                                Icon(Icons.Default.Add, contentDescription = "Add Permit")
                            }
                        }

                        if (item.compliancePermits.isNotEmpty()) {
                            item.compliancePermits.forEach { permit ->
                                val pDays = (permit.expiryDate - now) / ONE_DAY_MS
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                                        .padding(10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(permit.permitType, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        Text("Doc: ${permit.documentNumber} • ₹${permit.fee}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                    Text(
                                        text = if (pDays < 0) "Expired" else "${pDays}d left",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (pDays < 0) RedExpired else if (pDays <= 30) AmberAlert else GreenValid
                                    )
                                }
                            }
                        } else {
                            Text("No additional permits on file.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DossierRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
