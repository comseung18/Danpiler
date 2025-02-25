import java.io.File
import java.io.PrintWriter

fun printToFile(
    dotContent: String,
    outputFileName: String = "test1",
    extension: String = "pdf"
) {

    // 출력 파일 경로 설정
    val outputDirectory = File("/Users/dante.won/Documents/GitHub/Danpiler/src/test/kotlin")
    val outputPdfFile = File(outputDirectory, "$outputFileName.$extension")

    // 임시 DOT 파일 생성
    val dotFile = File(outputDirectory, "$outputFileName.dot")

    kotlin.runCatching {
        // DOT 내용을 임시 파일에 작성
        PrintWriter(dotFile).use { writer ->
            writer.write(dotContent)
        }

        // Graphviz dot 명령어를 실행하여 PDF 생성
        val process =
            ProcessBuilder("dot", "-T${extension}", dotFile.absolutePath, "-o", outputPdfFile.absolutePath)
                .redirectErrorStream(true) // 오류를 표준 출력으로 리디렉션
                .start()

        process.waitFor() // 명령어 실행 완료 대기
    }
}