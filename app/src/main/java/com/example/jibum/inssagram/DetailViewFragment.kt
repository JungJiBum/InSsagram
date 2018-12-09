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
import com.google.firebase.firestore.Transaction
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


            firestore?.collection("images")?.orderBy("timestamp")
                ?.addSnapshotListener { querySnapshot, firebaseFirestoreException ->
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

        private inner class CustomViewHolder(view: View?) : RecyclerView.ViewHolder(view)

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
            viewHodler.detailviewitem_favoritecounter_textview.text = "좋아요 " +contentDTOs!![position].favoriteCount.toString() + "개"
            var uid = FirebaseAuth.getInstance().currentUser!!.uid
            viewHodler.detailviewitem_favorite_imageview.setOnClickListener {
                favoriteEvent(position)
            }

            //좋아요 클릭시
            if (contentDTOs!![position].favorites.containsKey(uid)) {
                viewHodler.detailviewitem_favorite_imageview.setImageResource(R.drawable.ic_favorite)
                //클릭하지않았을경우
            } else {
                viewHodler.detailviewitem_favorite_imageview.setImageResource(R.drawable.ic_favorite_border)
            }

            viewHodler.detailviewitem_profile_image.setOnClickListener {

                var fragment = UserFragment()
                var bundle = Bundle()
                bundle.putString("destinationUid", contentDTOs[position].uid)
                bundle.putString("userId", contentDTOs[position].userID)
                fragment.arguments = bundle
                activity!!.supportFragmentManager.beginTransaction().replace(R.id.main_content, fragment).commit()

            }

        }

        private fun favoriteEvent(position: Int) {
            var tsDoc = firestore?.collection("images")?.document(contentUidList[position])
            firestore?.runTransaction { transaction ->
                var uid = FirebaseAuth.getInstance().currentUser!!.uid
                var contentDTO = transaction.get(tsDoc!!).toObject(ContentDTO::class.java)

                if (contentDTO!!.favorites.containsKey(uid)) {
                    //좋아요를 누른상태
                    contentDTO?.favoriteCount = contentDTO?.favoriteCount - 1
                    contentDTO?.favorites.remove(uid)


                } else {
                    //좋아요를 누르지않은상태
                    contentDTO?.favorites[uid] = true
                    contentDTO?.favoriteCount = contentDTO?.favoriteCount + 1


                }
                transaction.set(tsDoc,contentDTO)

            }

        }
    }

}