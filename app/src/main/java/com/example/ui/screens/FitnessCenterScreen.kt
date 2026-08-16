package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.VehicleWithCompliance
import com.example.ui.theme.AmberAlert
import com.example.ui.theme.AmberAlertContainer
import com.example.ui.theme.GreenValid
import com.example.ui.theme.GreenValidContainer
import com.example.ui.theme.RedExpired
import com.example.ui.theme.RedExpiredContainer
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val dateFormatter = SimpleDateFormat("dd MMM yyyy", Locale.US)

@Composable
fun FitnessCenterScreen(
    vehicles: List<VehicleWithCompliance>,
    onRenewFitness: (VehicleWithCompliance) -> Unit,
    onViewVehicle: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val now = System.currentTimeMillis()
    var selectedTabFilter by remember { mutableStateOf("ALL") }

    val filteredList = vehicles.filter { item ->
        val days = item.getFitnessDaysRemaining(now)
        when (selectedTabFilter) {
            "DUE" -> days != null && days in 0..30
            "EXPIRED" -> days != null && days < 0
            "VALID" -> days != null && days > 30
            else -> true
        }
    }

    var totalFcFees = 0.0
    var expiredCount = 0
    var dueCount = 0

    vehicles.forEach { item ->
        val fc = item.currentFitness
        if (fc != null) {
            totalFcFees += fc.inspectionFee
            val days = item.getFitnessDaysRemaining(now)
            if (days != null) {
                if (days < 0) expiredCount++
                else if (days <= 30) dueCount++
            }
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(6.dp))

            // Summary Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = AmberAlertContainer),
                shape = RoundedCornerShape(18.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
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
                                text = "RTO Fitness Certificate (FC) Monitor",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onTertiaryContainer
                            )
                        }
                        Text(
                            text = "${vehicles.size} Tracked",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = AmberAlert
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Pending RTO Inspection Slots", fontSize = 11.sp, color = Color(0xFF78350F))
                            Text("${dueCount + expiredCount} Vehicles", fontSize = 16.sp, fontWeight = FontWeight.Black)
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Total Estimated Inspection Fees", fontSize = 11.sp, color = Color(0xFF78350F))
                            Text("₹${NumberFormat.getNumberInstance().format(totalFcFees.toInt())}", fontSize = 16.sp, fontWeight = FontWeight.Black)
                        }
                    }
                }
            }
        }

        // Filter Pills
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(
                    "ALL" to "All FCs (${vehicles.size})",
                    "DUE" to "Due Soon ($dueCount)",
                    "EXPIRED" to "Expired at RTO ($expiredCount)",
                    "VALID" to "Valid Certificates"
                ).forEach { (key, label) ->
                    FilterChip(
                        selected = selectedTabFilter == key,
                        onClick = { selectedTabFilter = key },
                        label = { Text(label) }
                    )
                }
            }
        }

        // List
        if (filteredList.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = GreenValid, modifier = Modifier.size(40.dp))
                        Text("No vehicles match this fitness certificate filter", fontWeight = FontWeight.Bold)
                    }
                }
            }
        } else {
            items(filteredList, key = { it.vehicle.id }) { item ->
                FitnessCertificateCard(
                    item = item,
                    onRenew = { onRenewFitness(item) },
                    onViewVehicle = { onViewVehicle(item.vehicle.id) }
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

@Composable
fun FitnessCertificateCard(
    item: VehicleWithCompliance,
    onRenew: () -> Unit,
    onViewVehicle: () -> Unit
) {
    val now = System.currentTimeMillis()
    val fc = item.currentFitness
    val daysRemaining = item.getFitnessDaysRemaining(now)

    val (statusColor, bgTint, statusText) = when {
        daysRemaining == null -> Triple(Color.Gray, Color(0xFFF1F5F9), "No FC Recorded")
        daysRemaining < 0 -> Triple(RedExpired, RedExpiredContainer, "FC EXPIRED (${-daysRemaining}d ago)")
        daysRemaining <= 15 -> Triple(RedExpired, RedExpiredContainer, "CRITICAL DUE ($daysRemaining days)")
        daysRemaining <= 30 -> Triple(AmberAlert, AmberAlertContainer, "Due for RTO Test in $daysRemaining d")
        else -> Triple(GreenValid, GreenValidContainer, "FC Valid ($daysRemaining days)")
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(0.5.dp, statusColor.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
            .clickable { onViewVehicle() }
            .testTag("fitness_card_${item.vehicle.regNumber}"),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
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
                            .size(38.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(bgTint),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Assignment, contentDescription = null, tint = statusColor, modifier = Modifier.size(20.dp))
                    }
                    Column {
                        Text(
                            text = item.vehicle.regNumber,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "${item.vehicle.makeModel} • ${item.vehicle.driverName}",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(bgTint)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = statusText,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = statusColor
                    )
                }
            }

            if (fc != null) {
                // FC Details
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("FC Number", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(fc.certificateNumber, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("RTO Testing Authority", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(fc.rtoLocation, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }

                // Checklist Badges
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (fc.speedGovernorStatus == "Compliant") GreenValidContainer else AmberAlertContainer)
                            .padding(6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Speed Gov: ${fc.speedGovernorStatus}", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (fc.emissionStatus == "PUC Valid") GreenValidContainer else RedExpiredContainer)
                            .padding(6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Emission: ${fc.emissionStatus}", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Expiry Date", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(dateFormatter.format(Date(fc.expiryDate)), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    }
                    Column {
                        Text("Inspection Fee", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("₹${NumberFormat.getNumberInstance().format(fc.inspectionFee)}", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Brake Test", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(if (fc.brakeTestPassed) "PASSED" else "FAILED", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = if (fc.brakeTestPassed) GreenValid else RedExpired)
                    }
                }
            }

            // Renewal action bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Reflective Tape: ${if (fc?.reflectiveTapeValid == true) "Compliant" else "Pending"}",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Button(
                    onClick = onRenew,
                    colors = ButtonDefaults.buttonColors(containerColor = if (daysRemaining != null && daysRemaining <= 30) AmberAlert else MaterialTheme.colorScheme.primary),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("renew_fc_btn_${item.vehicle.regNumber}")
                ) {
                    Icon(Icons.Default.Assignment, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Renew FC", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
