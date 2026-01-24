package com.iberosolutions.shifterup

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.iberosolutions.shifterup.core.Resource
import com.iberosolutions.shifterup.ui.add.AddRoutineScreen
import com.iberosolutions.shifterup.ui.screens.MainScreen
import com.iberosolutions.shifterup.ui.theme.BlueActive
import com.iberosolutions.shifterup.ui.theme.BlueInactive
import com.iberosolutions.shifterup.ui.theme.DefaultBackground
import com.iberosolutions.shifterup.ui.theme.PoppinsFamily
import com.iberosolutions.shifterup.ui.theme.ShifterupTheme
import com.iberosolutions.shifterup.viewmodel.ForgotPasswordViewModel
import com.iberosolutions.shifterup.viewmodel.LoginViewModel
import com.iberosolutions.shifterup.viewmodel.RegisterViewModel
import com.iberosolutions.shifterup.viewmodel.VerificationViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ShifterupTheme {
                // 1. Un único controlador raíz para TODA la app
                val rootNavController = rememberNavController()

                // Inyección de ViewModels (Scoped al NavHost o a la pantalla)
                val loginViewModel: LoginViewModel = hiltViewModel()
                val registerViewModel: RegisterViewModel = hiltViewModel()
                val verificationViewModel: VerificationViewModel = hiltViewModel()

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = DefaultBackground
                ) {
                    // 2. Un único NavHost que contiene TODO (Auth, Main y Wizard)
                    NavHost(
                        navController = rootNavController,
                        startDestination = "email_login", // Tu punto de entrada real
                        enterTransition = { fadeIn(animationSpec = tween(700)) },
                        exitTransition = { fadeOut(animationSpec = tween(700)) }
                    ) {
                        // --- ZONA DE LOGIN / REGISTRO ---
                        composable("email_login") { EmailLogin(rootNavController, loginViewModel) }
                        composable("pass_login") { PassLogin(rootNavController, loginViewModel) }

                        composable("name_register") { NameRegister(rootNavController, registerViewModel) }
                        composable("email_register") { EmailRegister(rootNavController, registerViewModel) }
                        composable("pass_register") { PassRegister(rootNavController, registerViewModel) }
                        composable("repeat_pass_register") { RepeatPassRegister(rootNavController, registerViewModel) }
                        composable("verification_register") { VerificationRegister(rootNavController, verificationViewModel) }

                        composable("forgot_password") { ForgotPasswordScreen(rootNavController) }

                        // --- ZONA PRINCIPAL (CON BARRA DE NAVEGACIÓN) ---
                        composable("main_screen") {
                            // Pasamos el rootNavController para que desde aquí se pueda llamar al Wizard
                            MainScreen(rootNavController = rootNavController)
                        }

                        // --- ZONA PANTALLA COMPLETA (WIZARD) ---
                        // Al estar aquí, tapa a la MainScreen y oculta la barra
                        composable("create_routine_wizard") {
                            AddRoutineScreen(
                                onClose = {
                                    rootNavController.popBackStack()
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

// Gestor de navegación de la aplicación
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    // Inyectamos los ViewModels aquí o en cada pantalla según prefieras,
    // pero si los pasas como parámetro asegúrate de que sean singleton o scoped correctamente.
    // Lo más limpio es inyectarlos dentro de cada composable si son específicos,
    // pero tal como lo tienes estructura está bien.
    val loginViewModel: LoginViewModel = hiltViewModel()
    val registerViewModel: RegisterViewModel = hiltViewModel()
    val verificationViewModel: VerificationViewModel = hiltViewModel()

    NavHost(
        navController = navController,
        startDestination = "email_login",
        enterTransition = { fadeIn(animationSpec = tween(700)) },
        exitTransition = { fadeOut(animationSpec = tween(700)) }
    ) {
        // --- FLUJO DE INICIO DE SESIÓN ---
        composable("email_login") {
            EmailLogin(navController, loginViewModel)
        }
        composable("pass_login") {
            PassLogin(navController, loginViewModel)
        }

        // --- FLUJO DE REGISTRO ---
        composable("name_register") {
            NameRegister(navController, registerViewModel)
        }
        composable("email_register") {
            EmailRegister(navController, registerViewModel)
        }
        composable("pass_register") {
            PassRegister(navController, registerViewModel)
        }
        composable("repeat_pass_register") {
            RepeatPassRegister(navController, registerViewModel)
        }
        composable("verification_register") {
            VerificationRegister(navController, verificationViewModel)
        }

        // --- RECUPERAR CONTRASEÑA ---
        composable("forgot_password") {
            ForgotPasswordScreen(navController)
        }

        // --- PANTALLA PRINCIPAL ---
        composable("main_screen") {
            // Pasamos el navController principal (el que conoce "email_login")
            MainScreen(rootNavController = navController)
        }
    }
}

// ----------------------------------------------------------------
// PANTALLAS DE LOGIN
// ----------------------------------------------------------------

@Composable
fun EmailLogin(navController: NavHostController, viewModel: LoginViewModel) {
    val emailText = viewModel.email
    val isButtonEnabled = viewModel.isEmailNextEnabled
    val isError = viewModel.showEmailError

    // 1. ESTADO DE CARGA INICIAL (Auto-Login)
    var isCheckingSession by remember { mutableStateOf(true) }

    // 2. EFECTO DE AUTO-LOGIN
    LaunchedEffect(key1 = true) {
        if (viewModel.isUserLoggedIn()) {
            navController.navigate("main_screen") {
                popUpTo(navController.graph.id) { inclusive = true }
            }
        } else {
            isCheckingSession = false
        }
    }

    // 3. UI
    if (isCheckingSession) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = BlueActive)
        }
    } else {
        ContentEmailLogin(navController, viewModel, emailText, isButtonEnabled, isError)
    }
}

@Composable
fun ContentEmailLogin(
    navController: NavHostController,
    viewModel: LoginViewModel,
    emailText: String,
    isButtonEnabled: Boolean,
    isError: Boolean
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top = 180.dp, start = 20.dp, end = 20.dp)
        ) {
            Text(
                text = stringResource(id = R.string.app_str_textview_mail),
                color = Color.White,
                fontFamily = PoppinsFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp,
                lineHeight = 40.sp,
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = stringResource(id = R.string.app_str_textview_mail_description),
                color = Color.White,
                fontFamily = PoppinsFamily,
                fontWeight = FontWeight.Light,
                fontSize = 15.sp,
                lineHeight = 25.sp,
            )
            Spacer(modifier = Modifier.height(22.dp))

            OutlinedTextField(
                value = emailText,
                onValueChange = { viewModel.onEmailChanged(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = 70.dp),
                label = { Text(text = stringResource(R.string.app_str_edittext_mail), fontFamily = PoppinsFamily) },
                shape = RoundedCornerShape(16.dp),
                isError = isError,
                supportingText = {
                    if (isError) {
                        Text(
                            text = "Formato de correo inválido",
                            color = Color.Red,
                            fontFamily = PoppinsFamily,
                            fontSize = 12.sp
                        )
                    }
                },
                trailingIcon = {
                    if (emailText.isNotEmpty()) {
                        IconButton(onClick = { viewModel.onEmailChanged("") }) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Borrar texto")
                        }
                    }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.White.copy(alpha = 0.4f),
                    unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                    focusedLabelColor = Color.White,
                    cursorColor = Color.Black,
                    errorBorderColor = Color.Red,
                    errorLabelColor = Color.Red,
                    errorCursorColor = Color.Red,
                    errorTrailingIconColor = Color.Red,
                    errorSupportingTextColor = Color.Red
                ),
                textStyle = androidx.compose.ui.text.TextStyle(
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp,
                    color = Color.White,
                ),
                singleLine = true
            )

            val animatedColor by animateColorAsState(
                targetValue = if (isButtonEnabled) BlueActive else BlueInactive,
                animationSpec = tween(durationMillis = 400),
                label = "CambioColorBoton"
            )

            Spacer(modifier = Modifier.height(30.dp))

            Button(
                onClick = { navController.navigate("pass_login") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(53.dp),
                enabled = isButtonEnabled,
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(
                    containerColor = animatedColor,
                    disabledContainerColor = animatedColor,
                    contentColor = Color.White,
                    disabledContentColor = Color.White
                )
            ) {
                Text(
                    text = stringResource(R.string.app_str_btn_next),
                    fontFamily = PoppinsFamily,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Normal
                )
            }
        }

        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 80.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(id = R.string.app_str_edittext_havent_account),
                color = Color.White,
                fontFamily = PoppinsFamily,
                fontWeight = FontWeight.Light,
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.width(10.dp))
            Button(
                onClick = { navController.navigate("name_register") },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    contentColor = BlueActive
                ),
                contentPadding = PaddingValues(0.dp)
            ) {
                Text(
                    text = stringResource(R.string.app_str_btn_register),
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.Normal,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
fun PassLogin(navController: NavHostController, viewModel: LoginViewModel) {
    val passText = viewModel.password
    val passwordVisible = viewModel.isPasswordVisible
    val isButtonEnabled = viewModel.isLoginEnabled
    val loginFlow = viewModel.loginFlow
    val context = LocalContext.current
    val isError = loginFlow is Resource.Error
    val errorMessage = if (loginFlow is Resource.Error) loginFlow.exception.message else null

    // --- CORRECCIÓN APLICADA AQUÍ ---
    LaunchedEffect(loginFlow) {
        if (loginFlow is Resource.Success) {
            Toast.makeText(context, "¡Bienvenido!", Toast.LENGTH_SHORT).show()
            navController.navigate("main_screen") {
                // Limpiamos todo el grafo de navegación para que no pueda volver atrás
                popUpTo(navController.graph.id) { inclusive = true }
            }
        } else if (loginFlow is Resource.Error) {
            Toast.makeText(context, loginFlow.exception.message, Toast.LENGTH_LONG).show()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        IconButton(
            onClick = { navController.popBackStack() },
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top = 60.dp, start = 20.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_back),
                contentDescription = "Volver atrás",
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
        }

        Column(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top = 180.dp, start = 20.dp, end = 20.dp)
        ) {
            Text(
                text = stringResource(id = R.string.app_str_textview_password),
                color = Color.White,
                fontFamily = PoppinsFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp,
                lineHeight = 40.sp,
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = stringResource(id = R.string.app_str_textview_password_description),
                color = Color.White,
                fontFamily = PoppinsFamily,
                fontWeight = FontWeight.Light,
                fontSize = 15.sp,
                lineHeight = 25.sp,
            )
            Spacer(modifier = Modifier.height(22.dp))

            OutlinedTextField(
                value = passText,
                onValueChange = { viewModel.onPasswordChanged(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = 70.dp),
                label = { Text(text = stringResource(R.string.app_str_edittext_password), fontFamily = PoppinsFamily) },
                shape = RoundedCornerShape(16.dp),
                isError = isError,
                supportingText = {
                    if (isError && errorMessage != null) {
                        Text(
                            text = errorMessage,
                            color = MaterialTheme.colorScheme.error,
                            fontFamily = PoppinsFamily,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                },
                trailingIcon = {
                    val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                    IconButton(onClick = { viewModel.togglePasswordVisibility() }) {
                        Icon(imageVector = image, contentDescription = null, tint = Color.White)
                    }
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.White.copy(alpha = 0.4f),
                    unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                    focusedLabelColor = Color.White,
                    cursorColor = Color.Black,
                    errorBorderColor = Color.Red,
                    errorLabelColor = Color.Red,
                    errorCursorColor = Color.Red,
                    errorTrailingIconColor = Color.Red,
                    errorSupportingTextColor = Color.Red
                ),
                textStyle = androidx.compose.ui.text.TextStyle(
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp,
                    color = Color.White,
                ),
                singleLine = true
            )

            val animatedColor by animateColorAsState(
                targetValue = if (isButtonEnabled) BlueActive else BlueInactive,
                animationSpec = tween(durationMillis = 400),
                label = "CambioColorBoton"
            )

            Spacer(modifier = Modifier.height(1.dp))
            Text(
                text = "¿Olvidaste tu contraseña?",
                color = BlueActive,
                fontFamily = PoppinsFamily,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier
                    .align(Alignment.End)
                    .clickable { navController.navigate("forgot_password") }
            )

            Spacer(modifier = Modifier.height(30.dp))

            if (loginFlow is Resource.Loading) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.align(Alignment.CenterHorizontally))
            } else {
                Button(
                    onClick = { viewModel.login() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(53.dp),
                    enabled = isButtonEnabled,
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = animatedColor,
                        disabledContainerColor = animatedColor,
                        contentColor = Color.White,
                        disabledContentColor = Color.White
                    )
                ) {
                    Text(
                        text = stringResource(R.string.app_str_btn_login),
                        fontFamily = PoppinsFamily,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Normal
                    )
                }
            }
        }
    }
}

// ----------------------------------------------------------------
// RECUPERAR CONTRASEÑA
// ----------------------------------------------------------------

@Composable
fun ForgotPasswordScreen(
    navController: NavHostController,
    viewModel: ForgotPasswordViewModel = hiltViewModel()
) {
    val emailText = viewModel.email
    val isButtonEnabled = viewModel.isButtonEnabled
    val isError = viewModel.showEmailError
    val resetFlow = viewModel.resetFlow
    val context = LocalContext.current

    LaunchedEffect(resetFlow) {
        when (resetFlow) {
            is Resource.Error -> {
                Toast.makeText(context, resetFlow.exception.message, Toast.LENGTH_LONG).show()
            }
            is Resource.Success -> {
                Toast.makeText(context, "¡Correo enviado! Revisa tu bandeja de entrada.", Toast.LENGTH_LONG).show()
                navController.popBackStack()
            }
            else -> {}
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        IconButton(
            onClick = { navController.popBackStack() },
            modifier = Modifier.align(Alignment.TopStart).padding(top = 60.dp, start = 20.dp)
        ) {
            Icon(painter = painterResource(id = R.drawable.ic_back), contentDescription = "Volver", tint = Color.White, modifier = Modifier.size(20.dp))
        }

        Column(
            modifier = Modifier.align(Alignment.TopStart).padding(top = 180.dp, start = 20.dp, end = 20.dp)
        ) {
            Text(
                text = "Recuperar contraseña",
                color = Color.White,
                fontFamily = PoppinsFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp,
                lineHeight = 40.sp,
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Introduce tu correo electrónico y te enviaremos un enlace para restablecer tu contraseña.",
                color = Color.White,
                fontFamily = PoppinsFamily,
                fontWeight = FontWeight.Light,
                fontSize = 15.sp,
                lineHeight = 25.sp,
            )
            Spacer(modifier = Modifier.height(22.dp))

            OutlinedTextField(
                value = emailText,
                onValueChange = { viewModel.onEmailChanged(it) },
                modifier = Modifier.fillMaxWidth().defaultMinSize(minHeight = 70.dp),
                label = { Text("Correo electrónico", fontFamily = PoppinsFamily) },
                shape = RoundedCornerShape(16.dp),
                isError = isError,
                supportingText = {
                    if (isError) {
                        Text("Formato de correo inválido", color = Color.Red, fontSize = 12.sp, fontFamily = PoppinsFamily)
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.White.copy(alpha = 0.4f),
                    unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                    focusedLabelColor = Color.White,
                    cursorColor = Color.White,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    errorBorderColor = Color.Red,
                    errorLabelColor = Color.Red
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                textStyle = androidx.compose.ui.text.TextStyle(fontFamily = PoppinsFamily, fontWeight = FontWeight.Medium, fontSize = 16.sp, color = Color.White),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(30.dp))

            if (resetFlow is Resource.Loading) {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color.White)
                }
            } else {
                Button(
                    onClick = { viewModel.sendRecoveryEmail() },
                    modifier = Modifier.fillMaxWidth().height(53.dp),
                    enabled = isButtonEnabled,
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BlueActive,
                        disabledContainerColor = BlueInactive,
                        contentColor = Color.White,
                        disabledContentColor = Color.White
                    )
                ) {
                    Text("Enviar correo", fontFamily = PoppinsFamily, fontSize = 18.sp, fontWeight = FontWeight.Normal)
                }
            }
        }
    }
}

// ----------------------------------------------------------------
// PANTALLAS DE REGISTRO
// ----------------------------------------------------------------

@Composable
fun NameRegister(navController: NavHostController, viewModel: RegisterViewModel) {
    val nameText = viewModel.name
    val isButtonEnabled = viewModel.isNameValid

    Box(modifier = Modifier.fillMaxSize()) {
        IconButton(
            onClick = { navController.popBackStack() },
            modifier = Modifier.align(Alignment.TopStart).padding(top = 60.dp, start = 20.dp)
        ) {
            Icon(painter = painterResource(id = R.drawable.ic_back), contentDescription = "Volver atrás", tint = Color.White, modifier = Modifier.size(20.dp))
        }

        Column(modifier = Modifier.align(Alignment.TopStart).padding(top = 180.dp, start = 20.dp, end = 20.dp)) {
            Text(
                text = stringResource(id = R.string.app_str_textview_name),
                color = Color.White,
                fontFamily = PoppinsFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp,
                lineHeight = 40.sp,
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = stringResource(id = R.string.app_str_textview_name_description),
                color = Color.White,
                fontFamily = PoppinsFamily,
                fontWeight = FontWeight.Light,
                fontSize = 15.sp,
                lineHeight = 25.sp,
            )
            Spacer(modifier = Modifier.height(22.dp))

            OutlinedTextField(
                value = nameText,
                onValueChange = { viewModel.onNameChanged(it) },
                modifier = Modifier.fillMaxWidth().height(70.dp),
                label = { Text(text = stringResource(R.string.app_str_edittext_name), fontFamily = PoppinsFamily) },
                shape = RoundedCornerShape(16.dp),
                trailingIcon = {
                    if (nameText.isNotEmpty()) {
                        IconButton(onClick = { viewModel.onNameChanged("") }) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Borrar texto")
                        }
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.White.copy(alpha = 0.4f),
                    unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                    focusedLabelColor = Color.White,
                    cursorColor = Color.Black
                ),
                textStyle = androidx.compose.ui.text.TextStyle(
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp,
                    color = Color.White,
                ),
                singleLine = true
            )

            val animatedColor by animateColorAsState(
                targetValue = if (isButtonEnabled) BlueActive else BlueInactive,
                animationSpec = tween(durationMillis = 400),
                label = "CambioColorBoton"
            )

            Spacer(modifier = Modifier.height(30.dp))
            Button(
                onClick = { navController.navigate("email_register") },
                modifier = Modifier.fillMaxWidth().height(53.dp),
                enabled = isButtonEnabled,
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(
                    containerColor = animatedColor,
                    disabledContainerColor = animatedColor,
                    contentColor = Color.White,
                    disabledContentColor = Color.White
                )
            ) {
                Text(text = stringResource(R.string.app_str_btn_next), fontFamily = PoppinsFamily, fontSize = 18.sp, fontWeight = FontWeight.Normal)
            }
        }
    }
}

@Composable
fun EmailRegister(navController: NavHostController, viewModel: RegisterViewModel) {
    val emailText = viewModel.email
    val isButtonEnabled = viewModel.isEmailValid
    val isError = viewModel.showEmailError

    Box(modifier = Modifier.fillMaxSize()) {
        IconButton(
            onClick = { navController.popBackStack() },
            modifier = Modifier.align(Alignment.TopStart).padding(top = 60.dp, start = 20.dp)
        ) {
            Icon(painter = painterResource(id = R.drawable.ic_back), contentDescription = "Volver atrás", tint = Color.White, modifier = Modifier.size(20.dp))
        }

        Column(modifier = Modifier.align(Alignment.TopStart).padding(top = 180.dp, start = 20.dp, end = 20.dp)) {
            Text(
                text = stringResource(id = R.string.app_str_textview_mail),
                color = Color.White,
                fontFamily = PoppinsFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp,
                lineHeight = 40.sp,
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = stringResource(id = R.string.app_str_textview_mail_description_register),
                color = Color.White,
                fontFamily = PoppinsFamily,
                fontWeight = FontWeight.Light,
                fontSize = 15.sp,
                lineHeight = 25.sp,
            )
            Spacer(modifier = Modifier.height(22.dp))

            OutlinedTextField(
                value = emailText,
                onValueChange = { viewModel.onEmailChanged(it) },
                modifier = Modifier.fillMaxWidth().defaultMinSize(minHeight = 70.dp),
                label = { Text(text = stringResource(R.string.app_str_edittext_mail), fontFamily = PoppinsFamily) },
                shape = RoundedCornerShape(16.dp),
                isError = isError,
                supportingText = {
                    if (isError) {
                        Text(text = "Formato de correo inválido", color = Color.Red, fontFamily = PoppinsFamily, fontSize = 12.sp)
                    }
                },
                trailingIcon = {
                    if (emailText.isNotEmpty()) {
                        IconButton(onClick = { viewModel.onEmailChanged("") }) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Borrar texto")
                        }
                    }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.White.copy(alpha = 0.4f),
                    unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                    focusedLabelColor = Color.White,
                    cursorColor = Color.Black,
                    errorBorderColor = Color.Red,
                    errorLabelColor = Color.Red,
                    errorCursorColor = Color.Red,
                    errorTrailingIconColor = Color.Red,
                    errorSupportingTextColor = Color.Red
                ),
                textStyle = androidx.compose.ui.text.TextStyle(
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp,
                    color = Color.White,
                ),
                singleLine = true
            )

            val animatedColor by animateColorAsState(
                targetValue = if (isButtonEnabled) BlueActive else BlueInactive,
                animationSpec = tween(durationMillis = 400),
                label = "CambioColorBoton"
            )

            Spacer(modifier = Modifier.height(30.dp))
            Button(
                onClick = { navController.navigate("pass_register") },
                modifier = Modifier.fillMaxWidth().height(53.dp),
                enabled = isButtonEnabled,
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(
                    containerColor = animatedColor,
                    disabledContainerColor = animatedColor,
                    contentColor = Color.White,
                    disabledContentColor = Color.White
                )
            ) {
                Text(text = stringResource(R.string.app_str_btn_next), fontFamily = PoppinsFamily, fontSize = 18.sp, fontWeight = FontWeight.Normal)
            }
        }
    }
}

@Composable
fun PassRegister(navController: NavHostController, viewModel: RegisterViewModel) {
    val passText = viewModel.password
    val passwordVisible = viewModel.isPasswordVisible
    val isButtonEnabled = viewModel.isPasswordValid
    val isError = viewModel.showPasswordError

    Box(modifier = Modifier.fillMaxSize()) {
        IconButton(
            onClick = { navController.popBackStack() },
            modifier = Modifier.align(Alignment.TopStart).padding(top = 60.dp, start = 20.dp)
        ) {
            Icon(painter = painterResource(id = R.drawable.ic_back), contentDescription = "Volver atrás", tint = Color.White, modifier = Modifier.size(20.dp))
        }

        Column(modifier = Modifier.align(Alignment.TopStart).padding(top = 180.dp, start = 20.dp, end = 20.dp)) {
            Text(
                text = stringResource(id = R.string.app_str_textview_password_register),
                color = Color.White,
                fontFamily = PoppinsFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp,
                lineHeight = 40.sp,
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = stringResource(id = R.string.app_str_textview_password_description_register),
                color = Color.White,
                fontFamily = PoppinsFamily,
                fontWeight = FontWeight.Light,
                fontSize = 15.sp,
                lineHeight = 25.sp,
            )
            Spacer(modifier = Modifier.height(22.dp))

            OutlinedTextField(
                value = passText,
                onValueChange = { viewModel.onPasswordChanged(it) },
                modifier = Modifier.fillMaxWidth().defaultMinSize(minHeight = 70.dp),
                label = { Text(text = stringResource(R.string.app_str_edittext_password), fontFamily = PoppinsFamily) },
                shape = RoundedCornerShape(16.dp),
                isError = isError,
                supportingText = {
                    if (isError) {
                        Text(text = "La contraseña es muy débil (mín. 6 caracteres)", color = Color.Red, fontFamily = PoppinsFamily, fontSize = 12.sp)
                    }
                },
                trailingIcon = {
                    val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                    IconButton(onClick = { viewModel.togglePasswordVisibility() }) {
                        Icon(imageVector = image, contentDescription = null, tint = Color.White)
                    }
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.White.copy(alpha = 0.4f),
                    unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                    focusedLabelColor = Color.White,
                    cursorColor = Color.Black,
                    errorBorderColor = Color.Red,
                    errorLabelColor = Color.Red,
                    errorCursorColor = Color.Red,
                    errorTrailingIconColor = Color.Red,
                    errorSupportingTextColor = Color.Red
                ),
                textStyle = androidx.compose.ui.text.TextStyle(
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp,
                    color = Color.White,
                ),
                singleLine = true
            )

            val animatedColor by animateColorAsState(
                targetValue = if (isButtonEnabled) BlueActive else BlueInactive,
                animationSpec = tween(durationMillis = 400),
                label = "CambioColorBoton"
            )

            Spacer(modifier = Modifier.height(30.dp))
            Button(
                onClick = { navController.navigate("repeat_pass_register") },
                modifier = Modifier.fillMaxWidth().height(53.dp),
                enabled = isButtonEnabled,
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(
                    containerColor = animatedColor,
                    disabledContainerColor = animatedColor,
                    contentColor = Color.White,
                    disabledContentColor = Color.White
                )
            ) {
                Text(text = stringResource(R.string.app_str_btn_next), fontFamily = PoppinsFamily, fontSize = 18.sp, fontWeight = FontWeight.Normal)
            }
        }
    }
}

@Composable
fun RepeatPassRegister(navController: NavHostController, viewModel: RegisterViewModel) {
    val passText = viewModel.repeatPassword
    val passwordVisible = viewModel.isRepeatPasswordVisible
    val isButtonEnabled = viewModel.isRegisterEnabled
    val signupFlow = viewModel.signupFlow
    val context = LocalContext.current
    val isError = viewModel.showRepeatPasswordError

    LaunchedEffect(signupFlow) {
        when (signupFlow) {
            is Resource.Error -> {
                Toast.makeText(context, "Error: ${signupFlow.exception.message}", Toast.LENGTH_LONG).show()
            }
            is Resource.Success -> {
                navController.navigate("verification_register") {
                    popUpTo("name_register") { inclusive = true }
                }
            }
            else -> {}
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        IconButton(
            onClick = { navController.popBackStack() },
            modifier = Modifier.align(Alignment.TopStart).padding(top = 60.dp, start = 20.dp)
        ) {
            Icon(painter = painterResource(id = R.drawable.ic_back), contentDescription = "Volver atrás", tint = Color.White, modifier = Modifier.size(20.dp))
        }

        Column(modifier = Modifier.align(Alignment.TopStart).padding(top = 180.dp, start = 20.dp, end = 20.dp)) {
            Text(
                text = stringResource(id = R.string.app_str_textview_repit_password_register),
                color = Color.White,
                fontFamily = PoppinsFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp,
                lineHeight = 40.sp,
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = stringResource(id = R.string.app_str_textview_repit_password_description_register),
                color = Color.White,
                fontFamily = PoppinsFamily,
                fontWeight = FontWeight.Light,
                fontSize = 15.sp,
                lineHeight = 25.sp,
            )
            Spacer(modifier = Modifier.height(22.dp))

            OutlinedTextField(
                value = passText,
                onValueChange = { viewModel.onRepeatPasswordChanged(it) },
                modifier = Modifier.fillMaxWidth().defaultMinSize(minHeight = 70.dp),
                label = { Text(text = stringResource(R.string.app_str_edittext_password), fontFamily = PoppinsFamily) },
                shape = RoundedCornerShape(16.dp),
                isError = isError,
                supportingText = {
                    if (isError) {
                        Text(
                            text = "Las contraseñas no coinciden",
                            color = Color.Red,
                            fontFamily = PoppinsFamily,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                },
                trailingIcon = {
                    val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                    IconButton(onClick = { viewModel.toggleRepeatPasswordVisibility() }) {
                        Icon(imageVector = image, contentDescription = null, tint = Color.White)
                    }
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.White.copy(alpha = 0.4f),
                    unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                    focusedLabelColor = Color.White,
                    cursorColor = Color.Black,
                    errorBorderColor = Color.Red,
                    errorLabelColor = Color.Red,
                    errorCursorColor = Color.Red,
                    errorTrailingIconColor = Color.Red,
                    errorSupportingTextColor = Color.Red
                ),
                textStyle = androidx.compose.ui.text.TextStyle(
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp,
                    color = Color.White,
                ),
                singleLine = true
            )

            val animatedColor by animateColorAsState(
                targetValue = if (isButtonEnabled) BlueActive else BlueInactive,
                animationSpec = tween(durationMillis = 400),
                label = "CambioColorBoton"
            )

            Spacer(modifier = Modifier.height(30.dp))

            if (signupFlow is Resource.Loading) {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color.White)
                }
            } else {
                Button(
                    onClick = { viewModel.register() },
                    modifier = Modifier.fillMaxWidth().height(53.dp),
                    enabled = isButtonEnabled,
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = animatedColor,
                        disabledContainerColor = animatedColor,
                        contentColor = Color.White,
                        disabledContentColor = Color.White
                    )
                ) {
                    Text(text = stringResource(R.string.app_str_btn_register_register), fontFamily = PoppinsFamily, fontSize = 18.sp, fontWeight = FontWeight.Normal)
                }
            }
        }
    }
}

@Composable
fun VerificationRegister(navController: NavHostController, viewModel: VerificationViewModel) {
    val isVerified = viewModel.isVerified
    val context = LocalContext.current

    LaunchedEffect(isVerified) {
        if (isVerified) {
            Toast.makeText(context, "¡Correo verificado con éxito!", Toast.LENGTH_LONG).show()
            navController.navigate("email_login") {
                popUpTo(navController.graph.id) { inclusive = true }
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = stringResource(R.string.app_str_textview_verification),
                fontFamily = PoppinsFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                color = Color.White,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(30.dp))
            Text(
                text = stringResource(R.string.app_str_textview_verification_description),
                fontFamily = PoppinsFamily,
                fontWeight = FontWeight.Light,
                fontSize = 16.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = stringResource(R.string.app_str_textview_verification_description2),
                fontFamily = PoppinsFamily,
                fontWeight = FontWeight.Light,
                fontSize = 16.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(50.dp))
            CircularProgressIndicator(color = BlueActive)
        }
    }
}