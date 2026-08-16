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
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Timer
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
import com.example.data.model.InsuranceRecordEntity
import com.example.data.model.VehicleWithCompliance
import com.example.ui.theme.AmberAlert
import com.example.ui.theme.AmberAlertContainer
import com.example.ui.theme.BluePrimary
import com.example.ui.theme.BluePrimaryContainer
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
fun InsuranceCenterScreen(
    vehicles: List<VehicleWithCompliance>,
    onRenewInsurance: (VehicleWithCompliance) -> Unit,
    onViewVehicle: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val now = System.currentTimeMillis()
    var selectedTabFilter by remember { mutableStateOf("ALL") }

    val filteredList = vehicles.filter { item ->
        val days = item.getInsuranceDaysRemaining(now)
        when (selectedTabFilter) {
            "DUE" -> days != null && days in 0..30
            "EXPIRED" -> days != null && days < 0
            "VALID" -> days != null && days > 30
            else -> true
        }
    }

    var totalAnnualPremium = 0.0
    var totalIdv = 0.0
    var expiredCount = 0
    var dueCount = 0

    vehicles.forEach { item ->
        val ins = item.currentInsurance
        if (ins != null) {
            totalAnnualPremium += ins.premiumAmount
            totalIdv += ins.idvAmount
            val days = item.getInsuranceDaysRemaining(now)
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
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
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
                            Icon(Icons.Default.Shield, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Text(
                                text = "Fleet Insurance Portfolio",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                        Text(
                            text = "${vehicles.size} Covered",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Total Fleet IDV Valuation", fontSize = 11.sp, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f))
                            Text("₹${NumberFormat.getNumberInstance().format(totalIdv.toInt())}", fontSize = 16.sp, fontWeight = FontWeight.Black)
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Total Annual Premium", fontSize = 11.sp, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f))
                            Text("₹${NumberFormat.getNumberInstance().format(totalAnnualPremium.toInt())}", fontSize = 16.sp, fontWeight = FontWeight.Black)
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
                    "ALL" to "All Policies (${vehicles.size})",
                    "DUE" to "Due Soon ($dueCount)",
                    "EXPIRED" to "Expired ($expiredCount)",
                    "VALID" to "Valid Policies"
                ).forEach { (key, label) ->
                    FilterChip(
                        selected = selectedTabFilter == key,
                        onClick = { selectedTabFilter = key },
                        label = { Text(label) }
                    )
                }
            }
        }

        // Policy Cards List
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
                        Text("No policies match this filter criteria", fontWeight = FontWeight.Bold)
                    }
                }
            }
        } else {
            items(filteredList, key = { it.vehicle.id }) { item ->
                InsurancePolicyCard(
                    item = item,
                    onRenew = { onRenewInsurance(item) },
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
fun InsurancePolicyCard(
    item: VehicleWithCompliance,
    onRenew: () -> Unit,
    onViewVehicle: () -> Unit
) {
    val now = System.currentTimeMillis()
    val ins = item.currentInsurance
    val daysRemaining = item.getInsuranceDaysRemaining(now)

    val (statusColor, bgTint, statusText) = when {
        daysRemaining == null -> Triple(Color.Gray, Color(0xFFF1F5F9), "No Policy Recorded")
        daysRemaining < 0 -> Triple(RedExpired, RedExpiredContainer, "EXPIRED (${-daysRemaining}d ago)")
        daysRemaining <= 15 -> Triple(RedExpired, RedExpiredContainer, "CRITICAL EXPIRY ($daysRemaining days)")
        daysRemaining <= 30 -> Triple(AmberAlert, AmberAlertContainer, "Due for Renewal in $daysRemaining d")
        else -> Triple(GreenValid, GreenValidContainer, "Valid ($daysRemaining days)")
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(0.5.dp, statusColor.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
            .clickable { onViewVehicle() }
            .testTag("insurance_card_${item.vehicle.regNumber}"),
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
                        Icon(Icons.Default.Shield, contentDescription = null, tint = statusColor, modifier = Modifier.size(20.dp))
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

            if (ins != null) {
                // Policy Details Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Policy Number", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(ins.policyNumber, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Underwriter", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(ins.providerName, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Expiry Date", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(dateFormatter.format(Date(ins.expiryDate)), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    }
                    Column {
                        Text("Premium Paid", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("₹${NumberFormat.getNumberInstance().format(ins.premiumAmount)}", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Declared IDV", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("₹${NumberFormat.getNumberInstance().format(ins.idvAmount)}", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
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
                    text = "Policy Type: ${ins?.policyType ?: "Comprehensive"}",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Button(
                    onClick = onRenew,
                    colors = ButtonDefaults.buttonColors(containerColor = if (daysRemaining != null && daysRemaining <= 30) RedExpired else MaterialTheme.colorScheme.primary),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("renew_policy_btn_${item.vehicle.regNumber}")
                ) {
                    Icon(Icons.Default.Shield, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Renew Policy", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
