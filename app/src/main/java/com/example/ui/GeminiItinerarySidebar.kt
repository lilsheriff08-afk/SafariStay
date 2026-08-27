package com.example.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.StayItem
import com.example.viewmodel.GeminiItineraryState
import com.example.viewmodel.SafariViewModel

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun GeminiItinerarySidebar(
    viewModel: SafariViewModel,
    initialLodgeName: String? = null,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val itineraryState by viewModel.geminiItineraryState.collectAsState()
    val stays = viewModel.filteredStays.collectAsState().value

    // PDF Document Export Launcher
    val createPdfLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/pdf")
    ) { uri: Uri? ->
        if (uri != null && itineraryState.generatedPlanText != null) {
            generateGeminiItineraryPdf(
                context = context,
                uri = uri,
                lodgeLocation = itineraryState.lodgeLocation,
                country = itineraryState.country,
                durationDays = itineraryState.durationDays,
                travelParty = itineraryState.travelParty,
                budget = itineraryState.budget,
                interests = itineraryState.interests,
                pace = itineraryState.pace,
                planText = itineraryState.generatedPlanText!!
            )
        }
    }

    // East African Countries & Curated Destinations
    val eastAfricaCountries = listOf("Kenya 🇰🇪", "Tanzania 🇹🇿", "Uganda 🇺🇬", "Rwanda 🇷🇼", "Cross-Border 🌍")
    var selectedCountry by remember { mutableStateOf("Kenya 🇰🇪") }

    val destinationsByCountry = remember {
        mapOf(
            "Kenya 🇰🇪" to listOf(
                "Masai Mara National Reserve (Big Five & Migration)",
                "Amboseli National Park (Elephants & Mt Kilimanjaro)",
                "Ol Pejeta Conservancy (Rhino Sanctuary & Chimps)",
                "Samburu Game Reserve (Special 5 & Ewaso Nyiro)",
                "Lake Nakuru & Naivasha (Flamingos & Birding)",
                "Tsavo East & West (Red Elephants & Lava Flows)",
                "Diani Beach & Swahili Coast (Ocean & Reefs)"
            ),
            "Tanzania 🇹🇿" to listOf(
                "Serengeti National Park (Endless Savanna & Predators)",
                "Ngorongoro Crater (Intact Caldera & Dense Wildlife)",
                "Tarangire National Park (Baobabs & Elephant Herds)",
                "Lake Manyara National Park (Tree-climbing Lions)",
                "Mount Kilimanjaro Foothills (Forest & Wilderness)",
                "Zanzibar Island & Stone Town (Spice & Dhow Safari)"
            ),
            "Uganda 🇺🇬" to listOf(
                "Bwindi Impenetrable National Park (Mountain Gorillas)",
                "Kibale Forest National Park (Chimpanzee Capital)",
                "Queen Elizabeth National Park (Kazinga Boat Safari)",
                "Murchison Falls National Park (Nile & Savanna)",
                "Lake Bunyonyi (Island Relaxation & Canoeing)"
            ),
            "Rwanda 🇷🇼" to listOf(
                "Volcanoes National Park (Gorillas & Golden Monkeys)",
                "Nyungwe National Park (Rainforest Canopy Walk)",
                "Akagera National Park (Big Five Savanna & Lake Ihema)",
                "Lake Kivu (Scenic Rift Valley Waterfront)"
            ),
            "Cross-Border 🌍" to listOf(
                "Kenya & Tanzania Grand Migration Circuit (Mara + Serengeti)",
                "Primate & Savanna Expedition (Uganda Gorillas + Kenya Mara)",
                "East Africa Ultimate 4-Nation Wildlife Circuit"
            )
        )
    }

    var selectedDestination by remember {
        mutableStateOf(
            initialLodgeName ?: destinationsByCountry["Kenya 🇰🇪"]?.first() ?: "Masai Mara National Reserve"
        )
    }

    var customDestinationInput by remember { mutableStateOf("") }
    var isCustomDestinationMode by remember { mutableStateOf(false) }

    // Duration options (days)
    val durationOptions = listOf(3, 5, 7, 10)
    var selectedDuration by remember { mutableStateOf(5) }

    // Travel Party
    val partyOptions = listOf(
        "Couples & Honeymoon 💍",
        "Family with Kids 👨‍👩‍👧‍👦",
        "Solo Wildlife Adventurer 🧭",
        "Photography Expedition 📸",
        "Luxury Eco-Travelers 👑"
    )
    var selectedParty by remember { mutableStateOf("Couples & Honeymoon 💍") }

    // Budget Level
    val budgetOptions = listOf(
        "Budget Camping ($)",
        "Mid-Range Safari Lodges ($$)",
        "Luxury Fly-in Camps ($$$)"
    )
    var selectedBudget by remember { mutableStateOf("Mid-Range Safari Lodges ($$)") }

    // Passions & Interests
    val availableInterests = remember {
        listOf(
            "🦁 Big Five Game Drives",
            "🦍 Mountain Gorilla Trekking",
            "🦓 Great Migration Crossings",
            "📸 Pro Wildlife Photography",
            "🎈 Hot Air Balloon Safari",
            "⛺ Maasai / Samburu Culture",
            "🦏 Rhino & Conservation",
            "🦅 Rift Valley Birdwatching",
            "🚶 Guided Bush Walking",
            "🌊 Bush & Beach Combo",
            "🌌 Night Game Safaris",
            "🥂 Sunset Sundowners & Dining"
        )
    }

    val selectedInterests = remember {
        mutableStateListOf("🦁 Big Five Game Drives", "📸 Pro Wildlife Photography", "🥂 Sunset Sundowners & Dining")
    }

    var selectedPace by remember { mutableStateOf("Balanced & Immersive") }
    val paceOptions = listOf("Balanced & Immersive", "Action-Packed Early Riser", "Relaxed & Luxury")

    // Full screen dialog styled like an elegant sliding sidebar drawer from the right
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.55f))
                .clickable { onDismiss() }
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(0.94f) // Takes majority of screen as a dedicated sidebar
                    .align(Alignment.CenterEnd)
                    .clickable(enabled = false) {}, // Prevent dismiss on click inside
                shape = RoundedCornerShape(topStart = 24.dp, bottomStart = 24.dp),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .statusBarsPadding()
                        .navigationBarsPadding()
                ) {
                    // --- SIDEBAR TOP BAR ---
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(40.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            Icons.Default.AutoAwesome,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.onPrimary,
                                            modifier = Modifier.size(22.dp)
                                        )
                                    }
                                }
                                Column {
                                    Text(
                                        text = "Gemini AI Safari Planner",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 17.sp,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                    Text(
                                        text = "Custom East Africa Itinerary Generator",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f)
                                    )
                                }
                            }
                            IconButton(
                                onClick = onDismiss,
                                modifier = Modifier
                                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.3f), CircleShape)
                                    .size(32.dp)
                            ) {
                                Icon(
                                    Icons.Default.Close,
                                    contentDescription = "Close Sidebar",
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(vertical = 16.dp)
                    ) {
                        // --- PRESET EXPEDITION TEMPLATES ---
                        item {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(
                                    text = "✨ Quick Safari Presets (1-Tap)",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                LazyRow(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    item {
                                        PresetCard(
                                            title = "🇰🇪 Masai Mara Migration",
                                            subtitle = "5 Days • Big Five & Balloon",
                                            onClick = {
                                                selectedCountry = "Kenya 🇰🇪"
                                                selectedDestination = "Masai Mara National Reserve (Big Five & Migration)"
                                                selectedDuration = 5
                                                selectedParty = "Couples & Honeymoon 💍"
                                                selectedBudget = "Mid-Range Safari Lodges ($$)"
                                                selectedInterests.clear()
                                                selectedInterests.addAll(listOf("🦁 Big Five Game Drives", "🦓 Great Migration Crossings", "🎈 Hot Air Balloon Safari"))
                                                viewModel.generateAiItinerary(
                                                    lodgeLocation = "Masai Mara National Reserve",
                                                    interests = selectedInterests.toList(),
                                                    pace = selectedPace,
                                                    country = "Kenya",
                                                    durationDays = 5,
                                                    travelParty = selectedParty,
                                                    budget = selectedBudget
                                                )
                                            }
                                        )
                                    }
                                    item {
                                        PresetCard(
                                            title = "🇺🇬 Bwindi Mountain Gorillas",
                                            subtitle = "3 Days • Primate Habituation",
                                            onClick = {
                                                selectedCountry = "Uganda 🇺🇬"
                                                selectedDestination = "Bwindi Impenetrable National Park (Mountain Gorillas)"
                                                selectedDuration = 3
                                                selectedParty = "Solo Wildlife Adventurer 🧭"
                                                selectedBudget = "Luxury Fly-in Camps ($$$)"
                                                selectedInterests.clear()
                                                selectedInterests.addAll(listOf("🦍 Mountain Gorilla Trekking", "📸 Pro Wildlife Photography", "🚶 Guided Bush Walking"))
                                                viewModel.generateAiItinerary(
                                                    lodgeLocation = "Bwindi Impenetrable National Park",
                                                    interests = selectedInterests.toList(),
                                                    pace = selectedPace,
                                                    country = "Uganda",
                                                    durationDays = 3,
                                                    travelParty = selectedParty,
                                                    budget = selectedBudget
                                                )
                                            }
                                        )
                                    }
                                    item {
                                        PresetCard(
                                            title = "🇹🇿 Serengeti & Ngorongoro",
                                            subtitle = "7 Days • Savanna Grand Tour",
                                            onClick = {
                                                selectedCountry = "Tanzania 🇹🇿"
                                                selectedDestination = "Serengeti National Park (Endless Savanna & Predators)"
                                                selectedDuration = 7
                                                selectedParty = "Family with Kids 👨‍👩‍👧‍👦"
                                                selectedBudget = "Mid-Range Safari Lodges ($$)"
                                                selectedInterests.clear()
                                                selectedInterests.addAll(listOf("🦁 Big Five Game Drives", "🦓 Great Migration Crossings", "⛺ Maasai / Samburu Culture", "🌌 Night Game Safaris"))
                                                viewModel.generateAiItinerary(
                                                    lodgeLocation = "Serengeti National Park & Ngorongoro Crater",
                                                    interests = selectedInterests.toList(),
                                                    pace = selectedPace,
                                                    country = "Tanzania",
                                                    durationDays = 7,
                                                    travelParty = selectedParty,
                                                    budget = selectedBudget
                                                )
                                            }
                                        )
                                    }
                                }
                            }
                        }

                        // --- CONFIGURATION CARD ---
                        item {
                            ElevatedCard(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.elevatedCardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                )
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(14.dp)
                                ) {
                                    // 1. East African Country & Destination
                                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Text(
                                                text = "1. East African Destination",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.sp,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                            TextButton(
                                                onClick = { isCustomDestinationMode = !isCustomDestinationMode }
                                            ) {
                                                Icon(
                                                    if (isCustomDestinationMode) Icons.Default.List else Icons.Default.Edit,
                                                    contentDescription = null,
                                                    modifier = Modifier.size(14.dp)
                                                )
                                                Spacer(Modifier.width(4.dp))
                                                Text(
                                                    if (isCustomDestinationMode) "Pick from list" else "Custom place",
                                                    fontSize = 12.sp
                                                )
                                            }
                                        }

                                        // Country Tabs
                                        ScrollableTabRow(
                                            selectedTabIndex = eastAfricaCountries.indexOf(selectedCountry).coerceAtLeast(0),
                                            edgePadding = 0.dp,
                                            containerColor = Color.Transparent,
                                            divider = {}
                                        ) {
                                            eastAfricaCountries.forEach { country ->
                                                Tab(
                                                    selected = (selectedCountry == country),
                                                    onClick = {
                                                        selectedCountry = country
                                                        selectedDestination = destinationsByCountry[country]?.firstOrNull() ?: ""
                                                    },
                                                    text = { Text(country, fontSize = 12.sp, fontWeight = if (selectedCountry == country) FontWeight.Bold else FontWeight.Normal) }
                                                )
                                            }
                                        }

                                        if (!isCustomDestinationMode) {
                                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                                val destinations = destinationsByCountry[selectedCountry] ?: emptyList()
                                                destinations.forEach { dest ->
                                                    FilterChip(
                                                        selected = (selectedDestination == dest),
                                                        onClick = { selectedDestination = dest },
                                                        label = { Text(dest, fontSize = 12.5.sp) },
                                                        leadingIcon = if (selectedDestination == dest) {
                                                            { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                                                        } else {
                                                            { Icon(Icons.Default.Place, contentDescription = null, modifier = Modifier.size(16.dp)) }
                                                        },
                                                        modifier = Modifier.fillMaxWidth()
                                                    )
                                                }
                                            }
                                        } else {
                                            OutlinedTextField(
                                                value = customDestinationInput,
                                                onValueChange = {
                                                    customDestinationInput = it
                                                    selectedDestination = it
                                                },
                                                label = { Text("Lodge Name or Park Region") },
                                                placeholder = { Text("e.g. Samburu Elephant Bedroom Camp") },
                                                modifier = Modifier.fillMaxWidth(),
                                                shape = RoundedCornerShape(12.dp)
                                            )
                                        }
                                    }

                                    HorizontalDivider(modifier = Modifier.padding(vertical = 2.dp))

                                    // 2. Duration & Pace
                                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Text(
                                            text = "2. Expedition Duration & Pace",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp,
                                            color = MaterialTheme.colorScheme.primary
                                        )

                                        Row(
                                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            durationOptions.forEach { days ->
                                                FilterChip(
                                                    selected = (selectedDuration == days),
                                                    onClick = { selectedDuration = days },
                                                    label = {
                                                        Text(
                                                            "$days Days",
                                                            fontSize = 12.sp,
                                                            fontWeight = if (selectedDuration == days) FontWeight.Bold else FontWeight.Normal
                                                        )
                                                    },
                                                    modifier = Modifier.weight(1f)
                                                )
                                            }
                                        }

                                        Row(
                                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            paceOptions.forEach { pace ->
                                                FilterChip(
                                                    selected = (selectedPace == pace),
                                                    onClick = { selectedPace = pace },
                                                    label = { Text(pace.split(" ").first(), fontSize = 11.5.sp) },
                                                    modifier = Modifier.weight(1f)
                                                )
                                            }
                                        }
                                    }

                                    HorizontalDivider(modifier = Modifier.padding(vertical = 2.dp))

                                    // 3. Travel Style & Budget
                                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Text(
                                            text = "3. Travel Party & Budget Tier",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp,
                                            color = MaterialTheme.colorScheme.primary
                                        )

                                        FlowRow(
                                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                                            verticalArrangement = Arrangement.spacedBy(6.dp),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            partyOptions.forEach { party ->
                                                FilterChip(
                                                    selected = (selectedParty == party),
                                                    onClick = { selectedParty = party },
                                                    label = { Text(party, fontSize = 12.sp) }
                                                )
                                            }
                                        }

                                        Row(
                                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            budgetOptions.forEach { budget ->
                                                FilterChip(
                                                    selected = (selectedBudget == budget),
                                                    onClick = { selectedBudget = budget },
                                                    label = { Text(budget, fontSize = 11.sp) },
                                                    modifier = Modifier.weight(1f)
                                                )
                                            }
                                        }
                                    }

                                    HorizontalDivider(modifier = Modifier.padding(vertical = 2.dp))

                                    // 4. Wildlife & Highlight Passions
                                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Text(
                                            text = "4. Wildlife & Safari Highlights",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp,
                                            color = MaterialTheme.colorScheme.primary
                                        )

                                        FlowRow(
                                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                                            verticalArrangement = Arrangement.spacedBy(6.dp),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            availableInterests.forEach { interest ->
                                                val isSelected = selectedInterests.contains(interest)
                                                FilterChip(
                                                    selected = isSelected,
                                                    onClick = {
                                                        if (isSelected) selectedInterests.remove(interest)
                                                        else selectedInterests.add(interest)
                                                    },
                                                    label = { Text(interest, fontSize = 12.sp) },
                                                    shape = RoundedCornerShape(20.dp)
                                                )
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(4.dp))

                                    Button(
                                        onClick = {
                                            val targetDest = if (isCustomDestinationMode && customDestinationInput.isNotBlank()) {
                                                customDestinationInput
                                            } else {
                                                selectedDestination
                                            }
                                            val cleanCountry = selectedCountry.replace(Regex("[^a-zA-Z]"), "").trim()
                                            viewModel.generateAiItinerary(
                                                lodgeLocation = targetDest,
                                                interests = selectedInterests.toList(),
                                                pace = selectedPace,
                                                country = cleanCountry.ifBlank { "Kenya" },
                                                durationDays = selectedDuration,
                                                travelParty = selectedParty,
                                                budget = selectedBudget
                                            )
                                        },
                                        enabled = !itineraryState.isLoading,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(50.dp)
                                            .testTag("generate_ai_itinerary_button"),
                                        shape = RoundedCornerShape(14.dp)
                                    ) {
                                        if (itineraryState.isLoading) {
                                            CircularProgressIndicator(
                                                modifier = Modifier.size(22.dp),
                                                color = MaterialTheme.colorScheme.onPrimary,
                                                strokeWidth = 2.5.dp
                                            )
                                            Spacer(Modifier.width(10.dp))
                                            Text("Crafting Itinerary with Gemini AI...")
                                        } else {
                                            Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(20.dp))
                                            Spacer(Modifier.width(8.dp))
                                            Text("Generate $selectedDuration-Day Itinerary with Gemini", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                        }
                                    }
                                }
                            }
                        }

                        // --- GENERATED ITINERARY RESULT AREA ---
                        item {
                            if (itineraryState.isLoading) {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)),
                                    shape = RoundedCornerShape(16.dp)
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .padding(24.dp)
                                            .fillMaxWidth(),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                                        Text(
                                            text = "Consulting Gemini AI Naturalist & Ranger Network...",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        Text(
                                            text = "Creating ${itineraryState.durationDays}-day custom safari plans, game drive routes, and packing checklist for ${itineraryState.lodgeLocation} (${itineraryState.country})...",
                                            fontSize = 12.5.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            } else if (itineraryState.generatedPlanText != null) {
                                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                    // Action buttons bar
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(
                                                text = "Your AI Safari Itinerary Plan",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 16.sp,
                                                color = MaterialTheme.colorScheme.onBackground
                                            )
                                            Text(
                                                text = "${itineraryState.durationDays} Days • ${itineraryState.country} • ${itineraryState.travelParty}",
                                                fontSize = 11.5.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                            IconButton(
                                                onClick = {
                                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                                    val clip = ClipData.newPlainText("Safari Itinerary", itineraryState.generatedPlanText)
                                                    clipboard.setPrimaryClip(clip)
                                                    Toast.makeText(context, "Itinerary copied to clipboard!", Toast.LENGTH_SHORT).show()
                                                }
                                            ) {
                                                Icon(Icons.Default.ContentCopy, contentDescription = "Copy Itinerary", tint = MaterialTheme.colorScheme.primary)
                                            }
                                            IconButton(
                                                onClick = {
                                                    Toast.makeText(context, "${itineraryState.durationDays}-Day Safari saved to My Trips!", Toast.LENGTH_SHORT).show()
                                                }
                                            ) {
                                                Icon(Icons.Default.BookmarkAdd, contentDescription = "Save Plan", tint = MaterialTheme.colorScheme.primary)
                                            }
                                        }
                                    }

                                    // PROMINENT OFFLINE PDF DOWNLOAD BUTTON
                                    ElevatedButton(
                                        onClick = {
                                            val cleanLodge = itineraryState.lodgeLocation.take(15).replace(Regex("[^a-zA-Z0-9]"), "_")
                                            val fileName = "Safari_Itinerary_${itineraryState.country}_${cleanLodge}_${System.currentTimeMillis() / 1000}.pdf"
                                            createPdfLauncher.launch(fileName)
                                        },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .testTag("download_gemini_pdf_button"),
                                        shape = RoundedCornerShape(12.dp),
                                        colors = ButtonDefaults.elevatedButtonColors(
                                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                                        )
                                    ) {
                                        Icon(
                                            Icons.Default.PictureAsPdf,
                                            contentDescription = "Download PDF Itinerary",
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(Modifier.width(8.dp))
                                        Text(
                                            text = "Download PDF Itinerary (Offline Access) 📄",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp
                                        )
                                    }

                                    // Formatted Markdown Card
                                    FormattedItineraryCard(planText = itineraryState.generatedPlanText!!)
                                }
                            } else {
                                // Initial hint state
                                OutlinedCard(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(16.dp)
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .padding(20.dp)
                                            .fillMaxWidth(),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.Explore,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(38.dp)
                                        )
                                        Text(
                                            text = "Ready to Plan Your East Africa Safari?",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp
                                        )
                                        Text(
                                            text = "Choose your destination in Kenya, Tanzania, Uganda, or Rwanda, select your travel style and passions, and let Gemini AI craft an unforgettable wildlife expedition plan.",
                                            fontSize = 12.5.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.padding(horizontal = 8.dp)
                                        )
                                    }
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
fun PresetCard(
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    ElevatedCard(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
        ),
        modifier = Modifier.width(220.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Text(
                text = subtitle,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
            )
        }
    }
}

@Composable
fun FormattedItineraryCard(planText: String) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            val lines = planText.lines()
            for (line in lines) {
                val trimmed = line.trim()
                if (trimmed.isEmpty()) continue

                when {
                    trimmed.startsWith("# ") -> {
                        Text(
                            text = trimmed.removePrefix("# "),
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 18.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    trimmed.startsWith("## ") -> {
                        Surface(
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp)
                        ) {
                            Text(
                                text = trimmed.removePrefix("## "),
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.5.sp,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                    trimmed.startsWith("### ") -> {
                        Text(
                            text = trimmed.removePrefix("### "),
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.5.sp,
                            color = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                    trimmed.startsWith("> ") -> {
                        Surface(
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(8.dp))
                        ) {
                            Text(
                                text = trimmed.removePrefix("> ").replace("**", ""),
                                fontSize = 12.sp,
                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    }
                    trimmed.contains("Ranger AI Bush Tip") || trimmed.startsWith("💡") -> {
                        Surface(
                            color = Color(0xFFFEF8EC),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, Color(0xFFE6A100), RoundedCornerShape(10.dp))
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Text("💡", fontSize = 16.sp)
                                Text(
                                    text = trimmed.replace("**", ""),
                                    fontSize = 12.5.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF7A4A00)
                                )
                            }
                        }
                    }
                    trimmed.contains("Packing") || trimmed.startsWith("🎒") -> {
                        Surface(
                            color = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.35f),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Text("🎒", fontSize = 16.sp)
                                Text(
                                    text = trimmed.replace("**", ""),
                                    fontSize = 12.5.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onTertiaryContainer
                                )
                            }
                        }
                    }
                    trimmed.startsWith("- ") || trimmed.startsWith("* ") -> {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.Top,
                            modifier = Modifier.padding(start = 6.dp)
                        ) {
                            Text("•", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                            Text(
                                text = trimmed.substring(2).replace("**", ""),
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                    else -> {
                        Text(
                            text = trimmed.replace("**", ""),
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}

fun generateGeminiItineraryPdf(
    context: Context,
    uri: Uri,
    lodgeLocation: String,
    country: String = "Kenya",
    durationDays: Int = 3,
    travelParty: String = "Couples / Explorers",
    budget: String = "Mid-Range Safari Lodges",
    interests: List<String>,
    pace: String,
    planText: String
) {
    try {
        context.contentResolver.openOutputStream(uri)?.use { outputStream ->
            val pdfDocument = android.graphics.pdf.PdfDocument()

            val colorPrimary = android.graphics.Color.parseColor("#2E5A27") // Safari Green
            val colorAccent = android.graphics.Color.parseColor("#8F5E15")  // Safari Gold
            val colorDarkText = android.graphics.Color.parseColor("#1A1A1A")
            val colorMutedText = android.graphics.Color.parseColor("#555555")
            val colorLightBg = android.graphics.Color.parseColor("#F4F6F3")
            val colorTipBg = android.graphics.Color.parseColor("#FEF8EC")
            val colorTipBorder = android.graphics.Color.parseColor("#E6A100")
            val colorWhite = android.graphics.Color.WHITE
            val colorBorder = android.graphics.Color.parseColor("#D0D7CE")

            val bgPaint = android.graphics.Paint().apply { color = colorLightBg }
            val tipBgPaint = android.graphics.Paint().apply { color = colorTipBg }
            val tipBorderPaint = android.graphics.Paint().apply {
                color = colorTipBorder
                style = android.graphics.Paint.Style.STROKE
                strokeWidth = 1.5f
            }
            val borderPaint = android.graphics.Paint().apply {
                color = colorBorder
                style = android.graphics.Paint.Style.STROKE
                strokeWidth = 1f
            }
            val primaryHeaderPaint = android.graphics.Paint().apply { color = colorPrimary }
            val titlePaint = android.graphics.Paint().apply {
                color = colorWhite
                textSize = 17f
                isAntiAlias = true
                typeface = android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.BOLD)
            }
            val subtitlePaint = android.graphics.Paint().apply {
                color = colorWhite
                textSize = 9.5f
                isAntiAlias = true
                typeface = android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.NORMAL)
            }
            val sectionHeadingPaint = android.graphics.Paint().apply {
                color = colorPrimary
                textSize = 13f
                isAntiAlias = true
                typeface = android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.BOLD)
            }
            val subHeadingPaint = android.graphics.Paint().apply {
                color = colorAccent
                textSize = 11f
                isAntiAlias = true
                typeface = android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.BOLD)
            }
            val textPaint = android.graphics.Paint().apply {
                color = colorDarkText
                textSize = 10f
                isAntiAlias = true
                typeface = android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.NORMAL)
            }
            val boldTextPaint = android.graphics.Paint().apply {
                color = colorDarkText
                textSize = 10f
                isAntiAlias = true
                typeface = android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.BOLD)
            }
            val mutedTextPaint = android.graphics.Paint().apply {
                color = colorMutedText
                textSize = 9f
                isAntiAlias = true
                typeface = android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.ITALIC)
            }

            var pageNumber = 1
            var pageInfo = android.graphics.pdf.PdfDocument.PageInfo.Builder(595, 842, pageNumber).create()
            var currentPage = pdfDocument.startPage(pageInfo)
            var canvas = currentPage.canvas

            fun drawPageHeader(c: android.graphics.Canvas, pNum: Int) {
                c.drawRect(35f, 30f, 560f, 95f, primaryHeaderPaint)
                c.drawText("GEMINI AI SAFARI EXPEDITION ITINERARY", 50f, 60f, titlePaint)
                c.drawText("OFFLINE SAFARI COMPANION • $lodgeLocation ($country) • $durationDays DAYS", 50f, 80f, subtitlePaint)
                
                val footerPaint = android.graphics.Paint().apply {
                    color = colorMutedText
                    textSize = 8f
                    isAntiAlias = true
                }
                c.drawText("Generated by Gemini AI • Page $pNum • Offline Safari Access", 35f, 820f, footerPaint)
            }

            drawPageHeader(canvas, pageNumber)

            var currentY = 115f

            // Metadata card
            canvas.drawRect(35f, currentY, 560f, currentY + 54f, bgPaint)
            canvas.drawRect(35f, currentY, 560f, currentY + 54f, borderPaint)

            val formattedInterests = if (interests.isNotEmpty()) interests.joinToString(", ") else "Big Five Game Drives, Photography"
            canvas.drawText("Destination: $lodgeLocation ($country) • Duration: $durationDays Days", 45f, currentY + 16f, boldTextPaint)
            canvas.drawText("Style: $travelParty • Budget: $budget", 45f, currentY + 30f, textPaint)
            canvas.drawText("Passions: $formattedInterests • Pace: $pace", 45f, currentY + 44f, mutedTextPaint)

            currentY += 68f

            fun checkNewPage(neededHeight: Float) {
                if (currentY + neededHeight > 790f) {
                    pdfDocument.finishPage(currentPage)
                    pageNumber++
                    pageInfo = android.graphics.pdf.PdfDocument.PageInfo.Builder(595, 842, pageNumber).create()
                    currentPage = pdfDocument.startPage(pageInfo)
                    canvas = currentPage.canvas
                    drawPageHeader(canvas, pageNumber)
                    currentY = 110f
                }
            }

            val lines = planText.lines()
            for (line in lines) {
                val trimmed = line.trim()
                if (trimmed.isEmpty()) {
                    currentY += 6f
                    continue
                }

                when {
                    trimmed.startsWith("# ") -> {
                        val header = trimmed.removePrefix("# ")
                        checkNewPage(24f)
                        canvas.drawText(header, 35f, currentY, sectionHeadingPaint)
                        currentY += 20f
                    }
                    trimmed.startsWith("## ") -> {
                        val header = trimmed.removePrefix("## ")
                        checkNewPage(28f)
                        canvas.drawRect(35f, currentY - 12f, 560f, currentY + 6f, bgPaint)
                        canvas.drawText(header, 42f, currentY, sectionHeadingPaint)
                        currentY += 20f
                    }
                    trimmed.startsWith("### ") -> {
                        val header = trimmed.removePrefix("### ")
                        checkNewPage(20f)
                        canvas.drawText(header, 35f, currentY, subHeadingPaint)
                        currentY += 16f
                    }
                    trimmed.contains("Ranger AI Bush Tip") || trimmed.startsWith("💡") || trimmed.contains("Recommended Gear") || trimmed.startsWith("🎒") -> {
                        checkNewPage(40f)
                        val cleanText = trimmed.replace("**", "")
                        canvas.drawRect(35f, currentY - 10f, 560f, currentY + 22f, tipBgPaint)
                        canvas.drawRect(35f, currentY - 10f, 560f, currentY + 22f, tipBorderPaint)
                        
                        val textWidth = 500f
                        if (textPaint.measureText(cleanText) > textWidth) {
                            val words = cleanText.split(" ")
                            var lineBuilder = ""
                            var lineY = currentY
                            for (w in words) {
                                if (textPaint.measureText("$lineBuilder $w") > textWidth) {
                                    canvas.drawText(lineBuilder, 45f, lineY, boldTextPaint)
                                    lineBuilder = w
                                    lineY += 12f
                                    checkNewPage(14f)
                                } else {
                                    lineBuilder = if (lineBuilder.isEmpty()) w else "$lineBuilder $w"
                                }
                            }
                            if (lineBuilder.isNotEmpty()) {
                                canvas.drawText(lineBuilder, 45f, lineY, boldTextPaint)
                                currentY = lineY + 16f
                            }
                        } else {
                            canvas.drawText(cleanText, 45f, currentY + 4f, boldTextPaint)
                            currentY += 28f
                        }
                    }
                    trimmed.startsWith("- ") || trimmed.startsWith("* ") -> {
                        val bulletContent = trimmed.substring(2).replace("**", "")
                        val textWidth = 500f
                        val words = bulletContent.split(" ")
                        var lineBuilder = ""
                        var isFirstLine = true
                        for (w in words) {
                            if (textPaint.measureText("$lineBuilder $w") > textWidth) {
                                checkNewPage(14f)
                                if (isFirstLine) {
                                    canvas.drawText("• ", 42f, currentY, sectionHeadingPaint)
                                    canvas.drawText(lineBuilder, 54f, currentY, textPaint)
                                    isFirstLine = false
                                } else {
                                    canvas.drawText(lineBuilder, 54f, currentY, textPaint)
                                }
                                lineBuilder = w
                                currentY += 13f
                            } else {
                                lineBuilder = if (lineBuilder.isEmpty()) w else "$lineBuilder $w"
                            }
                        }
                        if (lineBuilder.isNotEmpty()) {
                            checkNewPage(14f)
                            if (isFirstLine) {
                                canvas.drawText("• ", 42f, currentY, sectionHeadingPaint)
                                canvas.drawText(lineBuilder, 54f, currentY, textPaint)
                            } else {
                                canvas.drawText(lineBuilder, 54f, currentY, textPaint)
                            }
                            currentY += 15f
                        }
                    }
                    else -> {
                        val cleanLine = trimmed.replace("**", "")
                        checkNewPage(14f)
                        canvas.drawText(cleanLine, 35f, currentY, textPaint)
                        currentY += 14f
                    }
                }
            }

            pdfDocument.finishPage(currentPage)
            pdfDocument.writeTo(outputStream)
            pdfDocument.close()

            Toast.makeText(context, "PDF Itinerary saved successfully! Ready for offline safari trips 📄🐘", Toast.LENGTH_LONG).show()
        }
    } catch (e: Exception) {
        android.util.Log.e("PdfExportError", "Failed to write PDF", e)
        Toast.makeText(context, "Error saving PDF: ${e.message}", Toast.LENGTH_SHORT).show()
    }
}
