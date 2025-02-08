@HiltViewModel
class VideoConverterViewModel @Inject constructor(
    private val videoConverterService: VideoConverterService
) : ViewModel() {
    var isConverting by mutableStateOf(false)
        private set

    fun convertVideo(inputUri: Uri) {
        viewModelScope.launch {
            isConverting = true
            try {
                videoConverterService.convertVideo(
                    inputUri = inputUri,
                    outputFormat = "video/quicktime",
                    onProgress = { /* Update progress */ },
                    onComplete = { /* Handle completion */ },
                    onError = { /* Handle error */ }
                )
            } finally {
                isConverting = false
            }
        }
    }
}