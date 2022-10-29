package com.example.record_a_day.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.record_a_day.R
import com.example.record_a_day.adapter.RecordAdapter
import com.example.record_a_day.data.RecordItem
import com.example.record_a_day.databinding.RecordFragmentBinding
import com.example.record_a_day.manager.UserDataManager
import com.google.firebase.firestore.FirebaseFirestore
import com.orhanobut.logger.Logger
import io.reactivex.observers.DisposableObserver
import java.text.SimpleDateFormat
import java.util.*

class RecordFragment : Fragment() {

    private var mBinding: RecordFragmentBinding? = null
    private val binding get() = mBinding!!

    var mContext: Context? = null

    private val TAG = "seok"

    lateinit var recordAdapter: RecordAdapter
    val datas = mutableListOf<RecordItem>()


    private var search_keyword: String? = null
    private var recyclerView: RecyclerView? = null

    val firestore = FirebaseFirestore.getInstance()


    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = RecordFragmentBinding.inflate(inflater, container, false)


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initFunctions()
        initRecyclerView()

    }

    private fun initFunctions() {
        binding.recordDay.setOnClickListener {
            val weatherData = resources.getStringArray(R.array.weather_array)
//            val weatherData = arrayOf(
//                R.drawable.clear,
//                R.drawable.cloudy,
//                R.drawable.cold,
//                R.drawable.partly_sunny,
//                R.drawable.raining,
//                R.drawable.snowing
//            )
            val myAdapter = object : ArrayAdapter<String>(mContext!!, R.layout.weather_item_spinner) {
                override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                    val v = super.getView(position, convertView, parent)
                    return v
                }

                override fun getCount(): Int {
                    return super.getCount() - 1
                }

            }
            myAdapter.addAll(weatherData.toMutableList())
            //힌트로 사용할 문구를 마지막 아이템에 추가해 줍니다.
            myAdapter.add("날씨를 선택해주세요.")
            Logger.d("initFunctions: click Record Btn")
            val builder = AlertDialog.Builder(mContext!!)
            val dialogView = layoutInflater.inflate(R.layout.record_dialog, null)
            val dialogTitle = dialogView.findViewById<EditText>(R.id.record_title)
            val dialogContent = dialogView.findViewById<EditText>(R.id.record_content)
            var dialogWeather = "clear"
            val spinner = dialogView.findViewById<Spinner>(R.id.weatherSpinner)

            spinner.adapter = myAdapter
            spinner.setSelection(myAdapter.count)
            spinner.dropDownVerticalOffset = dipToPixels(45f).toInt()

            //스피너 선택시 나오는 화면 입니다.
            spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {

                    //아이템이 클릭 되면 맨 위부터 position 0번부터 순서대로 동작하게 됩니다.
                    dialogWeather = when (position) {
                        0 -> "clear"
                        1 -> "cloudy"
                        2 -> "cold"
                        3 -> "partly_sunny"
                        4 -> "raining"
                        5 -> "snowing"
                        else -> "clear"
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    Logger.d("onNothingSelected")
                }
            }

            builder
                .setView(dialogView)
                .setPositiveButton("확인") { dialogInterface, i ->
                    var mDate = Date(System.currentTimeMillis())
                    var recordDate = SimpleDateFormat("yyyy/MM/dd").format(mDate)
                    var recordData = mutableMapOf(
                        "id" to UserDataManager.getInstance(mContext!!).id,
                        "title" to dialogTitle.text.toString(),
                        "content" to dialogContent.text.toString(),
                        "weather" to dialogWeather.toString(),
                        "date" to recordDate.toString()
                    )
                    firestore.collection("record")
                        .add(recordData)
                        .addOnSuccessListener {
                            Logger.d("onCreate: Success add record info")
                            initRecyclerView()
                        }
                        .addOnFailureListener {
                            Logger.d("onCreate: Fail add record info")

                        }
                    dialogInterface.dismiss()
                }
                .setNegativeButton("취소") { dialogInterface, i ->
                    /* 취소일 때 아무 액션이 없으므로 빈칸 */
                    dialogInterface.dismiss()
                }
                .show()
        }

    }


    private fun initRecyclerView() {
        Logger.d("initRecyclerView: ${UserDataManager.getInstance(mContext!!).id}")

        firestore.collection("record")
            .whereEqualTo("id", UserDataManager.getInstance(mContext!!).id)
            .get()
            .addOnSuccessListener { documents ->
                Log.d(TAG, "initRecyclerView: select success")
                datas.clear()
                for (document in documents) {
                    datas.apply {
                        add(
                            RecordItem(
                                document.data["title"].toString(),
                                document.data["date"].toString(),
                                document.data["weather"].toString()
                            )
                        )
                    }

                }
//                val source = Observable.create<MutableList<RecordItem>> {
//                    it.onNext(datas)
//                    it.onComplete()
//                }
//                source.subscribe(mObserver)

                recordAdapter = RecordAdapter()
                if (!recordAdapter.datas.isEmpty())
                    recordAdapter.datas.clear()
                recordAdapter.datas = datas
                binding.recyclerView.adapter = recordAdapter
                binding.recyclerView.layoutManager = LinearLayoutManager(mContext!!)
                recordAdapter.notifyDataSetChanged()
            }.addOnFailureListener {
                Logger.e("initRecyclerView: error! ")
            }

//        Log.i(TAG, "initRecyclerView: ${datas[0].title}")
//        Log.i(TAG, "initRecyclerView: ${datas[0].date}")
//        Log.i(TAG, "initRecyclerView: ${datas[0].weather}")

    }

    var mObserver = object : DisposableObserver<MutableList<RecordItem>>() {
        override fun onNext(datas: MutableList<RecordItem>) {
            for (data in datas) {
//                Log.d(TAG, "onNext: ${data.title}")
//                Log.d(TAG, "onNext: ${data.date}")
//                Log.d(TAG, "onNext: ${data.weather}")
            }

        }

        override fun onError(e: Throwable) {
            e.printStackTrace()
        }

        override fun onComplete() {
            Logger.i("onComplete")

        }

    }

    private fun dipToPixels(dipValue: Float): Float {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dipValue,
            resources.displayMetrics
        )
    }

}
