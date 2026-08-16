package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.ComplianceStatus
import com.example.data.model.VehicleWithCompliance
import com.example.ui.FleetFilter
import com.example.ui.FleetMetrics
import com.example.ui.FleetTab
import com.example.ui.components.MetricCard
import com.example.ui.components.UrgentRenewalAlertBanner
import com.example.ui.components.VehicleItemCard
import com.example.ui.theme.AmberAlert
import com.example.ui.theme.AmberAlertContainer
import com.example.ui.theme.BluePrimary
import com.example.ui.theme.BluePrimaryContainer
import com.example.ui.theme.GreenValid
import com.example.ui.theme.GreenValidContainer
import com.example.ui.theme.Navy900
import com.example.ui.theme.RedExpired
import com.example.ui.theme.RedExpiredContainer
import com.example.ui.theme.TealSecondary
import com.example.ui.theme.TealSecondaryContainer
import java.text.NumberFormat
import java.util.Locale

@Composable
fun DashboardScreen(
    metrics: FleetMetrics,
    vehicles: List<VehicleWithCompliance>,
    onNavigateTab: (FleetTab) -> Unit,
    onSetFilter: (FleetFilter) -> Unit,
    onViewVehicle: (Long) -> Unit,
    onAddNewVehicle: () -> Unit,
    onRenewInsurance: (VehicleWithCompliance) -> Unit,
    onRenewFitness: (VehicleWithCompliance) -> Unit,
    onEditVehicle: (VehicleWithCompliance) -> Unit,
    modifier: Modifier = Modifier
) {
    val now = System.currentTimeMillis()
    val urgentVehicles = vehicles.filter {
        val status = it.getOverallComplianceStatus(now)
        status == ComplianceStatus.CRITICAL || status == ComplianceStatus.EXPIRED || status == ComplianceStatus.DUE_SOON
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))

            // HERO BANNER
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp)),
                colors = CardDefaults.cardColors(containerColor = Navy900),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    Image(
                        painter = painterResource(id = R.drawable.img_fleet_hero),
                        contentDescription = "Fleet Hero Banner",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(170.dp),
                        contentScale = ContentScale.Crop,
                        alpha = 0.45f
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(GreenValid)
                            )
                            Text(
                                text = "FLEET COMPLIANCE HUB",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.2.sp,
                                color = GreenValid
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "FleetTrack Monitor",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )

                        Text(
                            text = "Insurance & FC Renewal Management with Relational Database",
                            fontSize = 12.sp,
                            color = Color(0xFFCBD5E1)
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = onAddNewVehicle,
                                colors = ButtonDefaults.buttonColors(containerColor = BluePrimary),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.testTag("dashboard_add_vehicle_button")
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("New Vehicle", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }

                            FilledTonalButton(
                                onClick = { onNavigateTab(FleetTab.MYSQL_HUB) },
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.testTag("dashboard_mysql_hub_button")
                            ) {
                                Icon(Icons.Default.Storage, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("MySQL DB", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // URGENT ALERT BANNER
        item {
            UrgentRenewalAlertBanner(
                urgentCount = metrics.insuranceDueSoon + metrics.fitnessDueSoon,
                expiredCount = metrics.insuranceExpired + metrics.fitnessExpired,
                onViewAlerts = {
                    onSetFilter(FleetFilter.CRITICAL_ATTENTION)
                    onNavigateTab(FleetTab.FLEET)
                }
            )
        }

        // COMPLIANCE HEALTH GAUGE
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
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
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.HealthAndSafety, contentDescription = null, tint = BluePrimary)
                            Text(
                                text = "Fleet Street-Legal Compliance",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Text(
                            text = "${metrics.complianceRatePercent}%",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            color = if (metrics.complianceRatePercent >= 80) GreenValid else AmberAlert
                        )
                    }

                    LinearProgressIndicator(
                        progress = { metrics.complianceRatePercent / 100f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = if (metrics.complianceRatePercent >= 80) GreenValid else AmberAlert,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "${metrics.activeVehicles} Active • ${metrics.maintenanceVehicles} In-Shop",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "${metrics.insuranceExpired + metrics.fitnessExpired} Expired Docs",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (metrics.insuranceExpired + metrics.fitnessExpired > 0) RedExpired else GreenValid
                        )
                    }
                }
            }
        }

        // METRICS GRID
        item {
            Text(
                text = "Key Operational Metrics",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                MetricCard(
                    title = "Total Fleet",
                    value = "${metrics.totalVehicles} Units",
                    subtitle = "${metrics.activeVehicles} Active on road",
                    icon = Icons.Default.LocalShipping,
                    color = BluePrimary,
                    bgColor = BluePrimaryContainer,
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigateTab(FleetTab.FLEET) }
                )

                MetricCard(
                    title = "Insurance Renewals",
                    value = "${metrics.insuranceDueSoon + metrics.insuranceExpired} Due",
                    subtitle = "${metrics.insuranceExpired} Expired",
                    icon = Icons.Default.Shield,
                    color = if (metrics.insuranceExpired > 0) RedExpired else AmberAlert,
                    bgColor = if (metrics.insuranceExpired > 0) RedExpiredContainer else AmberAlertContainer,
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigateTab(FleetTab.INSURANCE) }
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                MetricCard(
                    title = "FC (Fitness) Renewals",
                    value = "${metrics.fitnessDueSoon + metrics.fitnessExpired} Due",
                    subtitle = "${metrics.fitnessExpired} Expired at RTO",
                    icon = Icons.Default.Assignment,
                    color = if (metrics.fitnessExpired > 0) RedExpired else AmberAlert,
                    bgColor = if (metrics.fitnessExpired > 0) RedExpiredContainer else AmberAlertContainer,
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigateTab(FleetTab.FITNESS) }
                )

                MetricCard(
                    title = "Annual Premium Spend",
                    value = "₹${NumberFormat.getNumberInstance().format(metrics.totalInsuranceAnnualPremium.toInt())}",
                    subtitle = "Across all underwriters",
                    icon = Icons.Default.Payments,
                    color = TealSecondary,
                    bgColor = TealSecondaryContainer,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // URGENT ATTENTION VEHICLES
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Renewal Action Items (${urgentVehicles.size})",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = "View All Fleet",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.clickable { onNavigateTab(FleetTab.FLEET) }
                )
            }
        }

        if (urgentVehicles.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = GreenValidContainer.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = GreenValid, modifier = Modifier.size(28.dp))
                        Column {
                            Text("All Documents in Compliance", fontWeight = FontWeight.Bold, color = GreenValid)
                            Text("No vehicles currently have expired insurance policies or fitness certificates.", fontSize = 11.sp)
                        }
                    }
                }
            }
        } else {
            items(urgentVehicles, key = { it.vehicle.id }) { item ->
                VehicleItemCard(
                    item = item,
                    onViewDetails = { onViewVehicle(item.vehicle.id) },
                    onRenewInsurance = { onRenewInsurance(item) },
                    onRenewFitness = { onRenewFitness(item) },
                    onEdit = { onEditVehicle(item) }
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}
