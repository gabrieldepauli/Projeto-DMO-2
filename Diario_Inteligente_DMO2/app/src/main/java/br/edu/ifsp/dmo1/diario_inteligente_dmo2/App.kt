package br.edu.ifsp.dmo1.diario_inteligente_dmo2

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.os.Bundle
import br.edu.ifsp.dmo1.diario_inteligente_dmo2.ui.activity.MainActivity

class App : Application(), Application.ActivityLifecycleCallbacks {

    private var atividadeVisivel: Boolean = false
    private var appFoiBackground = false

    var ignorarVerificacaoFoco: Boolean = false

    override fun onCreate() {
        super.onCreate()
        registerActivityLifecycleCallbacks(this)
    }

    override fun onActivityResumed(activity: Activity) {
        if (appFoiBackground) {
            appFoiBackground = false
            if (activity !is MainActivity) {
                val intent = Intent(activity, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                activity.startActivity(intent)
            }
        }
        atividadeVisivel = true
    }

    override fun onActivityPaused(activity: Activity) {
        atividadeVisivel = false
    }

    override fun onActivityStopped(activity: Activity) {
        if (!atividadeVisivel && !ignorarVerificacaoFoco) {
            appFoiBackground = true
        }
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}
    override fun onActivityStarted(activity: Activity) {}
    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
    override fun onActivityDestroyed(activity: Activity) {}
}