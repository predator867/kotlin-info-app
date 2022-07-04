package ar.tech.lab.status_downloader_for_whatsapp.status.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import ar.tech.lab.status_downloader_for_whatsapp.status.adapter.AdapterStatus
import ar.tech.lab.status_downloader_for_whatsapp.status.R
import ar.tech.lab.status_downloader_for_whatsapp.status.manifests.SplashScreen
import ar.tech.lab.status_downloader_for_whatsapp.status.databinding.ActivityMainBinding
import ar.tech.lab.status_downloader_for_whatsapp.status.model.model

class MainActivity : AppCompatActivity() {

    lateinit var rv: RecyclerView
    lateinit var list: ArrayList<model>
    lateinit var adapetr: AdapterStatus

    private lateinit var binding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frameLayout, SplashScreen(), "SplashScreen")
        fragmentTransaction.commit()


//        rv = findViewById(R.id.rv)
//        list = ArrayList()
//        adapetr = application?.let {
//            AdapterStatus(
//                it, list
//            )
//        }!!

//        adapetr.registerAdapterDataObserver(object :
//            RecyclerView.AdapterDataObserver() {
//            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
//                rv.scrollToPosition(positionStart)
//            }
//        })

//        val result = resultDataFromPrefs()
//
//        if (result) {
//
//            val sh = getSharedPreferences("DATA_PATH", MODE_PRIVATE)
//            val uri = sh.getString("PATH", "")
//
//            contentResolver.takePersistableUriPermission(
//                Uri.parse(uri),
//                Intent.FLAG_GRANT_READ_URI_PERMISSION
//            )
//
//            if (uri != null) {
//
//                val fileDoc = DocumentFile.fromTreeUri(applicationContext, Uri.parse(uri))
//                for (file: DocumentFile in fileDoc!!.listFiles()) {
//                    if (!file.name!!.endsWith(".nomedia")) {
//                        val model = model(file.name!!, file.uri.toString())
//                        list.add(model)
//
//                    }
//                }
//                setUprecylerView(list)
//            }
//
//
//        } else {
//            getFolderPermmission()
//        }

    }

//    @RequiresApi(Build.VERSION_CODES.Q)
//    private fun getFolderPermmission() {
//
////        val storageManager = application.getSystemService(Context.STORAGE_SERVICE) as StorageManager
////        val intent = storageManager.primaryStorageVolume.createOpenDocumentTreeIntent()
////        val path = "Android%2Fmedia%2Fcom.whatsapp%2FWhatsApp%2FMedia%2F.Statuses"
////        var uri = intent.getParcelableExtra<Uri>("android.provider.extra.INITIAL_URI") as Uri
////        var uriPath = uri.toString()
////        uriPath = uriPath.replace("/root/", "/tree/")
////        uriPath = uriPath + "%3A$" + path
////        uri = Uri.parse(uriPath)
////        intent.putExtra("android.provider.extra.INITIAL_URI", uri)
////        intent.putExtra("android.content.extra.SHOW_ADVANCED", true)
////        startActivityForResult(intent, 500)
//
//
//        try {
//            val createOpenDocumentTreeIntent =
//                (getSystemService(STORAGE_SERVICE) as StorageManager).primaryStorageVolume.createOpenDocumentTreeIntent()
//            val replace =
//                (createOpenDocumentTreeIntent.getParcelableExtra<Parcelable>("android.provider.extra.INITIAL_URI") as Uri?).toString()
//                    .replace("/root/", "/document/")
//            createOpenDocumentTreeIntent.putExtra(
//                "android.provider.extra.INITIAL_URI", Uri.parse(
//                    "$replace%3AAndroid%2Fmedia%2Fcom.whatsapp%2FWhatsApp%2FMedia%2F.Statuses"
//                )
//            )
//            createOpenDocumentTreeIntent.putExtra("android.content.extra.SHOW_ADVANCED", true)
//            startActivityForResult(createOpenDocumentTreeIntent, 500)
//        } catch (unused: Exception) {
//            //Toast.makeText(getContext(), "can't find an app to select media, please active your 'Files' app and/or update your phone Google play services", 1).show();
//        }
//
//
//    }
//
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (resultCode == RESULT_OK) {
//            if (requestCode == 500) {
//                val uri = data?.data
//
//                val sharedPreferences = getSharedPreferences("DATA_PATH", MODE_PRIVATE)
//                val edit = sharedPreferences.edit()
//                edit.putString("PATH", uri.toString())
//                edit.apply()
//
//                if (uri != null) {
//                    contentResolver.takePersistableUriPermission(
//                        uri,
//                        Intent.FLAG_GRANT_READ_URI_PERMISSION
//                    )
//                    val fileDoc = DocumentFile.fromTreeUri(applicationContext, uri)
//                    for (file: DocumentFile in fileDoc!!.listFiles()) {
//                        if (!file.name!!.endsWith(".nomedia")) {
//                            val model = model(file.name!!, file.uri.toString())
//                            list.add(model)
//
//                        }
//                    }
//                    setUprecylerView(list)
//                }
//            }
//        }
//    }
//
//    private fun setUprecylerView(list: ArrayList<model>) {
//
//        adapetr = application?.let {
//            AdapterStatus(
//                it, list
//            )
//        }!!
//
//        rv.apply {
//            setHasFixedSize(true)
//            layoutManager = StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL)
//            adapter = adapetr
//        }
//
//    }
//
//    private fun resultDataFromPrefs(): Boolean {
//
//        val sh = getSharedPreferences("DATA_PATH", MODE_PRIVATE)
//        val uriPath = sh.getString("PATH", "")
//        if (uriPath != null) {
//            if (uriPath.isEmpty()) {
//                return false
//            }
//        }
//
//        return true
//    }

}