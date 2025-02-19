package br.com.concrete.githubconcretechallenge.features.repositories.datasource

import android.util.Log
import androidx.paging.PageKeyedDataSource
import br.com.concrete.githubconcretechallenge.features.repositories.model.RepositoryModel
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

class RepositoriesListPagedDataSource(
    private val remoteDataSource: RepositoriesListRemoteDataSource,
    private val compositeDisposable: CompositeDisposable,
    private var invalidateCache : Boolean = false
) : PageKeyedDataSource<Int, RepositoryModel>() {

    private fun addDisposable(disposable: Disposable) {
        compositeDisposable.add(disposable)
    }

    override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, RepositoryModel>) {

        val onSuccess = fun(response: List<RepositoryModel>) {
            invalidateCache = false
            callback.onResult(response, null, 2)
        }

        val onFailure = fun(error: Throwable) {
            Log.d("errorRepo", error.localizedMessage)
        }

        addDisposable(
            remoteDataSource.getRepositoriesList(1, invalidateCache)
                .subscribe(onSuccess, onFailure)
        )
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, RepositoryModel>) {

        val onSuccess = fun(response: List<RepositoryModel>) {
            callback.onResult(response, params.key + 1)
        }

        val onFailure = fun(error: Throwable) {
            Log.d("errorRepo", error.localizedMessage)
        }

        addDisposable(
            remoteDataSource.getRepositoriesList(params.key)
                .subscribe(onSuccess, onFailure)
        )
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, RepositoryModel>) {
    }

}