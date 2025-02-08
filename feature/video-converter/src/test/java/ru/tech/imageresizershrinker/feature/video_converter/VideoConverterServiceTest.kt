@RunWith(AndroidJUnit4::class)
class VideoConverterServiceTest {
    @get:Rule
    val tempFolder = TemporaryFolder()

    private lateinit var context: Context
    private lateinit var converterService: VideoConverterService

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        converterService = VideoConverterService(context)
    }

    @Test
    fun testVideoConversion() {
        // Create test video file
        val inputFile = tempFolder.newFile("test_video.mp4")
        val inputUri = Uri.fromFile(inputFile)

        var progress = 0f
        var completed = false
        var error: Exception? = null

        converterService.convertVideo(
            inputUri = inputUri,
            outputFormat = "video/quicktime",
            onProgress = { progress = it },
            onComplete = { completed = true },
            onError = { error = it }
        )

        assertNull(error)
        assertTrue(completed)
        assertTrue(progress > 0f)
    }
}