@Composable
fun VideoConverterScreen(
    viewModel: VideoConverterViewModel = hiltViewModel()
) {
    var showFilePicker by remember { mutableStateOf(false) }
    
    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        Button(
            onClick = { showFilePicker = true }
        ) {
            Text("Select Video")
        }

        if (viewModel.isConverting) {
            LinearProgressIndicator(
                progress = viewModel.progress,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Show conversion results
        viewModel.convertedUri?.let { uri ->
            Text("Converted file: ${uri.path}")
        }

        viewModel.error?.let { error ->
            Text(
                text = error.message ?: "Conversion failed",
                color = MaterialTheme.colorScheme.error
            )
        }
    }

    // File picker dialog
    if (showFilePicker) {
        val launcher = rememberLauncherForActivityResult(
            ActivityResultContracts.GetContent()
        ) { uri ->
            uri?.let { viewModel.convertVideo(it) }
        }
        launcher.launch("video/*")
        showFilePicker = false
    }
}