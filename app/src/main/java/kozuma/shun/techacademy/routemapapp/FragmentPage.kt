package kozuma.shun.techacademy.routemapapp

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.TextView
import com.google.firebase.database.*

class FragmentPage : Fragment() {



    //呼び出しもとのアクティビティのインスタンス
    private var mListener : OnFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }
    //
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val page = arguments!!.getInt(PAGE, 0)
        // fragment_pageを複製しtextへページを表示
        val view = inflater.inflate(R.layout.fragment_page, container, false)
        view.findViewById<TextView>(R.id.textView).text = page.toString()



        return view


    }
    // 親アクティビティとの紐づけ発生イベント
    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            mListener = context
        } else {
            throw RuntimeException()
        }
    }
    override fun onDetach() {
        super.onDetach()
        mListener = null
    }
    internal interface OnFragmentInteractionListener {
        fun onFragmentInteraction(uri: Uri)
    }
    companion object {
        private val PAGE = "PAGE"
        // PageFragment生成
        fun newInstance(page: Int): FragmentPage {
            val pageFragment = FragmentPage()
            val bundle = Bundle()
            bundle.putInt(PAGE, page)
            pageFragment.arguments = bundle
            return pageFragment
        }
    }
}
