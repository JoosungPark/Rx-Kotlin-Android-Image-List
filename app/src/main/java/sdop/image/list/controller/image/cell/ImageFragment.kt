package sdop.image.list.controller.image.cell

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import sdop.image.list.common.BaseFragment
import sdop.image.list.databinding.FragmentImageBinding
import sdop.image.list.model.ImageModel

/**
 *
 * Created by jei.park on 2017. 12. 26..
 */
class ImageFragment : BaseFragment(), ImageContract.View {
    private lateinit var binding: FragmentImageBinding
    private lateinit var viewModel: ImageContract.ViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        inflater.let { binding = FragmentImageBinding.inflate(it, container, false) }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.image = viewModel.model
    }


    companion object {
        fun newInstance(model: ImageModel): ImageFragment {
            val fragment = ImageFragment()
            fragment.viewModel = ImageViewModel(fragment, model)
            return fragment
        }
    }
}