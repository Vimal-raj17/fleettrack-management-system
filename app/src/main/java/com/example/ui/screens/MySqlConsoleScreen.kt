package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DataObject
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Schema
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CompliancePermitEntity
import com.example.data.model.FitnessCertificateEntity
import com.example.data.model.InsuranceRecordEntity
import com.example.data.model.VehicleEntity
import com.example.ui.theme.BluePrimary
import com.example.ui.theme.Navy800
import com.example.ui.theme.Navy900
import com.example.ui.theme.RedExpired
import com.example.ui.theme.TealSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.US)

@Composable
fun MySqlConsoleScreen(
    sqlDump: String,
    sqlDdl: String,
    vehicles: List<VehicleEntity>,
    insuranceList: List<InsuranceRecordEntity>,
    fitnessList: List<FitnessCertificateEntity>,
    permitsList: List<CompliancePermitEntity>,
    onResetDatabase: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedConsoleTab by remember { mutableIntStateOf(0) }
    var selectedTable by remember { mutableStateOf("vehicles") }
    var showResetDialog by remember { mutableStateOf(false) }

    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text("Restore Benchmark Demonstration Fleet?") },
            text = { Text("This will reset the database back to standard sample vehicles with expiring insurance policies and fitness certificates.") },
            confirmButton = {
                Button(
                    onClick = {
                        showResetDialog = false
                        onResetDatabase()
                    }
                ) {
                    Text("Restore Seed Data")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showResetDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Spacer(modifier = Modifier.height(4.dp))

        // Title Header Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
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
                            .background(Navy800),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Storage, contentDescription = null, tint = Color(0xFF60A5FA))
                    }
                    Column {
                        Text(
                            text = "MySQL Database Hub",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Relational Schema & Live SQL Backup",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                OutlinedButton(
                    onClick = { showResetDialog = true },
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("reset_db_button")
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Reset Seed", fontSize = 11.sp)
                }
            }
        }

        // Sub Tabs
        TabRow(
            selectedTabIndex = selectedConsoleTab,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.primary,
            modifier = Modifier.clip(RoundedCornerShape(12.dp))
        ) {
            Tab(
                selected = selectedConsoleTab == 0,
                onClick = { selectedConsoleTab = 0 },
                text = { Text("Live SQL Dump", fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                icon = { Icon(Icons.Default.DataObject, contentDescription = null, modifier = Modifier.size(16.dp)) }
            )
            Tab(
                selected = selectedConsoleTab == 1,
                onClick = { selectedConsoleTab = 1 },
                text = { Text("MySQL DDL", fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                icon = { Icon(Icons.Default.Schema, contentDescription = null, modifier = Modifier.size(16.dp)) }
            )
            Tab(
                selected = selectedConsoleTab == 2,
                onClick = { selectedConsoleTab = 2 },
                text = { Text("Table Grid", fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                icon = { Icon(Icons.Default.TableChart, contentDescription = null, modifier = Modifier.size(16.dp)) }
            )
        }

        // Tab Content
        when (selectedConsoleTab) {
            0 -> {
                // Live SQL Dump
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    colors = CardDefaults.cardColors(containerColor = Navy900),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "INSERT INTO Dump (${vehicles.size} vehicles, ${insuranceList.size} policies, ${fitnessList.size} FCs)",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF93C5FD)
                            )

                            Button(
                                onClick = {
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    val clip = ClipData.newPlainText("MySQL Dump", sqlDump)
                                    clipboard.setPrimaryClip(clip)
                                    Toast.makeText(context, "SQL Dump copied to clipboard!", Toast.LENGTH_SHORT).show()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = BluePrimary),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.testTag("copy_sql_dump_button")
                            ) {
                                Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Copy SQL", fontSize = 11.sp)
                            }
                        }

                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Navy800)

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                                .verticalScroll(rememberScrollState())
                                .horizontalScroll(rememberScrollState())
                        ) {
                            Text(
                                text = sqlDump,
                                color = Color(0xFFE2E8F0),
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }

            1 -> {
                // DDL Schema
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    colors = CardDefaults.cardColors(containerColor = Navy900),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "MySQL 8.0 DDL Schema Script",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF6EE7B7)
                            )

                            Button(
                                onClick = {
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    val clip = ClipData.newPlainText("MySQL DDL", sqlDdl)
                                    clipboard.setPrimaryClip(clip)
                                    Toast.makeText(context, "MySQL Schema DDL copied!", Toast.LENGTH_SHORT).show()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = TealSecondary),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.testTag("copy_ddl_button")
                            ) {
                                Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Copy DDL", fontSize = 11.sp)
                            }
                        }

                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Navy800)

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                                .verticalScroll(rememberScrollState())
                                .horizontalScroll(rememberScrollState())
                        ) {
                            Text(
                                text = sqlDdl,
                                color = Color(0xFFE2E8F0),
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }

            2 -> {
                // Table Data Explorer
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(
                            "vehicles" to "vehicles (${vehicles.size})",
                            "insurance_renewals" to "insurance_renewals (${insuranceList.size})",
                            "fitness_certificates" to "fitness_certificates (${fitnessList.size})",
                            "compliance_permits" to "compliance_permits (${permitsList.size})"
                        ).forEach { (tbl, label) ->
                            FilterChip(
                                selected = selectedTable == tbl,
                                onClick = { selectedTable = tbl },
                                label = { Text(label, fontSize = 11.sp) }
                            )
                        }
                    }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            when (selectedTable) {
                                "vehicles" -> {
                                    items(vehicles) { v ->
                                        DataRowItem(
                                            id = "ID: ${v.id}",
                                            title = "${v.regNumber} — ${v.makeModel}",
                                            details = "Driver: ${v.driverName} | Fuel: ${v.fuelType} | Status: ${v.status} | Year: ${v.manufactureYear}"
                                        )
                                    }
                                }
                                "insurance_renewals" -> {
                                    items(insuranceList) { i ->
                                        DataRowItem(
                                            id = "ID: ${i.id} (Vehicle ID: ${i.vehicleId})",
                                            title = "${i.policyNumber} — ${i.providerName}",
                                            details = "Exp: ${dateFormatter.format(Date(i.expiryDate))} | Prem: ₹${i.premiumAmount} | IDV: ₹${i.idvAmount} | Current: ${i.isCurrent}"
                                        )
                                    }
                                }
                                "fitness_certificates" -> {
                                    items(fitnessList) { f ->
                                        DataRowItem(
                                            id = "ID: ${f.id} (Vehicle ID: ${f.vehicleId})",
                                            title = "${f.certificateNumber} — ${f.rtoLocation}",
                                            details = "Exp: ${dateFormatter.format(Date(f.expiryDate))} | SpeedGov: ${f.speedGovernorStatus} | PUC: ${f.emissionStatus} | Fee: ₹${f.inspectionFee}"
                                        )
                                    }
                                }
                                "compliance_permits" -> {
                                    items(permitsList) { p ->
                                        DataRowItem(
                                            id = "ID: ${p.id} (Vehicle ID: ${p.vehicleId})",
                                            title = "${p.permitType} — ${p.documentNumber}",
                                            details = "Exp: ${dateFormatter.format(Date(p.expiryDate))} | Fee: ₹${p.fee} | Current: ${p.isCurrent}"
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))
    }
}

@Composable
fun DataRowItem(id: String, title: String, details: String) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp)),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = title, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Text(text = id, fontSize = 10.sp, color = MaterialTheme.colorScheme.primary, fontFamily = FontFamily.Monospace)
            }
            Text(text = details, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
