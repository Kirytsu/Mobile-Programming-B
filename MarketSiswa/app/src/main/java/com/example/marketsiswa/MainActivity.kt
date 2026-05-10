package com.example.marketsiswa

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.marketsiswa.ui.theme.MarketSiswaTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// Data Model
data class Product(
    val id: Long = System.currentTimeMillis(),
    val name: String,
    val price: String,
    val description: String,
    val category: String = "Umum"
)

// Daftar kategori bawaan
val DEFAULT_CATEGORIES = listOf(
    "Makanan & Minuman", "Fashion", "Elektronik",
    "Buku & Alat Tulis", "Aksesoris", "Jasa", "Lainnya"
)

// Entry Point
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { MarketSiswaTheme { MainScreen() } }
    }
}

// ===== MAIN SCREEN =====

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    var currentScreen by remember { mutableStateOf("home") }
    val productList = remember { mutableStateListOf<Product>() }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        if (productList.isEmpty()) {
            productList.addAll(listOf(
                Product(name = "Brownies Lumer", price = "15000",
                    description = "Cokelat lembut dan lumer di mulut.", category = "Makanan & Minuman"),
                Product(name = "Kaos Custom", price = "85000",
                    description = "Bahan cotton combed 30s, desain bebas.", category = "Fashion"),
                Product(name = "Charger Type-C", price = "35000",
                    description = "Fast charging 20W, kabel 1 meter.", category = "Elektronik"),
                Product(name = "Jasa Desain Logo", price = "50000",
                    description = "Desain profesional, revisi 2x.", category = "Jasa")
            ))
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        if (currentScreen == "add") "Tambah Produk" else "MarketSiswa",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    if (currentScreen == "add") {
                        IconButton(onClick = { currentScreen = "home" }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                        }
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        bottomBar = {
            NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
                NavigationBarItem(
                    selected = currentScreen == "home",
                    onClick = { currentScreen = "home" },
                    label = { Text("Home") },
                    icon = { Icon(
                        if (currentScreen == "home") Icons.Filled.Home else Icons.Outlined.Home,
                        contentDescription = null
                    ) }
                )
                NavigationBarItem(
                    selected = currentScreen == "profile",
                    onClick = { currentScreen = "profile" },
                    label = { Text("Profile") },
                    icon = { Icon(
                        if (currentScreen == "profile") Icons.Filled.Person else Icons.Outlined.Person,
                        contentDescription = null
                    ) }
                )
            }
        },
        floatingActionButton = {
            if (currentScreen == "home") {
                ExtendedFloatingActionButton(
                    onClick = { currentScreen = "add" },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Jual", fontWeight = FontWeight.SemiBold)
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            AnimatedContent(
                targetState = currentScreen,
                transitionSpec = {
                    slideInHorizontally { it } + fadeIn() togetherWith
                            slideOutHorizontally { -it } + fadeOut()
                },
                label = "nav"
            ) { screen ->
                when (screen) {
                    "home" -> HomeScreen(productList)
                    "add" -> AddProductScreen { newProduct ->
                        productList.add(0, newProduct)
                        scope.launch {
                            currentScreen = "home"
                            snackbarHostState.showSnackbar("Produk berhasil ditambahkan!")
                        }
                    }
                    "profile" -> ProfileScreen()
                }
            }
        }
    }
}

// ===== HOME SCREEN =====

@Composable
fun HomeScreen(products: List<Product>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Greeting
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(Modifier.padding(20.dp)) {
                    Text("Halo, Siswa!", fontSize = 24.sp, fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer)
                    Spacer(Modifier.height(4.dp))
                    Text("Temukan ${products.size} produk dari sesama mahasiswa",
                        fontSize = 14.sp, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f))
                }
            }
        }

        // Section label
        item {
            Text("Produk Terbaru", fontSize = 18.sp, fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 4.dp))
        }

        // Product cards
        items(products) { product ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(Modifier.fillMaxWidth().padding(14.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    // Category icon
                    Box(
                        modifier = Modifier.size(48.dp).clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(getCategoryIcon(product.category), contentDescription = null,
                            modifier = Modifier.size(24.dp),
                            tint = MaterialTheme.colorScheme.onPrimaryContainer)
                    }
                    // Product info
                    Column(Modifier.weight(1f)) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(product.name, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            Text("Rp ${product.price}", color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.ExtraBold, fontSize = 14.sp)
                        }
                        Spacer(Modifier.height(2.dp))
                        Text(product.description, fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant, lineHeight = 17.sp)
                        Spacer(Modifier.height(6.dp))
                        Surface(shape = RoundedCornerShape(6.dp),
                            color = MaterialTheme.colorScheme.secondaryContainer) {
                            Text(product.category, modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                fontSize = 11.sp, fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSecondaryContainer)
                        }
                    }
                }
            }
        }

        // Bottom spacer for FAB
        item { Spacer(Modifier.height(72.dp)) }
    }
}

fun getCategoryIcon(category: String) = when (category) {
    "Makanan & Minuman" -> Icons.Outlined.ShoppingCart
    "Fashion" -> Icons.Outlined.Star
    "Elektronik" -> Icons.Outlined.Phone
    "Buku & Alat Tulis" -> Icons.Outlined.Edit
    "Aksesoris" -> Icons.Outlined.Favorite
    "Jasa" -> Icons.Outlined.Build
    else -> Icons.Outlined.Info
}

// ===== ADD PRODUCT SCREEN =====

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProductScreen(onProductAdded: (Product) -> Unit) {
    var name by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(DEFAULT_CATEGORIES[0]) }
    var customCategory by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    // Kategori final: jika "Lainnya" dipilih, gunakan input custom
    val finalCategory = if (selectedCategory == "Lainnya" && customCategory.isNotBlank())
        customCategory else selectedCategory

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Info card
        Card(
            modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        ) {
            Row(Modifier.padding(14.dp), horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.Info, contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer)
                Text("Isi formulir untuk menambahkan produk baru.",
                    fontSize = 13.sp, color = MaterialTheme.colorScheme.onPrimaryContainer)
            }
        }

        // Text fields
        OutlinedTextField(value = name, onValueChange = { name = it },
            label = { Text("Nama Produk") }, modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp), singleLine = true)

        OutlinedTextField(value = price, onValueChange = { price = it },
            label = { Text("Harga (Rp)") }, modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp), singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))

        OutlinedTextField(value = desc, onValueChange = { desc = it },
            label = { Text("Deskripsi") }, modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp), minLines = 3)

        // Category dropdown
        ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
            OutlinedTextField(
                value = selectedCategory, onValueChange = {},
                readOnly = true, label = { Text("Kategori") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                modifier = Modifier.fillMaxWidth().menuAnchor(),
                shape = RoundedCornerShape(12.dp)
            )
            ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                DEFAULT_CATEGORIES.forEach { cat ->
                    DropdownMenuItem(
                        text = { Text(cat) },
                        onClick = { selectedCategory = cat; expanded = false }
                    )
                }
            }
        }

        // Custom category input (shown only when "Lainnya" is selected)
        AnimatedVisibility(visible = selectedCategory == "Lainnya") {
            OutlinedTextField(value = customCategory, onValueChange = { customCategory = it },
                label = { Text("Tulis kategori sendiri") }, modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp), singleLine = true)
        }

        Spacer(Modifier.height(4.dp))

        // Submit button
        Button(
            onClick = {
                isLoading = true
                scope.launch {
                    delay(800)
                    onProductAdded(Product(name = name, price = price,
                        description = desc, category = finalCategory))
                    isLoading = false
                }
            },
            modifier = Modifier.fillMaxWidth().height(54.dp),
            enabled = name.isNotBlank() && price.isNotBlank() && !isLoading
                    && (selectedCategory != "Lainnya" || customCategory.isNotBlank()),
            shape = RoundedCornerShape(14.dp)
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(22.dp),
                    color = MaterialTheme.colorScheme.onPrimary, strokeWidth = 2.dp)
                Spacer(Modifier.width(10.dp))
                Text("Menyimpan...", fontWeight = FontWeight.Bold)
            } else {
                Icon(Icons.Default.Check, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Simpan Produk", fontWeight = FontWeight.Bold)
            }
        }
    }
}

// ===== PROFILE SCREEN =====

@Composable
fun ProfileScreen() {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Spacer(Modifier.height(16.dp))

        // Avatar
        Box(
            modifier = Modifier.size(88.dp).clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary),
            contentAlignment = Alignment.Center
        ) {
            Text("NV", fontSize = 32.sp, fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimary)
        }

        Text("Nathanael Valen Susilo", fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Text("Teknik Informatika ITS", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

        // Stats row
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            listOf("4" to "Produk", "12" to "Terjual", "4.8" to "Rating").forEach { (value, label) ->
                Card(
                    modifier = Modifier.weight(1f), shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(Modifier.fillMaxWidth().padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(value, fontSize = 20.sp, fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary)
                        Text(label, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }

        // Settings menu
        Card(
            modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            listOf(
                Triple(Icons.Outlined.Person, "Edit Profil", "Ubah nama dan foto"),
                Triple(Icons.Outlined.Notifications, "Notifikasi", "Atur preferensi"),
                Triple(Icons.Outlined.Info, "Tentang", "MarketSiswa v1.0")
            ).forEachIndexed { index, (icon, title, subtitle) ->
                Row(
                    Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Icon(icon, contentDescription = null, modifier = Modifier.size(22.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    Column(Modifier.weight(1f)) {
                        Text(title, fontWeight = FontWeight.Medium, fontSize = 15.sp)
                        Text(subtitle, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Icon(Icons.Default.KeyboardArrowRight, contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f))
                }
                if (index < 2) {
                    HorizontalDivider(Modifier.padding(horizontal = 16.dp),
                        color = MaterialTheme.colorScheme.outlineVariant)
                }
            }
        }
    }
}