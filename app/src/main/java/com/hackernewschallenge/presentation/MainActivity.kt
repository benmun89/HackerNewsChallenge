package com.hackernewschallenge.presentation

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SwipeToDismiss
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshState
import com.hackernewschallenge.R
import com.hackernewschallenge.domain.models.Hits
import com.hackernewschallenge.presentation.theme.HackerNewsChallengeTheme
import com.hackernewschallenge.presentation.viewmodel.NewsViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.net.URLEncoder
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HackerNewsChallengeTheme {
                val navController = rememberNavController()

                NavHost(navController = navController, startDestination = "post_list") {
                    composable("post_list") {
                        PostList(modifier = Modifier.fillMaxSize(), onPostClick = { url ->
                            println("URL received in PostList: $url")
                            if (url.isNotEmpty()) {
                                val encodedUrl = URLEncoder.encode(url, "UTF-8")
                                navController.navigate("webview_screen/$encodedUrl")
                            } else {
                                println("URL is empty in PostList")
                            }
                        })
                    }
                    composable(
                        route = "webview_screen/{url}",
                        arguments = listOf(navArgument("url") { type = NavType.StringType })
                    ) { backStackEntry ->
                        val url = backStackEntry.arguments?.getString("url") ?: "https://google.com"
                        println("URL received in WebViewScreen: $url")
                        WebViewScreen(url = url, navController = navController)
                    }
                }
            }
        }
    }
}

@Composable
fun PostList(
    modifier: Modifier = Modifier,
    viewModel: NewsViewModel = hiltViewModel(),
    onPostClick: (String) -> Unit
) {
    val newsList by viewModel.news.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()
    val context = LocalContext.current

    val isInternetAvailable = remember { mutableStateOf(isInternetAvailable(context)) }
    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    DisposableEffect(context) {
        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                isInternetAvailable.value = true
            }

            override fun onLost(network: Network) {
                isInternetAvailable.value = false
            }
        }
        connectivityManager.registerDefaultNetworkCallback(callback)

        onDispose {
            connectivityManager.unregisterNetworkCallback(callback)
        }
    }

    LaunchedEffect(newsList) {
        if (newsList.isEmpty()) {
            viewModel.loadNews()
        }
    }

    val listState = rememberSaveable(saver = LazyListState.Saver) { LazyListState() }

    SwipeRefresh(state = SwipeRefreshState(isRefreshing), onRefresh = {
        viewModel.refreshNews()
    }) {
        Scaffold(content = { paddingValues ->
            Box(
                modifier = modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                if (isLoading && newsList.isEmpty()) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                } else {
                    if (newsList.isEmpty()) {
                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                            item {
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(16.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        text = stringResource(R.string.no_items),
                                        color = Color.Gray,
                                        style = MaterialTheme.typography.bodyLarge,
                                        modifier = Modifier.align(Alignment.CenterHorizontally)
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = stringResource(R.string.swipe_refresh),
                                        color = Color.Gray,
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            }
                        }
                    } else {
                        val uniqueNewsList = newsList
                            .filter { !it.isDeleted }
                            .distinctBy { it.storyId ?: "${it.title}-${it.author}" }

                        LazyColumn(
                            state = listState,
                            modifier = Modifier.padding(16.dp)
                        ) {
                            items(
                                uniqueNewsList,
                                key = { it.storyId ?: "${it.title}-${it.author}" }
                            ) { post ->
                                SwipeToDismissItem(
                                    newsItem = post,
                                    onSwipeToDelete = { viewModel.removeNewsItem(post) },
                                    onPostClick = { url ->
                                        if (isInternetAvailable.value) {
                                            onPostClick(url)
                                        } else {
                                            Toast.makeText(
                                                context,
                                                context.getString(R.string.no_internet),
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    })
                                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                            }
                        }
                    }
                }
            }
        })
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeToDismissItem(
    newsItem: Hits,
    onSwipeToDelete: (Hits) -> Unit,
    onPostClick: (String) -> Unit
) {
    val dismissState = rememberSwipeToDismissBoxState(confirmValueChange = { dismissValue ->
        if (dismissValue == SwipeToDismissBoxValue.EndToStart) {
            onSwipeToDelete(newsItem)
            true
        } else {
            false
        }
    })

    Box(modifier = Modifier.fillMaxWidth()) {
        SwipeToDismiss(state = dismissState, background = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Red)
                    .padding(horizontal = 16.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Text(text = stringResource(R.string.delete), color = Color.White)
            }
        }, dismissContent = {
            PostContent(title = newsItem.title ?: "No Title",
                subtitle = newsItem.author ?: "Unknown Author",
                createdAt = newsItem.createdAt ?: "Unknown Date",
                onClick = { onPostClick(newsItem.storyURL ?: "") })
        })
    }
}

@Composable
fun PostContent(
    title: String, subtitle: String, createdAt: String, onClick: () -> Unit
) {
    val relativeTime = getRelativeTime(createdAt)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 0.dp)
            .background(MaterialTheme.colorScheme.surface)
            .clickable { onClick() }, // Handle clicks to navigate
        shape = RectangleShape
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "$subtitle - $relativeTime",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
        }
    }
}

fun getRelativeTime(createdAt: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
        inputFormat.timeZone = TimeZone.getTimeZone("UTC")

        val date = inputFormat.parse(createdAt) ?: return "Unknown Time"
        val now = Date()

        val diffInMillis = now.time - date.time
        val diffInMinutes = TimeUnit.MILLISECONDS.toMinutes(diffInMillis)
        val diffInHours = TimeUnit.MILLISECONDS.toHours(diffInMillis)
        val diffInDays = TimeUnit.MILLISECONDS.toDays(diffInMillis)

        when {
            diffInMinutes < 60 -> "${diffInMinutes}m "
            diffInHours < 24 -> "${diffInHours}h "
            diffInDays == 1L -> "Yesterday"
            else -> {
                val outputFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                outputFormat.format(date)
            }
        }
    } catch (e: Exception) {
        "Unknown Time"
    }
}

fun isInternetAvailable(context: Context): Boolean {
    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val network = connectivityManager.activeNetwork
        val capabilities = connectivityManager.getNetworkCapabilities(network)
        return capabilities != null && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    } else {
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }
}