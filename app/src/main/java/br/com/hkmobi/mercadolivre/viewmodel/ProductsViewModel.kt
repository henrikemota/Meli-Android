package br.com.hkmobi.mercadolivre.viewmodel

import android.util.Property
import androidx.lifecycle.ViewModel
import androidx.lifecycle.MutableLiveData
import br.com.hkmobi.mercadolivre.model.Product
import androidx.lifecycle.LiveData
import br.com.hkmobi.mercadolivre.model.response.ResponseProduct
import br.com.hkmobi.mercadolivre.utils.Constants
import br.com.hkmobi.mercadolivre.utils.MeliInterface
import br.com.hkmobi.mercadolivre.utils.ServiceGenerator
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class ProductsViewModel: ViewModel() {

    private val mutableLiveDataProducts = MutableLiveData<List<Product>>()
    private val mutableLiveDataProductsError = MutableLiveData<String>()
    private val mutableLiveDataProductsProgress = MutableLiveData<Boolean>()
    private val mutableLiveDataCountProducts = MutableLiveData<Int>()

    fun getProducts(): LiveData<List<Product>> {
        return mutableLiveDataProducts
    }

    fun error(): LiveData<String> {
        return mutableLiveDataProductsError
    }

    fun statusProgress(): LiveData<Boolean> {
        return mutableLiveDataProductsProgress
    }

    fun countProducts(): LiveData<Int> {
        return mutableLiveDataCountProducts
    }

    fun getProducts(query: String, page: Int){
        ServiceGenerator
            .createService(MeliInterface::class.java)
            .getProducts(query, page, Constants.LIMIT)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : SingleObserver<ResponseProduct> {
                override fun onSubscribe(d: Disposable) {
                    mutableLiveDataProductsProgress.postValue(true)
                }

                override fun onError(error: Throwable) {
                    mutableLiveDataProductsProgress.postValue(false)
                    mutableLiveDataProductsError.postValue(error.message)
                }

                override fun onSuccess(response: ResponseProduct) {
                    mutableLiveDataProductsProgress.postValue(false)
                    mutableLiveDataProducts.postValue(response.results)
                    if(!response.results.isNullOrEmpty()) mutableLiveDataCountProducts.postValue(response.paging.total)
                }
            })
    }
}