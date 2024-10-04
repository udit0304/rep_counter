// Copyright (c) 2024 Magic Tech Ltd

package fit.magic.cv.repcounter

import android.util.Log
import kotlin.math.abs
import kotlin.math.sqrt
import kotlin.math.acos
import fit.magic.cv.PoseLandmarkerHelper
import com.google.mediapipe.tasks.components.containers.NormalizedLandmark
import kotlin.math.PI
import kotlin.math.max
import kotlin.math.pow

class ExerciseRepCounterImpl : ExerciseRepCounter() {

    private fun angle_gor(a: NormalizedLandmark, b: NormalizedLandmark, c: NormalizedLandmark, d: NormalizedLandmark): Float {
        val ab = floatArrayOf(a.x() - b.x(), a.y() - b.y())
        val cd = floatArrayOf(c.x() - d.x(), c.y() - d.y())
        val cos_val = abs(ab[0] * cd[0] + ab[1] * cd[1]) / (sqrt(ab[0].pow(2) + ab[1].pow(2)) * sqrt(cd[0].pow(2) + cd[1].pow(2)))

        val ang = acos(cos_val)
        return (ang * 180 / PI).toFloat()
    }
    private var do_increment_rep: Boolean = true

    override fun setResults(resultBundle: PoseLandmarkerHelper.ResultBundle) {
        // process pose data in resultBundle
        //
        // use functions in base class incrementRepCount(), sendProgressUpdate(),
        // and sendFeedbackMessage() to update the UI
        for(landmark in resultBundle.results.first().landmarks()) {
            if(landmark.size >= 29){
                val rangle = angle_gor(landmark[12], landmark[24], landmark[24], landmark[26])
                val langle = angle_gor(landmark[11], landmark[23], landmark[23], landmark[25])
                val max_angle = max(rangle, langle)
                if(do_increment_rep && max_angle < 90 && max_angle > 63){
                    sendProgressUpdate(1.0F)
                    incrementRepCount()
                    sendFeedbackMessage("successfully completed one rep")
                    do_increment_rep = false
                }
                else if(max_angle < 60){
                    Log.d("Udit %", (1.0F-(60-max_angle)/60).toString())
                    sendProgressUpdate((1.0F-(60-max_angle)/60))
                    sendFeedbackMessage("Go further down to complete the rep")
                    do_increment_rep = true
                }
            }
        }

    }
}
