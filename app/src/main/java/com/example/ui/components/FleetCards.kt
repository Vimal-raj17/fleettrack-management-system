package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.Assignment
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ComplianceStatus
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
private val currencyFormatter = NumberFormat.getCurrencyInstance(Locale("en", "IN"))

@Composable
fun StatusBadge(
    status: ComplianceStatus,
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor, icon) = when (status) {
        ComplianceStatus.VALID -> Triple(GreenValidContainer, GreenValid, Icons.Default.CheckCircle)
        ComplianceStatus.DUE_SOON -> Triple(AmberAlertContainer, AmberAlert, Icons.Default.Timer)
        ComplianceStatus.CRITICAL -> Triple(RedExpiredContainer, RedExpired, Icons.Default.Warning)
        ComplianceStatus.EXPIRED -> Triple(RedExpiredContainer, RedExpired, Icons.Default.Error)
        ComplianceStatus.INCOMPLETE -> Triple(Color(0xFFE2E8F0), Color(0xFF475569), Icons.Default.Warning)
    }

    Surface(
        modifier = modifier.clip(RoundedCornerShape(8.dp)),
        color = bgColor,
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = textColor,
                modifier = Modifier.size(14.dp)
            )
            Text(
                text = status.label,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
        }
    }
}

@Composable
fun MetricCard(
    title: String,
    value: String,
    subtitle: String,
    icon: ImageVector,
    color: Color,
    bgColor: Color,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(bgColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(26.dp)
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = value,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    color = color,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
fun VehicleItemCard(
    item: VehicleWithCompliance,
    onViewDetails: () -> Unit,
    onRenewInsurance: () -> Unit,
    onRenewFitness: () -> Unit,
    onEdit: () -> Unit,
    modifier: Modifier = Modifier
) {
    val now = System.currentTimeMillis()
    val insDays = item.getInsuranceDaysRemaining(now)
    val fcDays = item.getFitnessDaysRemaining(now)
    val complianceStatus = item.getOverallComplianceStatus(now)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(
                width = if (complianceStatus == ComplianceStatus.EXPIRED) 1.5.dp else 0.5.dp,
                color = if (complianceStatus == ComplianceStatus.EXPIRED) RedExpired else MaterialTheme.colorScheme.outlineVariant,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable { onViewDetails() }
            .testTag("vehicle_card_${item.vehicle.regNumber}"),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header Row: Vehicle Icon, Reg Number, Status Badge & Edit
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                val typeIcon = when {
                    item.vehicle.vehicleType.contains("Bus", ignoreCase = true) -> Icons.Default.DirectionsBus
                    item.vehicle.vehicleType.contains("Truck", ignoreCase = true) || item.vehicle.vehicleType.contains("Tipper", ignoreCase = true) || item.vehicle.vehicleType.contains("Tanker", ignoreCase = true) -> Icons.Default.LocalShipping
                    else -> Icons.Default.DirectionsCar
                }
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = typeIcon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.vehicle.regNumber,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.8.sp,
                        fontFamily = FontFamily.Monospace,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "${item.vehicle.makeModel} • ${item.vehicle.vehicleType}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                IconButton(
                    onClick = onEdit,
                    modifier = Modifier.size(36.dp).testTag("edit_vehicle_${item.vehicle.regNumber}")
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Edit,
                        contentDescription = "Edit Vehicle",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            // Driver & Fleet Meta
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = item.vehicle.driverName,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Speed,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${NumberFormat.getNumberInstance().format(item.vehicle.currentOdometer)} km",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            // Renewal Pill Sections (Insurance & FC)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Insurance Box
                CompliancePill(
                    title = "INSURANCE",
                    icon = Icons.Outlined.Shield,
                    recordNumber = item.currentInsurance?.policyNumber ?: "No Policy",
                    expiryDate = item.currentInsurance?.expiryDate,
                    daysRemaining = insDays,
                    onRenew = onRenewInsurance,
                    modifier = Modifier.weight(1f)
                )

                // Fitness Box
                CompliancePill(
                    title = "FITNESS (FC)",
                    icon = Icons.Outlined.Assignment,
                    recordNumber = item.currentFitness?.certificateNumber ?: "No FC",
                    expiryDate = item.currentFitness?.expiryDate,
                    daysRemaining = fcDays,
                    onRenew = onRenewFitness,
                    modifier = Modifier.weight(1f)
                )
            }

            // Action footer
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                StatusBadge(status = complianceStatus)

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Full Dossier",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun CompliancePill(
    title: String,
    icon: ImageVector,
    recordNumber: String,
    expiryDate: Long?,
    daysRemaining: Long?,
    onRenew: () -> Unit,
    modifier: Modifier = Modifier
) {
    val (statusColor, bgTint, statusText) = when {
        daysRemaining == null -> Triple(Color.Gray, Color(0xFFF1F5F9), "Not Recorded")
        daysRemaining < 0 -> Triple(RedExpired, RedExpiredContainer, "EXPIRED (${-daysRemaining}d ago)")
        daysRemaining <= 15 -> Triple(RedExpired, RedExpiredContainer, "URGENT ($daysRemaining days)")
        daysRemaining <= 30 -> Triple(AmberAlert, AmberAlertContainer, "Due in $daysRemaining d")
        else -> Triple(GreenValid, GreenValidContainer, "Valid ($daysRemaining d)")
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(bgTint)
            .border(0.5.dp, statusColor.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
            .padding(10.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = statusColor,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = title,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        color = statusColor,
                        letterSpacing = 0.5.sp
                    )
                }

                if (daysRemaining != null && daysRemaining <= 30) {
                    Text(
                        text = "RENEW",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color.White)
                            .clickable { onRenew() }
                            .padding(horizontal = 4.dp, vertical = 2.dp)
                    )
                }
            }

            Text(
                text = statusText,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = statusColor
            )

            if (expiryDate != null) {
                Text(
                    text = "Exp: ${dateFormatter.format(Date(expiryDate))}",
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun UrgentRenewalAlertBanner(
    urgentCount: Int,
    expiredCount: Int,
    onViewAlerts: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (urgentCount == 0 && expiredCount == 0) return

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable { onViewAlerts() }
            .testTag("urgent_alerts_banner"),
        colors = CardDefaults.cardColors(
            containerColor = if (expiredCount > 0) RedExpiredContainer else AmberAlertContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(if (expiredCount > 0) RedExpired else AmberAlert),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (expiredCount > 0) "Immediate Attention Required" else "Upcoming Renewal Alerts",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (expiredCount > 0) RedExpired else AmberAlert
                )
                Text(
                    text = when {
                        expiredCount > 0 && urgentCount > 0 -> "$expiredCount expired documents & $urgentCount expiring within 30 days"
                        expiredCount > 0 -> "$expiredCount documents expired! Vehicle operation at legal risk."
                        else -> "$urgentCount Insurance / FC renewals due within 30 days"
                    },
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = null,
                tint = if (expiredCount > 0) RedExpired else AmberAlert,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
