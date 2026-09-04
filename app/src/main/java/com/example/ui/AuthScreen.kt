package com.example.ui

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.viewmodel.SafariViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(viewModel: SafariViewModel) {
    val context = LocalContext.current
    var isSignUp by remember { mutableStateOf(false) }

    // Form states
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("traveler@safaristay.com") }
    var password by remember { mutableStateOf("safari2026") }
    var passportId by remember { mutableStateOf("KE-892041") }
    var selectedTier by remember { mutableStateOf("Luxury Lodge") }
    var passwordVisible by remember { mutableStateOf(false) }
    var rememberMe by remember { mutableStateOf(true) }

    // Tear-off animation states
    var isTearing by remember { mutableStateOf(false) }
    var isTornAndStamped by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    val snackbarHostState = remember { SnackbarHostState() }
    val uiEvent by viewModel.uiEvent.collectAsState()

    val ticketSerial = remember { "SF-REC-2026-" + (100000..999999).random() }
    val timeSdf = remember { SimpleDateFormat("EEE, MMM dd yyyy • HH:mm", Locale.US) }
    val currentDateStr = remember { timeSdf.format(Date()) }

    LaunchedEffect(uiEvent) {
        uiEvent?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearUiEvent()
        }
    }

    // Tear animation offsets
    val tearOffsetY by animateFloatAsState(
        targetValue = if (isTearing) -60f else 0f,
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "tearOffset"
    )
    val tearRotation by animateFloatAsState(
        targetValue = if (isTearing) -4f else 0f,
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "tearRotation"
    )
    val stampScale by animateFloatAsState(
        targetValue = if (isTornAndStamped) 1.1f else 0.2f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium),
        label = "stampScale"
    )

    fun executeTearAndAuth(actionType: String) {
        if (email.isBlank() || password.isBlank()) {
            Toast.makeText(context, "Please enter email & password on your receipt stub", Toast.LENGTH_SHORT).show()
            return
        }
        coroutineScope.launch {
            isTearing = true
            delay(300)
            isTornAndStamped = true
            Toast.makeText(context, "✂ Receipt Stub Torn! Authorizing $actionType...", Toast.LENGTH_SHORT).show()
            delay(600)
            if (actionType == "SIGN UP") {
                viewModel.registerWithEmail(email, password)
            } else {
                viewModel.loginWithEmail(email, password)
            }
            isTearing = false
            isTornAndStamped = false
        }
    }

    Scaffold(
        containerColor = Color(0xFF141619), // Deep dark matte studio canvas
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState()),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = 480.dp)
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header Logo Banner
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(bottom = 16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Landscape,
                        contentDescription = "Logo",
                        tint = Color(0xFFE5C158),
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "SAFARI STAY & EXPEDITIONS",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 2.sp,
                        color = Color.White
                    )
                }

                Text(
                    text = "TEAR-OFF RECEIPT AUTHENTICATION VOUCHER",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFE5C158),
                    letterSpacing = 1.sp,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Fill in your details, then tear off the stub to validate access",
                    fontSize = 12.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 4.dp, bottom = 20.dp)
                )

                // --- THE TEAR-OFF RECEIPT CONTAINER ---
                Card(
                    shape = RoundedCornerShape(4.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFAF7EE)), // Warm parchment thermal receipt paper
                    elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        
                        // === TOP STUB (TEARABLE PART) ===
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .graphicsLayer {
                                    translationY = tearOffsetY
                                    rotationZ = tearRotation
                                }
                                .background(Color(0xFFF2EBDC))
                                .padding(16.dp)
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "★ OFFICIAL ISSUANCE ★",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace,
                                        color = Color(0xFF6B5B45)
                                    )
                                    Text(
                                        text = ticketSerial,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Black,
                                        fontFamily = FontFamily.Monospace,
                                        color = Color.Black
                                    )
                                }
                                
                                Spacer(modifier = Modifier.height(8.dp))
                                
                                Text(
                                    text = if (isSignUp) "TRAVELER ACCOUNT CREATION VOUCHER" else "GUEST ACCESS & RESERVATION PASS",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontFamily = FontFamily.Monospace,
                                    color = Color(0xFF2C2213),
                                    textAlign = TextAlign.Center
                                )
                                
                                Spacer(modifier = Modifier.height(4.dp))
                                
                                Text(
                                    text = "ISSUED: $currentDateStr",
                                    fontSize = 10.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = Color.DarkGray
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                // Visual Barcode Canvas
                                Canvas(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(32.dp)
                                        .background(Color.White)
                                        .padding(4.dp)
                                ) {
                                    val barWidth = size.width
                                    val barHeight = size.height
                                    val randomSeed = ticketSerial.hashCode().toLong()
                                    val random = java.util.Random(randomSeed)
                                    var currentX = 0f
                                    while (currentX < barWidth) {
                                        val w = (random.nextInt(6) + 2).toFloat()
                                        val isBlack = random.nextBoolean()
                                        if (isBlack) {
                                            drawRect(
                                                color = Color.Black,
                                                topLeft = Offset(currentX, 0f),
                                                size = Size(w, barHeight)
                                            )
                                        }
                                        currentX += w + (random.nextInt(3) + 1).toFloat()
                                    }
                                }

                                Text(
                                    text = "*${ticketSerial}*",
                                    fontSize = 9.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = Color.DarkGray,
                                    modifier = Modifier.padding(top = 2.dp)
                                )
                            }
                        }

                        // === PERFORATION / DASHED CUT LINE WITH SCISSORS ===
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(24.dp)
                                .background(Color(0xFFFAF7EE)),
                            contentAlignment = Alignment.Center
                        ) {
                            Canvas(modifier = Modifier.fillMaxWidth()) {
                                drawLine(
                                    color = Color(0xFFB5A78F),
                                    start = Offset(0f, size.height / 2),
                                    end = Offset(size.width, size.height / 2),
                                    strokeWidth = 2f,
                                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 10f), 0f)
                                )
                            }
                            Surface(
                                color = Color(0xFFFAF7EE),
                                modifier = Modifier.padding(horizontal = 8.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ContentCut,
                                        contentDescription = "Tear here",
                                        tint = Color(0xFF8C7A5E),
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "TEAR HERE TO VALIDATE PASS",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace,
                                        color = Color(0xFF8C7A5E)
                                    )
                                }
                            }
                        }

                        // === MAIN RECEIPT FORM BODY ===
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(18.dp)
                        ) {
                            // Stamp Mode Switcher Tabs
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color(0xFFEBE3D3))
                                    .padding(3.dp)
                            ) {
                                Surface(
                                    onClick = { isSignUp = false },
                                    color = if (!isSignUp) Color(0xFF2C2213) else Color.Transparent,
                                    shape = RoundedCornerShape(4.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(vertical = 8.dp)) {
                                        Text(
                                            text = "🎟️ SIGN IN PASS",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            fontFamily = FontFamily.Monospace,
                                            color = if (!isSignUp) Color(0xFFE5C158) else Color(0xFF6B5B45)
                                        )
                                    }
                                }

                                Surface(
                                    onClick = { isSignUp = true },
                                    color = if (isSignUp) Color(0xFF2C2213) else Color.Transparent,
                                    shape = RoundedCornerShape(4.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(vertical = 8.dp)) {
                                        Text(
                                            text = "📝 SIGN UP VOUCHER",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            fontFamily = FontFamily.Monospace,
                                            color = if (isSignUp) Color(0xFFE5C158) else Color(0xFF6B5B45)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(18.dp))

                            // FORM FIELDS STYLED LIKE RECEIPT ENTRIES
                            AnimatedVisibility(visible = isSignUp) {
                                Column {
                                    Text(
                                        text = "FULL NAME / TRAVELER NAME",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace,
                                        color = Color(0xFF5E4E38)
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    OutlinedTextField(
                                        value = fullName,
                                        onValueChange = { fullName = it },
                                        placeholder = { Text("e.g. Alex Mercer", fontFamily = FontFamily.Monospace, fontSize = 12.sp, color = Color.Gray) },
                                        singleLine = true,
                                        modifier = Modifier.fillMaxWidth(),
                                        textStyle = LocalTextStyle.current.copy(fontFamily = FontFamily.Monospace, fontSize = 13.sp, color = Color.Black),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = Color(0xFF2C2213),
                                            unfocusedBorderColor = Color(0xFFC7BBA5),
                                            focusedContainerColor = Color.White,
                                            unfocusedContainerColor = Color.White
                                        ),
                                        shape = RoundedCornerShape(4.dp)
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))

                                    Text(
                                        text = "PASSPORT / NATIONAL ID NO.",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace,
                                        color = Color(0xFF5E4E38)
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    OutlinedTextField(
                                        value = passportId,
                                        onValueChange = { passportId = it },
                                        singleLine = true,
                                        modifier = Modifier.fillMaxWidth(),
                                        textStyle = LocalTextStyle.current.copy(fontFamily = FontFamily.Monospace, fontSize = 13.sp, color = Color.Black),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = Color(0xFF2C2213),
                                            unfocusedBorderColor = Color(0xFFC7BBA5),
                                            focusedContainerColor = Color.White,
                                            unfocusedContainerColor = Color.White
                                        ),
                                        shape = RoundedCornerShape(4.dp)
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))

                                    Text(
                                        text = "PREFERRED EXPEDITION TIER",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace,
                                        color = Color(0xFF5E4E38)
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        listOf("Luxury Lodge", "Eco Camp", "Overland").forEach { tier ->
                                            FilterChip(
                                                selected = selectedTier == tier,
                                                onClick = { selectedTier = tier },
                                                label = { Text(tier, fontSize = 10.sp, fontFamily = FontFamily.Monospace) },
                                                colors = FilterChipDefaults.filterChipColors(
                                                    selectedContainerColor = Color(0xFF2C2213),
                                                    selectedLabelColor = Color(0xFFE5C158),
                                                    containerColor = Color(0xFFEBE3D3),
                                                    labelColor = Color(0xFF4A3E2C)
                                                )
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(12.dp))
                                }
                            }

                            // EMAIL FIELD
                            Text(
                                text = "EMAIL ADDRESS (USER ID)",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                color = Color(0xFF5E4E38)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            OutlinedTextField(
                                value = email,
                                onValueChange = { email = it },
                                placeholder = { Text("you@safaristay.com", fontFamily = FontFamily.Monospace, fontSize = 12.sp, color = Color.Gray) },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                                modifier = Modifier.fillMaxWidth(),
                                textStyle = LocalTextStyle.current.copy(fontFamily = FontFamily.Monospace, fontSize = 13.sp, color = Color.Black),
                                leadingIcon = {
                                    Icon(Icons.Default.Email, contentDescription = null, tint = Color(0xFF8C7A5E), modifier = Modifier.size(18.dp))
                                },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFF2C2213),
                                    unfocusedBorderColor = Color(0xFFC7BBA5),
                                    focusedContainerColor = Color.White,
                                    unfocusedContainerColor = Color.White
                                ),
                                shape = RoundedCornerShape(4.dp)
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            // PASSWORD FIELD
                            Text(
                                text = "SECRET PASSCODE / PIN",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                color = Color(0xFF5E4E38)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            OutlinedTextField(
                                value = password,
                                onValueChange = { password = it },
                                singleLine = true,
                                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                modifier = Modifier.fillMaxWidth(),
                                textStyle = LocalTextStyle.current.copy(fontFamily = FontFamily.Monospace, fontSize = 13.sp, color = Color.Black),
                                leadingIcon = {
                                    Icon(Icons.Default.Lock, contentDescription = null, tint = Color(0xFF8C7A5E), modifier = Modifier.size(18.dp))
                                },
                                trailingIcon = {
                                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                        Icon(
                                            imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                            contentDescription = "Toggle password visibility",
                                            tint = Color(0xFF8C7A5E),
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFF2C2213),
                                    unfocusedBorderColor = Color(0xFFC7BBA5),
                                    focusedContainerColor = Color.White,
                                    unfocusedContainerColor = Color.White
                                ),
                                shape = RoundedCornerShape(4.dp)
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Checkbox(
                                    checked = rememberMe,
                                    onCheckedChange = { rememberMe = it },
                                    colors = CheckboxDefaults.colors(checkedColor = Color(0xFF2C2213), checkmarkColor = Color(0xFFE5C158))
                                )
                                Text(
                                    text = "Store authorization token on this device",
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = Color(0xFF5E4E38)
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // RECEIPT CALCULATION SUMMARY
                            Surface(
                                color = Color(0xFFF2EBDC),
                                border = BorderStroke(1.dp, Color(0xFFD6C8AF)),
                                shape = RoundedCornerShape(4.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                                        Text("SERVICE FEE:", fontSize = 10.sp, fontFamily = FontFamily.Monospace, color = Color.Gray)
                                        Text("$0.00 (COMPLIMENTARY)", fontSize = 10.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, color = Color(0xFF2C7D32))
                                    }
                                    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                                        Text("PARK ESCROW PROTECTION:", fontSize = 10.sp, fontFamily = FontFamily.Monospace, color = Color.Gray)
                                        Text("ACTIVE ✓", fontSize = 10.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, color = Color(0xFF2C7D32))
                                    }
                                    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                                        Text("ACCESS PERMIT STATUS:", fontSize = 10.sp, fontFamily = FontFamily.Monospace, color = Color.Gray)
                                        Text(if (isSignUp) "READY TO ISSUE" else "READY TO STAMP", fontSize = 10.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, color = Color(0xFF2C2213))
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(20.dp))

                            // === THE MAIN TEAR-OFF ACTION BUTTON ===
                            Box(contentAlignment = Alignment.Center) {
                                Button(
                                    onClick = {
                                        executeTearAndAuth(if (isSignUp) "SIGN UP" else "SIGN IN")
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(54.dp)
                                        .shadow(4.dp, RoundedCornerShape(6.dp)),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2C2213)),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.ContentCut,
                                            contentDescription = null,
                                            tint = Color(0xFFE5C158),
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Text(
                                            text = if (isSignUp) "TEAR OFF RECEIPT & SIGN UP" else "TEAR OFF RECEIPT & SIGN IN",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Black,
                                            fontFamily = FontFamily.Monospace,
                                            color = Color(0xFFE5C158),
                                            letterSpacing = 0.5.sp
                                        )
                                    }
                                }

                                // Animated Approved Stamp
                                if (isTornAndStamped) {
                                    Surface(
                                        color = Color(0xFF1B5E20).copy(alpha = 0.9f),
                                        shape = RoundedCornerShape(8.dp),
                                        border = BorderStroke(2.dp, Color(0xFFA5D6A7)),
                                        modifier = Modifier
                                            .graphicsLayer {
                                                scaleX = stampScale
                                                scaleY = stampScale
                                                rotationZ = -12f
                                            }
                                            .padding(8.dp)
                                    ) {
                                        Text(
                                            text = "★ STAMPED & VERIFIED ★",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Black,
                                            fontFamily = FontFamily.Monospace,
                                            color = Color.White,
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // QUICK 1-TAP DEMO LOGIN BUTTON
                            OutlinedButton(
                                onClick = {
                                    email = "alex.mercer@safari.com"
                                    password = "safari2026pass"
                                    executeTearAndAuth("DEMO GUEST")
                                },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(6.dp),
                                border = BorderStroke(1.dp, Color(0xFF8C7A5E))
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.FlashOn, contentDescription = null, tint = Color(0xFF8C7A5E), modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "⚡ INSTANT DEMO TEAR-OFF (1-TAP ENTER)",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace,
                                        color = Color(0xFF4A3E2C)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(20.dp))

                            // OR DIVIDER
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Canvas(modifier = Modifier.weight(1f).height(1.dp)) {
                                    drawLine(color = Color(0xFFC7BBA5), start = Offset(0f, 0f), end = Offset(size.width, 0f))
                                }
                                Text(
                                    text = "  OR STAMP VIA PROVIDER  ",
                                    fontSize = 10.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = Color.Gray
                                )
                                Canvas(modifier = Modifier.weight(1f).height(1.dp)) {
                                    drawLine(color = Color(0xFFC7BBA5), start = Offset(0f, 0f), end = Offset(size.width, 0f))
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // SOCIAL AUTH STUBS
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Surface(
                                    onClick = { viewModel.loginWithProvider("Google") },
                                    color = Color.White,
                                    border = BorderStroke(1.dp, Color(0xFFC7BBA5)),
                                    shape = RoundedCornerShape(4.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(10.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        Text(text = "🌐 Google Pass", fontSize = 11.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, color = Color.Black)
                                    }
                                }

                                Surface(
                                    onClick = { viewModel.loginWithProvider("Apple") },
                                    color = Color.Black,
                                    shape = RoundedCornerShape(4.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(10.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        Text(text = " Apple Key", fontSize = 11.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, color = Color.White)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(20.dp))

                            // BOTTOM SAWTOOTH CUT / TICKET FOOTER
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "─────────────────────────────────",
                                    fontSize = 10.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = Color(0xFFC7BBA5)
                                )
                                Text(
                                    text = "KEEP THIS RECEIPT STUB FOR PARK ENTRANCE",
                                    fontSize = 9.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = Color.Gray
                                )
                                Text(
                                    text = "Protected under Tanzania & Kenya Tourism Regulations 2026",
                                    fontSize = 8.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = Color.LightGray
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TearOffReceiptAuthDialog(
    viewModel: SafariViewModel,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color(0xFF141619)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp)
                        .size(40.dp)
                        .background(Color.White.copy(alpha = 0.15f), CircleShape)
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Close Pass", tint = Color.White)
                }

                AuthScreen(viewModel = viewModel)
            }
        }
    }
}

