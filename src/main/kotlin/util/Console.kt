package util

object Console {

    fun spaceLine() {
        println()
    }

    fun readLine(print: String): String? {
        print("$print > ")
        return readLine()
    }

    fun clearLine() {
        print("\r")
    }

    fun printProgress(
        max: Int,
        progress: Int,
        barCount: Int = 30,
        startTime: Long = 0,
        hasPercentage: Boolean = true,
        hasETA: Boolean = true
    ) {
        print("[")

        for (i in 0 until barCount) {
            if(progress.toDouble() / max > i.toDouble() / barCount) print("=")
            else print(" ")
        }

        print("] ")

        if(hasPercentage) print("%.1f%% ".format((progress.toDouble() / max) * 100))
        if(hasETA && startTime != 0L) print("[ETA: ${calculateETA(startTime, progress.toDouble() / max)}] ")
    }

    private fun calculateETA(startTime: Long, percentage: Double): String {
        val time = System.currentTimeMillis() - startTime
        val allTime = ((1 / percentage) * time * (1 - percentage)).toLong()
        return allTime.toTime()
    }

    private fun Long.toTime() = when {
        (this / 1000) >= 3600 -> {
            val hour = (this / 1000) / 3600
            val minutes = ((this / 1000) % 3600) / 60
            val second = (this / 1000) % 60
            "${String.format("%d", hour)}:${String.format("%02d", minutes)}:${String.format("%02d", second)}"
        }
        (this / 1000) >= 60   -> {
            val minutes = (this / 1000) / 60
            val second = (this / 1000) % 60
            "${String.format("%02d", minutes)}:${String.format("%02d", second)}"
        }
        else                  -> {
            val second = (this / 1000) % 60
            "00:${String.format("%02d", second)}"
        }
    }
}