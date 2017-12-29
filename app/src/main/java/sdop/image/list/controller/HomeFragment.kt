package sdop.image.list.controller

import android.os.Bundle
import android.view.View
import android.widget.Toast
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.full_list.*
import sdop.image.list.common.StaggeredGridGalleryItemDecoration
import sdop.image.list.controller.cell.home.FinderCell
import sdop.image.list.controller.cell.home.ImageCell
import sdop.image.list.controller.model.ImageModel
import sdop.image.list.databinding.FullListBinding
import sdop.image.list.http.model.server.SearchImageRequest
import sdop.image.list.http.model.server.SearchImageResponse
import sdop.image.list.model.UIEventPublisher
import sdop.image.list.rx.ImageResponseHandler
import sdop.image.list.rx.Variable
import sdop.image.list.rx.addTo
import sdop.image.list.rx.recycler.*
import sdop.image.list.util.Notifier

/**
 *
 * Created by jei.park on 2017. 12. 26..
 */
class HomeFragment : RxRecyclerFullListFragment() {
    private val event: UIEventPublisher = UIEventPublisher.create()
    private val images: Variable<List<ImageModel>> = Variable(listOf())

    override fun sourceObservable(): Observable<List<RxRecyclerCell>> {
        return images.asObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .map {
                    val list = arrayListOf<RxRecyclerCell>()
                    list.add(FinderCell(event))
                    it.map { list.add(ImageCell(this, event, it)) }
                    list
                }
    }

    override fun cellStyles(): List<RxRecyclerCellStyle> = listOf(FinderCell.style(), ImageCell.style())

    override fun adapter(): RxRecyclerViewBinder = RxRecyclerViewBinder.createStaggeredGridLayout(recycler_view, disposeBag, 2)

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (binding as? FullListBinding)?.apply {
            recyclerView.addItemDecoration(StaggeredGridGalleryItemDecoration(context))
        }

        event.subscribe {
            when (it) {
                is FinderCell.UISearchEvent -> {
                    val start = images.value.size
                    val request = SearchImageRequest(it.keyword, start.toString())
                    val handler = ImageResponseHandler.fromRequest(request)
                            .runOnSuccess {
                                (it as? SearchImageResponse)?.items?.let {
                                    images.value = it.map { ImageModel(it.link, it.sizewidth, it.sizeheight) }
                                }
                            }
                            .runOnError {
                                Notifier.toast(it.localizedMessage)
                            }

                    server.request(request)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(handler)
                }
                is ImageCell.UITabImageEvent -> {
                    // open photo
                }
            }
        }
    }

    companion object {
        fun newInstance() = HomeFragment()
    }
}