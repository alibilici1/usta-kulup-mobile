package com.ustakulup.ui.professional

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.WorkHistory
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ustakulup.data.model.*
import com.ustakulup.ui.components.*
import com.ustakulup.ui.theme.*
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff

// ─── Apply Screen ─────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfessionalApplyScreen(
    onSuccess: () -> Unit,
    onBack: () -> Unit,
    viewModel: ProfessionalViewModel = hiltViewModel()
) {
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val applySuccess by viewModel.applySuccess.collectAsState()

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var bio by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("") }
    var selectedDistrict by remember { mutableStateOf("") }
    var categoryExpanded by remember { mutableStateOf(false) }
    var districtExpanded by remember { mutableStateOf(false) }
    var passwordVisible by remember { mutableStateOf(false) }

    LaunchedEffect(applySuccess) {
        if (applySuccess) onSuccess()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Usta Başvurusu", color = Color.White, fontWeight = FontWeight.Bold) },
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
                .verticalScroll(rememberScrollState())
                .padding(padding)
                .padding(16.dp)
        ) {
            // Bilgi kartı
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = GoldLight,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text("ℹ️", fontSize = 20.sp)
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "Başvurunuz admin onayına gönderilecektir. Onay sonrası sisteme dahil olabilirsiniz.",
                        fontSize = 13.sp, color = Color(0xFF92400E)
                    )
                }
            }

            Spacer(Modifier.height(16.dp))
            SectionHeader("Kişisel Bilgiler")

            OutlinedTextField(
                value = name, onValueChange = { name = it },
                label = { Text("Ad Soyad *") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp), singleLine = true
            )
            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = phone, onValueChange = { phone = it },
                label = { Text("Telefon *") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp), singleLine = true
            )
            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = email, onValueChange = { email = it },
                label = { Text("E-posta *") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp), singleLine = true
            )
            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = password, onValueChange = { password = it },
                label = { Text("Şifre *") },
                visualTransformation = if (passwordVisible) VisualTransformation.None
                                       else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                            contentDescription = null
                        )
                    }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp), singleLine = true
            )

            Spacer(Modifier.height(16.dp))
            SectionHeader("Uzmanlık Bilgileri")

            DropdownField(
                label = "Kategori *",
                selected = selectedCategory,
                options = Categories.all,
                onSelect = { selectedCategory = it },
                expanded = categoryExpanded,
                onExpandedChange = { categoryExpanded = it }
            )
            Spacer(Modifier.height(12.dp))

            DropdownField(
                label = "Çalışma İlçesi *",
                selected = selectedDistrict,
                options = Districts.istanbul,
                onSelect = { selectedDistrict = it },
                expanded = districtExpanded,
                onExpandedChange = { districtExpanded = it }
            )
            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = bio, onValueChange = { bio = it },
                label = { Text("Hakkınızda (isteğe bağlı)") },
                modifier = Modifier.fillMaxWidth().height(100.dp),
                shape = RoundedCornerShape(12.dp), maxLines = 4
            )

            if (error != null) {
                Spacer(Modifier.height(8.dp))
                ErrorMessage(error!!)
            }

            Spacer(Modifier.height(24.dp))

            GoldButton(
                text = "Başvuruyu Gönder",
                onClick = {
                    viewModel.applyProfessional(name, email, password, phone,
                        selectedCategory, selectedDistrict, bio)
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = name.isNotBlank() && email.isNotBlank() && password.isNotBlank() &&
                          phone.isNotBlank() && selectedCategory.isNotBlank() && selectedDistrict.isNotBlank(),
                loading = isLoading
            )
            Spacer(Modifier.height(24.dp))
        }
    }
}

// ─── Professional Dashboard ───────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfessionalDashboardScreen(
    onOfferScreen: (String) -> Unit,
    onLogout: () -> Unit,
    viewModel: ProfessionalViewModel = hiltViewModel()
) {
    val dashboard by viewModel.dashboard.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Usta Paneli", color = Color.White, fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = { viewModel.logout(); onLogout() }) {
                        Icon(Icons.Filled.ExitToApp, contentDescription = "Çıkış", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Primary)
            )
        }
    ) { padding ->
        if (isLoading) {
            LoadingScreen()
        } else if (error != null) {
            ErrorMessage(error!!, onRetry = { viewModel.loadDashboard() })
        } else {
            LazyColumn(
                Modifier
                    .fillMaxSize()
                    .background(Background)
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Profil özet kartı
                dashboard?.let { dash ->
                    item {
                        UstaCard(Modifier.fillMaxWidth()) {
                            Row(
                                Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(dash.professional.user?.name ?: "Usta",
                                        fontWeight = FontWeight.Bold, fontSize = 18.sp)
                                    Text("${dash.professional.category} • ${dash.professional.district}",
                                        color = TextSecondary, fontSize = 13.sp)
                                    Spacer(Modifier.height(4.dp))
                                    dash.professional.rating?.let { rating ->
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text("⭐", fontSize = 14.sp)
                                            Text(" ${"%.1f".format(rating)} (${dash.professional.ratingCount} değerlendirme)",
                                                fontSize = 13.sp, color = TextSecondary)
                                        }
                                    }
                                }
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("${dash.completedCount}", fontSize = 28.sp,
                                        fontWeight = FontWeight.Bold, color = Gold)
                                    Text("Tamamlanan", fontSize = 11.sp, color = TextSecondary)
                                }
                            }
                        }
                    }

                    // Bekleyen atamalar
                    if (dash.pendingAssignments.isNotEmpty()) {
                        item {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Filled.Schedule, contentDescription = null,
                                    tint = Gold, modifier = Modifier.size(20.dp))
                                Spacer(Modifier.width(6.dp))
                                SectionHeader("Bekleyen Atamalar (${dash.pendingAssignments.size})")
                            }
                        }
                        items(dash.pendingAssignments) { assignment ->
                            AssignmentCard(
                                assignment = assignment,
                                onClick = { onOfferScreen(assignment.requestId) }
                            )
                        }
                    }

                    // Aktif talepler
                    if (dash.activeRequests.isNotEmpty()) {
                        item {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Filled.WorkHistory, contentDescription = null,
                                    tint = Primary, modifier = Modifier.size(20.dp))
                                Spacer(Modifier.width(6.dp))
                                SectionHeader("Aktif Talepler (${dash.activeRequests.size})")
                            }
                        }
                        items(dash.activeRequests) { request ->
                            ActiveRequestCard(request)
                        }
                    }

                    if (dash.pendingAssignments.isEmpty() && dash.activeRequests.isEmpty()) {
                        item {
                            Box(
                                Modifier.fillMaxWidth().padding(32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("🔧", fontSize = 48.sp)
                                    Spacer(Modifier.height(8.dp))
                                    Text("Şu an bekleyen talep yok", color = TextSecondary)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AssignmentCard(assignment: RequestAssignment, onClick: () -> Unit) {
    UstaCard(
        modifier = Modifier.fillMaxWidth().clickable { onClick() }
    ) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text("Yeni Talep", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Text("Talep ID: ${assignment.requestId.take(8)}...",
                    color = TextSecondary, fontSize = 12.sp)
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Schedule, contentDescription = null,
                        tint = Error, modifier = Modifier.size(14.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("24 saat içinde yanıtlayın", color = Error, fontSize = 12.sp)
                }
            }
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = Gold
            ) {
                Text("Teklif Ver →", color = Primary, fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp))
            }
        }
    }
}

@Composable
fun ActiveRequestCard(request: ServiceRequest) {
    UstaCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(Modifier.weight(1f)) {
                Text(request.title, fontWeight = FontWeight.Bold)
                Text("${request.district} • ${request.category}",
                    color = TextSecondary, fontSize = 13.sp)
            }
            StatusBadge(request.status)
        }
    }
}

// ─── Professional Offer Screen ────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfessionalOfferScreen(
    requestId: String,
    onBack: () -> Unit,
    viewModel: ProfessionalViewModel = hiltViewModel()
) {
    val request by viewModel.selectedRequest.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    var price by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }

    LaunchedEffect(requestId) { viewModel.loadRequest(requestId) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Teklif Ver", color = Color.White, fontWeight = FontWeight.Bold) },
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
                .verticalScroll(rememberScrollState())
                .padding(padding)
                .padding(16.dp)
        ) {
            // Talep bilgisi
            request?.let { req ->
                UstaCard(Modifier.fillMaxWidth()) {
                    Text("Talep Bilgisi", fontWeight = FontWeight.Bold, color = TextSecondary, fontSize = 12.sp)
                    Spacer(Modifier.height(8.dp))
                    Text(req.title, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Spacer(Modifier.height(4.dp))
                    Text(req.description ?: "", color = TextSecondary, fontSize = 14.sp)
                    Spacer(Modifier.height(8.dp))
                    Row {
                        Surface(shape = RoundedCornerShape(20.dp), color = Divider) {
                            Text(req.category, fontSize = 12.sp, color = TextSecondary,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp))
                        }
                        Spacer(Modifier.width(8.dp))
                        Surface(shape = RoundedCornerShape(20.dp), color = Divider) {
                            Text(req.district, fontSize = 12.sp, color = TextSecondary,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp))
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
            SectionHeader("Teklifiniz")

            OutlinedTextField(
                value = price, onValueChange = { price = it },
                label = { Text("Fiyat (₺) *") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp), singleLine = true,
                prefix = { Text("₺ ") }
            )
            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = note, onValueChange = { note = it },
                label = { Text("Not (isteğe bağlı)") },
                modifier = Modifier.fillMaxWidth().height(100.dp),
                shape = RoundedCornerShape(12.dp), maxLines = 4
            )

            if (error != null) {
                Spacer(Modifier.height(8.dp))
                ErrorMessage(error!!)
            }

            Spacer(Modifier.height(24.dp))

            GoldButton(
                text = "Teklif Gönder",
                onClick = {
                    val priceDouble = price.toDoubleOrNull()
                    if (priceDouble != null) {
                        viewModel.submitOffer(priceDouble, note.ifBlank { null }) { onBack() }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = price.toDoubleOrNull() != null && (price.toDoubleOrNull() ?: 0.0) > 0,
                loading = isLoading
            )
        }
    }
}
