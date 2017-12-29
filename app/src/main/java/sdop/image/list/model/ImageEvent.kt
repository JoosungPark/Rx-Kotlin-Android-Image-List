package sdop.image.list.model

import io.reactivex.subjects.PublishSubject

/**
 *
 * Created by jei.park on 2017. 12. 26..
 */
interface ImageUIEvent

typealias UIEventPublisher = PublishSubject<ImageUIEvent>