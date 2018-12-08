package com.example.jibum.inssagram

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.example.jibum.inssagram.model.ContentDTO
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_detail.view.*
import kotlinx.android.synthetic.main.item_detail.view.*

class DetailViewFragment : Fragment() {
    var firestore: FirebaseFirestore? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        firestore = FirebaseFirestore.getInstance()

        var view = LayoutInflater.from(inflater.context).inflate(R.layout.fragment_detail, container, false)
        view.detailviewfragment_recyclerview.adapter = DetailRecyclervierwAdapter()
        view.detailviewfragment_recyclerview.layoutManager = LinearLayoutManager(activity)


        return view
    }

    inner class DetailRecyclervierwAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        var contentDTOs: ArrayList<ContentDTO>
        val contentUidList: ArrayList<String>

        init {
            contentDTOs = ArrayList()
            contentUidList = ArrayList()

            //현재 로그인된 유저의 UID
            var uid = FirebaseAuth.getInstance().currentUser?.uid


            firestore?.collection("images")?.orderBy("timestamp")?.addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                    contentDTOs.clear()
                    contentUidList.clear()


                    for (snapshot in querySnapshot!!.documents) {
                        var item = snapshot.toObject(ContentDTO::class.java)
                        contentDTOs.add(item)
                        contentUidList.add(snapshot.id)

                    }
                    notifyDataSetChanged()
                }


        }

        //레이아웃 불러옴(item_detail)
        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {
            var view = LayoutInflater.from(parent?.context).inflate(R.layout.item_detail, parent, false)

            return CustomViewHolder(view)

        }

        inner class CustomViewHolder(view: View?) : RecyclerView.ViewHolder(view)

        override fun getItemCount(): Int {
            return contentDTOs.size

        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {


            val viewHodler = (holder as CustomViewHolder).itemView
            //유저 아이디
            viewHodler.detailviewitem_profile_textview.text = contentDTOs!![position].userID
            //이미지
            Glide.with(holder.itemView.context).load(contentDTOs!![position].imageUrl)
                .into(viewHodler.detailviewitem_imageview_content)
            //설명 텍스트
            viewHodler.detailviewitem_explain_textview.text = contentDTOs!![position].explain
            //좋아요 카운터 설정
            viewHodler.detailviewitem_favoritecounter_textview.text = "좋아요 " +
                    contentDTOs!![position].favoriteCOunt.toString() + "개"


        }
    }
}