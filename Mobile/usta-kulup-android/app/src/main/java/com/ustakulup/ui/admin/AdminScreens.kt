package com.ustakulup.ui.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ustakulup.data.model.*
import com.ustakulup.ui.components.*
import com.ustakulup.ui.theme.*

// ─── Admin Navigation Bar ─────────────────────────────────────────────────────

@Composable
fun AdminBottomBar(
    currentRoute: String,
    onProfessionals: () -> Unit,
    onQuotas: () -> Unit,
    onRequests: () -> Unit
) {
    NavigationBar(containerColor = Primary) {
        NavigationBarItem(
            selected = currentRoute == "professionals",
            onClick = onProfessionals,
            icon = { Icon(Icons.Filled.People, contentDescription = null) },
            label = { Text("Ustalar") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Gold,
                selectedTextColor = Gold,
                unselectedIconColor = Color.White.copy(alpha = 0.6f),
                unselectedTextColor = Color.White.copy(alpha = 0.6f),
                indicatorColor = Color.White.copy(alpha = 0.1f)
            )
        )
        NavigationBarItem(
            selected = currentRoute == "quotas",
            onClick = onQuotas,
            icon = { Icon(Icons.Filled.BarChart, contentDescription = null) },
            label = { Text("Kotalar") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Gold,
                selectedTextColor = Gold,
                unselectedIconColor = Color.White.copy(alpha = 0.6f),
                unselectedTextColor = Color.White.copy(alpha = 0.6f),
                indicatorColor = Color.White.copy(alpha = 0.1f)
            )
        )
        NavigationBarItem(
            selected = currentRoute == "requests",
            onClick = onRequests,
            icon = { Icon(Icons.Filled.List, contentDescription = null) },
            label = { Text("Talepler") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Gold,
                selectedTextColor = Gold,
                unselectedIconColor = Color.White.copy(alpha = 0.6f),
                unselectedTextColor = Color.White.copy(alpha = 0.6f),
                indicatorColor = Color.White.copy(alpha = 0.1f)
            )
        )
    }
}

// ─── Admin Professionals Screen ───────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminProfessionalsScreen(
    onNavigateToQuotas: () -> Unit,
    onNavigateToRequests: () -> Unit,
    onLogout: () -> Unit,
    viewModel: AdminViewModel = hiltViewModel()
) {
    val professionals by viewModel.professionals.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val successMessage by viewModel.successMessage.collectAsState()

    LaunchedEffect(successMessage) {
        if (successMessage != null) {
            kotlinx.coroutines.delay(2000)
            viewModel.clearSuccess()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Admin Panel", color = Color.White, fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = { viewModel.logout(); onLogout() }) {
                        Icon(Icons.Filled.ExitToApp, contentDescription = "Çıkış", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Primary)
            )
        },
        bottomBar = {
            AdminBottomBar(
                currentRoute = "professionals",
                onProfessionals = {},
                onQuotas = onNavigateToQuotas,
                onRequests = onNavigateToRequests
            )
        }
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .background(Background)
                .padding(padding)
        ) {
            // Özet banner
            Box(
                Modifier
                    .fillMaxWidth()
                    .background(Primary)
                    .padding(16.dp)
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                    StatItem(
                        value = professionals.count { !it.approved }.toString(),
                        label = "Bekleyen"
                    )
                    StatItem(
                        value = professionals.count { it.approved }.toString(),
                        label = "Onaylı"
                    )
                    StatItem(
                        value = professionals.size.toString(),
                        label = "Toplam"
                    )
                }
            }

            successMessage?.let {
                Surface(
                    Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(8.dp), color = Success.copy(alpha = 0.15f)
                ) {
                    Text(it, color = Success, fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(12.dp))
                }
            }

            if (isLoading) {
                LoadingScreen()
            } else if (error != null) {
                ErrorMessage(error!!, onRetry = { viewModel.loadProfessionals() })
            } else {
                // Tabs: Bekleyen / Onaylı
                var selectedTab by remember { mutableStateOf(0) }
                val pending  = professionals.filter { !it.approved }
                val approved = professionals.filter { it.approved }

                TabRow(selectedTabIndex = selectedTab, containerColor = Surface) {
                    Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 },
                        text = { Text("Bekleyen (${pending.size})") })
                    Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 },
                        text = { Text("Onaylı (${approved.size})") })
                }

                val list = if (selectedTab == 0) pending else approved

                LazyColumn(
                    Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (list.isEmpty()) {
                        item {
                            Box(Modifier.fillMaxWidth().padding(32.dp),
                                contentAlignment = Alignment.Center) {
                                Text("Kayıt yok", color = TextSecondary)
                            }
                        }
                    } else {
                        items(list, key = { it.id }) { pro ->
                            ProfessionalAdminCard(
                                professional = pro,
                                onApprove = { viewModel.approveProfessional(pro.id, true) },
                                onReject  = { viewModel.approveProfessional(pro.id, false) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StatItem(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, color = Gold, fontWeight = FontWeight.Bold, fontSize = 24.sp)
        Text(label, color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
    }
}

@Composable
fun ProfessionalAdminCard(
    professional: ProfessionalProfile,
    onApprove: () -> Unit,
    onReject: () -> Unit
) {
    UstaCard(Modifier.fillMaxWidth()) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(Modifier.weight(1f)) {
                Text(professional.user?.name ?: "İsimsiz",
                    fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(professional.user?.email ?: "",
                    color = TextSecondary, fontSize = 13.sp)
                Text(professional.user?.phone ?: "",
                    color = TextSecondary, fontSize = 13.sp)
                Spacer(Modifier.height(4.dp))
                Row {
                    Surface(shape = RoundedCornerShape(20.dp), color = GoldLight) {
                        Text(professional.category, fontSize = 12.sp,
                            color = Color(0xFF92400E),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp))
                    }
                    Spacer(Modifier.width(6.dp))
                    Surface(shape = RoundedCornerShape(20.dp), color = Divider) {
                        Text(professional.district, fontSize = 12.sp,
                            color = TextSecondary,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp))
                    }
                }
                professional.bio?.let {
                    Spacer(Modifier.height(4.dp))
                    Text(it, color = TextSecondary, fontSize = 12.sp, maxLines = 2)
                }
            }
        }

        if (!professional.approved) {
            Spacer(Modifier.height(12.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = onApprove,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Success),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Filled.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Onayla")
                }
                OutlinedButton(
                    onClick = onReject,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Error),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Error),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Filled.Close, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Reddet")
                }
            }
        } else {
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.Check, contentDescription = null,
                    tint = Success, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(4.dp))
                Text("Onaylı", color = Success, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                if (professional.subscriptionActive) {
                    Spacer(Modifier.width(12.dp))
                    Text("• Abonelik Aktif", color = Success, fontSize = 13.sp)
                }
            }
        }
    }
}

// ─── Admin Quotas Screen ──────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminQuotasScreen(
    onBack: () -> Unit,
    viewModel: AdminViewModel = hiltViewModel()
) {
    val quotas by viewModel.quotas.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val successMessage by viewModel.successMessage.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }
    var dialogDistrict by remember { mutableStateOf("") }
    var dialogCategory by remember { mutableStateOf("") }
    var dialogMax by remember { mutableStateOf("") }
    var districtExpanded by remember { mutableStateOf(false) }
    var categoryExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) { viewModel.loadQuotas() }

    LaunchedEffect(successMessage) {
        if (successMessage != null) {
            kotlinx.coroutines.delay(2000)
            viewModel.clearSuccess()
            showAddDialog = false
        }
    }

    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Kota Ayarla", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    DropdownField(
                        label = "İlçe",
                        selected = dialogDistrict,
                        options = Districts.istanbul,
                        onSelect = { dialogDistrict = it },
                        expanded = districtExpanded,
                        onExpandedChange = { districtExpanded = it }
                    )
                    DropdownField(
                        label = "Kategori",
                        selected = dialogCategory,
                        options = Categories.all,
                        onSelect = { dialogCategory = it },
                        expanded = categoryExpanded,
                        onExpandedChange = { categoryExpanded = it }
                    )
                    OutlinedTextField(
                        value = dialogMax, onValueChange = { dialogMax = it },
                        label = { Text("Maksimum Usta Sayısı") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp), singleLine = true
                    )
                    error?.let { ErrorMessage(it) }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.setQuota(dialogDistrict, dialogCategory,
                            dialogMax.toIntOrNull() ?: 0)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Gold, contentColor = Primary)
                ) { Text("Kaydet") }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) { Text("İptal") }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Kota Yönetimi", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Geri", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Primary)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = Gold, contentColor = Primary
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Kota Ekle")
            }
        }
    ) { padding ->
        if (isLoading) {
            LoadingScreen()
        } else {
            LazyColumn(
                Modifier
                    .fillMaxSize()
                    .background(Background)
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                successMessage?.let {
                    item {
                        Surface(
                            Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            color = Success.copy(alpha = 0.15f)
                        ) {
                            Text(it, color = Success, fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(12.dp))
                        }
                    }
                }

                if (quotas.isEmpty()) {
                    item {
                        Box(Modifier.fillMaxWidth().padding(32.dp),
                            contentAlignment = Alignment.Center) {
                            Text("Henüz kota ayarlanmamış", color = TextSecondary)
                        }
                    }
                } else {
                    items(quotas, key = { it.id }) { quota ->
                        QuotaCard(quota)
                    }
                }
            }
        }
    }
}

@Composable
fun QuotaCard(quota: DistrictQuota) {
    UstaCard(Modifier.fillMaxWidth()) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(quota.district, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(quota.category, color = TextSecondary, fontSize = 13.sp)
            }
            Column(horizontalAlignment = Alignment.End) {
                val current = quota.current ?: 0
                val isFull = current >= quota.max
                Text(
                    "$current / ${quota.max}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = if (isFull) Error else Success
                )
                Text(if (isFull) "Dolu" else "Müsait",
                    fontSize = 12.sp,
                    color = if (isFull) Error else Success)
            }
        }
        Spacer(Modifier.height(8.dp))
        LinearProgressIndicator(
            progress = if (quota.max > 0) (quota.current ?: 0).toFloat() / quota.max else 0f,
            modifier = Modifier.fillMaxWidth().height(6.dp),
            color = if ((quota.current ?: 0) >= quota.max) Error else Gold,
            trackColor = Divider
        )
    }
}

// ─── Admin Requests Screen ────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminRequestsScreen(
    onBack: () -> Unit,
    viewModel: AdminViewModel = hiltViewModel()
) {
    val requests by viewModel.requests.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    LaunchedEffect(Unit) { viewModel.loadRequests() }

    var filterStatus by remember { mutableStateOf("TÜMÜ") }
    val statuses = listOf("TÜMÜ", "PENDING", "ASSIGNED", "IN_PROGRESS", "COMPLETED", "CANCELLED")

    val filtered = if (filterStatus == "TÜMÜ") requests
                   else requests.filter { it.status == filterStatus }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tüm Talepler", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Geri", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Primary)
            )
        }
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .background(Background)
                .padding(padding)
        ) {
            // Durum filtresi
            androidx.compose.foundation.lazy.LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(statuses.size) { i ->
                    val s = statuses[i]
                    FilterChip(
                        selected = filterStatus == s,
                        onClick = { filterStatus = s },
                        label = { Text(if (s == "TÜMÜ") "Tümü" else RequestStatus.displayName(s), fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Gold,
                            selectedLabelColor = Primary
                        )
                    )
                }
            }

            if (isLoading) {
                LoadingScreen()
            } else if (error != null) {
                ErrorMessage(error!!, onRetry = { viewModel.loadRequests() })
            } else {
                LazyColumn(
                    Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filtered, key = { it.id }) { request ->
                        UstaCard(Modifier.fillMaxWidth()) {
                            Row(
                                Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Top
                            ) {
                                Column(Modifier.weight(1f)) {
                                    Text(request.title, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                    Text(request.user?.name ?: "Kullanıcı",
                                        color = Gold, fontSize = 13.sp)
                                    Text("${request.district} • ${request.category}",
                                        color = TextSecondary, fontSize = 12.sp)
                                }
                                StatusBadge(request.status)
                            }
                            val assignCount = request.assignments?.size ?: 0
                            if (assignCount > 0) {
                                Spacer(Modifier.height(6.dp))
                                Text("$assignCount usta atandı", color = TextSecondary, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}
