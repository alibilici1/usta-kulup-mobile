package com.ustakulup.ui.user

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ustakulup.data.model.*
import com.ustakulup.ui.components.*
import com.ustakulup.ui.theme.*

// ─── Dashboard ────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserDashboardScreen(
    onNewRequest: () -> Unit,
    onRequestDetail: (String) -> Unit,
    onLogout: () -> Unit,
    viewModel: UserViewModel = hiltViewModel()
) {
    val requests by viewModel.requests.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val error by viewModel.error.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Taleplerim", color = Color.White, fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = { viewModel.logout(); onLogout() }) {
                        Icon(Icons.Filled.ExitToApp, contentDescription = "Çıkış", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Primary)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNewRequest,
                containerColor = Gold, contentColor = Primary
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Yeni Talep")
            }
        }
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .background(Background)
                .padding(padding)
        ) {
            // Kullanıcı karşılama
            currentUser?.let { user ->
                Box(
                    Modifier
                        .fillMaxWidth()
                        .background(Primary)
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Column {
                        Text("Merhaba, ${user.name} 👋", color = Color.White, fontWeight = FontWeight.Medium)
                        Text("${requests.size} aktif talebiniz var", color = Gold, fontSize = 13.sp)
                    }
                }
            }

            if (isLoading) {
                LoadingScreen()
            } else if (error != null) {
                ErrorMessage(error!!, onRetry = { viewModel.loadRequests() })
            } else if (requests.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("🔧", fontSize = 48.sp)
                        Spacer(Modifier.height(8.dp))
                        Text("Henüz talebiniz yok", color = TextSecondary, fontSize = 16.sp)
                        Spacer(Modifier.height(8.dp))
                        GoldButton("Yeni Talep Oluştur", onClick = onNewRequest)
                    }
                }
            } else {
                LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(requests, key = { it.id }) { request ->
                        RequestCard(request = request, onClick = { onRequestDetail(request.id) })
                    }
                }
            }
        }
    }
}

@Composable
fun RequestCard(request: ServiceRequest, onClick: () -> Unit) {
    UstaCard(
        modifier = Modifier.fillMaxWidth().clickable { onClick() }
    ) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(Modifier.weight(1f)) {
                Text(request.title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(Modifier.height(4.dp))
                Text("${request.district} • ${request.category}", color = TextSecondary, fontSize = 13.sp)
            }
            StatusBadge(request.status)
        }
        if (!request.description.isNullOrBlank()) {
            Spacer(Modifier.height(8.dp))
            Text(
                request.description,
                color = TextSecondary, fontSize = 13.sp,
                maxLines = 2
            )
        }
    }
}

// ─── New Request Screen ───────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewRequestScreen(
    onSuccess: () -> Unit,
    onBack: () -> Unit,
    viewModel: UserViewModel = hiltViewModel()
) {
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("") }
    var selectedDistrict by remember { mutableStateOf("") }
    var categoryExpanded by remember { mutableStateOf(false) }
    var districtExpanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Yeni Talep", color = Color.White, fontWeight = FontWeight.Bold) },
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
            SectionHeader("Talep Bilgileri")

            OutlinedTextField(
                value = title, onValueChange = { title = it },
                label = { Text("Başlık") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp), singleLine = true
            )
            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = description, onValueChange = { description = it },
                label = { Text("Açıklama") },
                modifier = Modifier.fillMaxWidth().height(120.dp),
                shape = RoundedCornerShape(12.dp), maxLines = 5
            )
            Spacer(Modifier.height(12.dp))

            DropdownField(
                label = "Kategori",
                selected = selectedCategory,
                options = Categories.all,
                onSelect = { selectedCategory = it },
                expanded = categoryExpanded,
                onExpandedChange = { categoryExpanded = it }
            )
            Spacer(Modifier.height(12.dp))

            DropdownField(
                label = "İlçe",
                selected = selectedDistrict,
                options = Districts.istanbul,
                onSelect = { selectedDistrict = it },
                expanded = districtExpanded,
                onExpandedChange = { districtExpanded = it }
            )

            if (error != null) {
                Spacer(Modifier.height(8.dp))
                ErrorMessage(error!!)
            }

            Spacer(Modifier.height(24.dp))

            GoldButton(
                text = "Talep Oluştur",
                onClick = {
                    viewModel.createRequest(title, description, selectedCategory, selectedDistrict, onSuccess)
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = title.isNotBlank() && selectedCategory.isNotBlank() && selectedDistrict.isNotBlank(),
                loading = isLoading
            )
        }
    }
}

// ─── Request Detail Screen ────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RequestDetailScreen(
    requestId: String,
    onBack: () -> Unit,
    viewModel: UserViewModel = hiltViewModel()
) {
    val request by viewModel.selectedRequest.collectAsState()
    val offers by viewModel.offers.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val successMessage by viewModel.successMessage.collectAsState()
    val error by viewModel.error.collectAsState()

    var ratingScore by remember { mutableStateOf(0) }
    var showRatingDialog by remember { mutableStateOf(false) }

    LaunchedEffect(requestId) { viewModel.loadRequest(requestId) }

    if (successMessage != null) {
        LaunchedEffect(successMessage) {
            kotlinx.coroutines.delay(2000)
            viewModel.clearSuccess()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Talep Detayı", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Geri", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Primary)
            )
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
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                request?.let { req ->
                    item {
                        UstaCard(Modifier.fillMaxWidth()) {
                            Row(
                                Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Top
                            ) {
                                Text(req.title, fontWeight = FontWeight.Bold, fontSize = 18.sp, modifier = Modifier.weight(1f))
                                StatusBadge(req.status)
                            }
                            Spacer(Modifier.height(8.dp))
                            Text(req.description ?: "", color = TextSecondary)
                            Spacer(Modifier.height(8.dp))
                            Row {
                                Chip(label = req.category)
                                Spacer(Modifier.width(8.dp))
                                Chip(label = req.district)
                            }
                        }
                    }

                    // İptal butonu
                    if (req.status == RequestStatus.PENDING) {
                        item {
                            OutlinedButton(
                                onClick = { viewModel.cancelRequest(requestId) },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Error),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Error)
                            ) {
                                Text("Talebi İptal Et")
                            }
                        }
                    }

                    // Teklifler
                    if (offers.isNotEmpty()) {
                        item { SectionHeader("Gelen Teklifler (${offers.size})") }
                        items(offers) { offer ->
                            OfferCard(
                                offer = offer,
                                canSelect = req.status == RequestStatus.ASSIGNED && !offer.isSelected,
                                onSelect = { viewModel.selectOffer(requestId, offer.id) }
                            )
                        }
                    }

                    // Değerlendirme
                    if (req.status == RequestStatus.COMPLETED) {
                        item {
                            UstaCard(Modifier.fillMaxWidth()) {
                                Text("Değerlendirme", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                Spacer(Modifier.height(8.dp))
                                StarRating(rating = ratingScore, onRatingChange = { ratingScore = it })
                                Spacer(Modifier.height(8.dp))
                                GoldButton(
                                    text = "Değerlendir",
                                    onClick = {
                                        req.assignments?.firstOrNull()?.professionalId?.let { proId ->
                                            viewModel.submitRating(requestId, proId, ratingScore)
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    enabled = ratingScore > 0
                                )
                            }
                        }
                    }

                    if (successMessage != null) {
                        item {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Success.copy(alpha = 0.15f),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    successMessage!!,
                                    color = Success, fontWeight = FontWeight.Medium,
                                    modifier = Modifier.padding(12.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun OfferCard(offer: Offer, canSelect: Boolean, onSelect: () -> Unit) {
    UstaCard(
        modifier = Modifier.fillMaxWidth().then(
            if (offer.isSelected) Modifier.background(GoldLight, RoundedCornerShape(12.dp)) else Modifier
        )
    ) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text(offer.professional?.user?.name ?: "Usta", fontWeight = FontWeight.Bold)
                Text("${offer.price} ₺", color = Gold, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                if (!offer.note.isNullOrBlank()) {
                    Spacer(Modifier.height(4.dp))
                    Text(offer.note, color = TextSecondary, fontSize = 13.sp)
                }
            }
            if (offer.isSelected) {
                Surface(shape = RoundedCornerShape(20.dp), color = Gold) {
                    Text("Seçildi", color = Primary,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            } else if (canSelect) {
                GoldButton("Seç", onClick = onSelect)
            }
        }
    }
}

@Composable
fun Chip(label: String) {
    Surface(shape = RoundedCornerShape(20.dp), color = Divider) {
        Text(label, fontSize = 12.sp, color = TextSecondary,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp))
    }
}
