package sdop.image.list.http

import android.content.Context
import com.google.gson.GsonBuilder
import io.reactivex.observables.ConnectableObservable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.ReplaySubject
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import sdop.image.list.BuildConfig
import sdop.image.list.util.LogUtil
import sdop.image.list.util.Tag
import sdop.image.list.http.model.*
import sdop.image.list.http.model.server.ErrorCode
import sdop.image.list.rx.DisposeBag
import sdop.image.list.rx.Variable
import sdop.image.list.rx.delay
import java.io.IOException
import java.util.concurrent.TimeUnit

/**
 *
 * Created by jei.park on 2017. 12. 20..
 */
class ImageServer() {
    private val gson = GsonBuilder().create()
    private val disposeBag = DisposeBag()
    private val client: OkHttpClient
    private val networkInProgress = Variable<HashMap<String, Any>>(hashMapOf())

    private val JSONMediaType = MediaType.parse("application/json; charset=utf-8")

    init {
        val clientBuilder = OkHttpClient.Builder()
        if (Tag.ImageServer.getValue()) {
            val logger = HttpLoggingInterceptor()
            logger.level = HttpLoggingInterceptor.Level.HEADERS
            clientBuilder.addInterceptor(logger)
        }
        clientBuilder.writeTimeout(10, TimeUnit.SECONDS)
        clientBuilder.connectTimeout(10, TimeUnit.SECONDS)
        client = clientBuilder.build()
        client.dispatcher().maxRequestsPerHost = 5
    }

    fun <T : ImageResponse> request(request: ImageRequest<T>): ConnectableObservable<ImageRequest<T>> {
        val subject = ReplaySubject.create<ImageRequest<T>>()
        val broadcast = subject.publish()
        broadcast.connect()

        request.isNetworking.set(true)
        val token = request.uniqueToken
        token?.let {
            if (token.isNotEmpty()) {
                networkInProgress.get()[token]?.let { stream ->
                    @Suppress("UNCHECKED_CAST")
                    return stream as ConnectableObservable<ImageRequest<T>>
                }
            }
        }

        request.requestState.asObservable()
                .filter { it !is RequestInitializing }
                .take(1)
                .observeOn(Schedulers.computation())
                .subscribe {
                    val publishAndComplete = {
                        request.isNetworking.set(false)
                        subject.onNext(request)
                        subject.onComplete()
                    }

                    when (it) {
                        is RequestError -> {
                            delay {
                                request.error.set(ImageJavaError(it.error))
                                request.response.set(null)
                                publishAndComplete()
                            }
                        }
                        is RequestReady -> {
                            val url = request.url
                            LogUtil.i(Tag.ImageServer, "request [$url]")

                            var builder = Request.Builder().url(url)
                            builder = when (request.method) {
                                HTTPMethod.get -> builder.get()
                                HTTPMethod.post -> {
                                    LogUtil.i(Tag.ImageServer, "getting post param)")
                                    val param = request.getParams()
                                    LogUtil.i(Tag.ImageServer, "getting post param 2")
                                    val json = if (param != null) gson.toJson(param) else "{}"
                                    LogUtil.i(Tag.ImageServer, "request body : $json")
                                    builder.post(RequestBody.create(JSONMediaType, json))
                                }
                            }

                            request.header.forEach { builder.addHeader(it.key, it.value) }
                            val call = builder.build()

                            client.newCall(call).enqueue(object : Callback {
                                override fun onFailure(call: Call?, e: IOException?) {
                                    e?.let {
                                        try {
                                            it.printStackTrace()
                                            request.error.set(ImageJavaError(it))
                                            request.response.set(null)
                                            publishAndComplete()
                                        } finally {
                                            token?.let { networkInProgress.value.remove(it) }
                                        }
                                    }
                                }

                                override fun onResponse(call: Call?, response: Response?) {
                                    val stream = response?.body()?.charStream()
                                    stream?.apply {
                                        try {
                                            val res: T = gson.fromJson(this, request.responseType)


                                            if (res.errorCode != null && res.errorMessage != null) {
                                                request.error.set(ImageServerError(ErrorCode.from(res.errorCode), res.errorMessage!!))
                                                request.response.set(null)
                                            } else {
                                                request.processResult(res)
                                                request.response.set(res)
                                            }

                                            publishAndComplete()
                                        } catch (ex: Exception) {
                                            if (BuildConfig.DEBUG) {
                                                throw ex
                                            }
                                            ex.printStackTrace()
                                            request.error.set(ImageJavaError(ex))
                                            request.response.set(null)
                                            publishAndComplete()
                                        } finally {
                                            token?.let { networkInProgress.value.remove(it) }
                                            close()
                                        }
                                    }
                                }
                            })
                        }
                    }
                }

        token?.let { networkInProgress.value.remove(it) }
        return broadcast
    }
}