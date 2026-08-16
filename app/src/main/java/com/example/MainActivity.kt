package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.outlined.Assignment
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.LocalShipping
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material.icons.outlined.Storage
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.FleetFilter
import com.example.ui.FleetTab
import com.example.ui.FleetViewModel
import com.example.ui.components.AddEditVehicleDialog
import com.example.ui.components.AddPermitDialog
import com.example.ui.components.RenewFitnessDialog
import com.example.ui.components.RenewInsuranceDialog
import com.example.ui.components.VehicleDetailDossierSheet
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.FitnessCenterScreen
import com.example.ui.screens.FleetRegistryScreen
import com.example.ui.screens.InsuranceCenterScreen
import com.example.ui.screens.MySqlConsoleScreen
import com.example.ui.theme.AmberAlert
import com.example.ui.theme.BluePrimary
import com.example.ui.theme.GreenValid
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.RedExpired

class MainActivity : ComponentActivity() {

    private val viewModel: FleetViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                val context = LocalContext.current
                val snackbarHostState = remember { SnackbarHostState() }

                val selectedTab by viewModel.selectedTab.collectAsStateWithLifecycle()
                val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
                val selectedFilter by viewModel.selectedFilter.collectAsStateWithLifecycle()
                val filteredVehicles by viewModel.filteredVehicles.collectAsStateWithLifecycle()
                val allVehiclesWithCompliance by viewModel.rawVehicles.collectAsStateWithLifecycle()
                val metrics by viewModel.fleetMetrics.collectAsStateWithLifecycle()

                val selectedVehicleId by viewModel.selectedVehicleId.collectAsStateWithLifecycle()
                val showAddDialog by viewModel.showAddVehicleDialog.collectAsStateWithLifecycle()
                val editingVehicle by viewModel.editingVehicle.collectAsStateWithLifecycle()
                val renewInsuranceVehicle by viewModel.renewInsuranceVehicle.collectAsStateWithLifecycle()
                val renewFitnessVehicle by viewModel.renewFitnessVehicle.collectAsStateWithLifecycle()
                val addPermitVehicle by viewModel.addPermitVehicle.collectAsStateWithLifecycle()
                val userMessage by viewModel.userMessage.collectAsStateWithLifecycle()

                val allInsurance by viewModel.allInsuranceRecords.collectAsStateWithLifecycle()
                val allFitness by viewModel.allFitnessRecords.collectAsStateWithLifecycle()
                val allPermits by viewModel.allPermits.collectAsStateWithLifecycle()

                val selectedVehicleItem = remember(selectedVehicleId, allVehiclesWithCompliance) {
                    allVehiclesWithCompliance.firstOrNull { it.vehicle.id == selectedVehicleId }
                }

                // Show toast / snackbar on user actions
                LaunchedEffect(userMessage) {
                    userMessage?.let {
                        Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                        viewModel.clearUserMessage()
                    }
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        CenterAlignedTopAppBar(
                            title = {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(28.dp)
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(BluePrimary),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.LocalShipping,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "FleetTrack",
                                        fontWeight = FontWeight.Black,
                                        fontSize = 18.sp,
                                        letterSpacing = 0.5.sp
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Surface(
                                        color = MaterialTheme.colorScheme.primaryContainer,
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text(
                                            text = "ENTERPRISE",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Black,
                                            color = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            },
                            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            )
                        )
                    },
                    bottomBar = {
                        NavigationBar(
                            containerColor = MaterialTheme.colorScheme.surface,
                            tonalElevation = 6.dp,
                            modifier = Modifier
                                .windowInsetsPadding(WindowInsets.navigationBars)
                                .testTag("bottom_navigation_bar")
                        ) {
                            // 1. Dashboard
                            NavigationBarItem(
                                selected = selectedTab == FleetTab.DASHBOARD,
                                onClick = { viewModel.selectTab(FleetTab.DASHBOARD) },
                                icon = {
                                    Icon(
                                        if (selectedTab == FleetTab.DASHBOARD) Icons.Default.Dashboard else Icons.Outlined.Dashboard,
                                        contentDescription = "Dashboard"
                                    )
                                },
                                label = { Text("Overview", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = BluePrimary,
                                    indicatorColor = MaterialTheme.colorScheme.primaryContainer
                                ),
                                modifier = Modifier.testTag("nav_tab_dashboard")
                            )

                            // 2. Fleet Registry
                            NavigationBarItem(
                                selected = selectedTab == FleetTab.FLEET,
                                onClick = { viewModel.selectTab(FleetTab.FLEET) },
                                icon = {
                                    Icon(
                                        if (selectedTab == FleetTab.FLEET) Icons.Default.LocalShipping else Icons.Outlined.LocalShipping,
                                        contentDescription = "Fleet"
                                    )
                                },
                                label = { Text("Fleet", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = BluePrimary,
                                    indicatorColor = MaterialTheme.colorScheme.primaryContainer
                                ),
                                modifier = Modifier.testTag("nav_tab_fleet")
                            )

                            // 3. Insurance Center
                            NavigationBarItem(
                                selected = selectedTab == FleetTab.INSURANCE,
                                onClick = { viewModel.selectTab(FleetTab.INSURANCE) },
                                icon = {
                                    val dueCount = metrics.insuranceDueSoon + metrics.insuranceExpired
                                    if (dueCount > 0) {
                                        BadgedBox(badge = {
                                            Badge(
                                                containerColor = if (metrics.insuranceExpired > 0) RedExpired else AmberAlert
                                            ) {
                                                Text("$dueCount")
                                            }
                                        }) {
                                            Icon(
                                                if (selectedTab == FleetTab.INSURANCE) Icons.Default.Shield else Icons.Outlined.Shield,
                                                contentDescription = "Insurance"
                                            )
                                        }
                                    } else {
                                        Icon(
                                            if (selectedTab == FleetTab.INSURANCE) Icons.Default.Shield else Icons.Outlined.Shield,
                                            contentDescription = "Insurance"
                                        )
                                    }
                                },
                                label = { Text("Insurance", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = BluePrimary,
                                    indicatorColor = MaterialTheme.colorScheme.primaryContainer
                                ),
                                modifier = Modifier.testTag("nav_tab_insurance")
                            )

                            // 4. Fitness (FC) Center
                            NavigationBarItem(
                                selected = selectedTab == FleetTab.FITNESS,
                                onClick = { viewModel.selectTab(FleetTab.FITNESS) },
                                icon = {
                                    val fcDueCount = metrics.fitnessDueSoon + metrics.fitnessExpired
                                    if (fcDueCount > 0) {
                                        BadgedBox(badge = {
                                            Badge(
                                                containerColor = if (metrics.fitnessExpired > 0) RedExpired else AmberAlert
                                            ) {
                                                Text("$fcDueCount")
                                            }
                                        }) {
                                            Icon(
                                                if (selectedTab == FleetTab.FITNESS) Icons.Default.Assignment else Icons.Outlined.Assignment,
                                                contentDescription = "Fitness"
                                            )
                                        }
                                    } else {
                                        Icon(
                                            if (selectedTab == FleetTab.FITNESS) Icons.Default.Assignment else Icons.Outlined.Assignment,
                                            contentDescription = "Fitness"
                                        )
                                    }
                                },
                                label = { Text("FC Renewal", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = BluePrimary,
                                    indicatorColor = MaterialTheme.colorScheme.primaryContainer
                                ),
                                modifier = Modifier.testTag("nav_tab_fitness")
                            )

                            // 5. MySQL DB Hub
                            NavigationBarItem(
                                selected = selectedTab == FleetTab.MYSQL_HUB,
                                onClick = { viewModel.selectTab(FleetTab.MYSQL_HUB) },
                                icon = {
                                    Icon(
                                        if (selectedTab == FleetTab.MYSQL_HUB) Icons.Default.Storage else Icons.Outlined.Storage,
                                        contentDescription = "MySQL DB"
                                    )
                                },
                                label = { Text("MySQL Hub", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = BluePrimary,
                                    indicatorColor = MaterialTheme.colorScheme.primaryContainer
                                ),
                                modifier = Modifier.testTag("nav_tab_mysql")
                            )
                        }
                    },
                    snackbarHost = { SnackbarHost(snackbarHostState) }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        when (selectedTab) {
                            FleetTab.DASHBOARD -> {
                                DashboardScreen(
                                    metrics = metrics,
                                    vehicles = allVehiclesWithCompliance,
                                    onNavigateTab = { viewModel.selectTab(it) },
                                    onSetFilter = { viewModel.setFilter(it) },
                                    onViewVehicle = { viewModel.selectVehicle(it) },
                                    onAddNewVehicle = { viewModel.openAddVehicleDialog() },
                                    onRenewInsurance = { viewModel.openRenewInsurance(it) },
                                    onRenewFitness = { viewModel.openRenewFitness(it) },
                                    onEditVehicle = { viewModel.openEditVehicleDialog(it.vehicle) }
                                )
                            }
                            FleetTab.FLEET -> {
                                FleetRegistryScreen(
                                    vehicles = filteredVehicles,
                                    searchQuery = searchQuery,
                                    onSearchChange = { viewModel.updateSearchQuery(it) },
                                    selectedFilter = selectedFilter,
                                    onSelectFilter = { viewModel.setFilter(it) },
                                    onViewVehicle = { viewModel.selectVehicle(it) },
                                    onAddNewVehicle = { viewModel.openAddVehicleDialog() },
                                    onRenewInsurance = { viewModel.openRenewInsurance(it) },
                                    onRenewFitness = { viewModel.openRenewFitness(it) },
                                    onEditVehicle = { viewModel.openEditVehicleDialog(it.vehicle) }
                                )
                            }
                            FleetTab.INSURANCE -> {
                                InsuranceCenterScreen(
                                    vehicles = allVehiclesWithCompliance,
                                    onRenewInsurance = { viewModel.openRenewInsurance(it) },
                                    onViewVehicle = { viewModel.selectVehicle(it) }
                                )
                            }
                            FleetTab.FITNESS -> {
                                FitnessCenterScreen(
                                    vehicles = allVehiclesWithCompliance,
                                    onRenewFitness = { viewModel.openRenewFitness(it) },
                                    onViewVehicle = { viewModel.selectVehicle(it) }
                                )
                            }
                            FleetTab.MYSQL_HUB -> {
                                MySqlConsoleScreen(
                                    sqlDump = viewModel.getExportSqlDump(),
                                    sqlDdl = viewModel.getMySqlDDL(),
                                    vehicles = allVehiclesWithCompliance.map { it.vehicle },
                                    insuranceList = allInsurance,
                                    fitnessList = allFitness,
                                    permitsList = allPermits,
                                    onResetDatabase = { viewModel.resetFleetData() }
                                )
                            }
                        }
                    }
                }

                // Modal Dialogs & Sheets
                if (showAddDialog) {
                    AddEditVehicleDialog(
                        vehicle = editingVehicle,
                        onDismiss = { viewModel.dismissAddEditVehicleDialog() },
                        onSave = { id, reg, make, type, year, fuel, chassis, eng, driver, phone, odo, st ->
                            viewModel.saveVehicle(id, reg, make, type, year, fuel, chassis, eng, driver, phone, odo, st)
                        }
                    )
                }

                renewInsuranceVehicle?.let { vehicleWithCompliance ->
                    RenewInsuranceDialog(
                        vehicleWithCompliance = vehicleWithCompliance,
                        onDismiss = { viewModel.dismissRenewInsurance() },
                        onRenew = { vId, pol, prov, pType, start, exp, prem, idv, ag, nt ->
                            viewModel.processInsuranceRenewal(vId, pol, prov, pType, start, exp, prem, idv, ag, nt)
                        }
                    )
                }

                renewFitnessVehicle?.let { vehicleWithCompliance ->
                    RenewFitnessDialog(
                        vehicleWithCompliance = vehicleWithCompliance,
                        onDismiss = { viewModel.dismissRenewFitness() },
                        onRenew = { vId, cert, rto, iss, exp, fee, sg, em, ref, brk, nt ->
                            viewModel.processFitnessRenewal(vId, cert, rto, iss, exp, fee, sg, em, ref, brk, nt)
                        }
                    )
                }

                addPermitVehicle?.let { vehicleWithCompliance ->
                    AddPermitDialog(
                        vehicleWithCompliance = vehicleWithCompliance,
                        onDismiss = { viewModel.dismissAddPermit() },
                        onSave = { vId, type, doc, exp, fee ->
                            viewModel.processPermitAdd(vId, type, doc, exp, fee)
                        }
                    )
                }

                selectedVehicleItem?.let { vehicleItem ->
                    VehicleDetailDossierSheet(
                        item = vehicleItem,
                        onDismiss = { viewModel.selectVehicle(null) },
                        onRenewInsurance = {
                            viewModel.selectVehicle(null)
                            viewModel.openRenewInsurance(vehicleItem)
                        },
                        onRenewFitness = {
                            viewModel.selectVehicle(null)
                            viewModel.openRenewFitness(vehicleItem)
                        },
                        onAddPermit = {
                            viewModel.selectVehicle(null)
                            viewModel.openAddPermit(vehicleItem)
                        },
                        onEditVehicle = {
                            viewModel.selectVehicle(null)
                            viewModel.openEditVehicleDialog(vehicleItem.vehicle)
                        },
                        onDeleteVehicle = {
                            viewModel.deleteVehicle(vehicleItem.vehicle)
                        }
                    )
                }
            }
        }
    }
}
