package com.hackernewschallenge.presentation

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.webkit.WebView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.hackernewschallenge.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WebViewScreen(
    url: String,
    modifier: Modifier = Modifier,
    navController: NavController
) {
    val context = LocalContext.current
    val webView = remember { WebView(context) }

    val isInternetAvailable = remember { mutableStateOf(isInternetAvailable(context)) }
    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    val networkCallback =
        rememberUpdatedState { isInternetAvailable.value = isInternetAvailable(context) }

    LaunchedEffect(context) {
        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                networkCallback.value()
            }

            override fun onLost(network: Network) {
                networkCallback.value()
            }
        }

        connectivityManager.registerDefaultNetworkCallback(callback)
    }

    if (!isInternetAvailable.value) {
        Text(
            text = stringResource(R.string.no_internet),
            modifier = Modifier
                .fillMaxSize()
                .wrapContentSize(Alignment.Center)
        )
    } else {
        LaunchedEffect(url) {
            if (isInternetAvailable.value) {
                webView.loadUrl(url)
            }
        }
    }

    DisposableEffect(context) {
        onDispose {
            webView.destroy()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "") },
                navigationIcon = {
                    IconButton(onClick = {
                        if (webView.canGoBack()) {
                            webView.goBack()
                        } else {
                            navController.popBackStack()
                        }
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        content = { paddingValues ->
            Box(
                modifier = modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                AndroidView(
                    modifier = Modifier.fillMaxSize(),
                    factory = {
                        webView.apply {
                            settings.javaScriptEnabled = true
                        }
                    }
                )
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun WebViewScreenPreview() {
    WebViewScreen(
        url = "https://www.example.com",
        navController = NavController(LocalContext.current)
    )
}