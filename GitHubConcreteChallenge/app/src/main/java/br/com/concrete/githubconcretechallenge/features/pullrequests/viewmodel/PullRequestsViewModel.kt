package br.com.concrete.githubconcretechallenge.features.pullrequests.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import br.com.concrete.githubconcretechallenge.features.pullrequests.datasource.PullRequestsDataSource
import br.com.concrete.githubconcretechallenge.features.pullrequests.model.PullRequestModel
import br.com.concrete.githubconcretechallenge.features.repositories.model.RepositoryModel
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

class PullRequestsViewModel(private val pullRequestsDataSource: PullRequestsDataSource) : ViewModel() {

    private val compositeDisposable = CompositeDisposable()

    private val _liveDataPullRequestList = MutableLiveData<List<PullRequestModel>>()
    val liveDataPullRequestList : LiveData<List<PullRequestModel>>
        get() {
            return _liveDataPullRequestList
        }

    val liveDataOpenedClosedPullRequestCount : LiveData<Pair<Int, Int>> =
        Transformations.map(_liveDataPullRequestList) { pullRequestList ->
            val openedPrs = pullRequestList.count { pr -> pr.state == "open" }
            val closedPrs = pullRequestList.size - openedPrs
            openedPrs to closedPrs
        }

    private fun addDisposable(disposable: Disposable) {
        compositeDisposable.add(disposable)
    }

    fun loadPullRequests(repositoryModel: RepositoryModel) {

        val onSuccess = fun(response: List<PullRequestModel>) {
            _liveDataPullRequestList.value = response
        }

        val onFailure = fun(error: Throwable) {
            Log.d("errorRepo", error.localizedMessage)
        }

        addDisposable(
            pullRequestsDataSource.getPullRequests(repositoryModel.owner.login, repositoryModel.name)
                .subscribe(onSuccess, onFailure)
        )

    }

    override fun onCleared() {
        compositeDisposable.dispose()
    }

}
